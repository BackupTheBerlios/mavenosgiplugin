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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarException;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.CRC32;

import org.apache.tools.ant.DirectoryScanner;

/**
 * Provides utilities to extract or package JAR files.
 * <p>
 * Methods are provided to either extract or package JAR files using the JDK1.2
 * java.util.jar package. Noted that JAR files created with the <code>jar</code>
 * method are smaller than their counterpart created by using the
 * <code>jar</code> command provided by Java. This is probably because the
 * <code>jar</code> method here does not include any options.
 * <p>
 * Code snips from: Java Cookbook, pp 268-270 and The Java Class Libraries, pp
 * 487-488
 * 
 * @author Joy M. Agustin; JarFactory from csdl-jblanket
 * @version $Id: JarFactory.java,v 1.1 2004/11/09 08:59:13 afrei Exp $
 */
public class JarFactory
{

    /** Buffer for reading/writing the JarFile data */
    private byte[] buffer;

    /** Directory to that does/will contain the class files */
    private String dir;

    /** Directory separator */
    private static final String SLASH = File.separator;

    /** Name of directory holding manifest file */
    private static final String MAN_DIR = "META-INF";

    /** Relative path to the manifest file starting at MAN_DIR */
    private static final String MAN_FILE_PATH = MAN_DIR + SLASH + "MANIFEST.MF";

    /**
     * Constructs an JarFactory object.
     * 
     * @param dir
     *            the directory that contains/will contain the class files.
     */
    public JarFactory(String dir) {

        this.dir = dir;
        this.buffer = new byte[8092];
    }

