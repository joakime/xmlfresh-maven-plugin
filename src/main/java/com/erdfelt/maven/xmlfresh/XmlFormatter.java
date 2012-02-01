package com.erdfelt.maven.xmlfresh;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.erdfelt.maven.xmlfresh.util.BasicAttrComparator;
import com.erdfelt.maven.xmlfresh.util.IO;

public class XmlFormatter
{
    private Comparator<String> attributeSorter = new BasicAttrComparator();

    public Comparator<String> getAttributeSorter()
    {
        return attributeSorter;
    }

    /**
     * Read xml file using JAXP
     */
    public Document read(File xmlFile) throws IOException, ParserConfigurationException, SAXException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xmlFile);
        return doc;
    }

    public void setAttributeSorter(Comparator<String> attributeSorter)
    {
        this.attributeSorter = attributeSorter;
    }

    public void writePretty(File xmlFile, Document doc) throws IOException
    {
        FileWriter writer = null;
        try
        {
            System.out.printf("Writing XML: %s%n",xmlFile);
            writer = new FileWriter(xmlFile);
            writePretty(writer,doc);
        }
        finally
        {
            IO.close(writer);
        }
    }

    public void writePretty(Writer writer, Document doc)
    {
        XmlPrettyWriter pretty = null;
        try
        {
            pretty = new XmlPrettyWriter(writer);
            pretty.setAttributeSorter(attributeSorter);
            pretty.write(doc);
        }
        finally
        {
            IO.close(pretty);
        }
    }
}
