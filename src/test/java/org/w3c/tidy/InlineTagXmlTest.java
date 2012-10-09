package org.w3c.tidy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.eclipse.jetty.toolchain.test.IO;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class InlineTagXmlTest
{
    private Tidy tidy;

    @Before
    public void before() throws Exception
    {
        tidy = new Tidy();
        // Properties can be found at http://tidy.sourceforge.net/docs/quickref.html
        Properties props = new Properties();
        props.setProperty("output-xml","true");
        props.setProperty("input-xml","true");
        props.setProperty("add-xml-space","false");
        props.setProperty("add-xml-decl","true");

        props.setProperty("wrap","92");
        props.setProperty("indent","auto");
        // props.setProperty("indent-attributes", "true");
        props.setProperty("indent-spaces","2");
        props.setProperty("indent-cdata","false");
        props.setProperty("escape-cdata","false");
        props.setProperty("new-inline-tags","code");

        // Not present in jtidy (yet)
        // props.setProperty("sort-attributes","true");
        // props.setProperty("hide-end-tags","false");

        tidy.setConfigurationFromProps(props);

        // Make output quiet
        tidy.setOnlyErrors(false);
        tidy.setQuiet(false);

        tidy.getConfiguration().printConfigOptions(new PrintWriter(System.out),true);
       
    }

    @Test
    public void testInlineTagXml() throws Exception
    {
        File input = MavenTestingUtils.getTestResourceFile("inline-text/inline-tag-input.xml");
        
        MavenTestingUtils.getTargetTestingDir("inline-text").mkdirs();
        File outputFile = new File(MavenTestingUtils.getTargetTestingDir("inline-text") + "/inline-tag-test.xml");
        
        FileOutputStream out = null;
        FileReader reader = null;
        try
        {
            outputFile.createNewFile();
            
            reader = new FileReader(input);
            Document document = tidy.parseDOM(reader,null);
            out = new FileOutputStream(outputFile,false);

            tidy.pprint(document,out);          
        }
        finally
        {
            IO.close(out);
        }
        
        String expected = IO.readToString(MavenTestingUtils.getTestResourceFile("inline-text/inline-tag-expected.xml"));
        String actual = IO.readToString(new File(MavenTestingUtils.getTargetTestingDir("inline-text") + "/inline-tag-test.xml"));

        Assert.assertEquals("Actual output should match expected", expected,actual);
    }

}
