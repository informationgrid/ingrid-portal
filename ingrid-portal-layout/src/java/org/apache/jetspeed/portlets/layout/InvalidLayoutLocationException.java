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
 * Indicates an attempt to access a local within a layout that is outside
 * of the bounds of that layout.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class InvalidLayoutLocationException extends LayoutException
{
    public InvalidLayoutLocationException(LayoutCoordinate coordinate)
    {
        super("Invalid layout coordinate "+coordinate.toString());
    }    
    
    public InvalidLayoutLocationException(int column)
    {
        super("Column number "+column+" is not a valid column for this layout.");
    }
}
