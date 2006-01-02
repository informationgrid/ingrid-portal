/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.layout;

import org.apache.jetspeed.om.page.Fragment;

/**
 * A LayoutEvent is used by ColumnLayout to notify its LayoutAeventListeners
 * that there have been a change in the position of a fragment within the layout.
 * <h3>Constant Values</h3>
 * <ul>
 *   <li>ADDED == 0</li>
 *   <li>MOVED_UP == 1</li>
 *   <li>MOVED_DOWN == 2</li>
 *   <li>MOVED_LEFT == 3</li>
 *   <li>MOVED_RIGHT == 4</li>
 * </ul>
 * 
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @see org.apache.jetspeed.om.page.Fragment
 * @see org.apache.jetspeed.portlets.layout.LayoutEventListener
 * @see org.apache.jetspeed.portlets.layout.ColumnLayout
 *
 */
public class LayoutEvent
{   
    /**Event type value that notifies that a fragment has been added */
    public static final int ADDED =0;
    /**Event type value that notifies that a fragment has been moved up */
    public static final int MOVED_UP = 1;
    /**Event type value that notifies that a fragment has been moved down */
    public static final int MOVED_DOWN = 2;
    /**Event type value that notifies that a fragment has been moved left */
    public static final int MOVED_LEFT = 3;
    /**Event type value that notifies that a fragment has been moved right */
    public static final int MOVED_RIGHT = 4;
    
    private final int eventType;
    private final Fragment fragment;
    private final LayoutCoordinate originalCoordinate;
    private final LayoutCoordinate newCoordinate;    
   
    /**
     * 
     * @param eventType The type of event (see the event constants)
     * @param fragment Fragment that is the target of this event.
     * @param originalCoordinate the previous LayoutCoordinate of this Fragment
     * @param newCoordinate the new and current coordinates of this fragment.
     * @see org.apache.jetspeed.om.page.Fragment
     */
    public LayoutEvent(int eventType, Fragment fragment, LayoutCoordinate originalCoordinate, LayoutCoordinate newCoordinate)
    {
        super();       
        this.eventType = eventType;
        this.fragment = fragment;
        this.originalCoordinate = originalCoordinate;
        this.newCoordinate = newCoordinate;
    }
   
   
    /** 
     * Returns the event type (see event constants)
     * @return the event type (see event constants)
     * @see ColumnLayout#layoutType
     */    
    public int getEventType()
    {
        return eventType;
    }
    
    /**
     * Returns the fragment that is the target of this event.
     * @return Fragment the fragment that is the target of this event.
     * @see org.apache.jetspeed.om.page.Fragment
     */
    public Fragment getFragment()
    {
        return fragment;
    }
    
    /**
     * Returns the new/current coordinate of the Fragment targeted by this event.
     * @return the new/current coordinate of the Fragment targeted by this event.
     * @see LayoutCoordinate
     */
    public LayoutCoordinate getNewCoordinate()
    {
        return newCoordinate;
    }
    
    /**
     * Returns the original (prior to the event) coordinate of the Fragment targeted by this event.
     * @return the original (prior to the event) coordinate of the Fragment targeted by this event.
     * @see LayoutCoordinate
     */
    public LayoutCoordinate getOriginalCoordinate()
    {
        return originalCoordinate;
    }


    public boolean equals(Object obj)
    {
        if(obj instanceof LayoutEvent)
        {
            LayoutEvent event = (LayoutEvent) obj;
            return event.fragment.equals(fragment) 
              && event.eventType == eventType
              && event.originalCoordinate.equals(originalCoordinate)
              && event.newCoordinate.equals(newCoordinate);
            
        }
        else
        {
            return false;
        }
    }


    public String toString()
    {        
        return "event_target="+fragment.getId()+",event_type_code="+ eventType + ",orginial_coordinate="+ originalCoordinate+
               ",new_coordinate="+newCoordinate;
    }
    

}
