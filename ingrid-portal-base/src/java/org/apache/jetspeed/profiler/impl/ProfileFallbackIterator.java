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
package org.apache.jetspeed.profiler.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.profiler.ProfileLocatorProperty;
import org.apache.jetspeed.profiler.rules.RuleCriterion;

/**
 * ProfileFallbackIterator
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ProfileFallbackIterator.java 216120 2005-07-13 06:40:27Z rwatler $
 */
public class ProfileFallbackIterator implements Iterator
{
    private ProfileLocatorControl locator;
    private int last = 0;
    private int state = RuleCriterion.FALLBACK_CONTINUE;     
    private ProfileFallbackIterator()
    {
    }
    
    public ProfileFallbackIterator(ProfileLocatorControl locator)
    {
        this.locator = locator;
        last = locator.getElements().size() - 1;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        // TODO Auto-generated method stub
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        boolean hasNext = false;
        
        List elements = locator.getElements();
        
        if (last < 0 || last >= elements.size())
        {
            state = RuleCriterion.FALLBACK_STOP;
            return false;
        }
        
        if (state == RuleCriterion.FALLBACK_STOP)
        {
            hasNext = false;
        }        
        else if (state == RuleCriterion.FALLBACK_CONTINUE ||
                 state == RuleCriterion.FALLBACK_LOOP)
        {
            hasNext = true;
        }
        
        return hasNext;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next()
    {
        ProfileLocatorProperty [] properties = null;

        if (last >= 0)
        {
            // generate properties list to return
            List elements = locator.getElements();
            properties = new ProfileLocatorProperty[last+1];
            ProfileLocatorProperty lastElement = null;
            Iterator it = elements.listIterator();
            for (int count = 0; (count <= last) && it.hasNext(); count++)
            {
                lastElement = (ProfileLocatorProperty)it.next();
                properties[count] = lastElement;
            }

            // modify iterator state based on fallback type;
            // performed here to prevent multiple calls to
            // hasNext() from changing iterator state
            state = lastElement.getFallbackType();
            last--;
        }

        return properties;
    }
}
