package net.erdfelt.maven.xmlfresh;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import net.erdfelt.maven.xmlfresh.io.FileFinder;

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
     * configuration properties for tidy
     * 
     * @parameter
     */
    protected Map<String,String> tidy;
    
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

    
    protected static Map<String,String> getDefaultTidyConfiguration()
    {
        Map<String,String> defaultTidyConfiguration = new HashMap<String,String>();
        
        defaultTidyConfiguration.put("output-xml","true");
        defaultTidyConfiguration.put("input-xml","true");
        defaultTidyConfiguration.put("add-xml-space","false");
        defaultTidyConfiguration.put("add-xml-decl","true");
        defaultTidyConfiguration.put("wrap","80");
        defaultTidyConfiguration.put("indent","auto");
        defaultTidyConfiguration.put("indent-spaces","2");
        defaultTidyConfiguration.put("indent-cdata","false");
        defaultTidyConfiguration.put("escape-cdata","false");
        
        return defaultTidyConfiguration;
    }
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        boolean useDefaultExcludes = true;
        FileFinder finder = new FileFinder(basedir,includes,excludes,useDefaultExcludes);

        Map<String,String> tidyConfiguration = getDefaultTidyConfiguration();
        
        tidyConfiguration.putAll(tidy);
        
        xmlformatter = new XmlFormatter(tidyConfiguration);
        
        for (String filename : finder.getSelectedFiles())
        {
            File file = new File(filename);
            getLog().info("Format in-place: " + file);
            xmlformatter.format(file);
        }

    }
}
