package net.erdfelt.maven.xmlfresh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

import net.erdfelt.maven.xmlfresh.io.IO;

public class XmlFormatter
{
    private Tidy tidy;

    public XmlFormatter(Map<String, String> tidyConfiguration)
    {
        tidy = new Tidy();
                
        // Properties can be found at http://tidy.sourceforge.net/docs/quickref.html
        Properties props = new Properties();
        
        for ( String key : tidyConfiguration.keySet() )
        {
            props.setProperty(key, tidyConfiguration.get(key));
        }
     
        //props.setProperty("new-inline-tags", "code, filename, seeds, fruit");
       
        // Not present in jtidy (yet)
        // props.setProperty("sort-attributes","true");
        // props.setProperty("hide-end-tags","false");

        tidy.setConfigurationFromProps(props);

        // Make output quiet
        tidy.setOnlyErrors(true);
        tidy.setQuiet(true);

    }

    /**
     * Read xml file using JTidy
     */
    public Document read(File xmlFile) throws IOException, ParserConfigurationException, SAXException
    {
        FileReader reader = null;
        try
        {
            reader = new FileReader(xmlFile);
            return tidy.parseDOM(reader,null);
        }
        finally
        {
            IO.close(reader);
        }
    }

    public void writePretty(File xmlFile, Document doc) throws IOException
    {
        FileOutputStream out = null;
        try
        {
            System.out.printf("Writing XML: %s%n",xmlFile);
            out = new FileOutputStream(xmlFile,false);
            writePretty(out,doc);
        }
        finally
        {
            IO.close(out);
        }
    }

    public void writePretty(OutputStream out, Document doc) throws IOException
    {
        tidy.pprint(doc,out);
    }

    public void format(File file) throws MojoFailureException
    {
        try
        {
            Document doc = read(file);
            writePretty(file,doc);
        }
        catch (Throwable t)
        {
            throw new MojoFailureException("Failed to format XML: " + file,t);
        }
    }
}
