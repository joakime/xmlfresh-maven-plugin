package com.erdfelt.maven.xmlfresh;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.erdfelt.maven.xmlfresh.io.FileFinder;

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
    
    private XmlFormatter xmlformatter;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        boolean useDefaultExcludes = true;
        FileFinder finder = new FileFinder(basedir,includes,excludes,useDefaultExcludes);

        xmlformatter = new XmlFormatter();

        for (String filename : finder.getSelectedFiles())
        {
            File file = new File(filename);
            getLog().info("Format in-place: " + file);
            xmlformatter.format(file);
        }

    }
}
