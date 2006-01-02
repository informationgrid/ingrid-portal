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

import org.apache.jetspeed.i18n.KeyedMessage;

public class LayoutEventException extends LayoutException
{

    public LayoutEventException()
    {
        super();
    }

    public LayoutEventException(String message)
    {
        super(message);
    }

    public LayoutEventException(KeyedMessage typedMessage)
    {
        super(typedMessage);
    }

    public LayoutEventException(Throwable nested)
    {
        super(nested);
    }

    public LayoutEventException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

    public LayoutEventException(KeyedMessage keyedMessage, Throwable nested)
    {
        super(keyedMessage, nested);
    }

}
