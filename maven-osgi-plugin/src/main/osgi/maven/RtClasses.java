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
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author andfrei
 *  
 */
public class RtClasses {

    private boolean SYSTEM_CP = true;

    private Vector systemJars = new Vector();

    public RtClasses() throws IOException {

        if (SYSTEM_CP) {

            ArrayList syslibpaths = new ArrayList();
            
            // check if using a MacOSX VM to get runtime jars
            if (System.getProperty("os.name").matches("(?i).*mac.*"))
            {
                syslibpaths.add(System.getProperty("java.home") + 
                        File.separatorChar+"lib");
                syslibpaths.add(System.getProperty("java.home") + 
                        File.separatorChar+".."+ File.separatorChar+"/Classes");
            }
            // else use the usual way for runtime jars
            else
            {
                syslibpaths.add(System.getProperty("java.home") + File.separatorChar + "lib");
            }
            
            // go through the list of systempaths
            for (Iterator it = syslibpaths.iterator(); it.hasNext(); )
            {
                String syslibpath = (String)it.next();
                
	            File systemLibDir = new File(syslibpath);
	            if (systemLibDir.exists()) 
	            {
	
	                File[] jarFiles = systemLibDir.listFiles(new FileFilter() {
	
	                    public boolean accept(File pathname) {
	
	                        return pathname.getName()
	                                .toLowerCase()
	                                .endsWith(".jar")
	                                && pathname.isFile();
	
	                    }
	
	                });
	
	                for (int i = 0; i < jarFiles.length; i++) 
	                {	                    
	                    systemJars.add(new JarFile(jarFiles[i]));
	                }     
	            }
            }
        }

    }

    /**
     * Check if class is from the runtime environment classpath.
     * 
     * @todo Extend the Runtime checking classpath for other vm's like the CDC.
     * 
     * @param clazz
     * @return
     */
    public boolean isRtClass(String clazz) {

        if (SYSTEM_CP) {
            
            String jarname = clazz.replace('.', '/') + ".class";
            for (int i = 0, s = systemJars.size(); i < s; i++) {
                
                JarFile jarFile = (JarFile) systemJars.get(i);
                ZipEntry zipentry = jarFile.getEntry(jarname);
                if (zipentry != null)
                    return true;
                
            }

        } else {
            // check against the
            if (EE.isMinimum(clazz) || EE.isFoundation(clazz))
                return true;
        }

        return false;
    }
}