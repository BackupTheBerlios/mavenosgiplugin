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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarException;
import java.util.jar.JarFile;

import javassist.NotFoundException;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.apache.maven.repository.DefaultArtifactFactory;

/**
 * @todo Add versions to the export, import package.
 * @todo Put the parsing part into a verification step.
 * 
 * @author andfrei
 *  
 */
public class Bundle
{

    /**
     * bgroup, bname, bversion defines a bundle
     */
    private String bgroup;

    private String bname;

    private String bversion;

    private String apivendor;

    private String vendor;

    private String category;
    
    private String updatelocation;

    private String sourceUrl;
    
    private String docUrl;
    
    private Project project;
    
    /**
     * bgroup, bname, bversion can also be represented as a maven artifact.
     */
    Artifact artifact;

    /**
     * Set withpkgversion so the import/export package string has the
     * specification-version. Default is false, not version numbers.
     */
    boolean withpkgv = false;

    /**
     * Specify the path where the jar resides.
     */
    private String bundledir = ".";

    /**
     * Specify the root path for the repository.
     */
    private String repolocal = ".";

    /**
     * The path to the deployed osgi jar.
     */
    String deployosgijar = null;

    /**
     * check the system_CP if class supported (false), else check the CDC-Basis,
     * CDC-Foundation (EE) classes.
     */
    boolean SYSTEM_CP = true;

    private boolean thirdparty = false;

    /**
     * List of all dependency artifacts.
     */
    private Vector importArtifacts = new Vector();

    /**
     * ImportSet collecting the packages which have to be imported.
     */
    Set importSet = new TreeSet();

    /**
     * ExportSet collecting the packages wich can be exported.
     */
    Set exportSet = new TreeSet();

    /**
     * ActivatorSet collects the Classes implementing the BundleActivator.
     */
    Set activatorSet = new TreeSet();


    /**
     * A Bundle represents a normal jar file.
     * 
     * @param repolocal
     * @param bundledir
     * @param group
     * @param name
     * @param version
     */
    public Bundle(String repolocal, String bundledir, String group, String name, String version)
            throws NotFoundException
    {
        this.repolocal = repolocal;
        this.bundledir = bundledir;
        this.bgroup = group;
        this.bname = name;
        this.bversion = version;

        // generate a Bundle-Structure, hacky but no API
        Dependency thisdep = new Dependency();
        thisdep.setArtifactId(name);
        thisdep.setGroupId(group);
        thisdep.setVersion(version);
        this.artifact = DefaultArtifactFactory.createArtifact(thisdep);

        String path = bundledir + File.separatorChar + artifact.getName();

        BundleInfo.bverifier.getDepsClasses().addImportJar(path);
    }

    /**
     * Specify thirdarty for packages which have to be transformed into OSGi
     * packages.
     * 
     * @param repolocal
     * @param bundledir
     * @param group
     * @param name
     * @param version
     * @param newversion
     */
    public Bundle(String repolocal, String bundledir, String group, 
            String name, String version, boolean thirdparty)
            throws NotFoundException
    {
        this(repolocal, bundledir, group, name, version);
        this.thirdparty = thirdparty;
    }

    public Bundle(String repolocal, String bundledir, String group, 
            String name, String version, String apivendor,
            String vendor, String category, String updatelocation,
            String srcurl, String docurl, Project project) throws NotFoundException
    {
        this(repolocal, bundledir, group, name, version);
        this.apivendor = apivendor;
        this.vendor = vendor;
        this.category = category;
        this.updatelocation = updatelocation;
        this.sourceUrl = srcurl;
        this.docUrl = docurl;
        this.project = project;
    }

    public void setWithPkgVersion()
    {
        withpkgv = true;
    }

    /**
     * Add a dependency artifact to the bundle.
     * 
     * @param artifact
     */
    public void addImportArtifact(Artifact artifact)
    {
        importArtifacts.add(artifact);

        // artifact to the javassist classpool
        BundleInfo.bverifier.getDepsClasses().addImportArtifact(repolocal, artifact);
    }

