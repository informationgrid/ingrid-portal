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


import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.i18n.KeyedMessage;

/**
 * Base exception for all layout exceptions.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class LayoutException extends JetspeedException
{

    public LayoutException()
    {
        super();
    }

    public LayoutException(String message)
    {
        super(message);
    }

    public LayoutException(KeyedMessage typedMessage)
    {
        super(typedMessage);       
    }

    public LayoutException(Throwable nested)
    {
        super(nested);
    }

    public LayoutException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

    public LayoutException(KeyedMessage keyedMessage, Throwable nested)
    {
        super(keyedMessage, nested);
    }

}
