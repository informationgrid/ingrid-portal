/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
 */
package org.apache.jetspeed.locator;

import java.io.File;

/**
 * Jetspeed default Template Descriptor implementation
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedTemplateDescriptor.java 188098 2004-12-22 04:42:57Z rwatler $
 */
public class JetspeedTemplateDescriptor extends JetspeedLocatorDescriptor implements TemplateDescriptor 
{
    String absolutePath;
    String appRelativePath;
    
    public JetspeedTemplateDescriptor()
    {
        super();
    }
    
    public JetspeedTemplateDescriptor(LocatorDescriptor locator)
    {
        this.setCountry(locator.getCountry());
        this.setLanguage(locator.getLanguage());
        this.setMediaType(locator.getMediaType());
        this.setName(locator.getName());
        this.setType(locator.getType());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.Template#getAbsolutePath()
     */
    public String getAbsolutePath()
    {
        return this.absolutePath;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.Template#setAbsolutePath(java.lang.String)
     */
    public void setAbsolutePath(String path)
    {
        this.absolutePath = (new File(path)).getAbsolutePath();
    }
    
    
    
    /**
     * @see Object#clone
     * @return an instance copy of this object
     */
    public Object clone() throws java.lang.CloneNotSupportedException
    {
        return super.clone();
    }
    
    /**
     * @return
     */
    public String getAppRelativePath()
    {
        return appRelativePath;
    }

    /**
     * @param string
     */
    public void setAppRelativePath(String string)
    {
        appRelativePath = string;
    }

}
