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
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author andfrei
 *  
 */
public class RtClasses {

    private boolean SYSTEM_CP = true;
    
    JarFile jarfile;
    
    public RtClasses() throws IOException {
        
        if (SYSTEM_CP)
        {
		        // setup rtpool
		        //  get the rt.jar over java.home
		        String rtfile = System.getProperty("java.home") + File.separatorChar
		                + "lib" + File.separatorChar + "rt.jar";
		        
		        jarfile = new JarFile(rtfile);
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
    public boolean isRtClass(String clazz)
    {

        if (SYSTEM_CP)
        {
            String jarname = clazz.replace('.','/') + ".class";
        
		  			ZipEntry zipentry = jarfile.getEntry(jarname);
		  			
		  			if (zipentry != null)
		  			    return true;
        } else
        {
            // check against the
            if (EE.isMinimum(clazz) || EE.isFoundation(clazz))
                return true;
        }

        return false;
    }
}