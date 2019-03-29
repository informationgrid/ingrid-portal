/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.mdek.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;


public class MarkdownContextHelpUtilsTest {

    @Test
    public void testBuildAvailableMarkdownHelp() throws Exception {
        MarkdownContextHelpUtils mchu = new MarkdownContextHelpUtils("context_help_test/");
        
        Map<MarkdownContextHelpItem, String> m = mchu.buildAvailableMarkdownHelp();
        
        Assert.assertEquals( false, m.isEmpty() );

        Optional<Entry<MarkdownContextHelpItem, String>> findAny = m.entrySet().stream().findAny();
        MarkdownContextHelpItem item = findAny.get().getKey();
        Assert.assertEquals( "3000", item.getGuid() );
        Assert.assertEquals( "1", item.getOid() );
        Assert.assertEquals( "Objektname", item.getTitle() );
        
        Assert.assertEquals( true, findAny.get().getValue().length() > 0 );
        
    }
}
