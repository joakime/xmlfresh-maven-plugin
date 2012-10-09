package net.erdfelt.maven.xmlfresh;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.jetty.toolchain.test.IO;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

public class XmlFormatterTest
{
    @Test
    public void testFormatSample() throws Exception
    {
        XmlFormatter xmlformatter = new XmlFormatter(XmlFreshenMojo.getDefaultTidyConfiguration());

        String formatted = formatXml(xmlformatter,"sample-raw.xml");
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

    private String formatXml(XmlFormatter xmlformatter, String filename) throws Exception
    {
        File inputXml = MavenTestingUtils.getTestResourceFile(filename);
        Document doc = xmlformatter.read(inputXml);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        xmlformatter.writePretty(out,doc);
        return out.toString();
    }
}
