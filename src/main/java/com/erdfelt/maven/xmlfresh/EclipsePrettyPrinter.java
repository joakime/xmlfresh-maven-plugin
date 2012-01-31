package com.erdfelt.maven.xmlfresh;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
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

public class EclipsePrettyPrinter implements Closeable {
    private PrintWriter out;
    private int         level     = 0;
    private boolean     wroteText = false;

    public EclipsePrettyPrinter(Writer writer) {
        if (writer instanceof PrintWriter) {
            out = (PrintWriter) writer;
        } else {
            out = new PrintWriter(writer);
        }
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    private CharSequence escapeAttrValue(String rawtext) {
        StringBuilder b = new StringBuilder();
        for (char c : rawtext.toCharArray()) {
            switch (c) {
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

    private Map<String, String> getAttrMap(Element elem) {
        Map<String, String> attrmap = new TreeMap<String, String>(WeightedAttrComparator.INSTANCE);

        NamedNodeMap nnm = elem.getAttributes();
        int len = nnm.getLength();
        for (int i = 0; i < len; i++) {
            Attr attr = (Attr) nnm.item(i);
            attrmap.put(attr.getName(), attr.getValue());
        }

        return attrmap;
    }

    private void outNewLine() {
        out.println();
        for (int i = 0; i < level; i++) {
            out.print("  ");
        }
    }

    private void outXmlEncoded(String rawtext) {
        for (char c : rawtext.toCharArray()) {
            switch (c) {
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

    public void write(Document doc) {
        writeXmlDeclaration(doc);
        writeNode(doc.getDocumentElement());
        out.println(); // last EOL
    }

    private void writeComment(Comment node) {
        String rawtext = node.getNodeValue();
        if (StringUtils.isNotBlank(rawtext)) {
            outNewLine();
            out.printf("<!--%s-->", rawtext);
            wroteText = true;
        }
    }

    private void writeElement(Element elem) {
        outNewLine();
        out.printf("<%s", elem.getNodeName());
        // Attributes
        String elemspace = elem.getNodeName();
        elemspace = elemspace.replaceAll(".", " ");
        Map<String, String> attrmap = getAttrMap(elem);
        boolean linebreak = false;
        for (Map.Entry<String, String> attr : attrmap.entrySet()) {
            if (linebreak) {
                outNewLine();
                out.printf("%s ", elemspace);
            }
            out.printf(" %s=\"%s\"", attr.getKey(), escapeAttrValue(attr.getValue()));
            linebreak = true;
        }

        NodeList nodes = elem.getChildNodes();
        int length = nodes.getLength();
        if (length > 0) {
            out.printf(">");
            level++;
            for (int i = 0; i < length; i++) {
                writeNode(nodes.item(i));
            }
            level--;
            if (!wroteText) {
                outNewLine();
            }
            out.printf("</%s>", elem.getNodeName());
        } else {
            out.printf("/>");
        }
    }

    private void writeNode(Node node) {
        wroteText = false;
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                writeElement((Element) node);
                break;
            case Node.TEXT_NODE:
                writeText((Text) node);
                break;
            case Node.COMMENT_NODE:
                writeComment((Comment) node);
                break;
        }
    }

    private void writeText(Text node) {
        String rawtext = node.getNodeValue();
        if (StringUtils.isNotBlank(rawtext)) {
            outXmlEncoded(rawtext);
            wroteText = true;
        }
    }

    private void writeXmlDeclaration(Document doc) {
        String ver = doc.getXmlVersion();
        if (StringUtils.isBlank(ver)) {
            ver = "1.0";
        }
        String encoding = doc.getXmlEncoding();
        if (StringUtils.isBlank(encoding)) {
            encoding = "UTF-8";
        }
        out.printf("<?xml version=\"%s\" encoding=\"%s\"?>", ver, encoding);
    }
}
