package com.erdfelt.maven.xmlfresh.io;

import static java.util.Arrays.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.AbstractScanner;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Facade for {@link DirectoryScanner} to make things easier
 */
public final class FileFinder
{
    private File basedir;
    private String[] included;
    private String[] excluded;

    private DirectoryScanner scanner;

    public FileFinder(File basedir, String included[], String excluded[], boolean useDefaultExcludes)
    {
        this.basedir = basedir;
        this.included = buildInclusions(included);
        this.excluded = buildExclusions(excluded,useDefaultExcludes);
    }

    public String[] getIncluded()
    {
        return included;
    }

    public String[] getExcluded()
    {
        return excluded;
    }

    public File getBasedir()
    {
        return basedir;
    }

    public String[] getSelectedFiles()
    {
        if (scanner == null)
        {
            scanner = new DirectoryScanner();
            scanner.setBasedir(basedir);
            scanner.setIncludes(included);
            scanner.setExcludes(excluded);
            scanner.scan();
        }
        return scanner.getIncludedFiles();
    }

    private static String[] buildExclusions(String[] excluded, boolean useDefaultExcludes)
    {
        List<String> exclusions = new ArrayList<String>();

        if (useDefaultExcludes)
        {
            // Default SCM excludes as managed by Plexus
            exclusions.addAll(asList(AbstractScanner.DEFAULTEXCLUDES));

            // Maven files/dirs
            exclusions.add("**/target/**");

            // Eclipse files/dirs
            exclusions.add("**/.classpath");
            exclusions.add("**/.project");
            exclusions.add("**/.settings/**");

            // IDEA files/dirs
            exclusions.add("**/*.iml");
            exclusions.add("**/*.ipr");
            exclusions.add("**/*.iws");

            // Descriptors / service files
            exclusions.add("**/MANIFEST.MF");
            exclusions.add("**/META-INF/services/**");
        }

        if ((excluded != null) && (excluded.length > 0))
        {
            exclusions.addAll(asList(excluded));
        }

        return exclusions.toArray(new String[exclusions.size()]);
    }

    private static String[] buildInclusions(String[] included)
    {
        if ((included != null) && (included.length > 0))
        {
            return included;
        }
        else
        {
            // default includes
            return new String[]
            { "**" };
        }
    }
}