    /**
     * Append import-, export-package, and bundle-category to the bundles
     * jar-manifest.
     *  
     */
    public void appendMainAttributes(String[] lines)
    {
        try {

            File manfile = new File(dir + SLASH + MAN_FILE_PATH);

            // read file until first blank line, where the main attributes are
            // inserted
            FileReader fr = new FileReader(manfile);
            BufferedReader br = new BufferedReader(fr);
            String s = null;
            StringBuffer sb = new StringBuffer();
            while ((s = br.readLine()) != null && s.length() != 0) {
                sb.append(s + "\n");
            }

            // insert the main-attributes
            for (int i = 0; i < lines.length; i++)
                sb.append(lines[i] + "\n");

            if (s != null) {
                sb.append("\n");
                while ((s = br.readLine()) != null) {
                    sb.append(s + "\n");
                }
            }

            br.close();
            fr.close();

            // write the manifest back
            FileWriter fw = new FileWriter(manfile);
            fw.write(sb.toString());
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Extracts all files in a JAR file.
     * 
     * @param jarFile
     *            the JAR file to unjar.
     * @throws IOException
     *             if cannot unjar <code>fileName</code>.
     * @throws JarException
     *             if error extracting files from JAR file.
     */
    public void unJar(File jarFile) throws IOException, JarException
    {

        if (!jarFile.getName().endsWith(".jar")) { throw new JarException(
                "Not a zip file? " + jarFile.getName()); }

        // process all entries in that JAR file
        JarFile jar = new JarFile(jarFile);
        Enumeration all = jar.entries();
        while (all.hasMoreElements()) {
            getEntry(jar, ((JarEntry) (all.nextElement())));
        }

        jar.close();
    }

    /**
     * Gets one file <code>entry</code> from <code>jarFile</code>.
     * 
     * @param jarFile
     *            the JAR file reference to retrieve <code>entry</code> from.
     * @param entry
     *            the file from the JAR to extract.
     * @throws IOException
     *             if error trying to read entry.
     */
    private void getEntry(JarFile jarFile, JarEntry entry) throws IOException
    {

        String entryName = entry.getName();
        // if a directory, mkdir it (remember to create intervening
        // subdirectories if needed!)
        if (entryName.endsWith("/")) {
            new File(dir, entryName).mkdirs();
            return;
        }

        File f = new File(dir, entryName);

        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        // Must be a file; create output stream to the file
        FileOutputStream fostream = new FileOutputStream(f);
        InputStream istream = jarFile.getInputStream(entry);

        // extract files
        int n = 0;
        while ((n = istream.read(buffer)) > 0) {
            fostream.write(buffer, 0, n);
        }

        try {
            istream.close();
            fostream.close();
        } catch (IOException e) {
            // do nothing -- following "Java Examples in a Nutshell" from
            // O'Reilly
        }
    }

    /**
     * Creates a JAR file. If the JAR file already exists, it will be
     * overwritten.
     * 
     * @param jarFile
     *            the JAR file to create.
     * @throws IOException
     *             if cannot create JAR file.
     * @throws JarException
     *             if error putting files into JAR file.
     */
    public void jar(File jarFile) throws IOException, JarException
    {

        if (!jarFile.getName().endsWith(".jar")) { throw new JarException(
                "Not a zip file? " + jarFile.getName()); }

        // get all files to include in JAR file, except for manifest
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[] { "**"});
        //    scanner.setExcludes(new String[] {"**" + SLASH + MAN_DIR + SLASH +
        // "**"});
        scanner.setBasedir(new File(this.dir));
        scanner.setCaseSensitive(true);
        scanner.scan();
        String[] directories = scanner.getIncludedDirectories();
        String[] files = scanner.getIncludedFiles();

        // create JAR file
        FileOutputStream fostream = new FileOutputStream(jarFile);
        JarOutputStream jostream = new JarOutputStream(fostream);

        // create manifest directory and manifest
        //    if ((new File(dir, MAN_DIR)).exists()) {
        //      putManifest(jostream);
        //    }

        //create subdirectories
        String currentDirectory = "";
        for (int i = 0; i < directories.length; i++) {
            // do not need to put the current directory in JAR file
            if (directories[i].equals(currentDirectory)) {
                continue;
            }
            putEntry(directories[i] + SLASH, jostream);
        }

        // create files
        for (int i = 0; i < files.length; i++) {
            putEntry(files[i], jostream);
        }

        jostream.finish();

        try {
            jostream.close();
            fostream.close();
        } catch (IOException e) {
            // do nothing -- following "Java Examples in a Nutshell" from
            // O'Reilly
        }
    }

    /**
     * Puts manifest file into a JAR file.
     * 
     * @param jostream
     *            the JAR file to put manifest into.
     * @throws IOException
     *             if error trying to write entry.
     */
    private void putManifest(JarOutputStream jostream) throws IOException
    {

        // begin borrowed code from Ant Jar and Zip classes
        // put the META-INF directory into JAR file.
        String manDirPath = MAN_DIR + SLASH;
        JarEntry manifestDirEntry = new JarEntry(manDirPath.replace(
                File.separatorChar, '/'));
        manifestDirEntry.setTime(System.currentTimeMillis());
        manifestDirEntry.setSize(0);
        manifestDirEntry.setMethod(JarEntry.STORED);

        // This is faintly ridiculous - empty CRC value
        manifestDirEntry.setCrc((new CRC32()).getValue());
        jostream.putNextEntry(manifestDirEntry);

        // now put the manifest file into JAR file.
        JarEntry manifestFileEntry = new JarEntry(MAN_FILE_PATH.replace(
                File.separatorChar, '/'));
        manifestFileEntry.setTime(System.currentTimeMillis());
        FileInputStream fistream = new FileInputStream(new File(this.dir,
                MAN_FILE_PATH));
        jostream.putNextEntry(manifestFileEntry);

        int n = 0;
        while ((n = fistream.read(buffer)) >= 0) {
            jostream.write(buffer, 0, n);
        }
        jostream.closeEntry();

        // end borrowed code from Ant Jar and Zip classes
        try {
            fistream.close();
        } catch (IOException e) {
            // do nothing -- following "Java Examples in a Nutshell" from
            // O'Reilly
        }
    }

    /**
     * Puts a file into a JAR file.
     * 
     * @param fileName
     *            the file to put in the JAR.
     * @param jostream
     *            the JAR file to put <code>fileName</code> into.
     * @throws IOException
     *             if error trying to write entry.
     */
    private void putEntry(String fileName, JarOutputStream jostream)
            throws IOException
    {

        // prepare fileName for entry into JAR file
        String entryName = fileName.replace(File.separatorChar, '/');

        // put directory (remember to create intervening subdirectories if
        // needed!)
        if (entryName.endsWith("/")) {
            jostream.putNextEntry(new JarEntry(entryName));
            jostream.closeEntry();
            return;
        }

        // put file
        FileInputStream fistream = new FileInputStream(new File(this.dir,
                fileName));
        jostream.putNextEntry(new JarEntry(entryName));
        int n;

        // now read and write the JAR entry data.
        while ((n = fistream.read(buffer)) >= 0) {
            jostream.write(buffer, 0, n);
        }

        jostream.closeEntry();

        try {
            fistream.close();
        } catch (IOException e) {
            // do nothing -- following "Java Examples in a Nutshell" from
            // O'Reilly
        }
    }

    // delete dir recursively
    public static void deleteDirReq(File path)
    {
        File[] files = path.listFiles();

        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) deleteDirReq(files[i]);

            files[i].delete();
        }
        path.delete();
    }

}