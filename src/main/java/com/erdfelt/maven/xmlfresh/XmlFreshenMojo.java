package com.erdfelt.maven.xmlfresh;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;
import org.w3c.dom.Document;

import com.erdfelt.maven.xmlfresh.util.FileFinder;
import com.erdfelt.maven.xmlfresh.util.WeightedAttrComparator;

/**
 * Formats the pom.xml and other XML files a known pretty print format that is consistent for source control and
 * readable by humans in editors.<br/>
 * Call
 * 
 * <pre>
 *   mvn xmlfresh:freshen
 * </pre>
 * 
 * @goal freshen
 * @requiresProject true
 * @phase initialize
 */
public class XmlFreshenMojo extends AbstractMojo
{
    /**
     * The base directory
     * 
     * @parameter expression="${xmlfresh.basedir}" default-value="${basedir}"
     * @required
     */
    protected File basedir;

    /**
     * The list of file patterns to look for. by default all XML files are selected.
     * 
     * @parameter
     */
    protected String includes[] = new String[]
    { "**/*.xml" };

    /**
     * The list of file patterns to exclude. by default all SCM/Temp/IDE files are excluded.
     * 
     * @parameter
     */
    protected String excludes[] = new String[0];

    /**
     * The list of weighted attribute names (comma separated)
     * <p>
     * Used for sorting the attributes on an xml element.
     * <p>
     * Entries present on this configurable parameter are placed before all other sorting on an attribute list.
     * <p>
     * Sorting of attributes is done in the order, Namespace Definitions, Weighted / Important Attributes in order
     * defined, all remaining attributes sorted by name alphabetically
     * 
     * @parameter default-value="id"
     */
    protected String importantAttributes;

    private XmlFormatter formatter;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        List<String> importantNames = new ArrayList<String>();
        if (StringUtils.isNotBlank(importantAttributes))
        {
            String names[] = StringUtils.split(importantAttributes,",");
            importantNames.addAll(Arrays.asList(names));
        }

        WeightedAttrComparator weightedSorter = new WeightedAttrComparator();
        weightedSorter.setImportantNames(importantNames);

        formatter = new XmlFormatter();
        formatter.setAttributeSorter(weightedSorter);

        boolean useDefaultExcludes = true;
        FileFinder finder = new FileFinder(basedir,includes,excludes,useDefaultExcludes);

        for (String filename : finder.getSelectedFiles())
        {
            File file = new File(filename);
            getLog().info("Format in-place: " + file);
        }
    }

    public void formatInplace(File file) throws MojoFailureException
    {
        try
        {
            Document doc = formatter.read(file);
            formatter.writePretty(file,doc);
        }
        catch (Throwable t)
        {
            throw new MojoFailureException("Failed to format XML: " + file,t);
        }
    }
}
