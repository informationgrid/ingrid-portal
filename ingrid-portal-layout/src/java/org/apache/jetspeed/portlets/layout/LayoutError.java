/*
 * **************************************************-
 * Ingrid Portal Layout
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
 * Should only be thrown when something truely unexpected happens
 * when processing a layout.  Basically used in the case where something
 * that "should never happen" happens.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class LayoutError extends Error
{
    private static final String BUG_MESSAGE = "Congratulations!!! You have found a bug! Please log this issue at http://issues.apache.org/jira.";

    public LayoutError()
    {
        super(BUG_MESSAGE);
    }

    public LayoutError(String message)
    {
        super(BUG_MESSAGE+"\n"+message);
    }

    public LayoutError(Throwable cause)
    {
        super(cause);
    }

    public LayoutError(String message, Throwable cause)
    {
        super(BUG_MESSAGE+"\n"+message, cause);
    }

}
