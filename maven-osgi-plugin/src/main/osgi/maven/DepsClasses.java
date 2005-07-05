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

import java.util.Collection;

import org.apache.maven.repository.Artifact;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;


/**
 * @author andfrei
 * 
 */
public class DepsClasses {

    private ClassPool cpool = ClassPool.getDefault();
    
    public DepsClasses()
    {
        
    }
    
    public void addImportJar(String path) throws NotFoundException
    {
        cpool.appendClassPath(path);
    }
    
    /**
     * @todo append only the packages specified in the export of the manifest
     * 
     */
    public void addImportArtifact(String dir, Artifact artifact)
    {
        String path = dir + artifact.generatePath();
        
        // append the packages specified in the manifest
        
        
        // append the whole jar file
        try {
            cpool.appendClassPath(path);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public boolean doesImplementActivatorBundle(String clname)
    {
        // parse class with javassist
        CtClass clas;
        try
        {
            final Class bundleActivatorClass = Class.forName("org.osgi.framework.BundleActivator");
            clas = cpool.get(clname);
	        CtClass[] interfaces = clas.getInterfaces();
	        for (int i = 0; i < interfaces.length; i++) {
	            // deals with classes which inherit from BundleActivator
	            if (bundleActivatorClass.isAssignableFrom(Class.forName(interfaces[i].getName())))
	                    return true;;
	        }
        } catch (Exception e)
        {
            return false;
        }
        
        return false;
            
    }
    
    public Collection getClassImports(String clname)
    {
        try
        {
            CtClass clas = cpool.get(clname);
            return clas.getRefClasses();
            
        } catch (NotFoundException e)
        {
            return null;
        }


    }
}
