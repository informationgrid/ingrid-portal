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

import java.io.Serializable;

/**
 * 
 * Simple class that holds an x,y (column,row) coordinate.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public final class LayoutCoordinate implements Comparable, Serializable
{
    private final int x;
    private final int y;
    
    public LayoutCoordinate(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return the x axis (column) value of this coordinate.
     */
    public int getX()
    {
        return x;
    }
    
    /**
     * @return the y axis (row) value of this coordinate.
     */
    public int getY()
    {
        return y;
    }
    
    /**
     * Two LayoutCoordinates are equal if thier respective x and y values are equal.
     */
    public boolean equals(Object obj)
    {
        if(obj instanceof LayoutCoordinate)
        {
            LayoutCoordinate coordinate = (LayoutCoordinate) obj;
            return x == coordinate.x && y == coordinate.y;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {        
        return toString().hashCode();
    }

    public String toString()
    {      
        return x+","+y;
    }

    public int compareTo(Object obj)
    {
        LayoutCoordinate coordinate = (LayoutCoordinate) obj;
        if(!coordinate.equals(this))
        {
           if(y == coordinate.y)
           {
               return x > coordinate.x ? 1 : -1;
           }
           else
           {
               return y > coordinate.y ? 1 : -1;
           }
           
        }
        else
        {
            return 0;
        }
    }
    

}
