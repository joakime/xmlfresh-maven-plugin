package com.erdfelt.maven.xmlfresh;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlUtil {
    private static void close(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (IOException ignore) {
            /* ignore */
        }
    }

    /**
     * Get attribute value.
     * 
     * @param element
     * @param keyName
     * @return
     */
    public static String getAttributeValue(Element element, String keyName) {
        Attr attr = element.getAttributeNode(keyName);
        if (attr == null) {
            return null;
        }
        return attr.getValue();
    }

    public static Element getElement(Element startElem, String... elementNames) {
        Element elem = startElem;
        for (String elemName : elementNames) {
            elem = getFirstElement(elem, elemName);
            if (elem == null) {
                return null;
            }
        }
        return elem;
    }

    public static Element getFirstElement(Element elem, String childName) {
        NodeList nodes = elem.getChildNodes();
        if (nodes == null) {
            return null;
        }
        int count = nodes.getLength();
        for (int i = 0; i < count; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue; // skip, not an element node.
            }
            if (childName.equals(node.getNodeName())) {
                return (Element) node;
            }
        }
        return null;
    }

    /**
     * Read xml file using JAXP
     */
    public static Document read(File xmlFile) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xmlFile);
        return doc;
    }

    public static void writePretty(File xmlFile, Document doc) throws IOException {
        FileWriter writer = null;
        EclipsePrettyPrinter pretty = null;
        try {
            System.out.printf("Writing XML: %s%n", xmlFile);
            writer = new FileWriter(xmlFile);
            pretty = new EclipsePrettyPrinter(writer);
            pretty.write(doc);
        } finally {
            close(pretty);
            close(writer);
        }
    }
}