    /**
     * Parses the given jarfile for the imported, exported packages and the
     * BundleActivator implementation classes.
     * 
     * @param jarfile
     */
    public void parseBundle(String jarfile)
    {
        // clean up for a new parse
        importSet.clear();
        exportSet.clear();
        activatorSet.clear();

        try
        {
            JarFile file = new JarFile(new File(jarfile));

            // mainloop over all classes in the jar
            for (Enumeration en = file.entries(); en.hasMoreElements();)
            {
                JarEntry entry = (JarEntry) en.nextElement();
                if (entry.getName().endsWith(".class"))
                {
                    // get the classname
                    String clname = entry.getName().replaceAll("/", ".");
                    clname = clname.substring(0, clname.indexOf(".class"));

                    //                    System.out.println(clname +" index: "+
                    // clname.lastIndexOf("."));

                    // packagename
                    int indexpkg;
                    if ((indexpkg = clname.lastIndexOf(".")) > 0)
                    {
                        String pkg = clname.substring(0, indexpkg);
                        exportSet.add(pkg);
                    }

                    if (!thirdparty)
                    {
                        boolean impls = BundleInfo.bverifier.getDepsClasses().doesImplementActivatorBundle(clname);

                        if (impls)
                        {
                            activatorSet.add(clname);

                        }

                    }

                    Collection classes = BundleInfo.bverifier.getDepsClasses().getClassImports(clname);

                    if (classes == null)
                        continue;

                    for (Iterator it = classes.iterator(); it.hasNext();)
                    {
                        String refclass = (String) it.next();

                        // check if the class is not of a system class
                        // (cdc-minimum, foundation) and
                        // not in the same jar file
                        // tocheckimports.add(refclass);
                        // Make sure that the import is not in one of the Java
                        // libraries, and that it is in one of the dependent
                        // classes..
                        if (!BundleInfo.bverifier.getRtClasses().isRtClass(refclass)
                                && BundleInfo.bverifier.getDepsClasses().getClassImports(refclass) != null)
                        {
                            //System.out.println(refclass);
                            int index = refclass.lastIndexOf(".");
                            String imppkg = "";
                            if (index > -1)
                            {
                                imppkg = refclass.substring(0, refclass.lastIndexOf("."));

                                //                            System.out.println("import candidate: " +
                                // imppkg);

                                importSet.add(imppkg);
                            }
                        }

                    }
                }
            }

            file.close();

            // remove now from the importSet the ones which are included
            // in the bundle itself
            for (Iterator it = exportSet.iterator(); it.hasNext();)
            {
                importSet.remove(it.next());
            }

            // Generate extended OBR File
            generateOBR(new File(jarfile));
            
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    protected void generateOBR(File file) throws IOException
    {
        StringBuffer buf = new StringBuffer();
        String description = null;

        String md5;
        if (file == null)
        {
            md5 = MD5.getHashString(new File(bundledir + File.separatorChar + bname + "-" + bversion + ".jar"));
        } else
        {
            md5 = MD5.getHashString(file);
        }
        Vector dependencies = new Vector();

        if (thirdparty || file == null)
        {

            // defaults for thirdparty
            description = new String("thirdparty-jar " + bname);
            sourceUrl = new String("unknown");
            docUrl = new String("unknown");

        } else
        {
            
            // get short description
            description = project.getShortDescription();
            
            // get dependencies
            List deps = project.getDependencies();
            
            for( Iterator it = deps.iterator(); it.hasNext();)
            {
                Dependency dep = (Dependency)it.next();
                                
                if (dep.getProperty("obr.dynamic") == null)
                    dependencies.add(dep);
            }

//            BufferedReader breader = new BufferedReader(new FileReader(new File(bundledir + File.separatorChar + ".."
//                    + File.separatorChar + "project.xml")));
//
//            String line = breader.readLine();
            // parse project.xml
//            while (line != null)
//            {
//                if (line.indexOf("shortDescription") > -1)
//                {
//                    description = XMLHelpers.getTagContent(breader, line, "shortDescription");
//                }      
//                if (line.indexOf("dependency") > -1)
//                {
//                    String dependency = XMLHelpers.getTagContent(breader, line, "dependency");
//                    
//                    dependencies.add(XMLHelpers.getTagContent(dependency, "artifactId"));
//                    dependencies.add(XMLHelpers.getTagContent(dependency, "groupId"));
//
//                    String version = XMLHelpers.getTagContent(dependency, "version");
//                    if (version.equals("${pom.currentVersion}"))
//                    {
//                        version = bversion;
//                    }
//
//                    dependencies.add(version);
//                    
//
//                }
//                line = breader.readLine();
//            }
            
//            sourceUrl = new String("unknown");
//            docUrl = new String("unknown");
        }

        // set up OBR file
        buf.append("<bundle>\n");
        buf.append(XMLHelpers.emitTag("bundle-name", bname, 1));
        buf.append(XMLHelpers.emitTag("bundle-group", bgroup, 1));
        buf.append(XMLHelpers.emitTag("bundle-version", bversion, 1));

        // ok the uuid is repition, but to be compatible with knopflerfish
        buf.append(XMLHelpers.emitTag("bundle-uuid", bgroup + ":" + bname + ":" + bversion + ":", 1));

        buf.append(XMLHelpers.emitMultilineTag("bundle-description", description, 1));

        if (apivendor == null)
            buf.append(XMLHelpers.emitTag("bundle-apivendor", "", 1));
        else
            buf.append(XMLHelpers.emitTag("bundle-apivendor", apivendor, 1));

        buf.append(XMLHelpers.emitTag("bundle-vendor", vendor, 1));

        // Jan, do we still need the update-location element??
        buf.append(XMLHelpers.emitTag("update-location", "localhost://" + repolocal, 1));
        
        String bundlepath = bgroup + "/jars/" + bname + "-" + bversion + ".jar";
        if (updatelocation == null)
            buf.append(XMLHelpers.emitTag("bundle-updatelocation", 
                    "http://osgirepo.berlios.de/maven/repository/" + bundlepath, 1));
        else
            buf.append(XMLHelpers.emitTag("bundle-updatelocation", 
                    updatelocation +"/"+ bundlepath, 1));
        
        // sourceurl
        if (sourceUrl != null)
            buf.append(XMLHelpers.emitMultilineTag("bundle-sourceurl", sourceUrl, 1));
        else
            buf.append(XMLHelpers.emitMultilineTag("bundle-sourceurl", "", 1));
        
        // docurl
        if (docUrl != null)
            buf.append(XMLHelpers.emitMultilineTag("bundle-docurl", "http://"+docUrl, 1));
        else
            buf.append(XMLHelpers.emitMultilineTag("bundle-docurl", "", 1));
        
        // category
        if (category != null)
            buf.append(XMLHelpers.emitTag("bundle-category", category, 1));
        else
            buf.append(XMLHelpers.emitTag("bundle-category", "general", 1));
        
        buf.append(XMLHelpers.emitTag("bundle-checksum", md5, 1));

        for (Iterator iter = importSet.iterator(); iter.hasNext();)
        {
            buf.append("\t<import-package package=\"" + iter.next() + "\"/>\n");
        }
        
        for (Iterator iterexp = exportSet.iterator(); iterexp.hasNext();)
        {
            buf.append("\t<export-package package=\"" + iterexp.next() + "\"/>\n");
        }

        if (!dependencies.isEmpty())
        {
            for (Enumeration en = dependencies.elements(); en.hasMoreElements();)
            {
                Dependency dep = (Dependency)en.nextElement();
                
                String depname = dep.getArtifactId();
                String depgroup = dep.getGroupId();
                String depversion = dep.getVersion();
                buf.append("\t<dependency-uuid>");
                buf.append(depgroup + ":" + depname + ":" + depversion + ":");
                buf.append("</dependency-uuid>\n");
            }
        }

        //    	if (!dependencies.isEmpty()) {
        //    		buf.append("\t<dependencies>\n");
        //    		for (Enumeration en = dependencies.elements(); en.hasMoreElements();
        // ) {
        //    			buf.append("\t\t<bundle>\n");
        //    			buf.append(XMLHelpers.emitTag("bundle-name",
        // (String)en.nextElement(), 3));
        //				buf.append(XMLHelpers.emitTag("bundle-group",
        // (String)en.nextElement(), 3));
        //				buf.append(XMLHelpers.emitTag("bundle-version",
        // (String)en.nextElement(), 3));
        //    			buf.append("\t\t</bundle>\n");
        //    		}
        //    		buf.append("\t</dependencies>\n");
        //    	}
        buf.append("</bundle>\n");

        // Debug output:
        //    	System.out.println("OBR:");
        //    	System.out.println(buf.toString());

        File obrfile = new File(bundledir + File.separatorChar + bname + "-" + bversion + ".obr");
        BufferedWriter bwriter = new BufferedWriter(new FileWriter(obrfile));
        bwriter.write(buf.toString());
        bwriter.flush();

    }

    /**
     * Returns the imported packages as a String compatible to the OSGi
     * specification, (e.g. eventsystem-api;specification-version=0.6,...)
     * 
     * @return
     */
    public String getImportPackage()
    {

        String pkgs = BundleInfo.Set2String(importSet);

        return pkgs;
    }

    /**
     * Returns the exported packages as a String compatible to the OSGi
     * specification, (e.g. eventsystem-api;specification-version=0.6,...)
     * 
     * @return
     */
    public String getExportPackage()
    {
        //String pkgs = BundleInfo.Set2String(exportSet);

        if (exportSet.isEmpty())
            return "";

        StringBuffer sb = new StringBuffer();
        for (Iterator it = exportSet.iterator(); it.hasNext();)
        {

            sb.append((String) it.next());
            // if withpkgversion set, add the specification-version
            if (withpkgv)
                sb.append("; specification-version=" + bversion);

            if (it.hasNext())
                sb.append(",");
        }

        return sb.toString();
    }

    /**
     * Returns the path to the generated jar file, depending on the given
     * bundle, suffix and deployment place.
     * 
     * @return
     */
    public String getDeployOSGiJar()
    {
        return deployosgijar;
    }

    /**
     * Check if refclass is imported from an other package then the vm core
     * packages. Can also include packages from the same bundle but in another
     * package.
     * 
     * @param refclass
     */
    //    private boolean isImported(String refclass)
    //    {
    //        String pkg = refclass.substring(0, refclass.lastIndexOf("."));
    //
    //        
    //        
    //        if (SYSTEM_CP)
    //        {
    //            // check against the system.classpath
    //            // check against another rt.jar
    //
    //            if (!BundleVerifier.Instance().getRtClasses().isRtClass(refclass))
    //            {
    //                System.out.println("import package:" + pkg);
    //                importSet.add(pkg);
    //            } else
    //                System.out.println("class found: " + refclass);
    //
    //        } else
    //        {
    //            // check against the
    //            if (!EE.isMinimum(refclass) && !EE.isFoundation(refclass))
    //                    importSet.add(pkg);
    //        }
    //    }
    /**
     * @param deploy
     * @param suffix
     * @param atts -
     *            list of attributes, supported: export, import
     * @throws IOException
     * @throws JarException
     */
    public void createOSGiBundle(boolean deploy, String suffix, String atts) throws IOException, JarException
    {

        String toaddsuffix = "";
        if (suffix != null)
            toaddsuffix = "-" + suffix;

        String jarfile = bundledir + File.separatorChar + bname + "-" + bversion + ".jar";
        String osgijarfile = bname + "-" + bversion + toaddsuffix + ".jar";

        if (deploy)
            deployosgijar = repolocal + File.separatorChar + bgroup + File.separatorChar + "jars" + File.separatorChar
                    + osgijarfile;

        else
            deployosgijar = bundledir + File.separatorChar + osgijarfile;

        System.out.println("DeployOSGiJar: " + deployosgijar);
        System.out.println("atts: " + atts);

        // parse the bundle to generate the import/export, and activator set
        parseBundle(jarfile);

        System.out.println("jarfile parsed");

        Vector attv = new Vector();

        if (atts.matches(".*(export)+.*"))
        {
            String pkgs = getExportPackage();
            if (pkgs != null)
                attv.add("Export-Package: " + pkgs);
        }

        if (atts.matches(".*(import)+.*"))
        {
            String pkgs = getImportPackage();
            if (pkgs != null)
                attv.add("Import-Package: " + pkgs);
        }

        String[] attar = new String[attv.size()];
        attv.copyInto(attar);

        // Now that we have the import, export it has to be merged
        // with the already created Manifest file inside the jar
        // to be backward compatible.

        // unzip to a tmp dir
        String tmppath = bundledir + File.separatorChar + "_tmp_osgi";

        File tmpdir = new File(tmppath);
        tmpdir.mkdir();

        JarFactory jarfact = new JarFactory(tmppath);

        jarfact.unJar(new File(jarfile));

        jarfact.appendMainAttributes(attar);

        jarfact.jar(new File(deployosgijar));

        // delete tmp folder recursively
        JarFactory.deleteDirReq(tmpdir);

    }

}