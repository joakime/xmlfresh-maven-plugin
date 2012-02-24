package net.erdfelt.maven.xmlfresh.io;

import java.io.Closeable;
import java.io.IOException;

public final class IO
{
    public static final void close(Closeable c)
    {
        if (c == null)
        {
            return;
        }

        try
        {
            c.close();
        }
        catch (IOException ignore)
        {
            /* ignore */
        }
    }
}
