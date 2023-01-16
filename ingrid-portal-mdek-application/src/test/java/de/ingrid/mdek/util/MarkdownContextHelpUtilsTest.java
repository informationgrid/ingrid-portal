/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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

import java.nio.file.Paths;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


public class MarkdownContextHelpUtilsTest {

    @Test
    public void testBuildAvailableMarkdownHelp() {
        MarkdownContextHelpUtils mchu = new MarkdownContextHelpUtils("context_help_test");
        
        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> m = mchu.getAvailableMarkdownHelpFiles();

        Assert.assertFalse(m.isEmpty());
        // invalid mark down files must be ignored
        Assert.assertEquals( 1, m.size() );

        MarkdownContextHelpItemKey key = new MarkdownContextHelpItemKey("3000");
        key.setOid( "1" );
        key.setLang( "de" );

        Assert.assertNotNull(m.get(key));
        
        MarkdownContextHelpItem val = m.get( key );
        
        Assert.assertEquals( "Objektname", val.getTitle() );
        Assert.assertTrue(val.getMarkDownFilename().toString().endsWith(Paths.get("context_help_test", "object_name.md").toString()));
    }

    @Test
    public void testBuildAvailableLocalizedMarkdownHelp() {
        MarkdownContextHelpUtils mchu = new MarkdownContextHelpUtils("context_help_test_loc");
        
        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> m = mchu.getAvailableMarkdownHelpFiles();

        Assert.assertFalse(m.isEmpty());
        
        MarkdownContextHelpItemKey key = new MarkdownContextHelpItemKey("3000");
        key.setOid( "1" );
        key.setLang( "de" );

        Assert.assertNotNull(m.get(key));
        
        MarkdownContextHelpItem val = m.get( key );
        
        Assert.assertEquals( "Objektname", val.getTitle() );
        Assert.assertTrue(val.getMarkDownFilename().toString().endsWith(Paths.get("context_help_test_loc", "object_name.md").toString()));
        
        key.setLang( "en" );

        Assert.assertNotNull(m.get(key));
        
        val = m.get( key );
        
        Assert.assertEquals( "Objektname (en)", val.getTitle() );
        Assert.assertTrue(val.getMarkDownFilename().toString().endsWith(Paths.get("context_help_test_loc", "en", "object_name.md").toString()));

        key.setGuid( "3001" );
        key.setOid( "2" );

        Assert.assertNotNull(m.get(key));
        
        val = m.get( key );
        
        Assert.assertEquals( "Objektname2 (en)", val.getTitle() );
        Assert.assertTrue(val.getMarkDownFilename().toString().endsWith(Paths.get("context_help_test_loc", "en", "object_name2.md").toString()));
    
    }
    

    @Test
    public void testBuildAvailableProfiledMarkdownHelp() {
        MarkdownContextHelpUtils mchu = new MarkdownContextHelpUtils("context_help_test_profile");
        
        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> m = mchu.getAvailableMarkdownHelpFiles();

        Assert.assertFalse(m.isEmpty());
        
        MarkdownContextHelpItemKey key = new MarkdownContextHelpItemKey("3000");
        key.setOid( "1" );
        key.setLang( "de" );

        Assert.assertNotNull(m.get(key));
        
        MarkdownContextHelpItem val = m.get( key );
        
        Assert.assertEquals( "Objektname", val.getTitle() );
        Assert.assertTrue(val.getMarkDownFilename().toString().endsWith(Paths.get("context_help_test_profile", "object_name.md").toString()));
        
        key.setLang( "en" );

        Assert.assertNotNull(m.get(key));
        
        val = m.get( key );
        
        Assert.assertEquals( "Objektname (en)", val.getTitle() );
        Assert.assertTrue(val.getMarkDownFilename().toString().endsWith(Paths.get("context_help_test_profile", "en", "object_name.md").toString()));
        

        key.setLang( "de" );
        key.setProfile( "myprofile" );

        Assert.assertNotNull(m.get(key));
        
        val = m.get( key );
        
        Assert.assertEquals( "Objektname (myprofile)", val.getTitle() );
        Assert.assertTrue(val.getMarkDownFilename().toString().endsWith(Paths.get("context_help_test_profile", MarkdownContextHelpUtils.PROFILE_DIR, "myprofile", "object_name.md").toString()));
    
        key.setLang( "en" );
        key.setProfile( "myprofile" );

        Assert.assertNotNull(m.get(key));
        
        val = m.get( key );
        
        Assert.assertEquals( "Objektname (en) (myprofile)", val.getTitle() );
        Assert.assertTrue(val.getMarkDownFilename().toString().endsWith(Paths.get("context_help_test_profile", MarkdownContextHelpUtils.PROFILE_DIR, "myprofile", "en", "object_name.md").toString()));
    }
    
    
    
    
    @Test
    public void testRenderMarkdownFile() {
        MarkdownContextHelpUtils mchu = new MarkdownContextHelpUtils("context_help_test");
        
        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> m = mchu.getAvailableMarkdownHelpFiles();

        Assert.assertFalse(m.isEmpty());

        MarkdownContextHelpItemKey mchik = new MarkdownContextHelpItemKey("3000");
        mchik.setOid( "1" );
        mchik.setLang( "de" );
        
        MarkdownContextHelpItem val = m.get( mchik );
        
        String html = mchu.renderMarkdownFile( val.getMarkDownFilename() );
        Assert.assertTrue(html.contains("h1"));
        Assert.assertTrue(html.contains("table"));
    }
    
}
