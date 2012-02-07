package com.erdfelt.maven.xmlfresh;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.codehaus.plexus.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.erdfelt.maven.xmlfresh.io.PendingPrintWriter;
import com.erdfelt.maven.xmlfresh.util.BasicAttrComparator;

public class XmlPrettyWriter implements Closeable
{
    private Comparator<String> attributeSorter;
    private int level = 0;
    private PendingPrintWriter out;

    public XmlPrettyWriter(Writer writer)
    {
        attributeSorter = new BasicAttrComparator();
        out = new PendingPrintWriter(writer);
    }

    @Override
    public void close() throws IOException
    {
        out.close();
    }

    private CharSequence escapeAttrValue(String rawtext)
    {
        StringBuilder b = new StringBuilder();
        for (char c : rawtext.toCharArray())
        {
            switch (c)
            {
                case '"':
                    b.append("&quot;");
                    break;
                case '<':
                    b.append("&lt;");
                    break;
                case '>':
                    b.append("&gt;");
                    break;
                case '&':
                    b.append("&amp;");
                    break;
                default:
                    b.append(c);
                    break;
            }
        }
        return b;
    }

    public Comparator<String> getAttributeSorter()
    {
        return attributeSorter;
    }

    private Map<String, String> getAttrMap(Element elem)
    {
        Map<String, String> attrmap = new TreeMap<String, String>(attributeSorter);

        NamedNodeMap nnm = elem.getAttributes();
        int len = nnm.getLength();
        for (int i = 0; i < len; i++)
        {
            Attr attr = (Attr)nnm.item(i);
            attrmap.put(attr.getName(),attr.getValue());
        }

        return attrmap;
    }

    private void outNewLine() throws IOException
    {
        out.println();
        for (int i = 0; i < level; i++)
        {
            out.print("  ");
        }
    }

    private void outXmlEncoded(String rawtext) throws IOException
    {
        for (char c : rawtext.toCharArray())
        {
            switch (c)
            {
                case '<':
                    out.print("&lt;");
                    break;
                case '>':
                    out.print("&gt;");
                    break;
                case '&':
                    out.print("&amp;");
                    break;
                default:
                    out.print(c);
                    break;
            }
        }
    }

    public void setAttributeSorter(Comparator<String> attributeSorter)
    {
        this.attributeSorter = attributeSorter;
    }

    public void write(Document doc) throws IOException
    {
        writeXmlDeclaration(doc);
        writeNode(doc.getDocumentElement());
        out.println(); // last EOL
    }

    private boolean writeComment(Comment node) throws IOException
    {
        String rawtext = node.getNodeValue();
        if (StringUtils.isNotBlank(rawtext))
        {
            outNewLine();
            out.printf("<!--%s-->",rawtext);
            return true;
        }

        return false;
    }

    private void writeElement(Element elem) throws IOException
    {
        outNewLine();
        out.printf("<%s",elem.getNodeName());
        // Attributes
        String elemspace = elem.getNodeName();
        elemspace = elemspace.replaceAll("."," ");
        Map<String, String> attrmap = getAttrMap(elem);
        boolean linebreak = false;
        for (Map.Entry<String, String> attr : attrmap.entrySet())
        {
            if (linebreak)
            {
                outNewLine();
                out.printf("%s ",elemspace);
            }
            out.printf(" %s=\"%s\"",attr.getKey(),escapeAttrValue(attr.getValue()));
            linebreak = true;
        }

        NodeList nodes = elem.getChildNodes();
        int length = nodes.getLength();
        if (length > 0)
        {
            boolean hasOutput = false;
            out.pendingWrite(">");
            level++;
            for (int i = 0; i < length; i++)
            {
                hasOutput ^= writeNode(nodes.item(i));
            }
            level--;
            if (hasOutput)
            {
                outNewLine();
                out.printf("</%s>",elem.getNodeName());
            } else {
                out.dropPending();
                out.printf("/>");
            }
        }
        else
        {
            out.printf("/>");
        }
    }

    private boolean writeNode(Node node) throws IOException
    {
        switch (node.getNodeType())
        {
            case Node.ELEMENT_NODE:
                writeElement((Element)node);
                return true;
            case Node.TEXT_NODE:
                return writeText((Text)node);
            case Node.COMMENT_NODE:
                return writeComment((Comment)node);
        }

        return false;
    }

    private boolean writeText(Text node) throws IOException
    {
        String rawtext = node.getNodeValue().trim();
        if (StringUtils.isNotBlank(rawtext))
        {
            outXmlEncoded(rawtext);
            return true;
        }

        return false;
    }

    private void writeXmlDeclaration(Document doc) throws IOException
    {
        String ver = doc.getXmlVersion();
        if (StringUtils.isBlank(ver))
        {
            ver = "1.0";
        }
        String encoding = doc.getXmlEncoding();
        if (StringUtils.isBlank(encoding))
        {
            encoding = "UTF-8";
        }
        out.printf("<?xml version=\"%s\" encoding=\"%s\"?>",ver,encoding);
    }
}
