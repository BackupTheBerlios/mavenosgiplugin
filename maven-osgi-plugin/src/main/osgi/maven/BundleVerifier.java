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

import java.io.IOException;

/**
 * The BundleVerifier collects the dependencies of the bundle and
 * the runtime system classpath.
 * 
 * @todo BundleVerifier should take over the parsing rules which are currently in the Bundle.
 * 
 * @author andfrei
 *  
 */
public class BundleVerifier
{

    private DepsClasses           depsclasses;

    private RtClasses             rtclasses;

    /**
     * BundleVerifier as a singleton, use Instance to get an instance.
     *
     */
    public BundleVerifier(String rtclpath)
    {

        depsclasses = new DepsClasses();

        try
        {
            rtclasses = new RtClasses(rtclpath);
        } catch (IOException ioe)
        {
            //TODO should inform the creater that no runtime available
        }
    }


    /**
     * Returns the Runtime environment libs reference.
     *  
     * @return
     */
    public RtClasses getRtClasses()
    {
        return rtclasses;
    }

    /**
     * Returns a reference to the Dependency classes of the bundle.
     * 
     * @return
     */
    public DepsClasses getDepsClasses()
    {
        return depsclasses;
    }
}