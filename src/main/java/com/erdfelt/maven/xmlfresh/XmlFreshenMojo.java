package com.erdfelt.maven.xmlfresh;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

/**
 * Formats the pom.xml and other XML files a known pretty print format that is consistent for source control and readable by humans in editors.<br/>
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
public class XmlFreshenMojo extends AbstractMojo {
    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * TODO: make a configurable list of XML patterns to scan for and freshen in place.
     * The <code>AndroidManifest.xml</code> file.
     * 
     * @parameter default-value="${project.basedir}/AndroidManifest.xml"
     */
    protected File       androidManifestFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        formatPom();
        if ("apk".equals(project.getPackaging()) && fileExists(androidManifestFile)) {
            formatAndroidManifest();
        }
    }

    private boolean fileExists(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            return false;
        }
        return file.isFile();
    }

    private void formatAndroidManifest() throws MojoFailureException {
        try {
            Document doc = XmlUtil.read(androidManifestFile);
            XmlUtil.writePretty(androidManifestFile, doc);
        } catch (Throwable t) {
            throw new MojoFailureException("Failed to format android manifest: " + androidManifestFile, t);
        }
    }

    private void formatPom() throws MojoFailureException {
        try {
            Document doc = XmlUtil.read(project.getFile());
            XmlUtil.writePretty(project.getFile(), doc);
        } catch (Throwable t) {
            throw new MojoFailureException("Failed to format pom: " + project.getFile(), t);
        }
    }
}
