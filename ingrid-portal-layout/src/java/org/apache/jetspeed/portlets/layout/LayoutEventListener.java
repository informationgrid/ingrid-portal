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

/**
 * 
 * Interface to be implemented by classes that want to handle 
 * LayoutEvents
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @see LayoutEvent
 * @see org.apache.jetspeed.portlets.layout.ColumnLayout
 */
public interface LayoutEventListener
{
    /**
     * Invoked anytime a LayoutEvent is dispatched.
     * 
     * @param event LayoutEvent that has been dispatched.
     * @throws LayoutEventException if an error occurs will processing the event.
     */
    void handleEvent(LayoutEvent event) throws LayoutEventException;
}
