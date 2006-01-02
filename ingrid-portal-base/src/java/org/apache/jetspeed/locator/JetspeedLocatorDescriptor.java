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

/**
 * Jetspeed default Locator Descriptor implementation
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedLocatorDescriptor.java 186583 2004-05-20 16:06:38Z weaver $
 */
public class JetspeedLocatorDescriptor implements LocatorDescriptor
{
    public JetspeedLocatorDescriptor()
    {
    }
            
    private String type;
    private String name;
    private String mediaType;
    private String language;
    private String country;   
    private static final String DELIM = "/";
        

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {   
        StringBuffer value = new StringBuffer();

        // type
        if (type != null)
        {
            value.append(LocatorDescriptor.PARAM_TYPE).append(DELIM);            
            value.append(type).append(DELIM);
        }
        
        // media type
        if (mediaType != null)
        {
            value.append(LocatorDescriptor.PARAM_MEDIA_TYPE).append(DELIM);
            value.append(mediaType).append(DELIM);
        }

        // language
        if (language != null)
        {
            value.append(LocatorDescriptor.PARAM_LANGUAGE).append(DELIM);
            value.append(language).append(DELIM);
        }
        
        // country
        if (country != null)
        {
            value.append(LocatorDescriptor.PARAM_COUNTRY).append(DELIM);
            value.append(country).append(DELIM);
        }
        
        // template name
        if (name != null)
        {
            value.append(LocatorDescriptor.PARAM_NAME).append(DELIM);                    
            value.append(name).append(DELIM);
        }
        
        value.deleteCharAt(value.length()-1);
        return value.toString();
         
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#toPath()
     */
    public String toPath()
    {
        StringBuffer value = new StringBuffer("/");

        // type
        if (type != null)
        {
            value.append(type).append(DELIM);
        }
        
        // media type
        if (mediaType != null)
        {
            value.append(mediaType).append(DELIM);
        }

        // language
        if (language != null)
        {
            value.append(language).append(DELIM);
        }
        
        // country
        if (country != null)
        {
            value.append(country).append(DELIM);
        }
        
        // template name
        if (name != null)
        {
            value.append(name).append(DELIM);
        }
        
        value.deleteCharAt(value.length()-1);
        return value.toString();
        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#getType()
     */
    public String getType()
    {
        return type;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#setType(java.lang.String)
     */
    public void setType(String type)
    {
        this.type = type;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#getName()
     */
    public String getName()
    {
        return name;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#getMediaType()
     */
    public String getMediaType()
    {
        return mediaType;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#setMediaType(java.lang.String)
     */
    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#getLanguage()
     */
    public String getLanguage()
    {
        return language;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#setLanguage(java.lang.String)
     */
    public void setLanguage(String language)
    {
        this.language = language;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#getCountry()
     */
    public String getCountry()
    {
        return country;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#setCountry(java.lang.String)
     */
    public void setCountry(String country)
    {
        this.country = country;
    }
    
    /**
     * @see Object#clone
     * @return an instance copy of this object
     */
    public Object clone() throws java.lang.CloneNotSupportedException
    {
        return super.clone();
    }
    
}
