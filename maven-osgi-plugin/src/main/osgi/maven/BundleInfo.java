/*
 * This file is part of the maven-osgi-plugin package.
 * Copyright (C) 2004 Andreas Frei
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Created on Apr 18, 2004
 *  
 */
package osgi.maven;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarException;

import javassist.NotFoundException;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.apache.maven.repository.DefaultArtifactFactory;

/**
 * @todo thirdparty extended to generate directories in .maven/repository
 * if it does not exists
 * @todo adding of export,import should be idempotent
 * @todo new thirdparty parameter to specify the system-classpath
 * 
 * @author andfrei
 *  
 */
public class BundleInfo
{

    private Project project;

    private String repolocal;

    private String artifactGroup;

    private String artifactId;

    private String artifactVersion;

    private String bundledir;

    private Bundle m_bundle;

    // sets as string
    private String importedPackages = "";

    private String exportedPackages = "";

    private String activator = "";

    // allow also transforming third-party libraries without activator
    private boolean thirdparty = false;

    //jar to create -osgi.jar locally, or deploy for .maven/repository
    private String thirdpartyType = "jar";

    public BundleInfo() {

    }

    //--------------------------------------------------------//
    // Public Methods
    //--------------------------------------------------------//
    public void doExecute()
    {
        System.out.println("called BundleInfo.doExecute()");
        try {
            parseJar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

    }

    public void thirdpartyOBR() {
        String group = System.getProperty("groupId");
        String name = System.getProperty("artifactId");
        String version = System.getProperty("version");
        String suffix = System.getProperty("suffix");
        String atts = System.getProperty("attributes");
        String pkgversion = System.getProperty("withpkgv");

        if (name == null || version == null || group == null) {
            System.out
                    .println("Please specify at least a name for the package: \n");
            System.out
                    .println("   Usage: maven osgi:thirdparty-obr -DgroupId=<group> "
                            + "-DartifactId=<name> -Dversion=<version>");
            return;
        }
    	
        try {
			m_bundle = new Bundle(repolocal, bundledir, group, name, version,
			        true);
			m_bundle.generateOBR(null);
			System.out.println("[echo] OBR file generated");
            
		} catch (NotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
    
    public void thirdparty()
    {

        String group = System.getProperty("groupId");
        String name = System.getProperty("artifactId");
        String version = System.getProperty("version");
        String suffix = System.getProperty("suffix");
        String atts = System.getProperty("attributes");
        String pkgversion = System.getProperty("withpkgv");
        
        if (atts == null) atts = "export,import";

        if (name == null || version == null || group == null) {
            System.out
                    .println("Please specify at least a name for the package: \n");
            System.out
                    .println("   Usage: maven osgi:thirdparty-[jar|deploy] -DgroupId=<group> "
                            + "-DartifactId=<name> -Dversion=<version>");
            return;
        }

        try {
            m_bundle = new Bundle(repolocal, bundledir, group, name, version,
                    true);
            if (pkgversion != null)
                m_bundle.setWithPkgVersion();

            if (thirdpartyType.equals("jar"))
                m_bundle.createOSGiBundle(false, suffix, atts);
            else if (thirdpartyType.equals("deploy"))
                    m_bundle.createOSGiBundle(true, suffix, atts);

        } catch (NotFoundException e) {
            System.out.println("couldn't resolve the bundle path!");
            e.printStackTrace();
        } catch (JarException e) {
            System.out.println("couldn't jar/unjar bundle!");
        } catch (IOException e) {
            System.out.println("couldn't jar/unjar bundle!");
        }

    }

    //--------------------------------------------------------//
    // Bean Methods
    //--------------------------------------------------------//

    // 
    // Setters
    //

    public void setRepositoryLocal(String repolocal)
    {
        this.repolocal = repolocal;
    }

    public void setArtifactGroup(String artifactGroup)
    {
        this.artifactGroup = artifactGroup;
    }

    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    public void setArtifactVersion(String artifactVersion)
    {
        this.artifactVersion = artifactVersion;
    }

    public void setBundleDir(String bundledir)
    {
        this.bundledir = bundledir;
    }

    public void setProject(Project project)
    {
        this.project = project;
    }

    /**
     * jar to create -osgi.jar locally, or deploy for .maven/repository
     */
    public void setThirdparty(String type)
    {
        thirdpartyType = type;
    }

    // 
    // Getters
    //

    public String getImportedPackages()
    {
        importedPackages = m_bundle.getImportPackage();

        return importedPackages;
    }

    public String getExportedPackages()
    {
        exportedPackages = m_bundle.getExportPackage();

        return exportedPackages;
    }

    public String getBundleActivator()
    {
        if (m_bundle.activatorSet.size() > 1) {
            System.out
                    .println("found more than 1 BundleActivator, please specify"
                            + "one in the project.xml");
            return null;
        }

        activator = Set2String(m_bundle.activatorSet);

        return activator;

    }

    public String getDeployedOSGiJar()
    {
        return m_bundle.getDeployOSGiJar();
    }

    //--------------------------------------------------------//
    // Helper functions
    //--------------------------------------------------------//
    public static String Set2String(Set set)
    {
    	
    	if (set.isEmpty())
    		return null;
    	
        StringBuffer sb = new StringBuffer();
        for (Iterator it = set.iterator(); it.hasNext();) {
            sb.append((String) it.next());
            if (it.hasNext()) sb.append(",");
        }

        return sb.toString();
    }

    protected void parseJar() throws IOException, NotFoundException
    {

        m_bundle = new Bundle(repolocal, bundledir, artifactGroup, artifactId,
                artifactVersion);

        // check the withpkgversion flag
        String pkgversion = System.getProperty("withpkgv");
        if (pkgversion != null)
            m_bundle.setWithPkgVersion();
        
        List deps = project.getDependencies();
        for (Iterator dit = deps.iterator(); dit.hasNext();) {
            Dependency dep = (Dependency) dit.next();
            Artifact artifact = DefaultArtifactFactory.createArtifact(dep);
            System.out.println("import now: " + artifact.getName());
            m_bundle.addImportArtifact(artifact);
        }

        String targetfile = bundledir + File.separatorChar + 
        	artifactId + "-"+  artifactVersion+".jar";
        System.out.println(targetfile);
        m_bundle.parseBundle(targetfile);

    }

}