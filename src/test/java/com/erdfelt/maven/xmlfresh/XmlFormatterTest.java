package com.erdfelt.maven.xmlfresh;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.toolchain.test.IO;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import com.erdfelt.maven.xmlfresh.util.WeightedAttrComparator;

public class XmlFormatterTest
{
    @Test
    public void testFormatSample() throws Exception
    {
        String formatted = formatXml("sample-raw.xml");
        System.out.println("## formatted\n" + formatted);
        String expected = loadXml("sample-formatted.xml");
        Assert.assertEquals(expected,formatted);
    }

    private String loadXml(String filename) throws IOException
    {
        File inputXml = MavenTestingUtils.getTestResourceFile(filename);
        FileInputStream in = null;
        ByteArrayOutputStream out = null;
        try
        {
            in = new FileInputStream(inputXml);
            int len = (int)inputXml.length();
            out = new ByteArrayOutputStream(len);
            IO.copy(in,out);
            return new String(out.toByteArray());
        }
        finally
        {
            IO.close(out);
            IO.close(in);
        }
    }

    private String formatXml(String filename) throws Exception
    {
        File inputXml = MavenTestingUtils.getTestResourceFile(filename);
        XmlFormatter formatter = new XmlFormatter();
        WeightedAttrComparator weightedSorter = new WeightedAttrComparator();
        List<String> importantNames = new ArrayList<String>();
        importantNames.add("id");
        weightedSorter.setImportantNames(importantNames);
        formatter.setAttributeSorter(weightedSorter);
        Document doc = formatter.read(inputXml);
        StringWriter writer = new StringWriter();
        formatter.writePretty(writer,doc);
        return writer.toString();
    }
}
