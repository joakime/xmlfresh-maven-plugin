package com.erdfelt.maven.xmlfresh.io;

import java.io.IOException;
import java.io.Writer;

/**
 * Similar to PrintWriter, but with additional .pending(String) PendingPrintWriter
 */
public class PendingPrintWriter extends Writer
{
    private Writer out;
    private StringBuilder pending = new StringBuilder();
    private boolean hasPending = false;
    private String lineSeparator;

    public PendingPrintWriter(Writer out)
    {
        super(out);
        this.out = out;
        lineSeparator = System.getProperty("line.separator");
    }

    public void pendingWrite(String str)
    {
        if (str == null)
        {
            return;
        }
        if (str.length() <= 0)
        {
            return;
        }
        pending.append(str);
        hasPending = true;
    }

    public void dropPending()
    {
        pending.setLength(0);
        hasPending = false;
    }

    public void printf(String format, Object... args) throws IOException
    {
        write(String.format(format,args));
    }

    private void newLine() throws IOException
    {
        synchronized (lock)
        {
            ensureOpen();
            out.write(lineSeparator);
        }
    }

    public void println() throws IOException
    {
        newLine();
    }

    public void print(String s) throws IOException
    {
        if (s == null)
        {
            s = "null";
        }
        write(s);
    }

    public void print(char c) throws IOException
    {
        write(c);
    }

    @Override
    public void write(int c) throws IOException
    {
        synchronized (lock)
        {
            ensureOpen();
            writePending();
            out.write(c);
        }
    }

    @Override
    public void write(char[] buf, int off, int len) throws IOException
    {
        synchronized (lock)
        {
            ensureOpen();
            writePending();
            out.write(buf,off,len);
        }
    }

    @Override
    public void write(String s, int off, int len) throws IOException
    {
        synchronized (lock)
        {
            ensureOpen();
            writePending();
            out.write(s,off,len);
        }
    }

    @Override
    public void write(String s) throws IOException
    {
        synchronized (lock)
        {
            ensureOpen();
            writePending();
            write(s,0,s.length());
        }
    }

    private void writePending() throws IOException
    {
        if (hasPending)
        {
            out.write(pending.toString());
            dropPending();
        }
    }

    private void ensureOpen() throws IOException
    {
        if (out == null)
        {
            throw new IOException("Stream closed");
        }
    }

    @Override
    public void flush() throws IOException
    {
        synchronized (lock)
        {
            ensureOpen();
            out.flush();
        }
    }

    @Override
    public void close() throws IOException
    {
        synchronized (lock)
        {
            if (out == null)
            {
                return;
            }
            out.close();
            out = null;
        }
    }
}
