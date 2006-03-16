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
package de.ingrid.portal.portlets.browser;

import java.util.List;

import javax.portlet.RenderRequest;


/**
 * GemsBrowser - defines the inteface for Gems Browsers
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: Browser.java 188123 2004-12-31 23:59:27Z taylor $
 */
public interface Browser
{
    void getRows(RenderRequest request, String sql, int windowSize)
    throws Exception;
    
    boolean filter(List row, RenderRequest request);    
    
    public void populate(int rowIndex, int columnIndex, List row);    
    
}