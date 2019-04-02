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

import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;


public class MarkdownContextHelpUtilsTest {

    @Test
    public void testBuildAvailableMarkdownHelp() throws Exception {
        MarkdownContextHelpUtils mchu = new MarkdownContextHelpUtils("context_help_test");
        
        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> m = mchu.getAvailableMarkdownHelpFiles();
        
        Assert.assertEquals( false, m.isEmpty() );
        // invalid mark down files must be ignored
        Assert.assertEquals( 1, m.size() );

        MarkdownContextHelpItemKey key = new MarkdownContextHelpItemKey("3000");
        key.setOid( "1" );
        key.setLang( "de" );

        Assert.assertEquals( true, m.get( key ) != null );
        
        MarkdownContextHelpItem val = m.get( key );
        
        Assert.assertEquals( "Objektname", val.getTitle() );
        Assert.assertEquals( true, val.getMarkDownFilename().toString().endsWith( Paths.get( "context_help_test","object_name.md" ).toString() ));
    }

    @Test
    public void testBuildAvailableLocalizedMarkdownHelp() throws Exception {
        MarkdownContextHelpUtils mchu = new MarkdownContextHelpUtils("context_help_test_loc");
        
        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> m = mchu.getAvailableMarkdownHelpFiles();
        
        Assert.assertEquals( false, m.isEmpty() );
        
        MarkdownContextHelpItemKey key = new MarkdownContextHelpItemKey("3000");
        key.setOid( "1" );
        key.setLang( "de" );
        
        Assert.assertEquals( true, m.get( key ) != null );
        
        MarkdownContextHelpItem val = m.get( key );
        
        Assert.assertEquals( "Objektname", val.getTitle() );
        Assert.assertEquals( true, val.getMarkDownFilename().toString().endsWith( Paths.get( "context_help_test_loc","object_name.md" ).toString() ));
        
        key.setLang( "en" );
        
        Assert.assertEquals( true, m.get( key ) != null );
        
        val = m.get( key );
        
        Assert.assertEquals( "Objektname (en)", val.getTitle() );
        Assert.assertEquals( true, val.getMarkDownFilename().toString().endsWith( Paths.get( "context_help_test_loc", "en", "object_name.md" ).toString() ));

        key.setGuid( "3001" );
        key.setOid( "2" );
        
        Assert.assertEquals( true, m.get( key ) != null );
        
        val = m.get( key );
        
        Assert.assertEquals( "Objektname2 (en)", val.getTitle() );
        Assert.assertEquals( true, val.getMarkDownFilename().toString().endsWith( Paths.get( "context_help_test_loc", "en", "object_name2.md" ).toString() ));
    
    }
    

    @Test
    public void testBuildAvailableProfiledMarkdownHelp() throws Exception {
        MarkdownContextHelpUtils mchu = new MarkdownContextHelpUtils("context_help_test_profile");
        
        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> m = mchu.getAvailableMarkdownHelpFiles();
        
        Assert.assertEquals( false, m.isEmpty() );
        
        MarkdownContextHelpItemKey key = new MarkdownContextHelpItemKey("3000");
        key.setOid( "1" );
        key.setLang( "de" );
        
        Assert.assertEquals( true, m.get( key ) != null );
        
        MarkdownContextHelpItem val = m.get( key );
        
        Assert.assertEquals( "Objektname", val.getTitle() );
        Assert.assertEquals( true, val.getMarkDownFilename().toString().endsWith( Paths.get( "context_help_test_profile", "object_name.md" ).toString() ));
        
        key.setLang( "en" );
        
        Assert.assertEquals( true, m.get( key ) != null );
        
        val = m.get( key );
        
        Assert.assertEquals( "Objektname (en)", val.getTitle() );
        Assert.assertEquals( true, val.getMarkDownFilename().toString().endsWith( Paths.get( "context_help_test_profile", "en", "object_name.md" ).toString() ));
        

        key.setLang( "de" );
        key.setProfile( "myprofile" );
        
        Assert.assertEquals( true, m.get( key ) != null );
        
        val = m.get( key );
        
        Assert.assertEquals( "Objektname (myprofile)", val.getTitle() );
        Assert.assertEquals( true, val.getMarkDownFilename().toString().endsWith( Paths.get( "context_help_test_profile", MarkdownContextHelpUtils.PROFILE_DIR, "myprofile", "object_name.md" ).toString() ));
    
        key.setLang( "en" );
        key.setProfile( "myprofile" );
        
        Assert.assertEquals( true, m.get( key ) != null );
        
        val = m.get( key );
        
        Assert.assertEquals( "Objektname (en) (myprofile)", val.getTitle() );
        Assert.assertEquals( true, val.getMarkDownFilename().toString().endsWith( Paths.get( "context_help_test_profile", MarkdownContextHelpUtils.PROFILE_DIR, "myprofile", "en", "object_name.md" ).toString() ));
    }
    
    
    
    
    @Test
    public void testRenderMarkdownFile() throws Exception {
        MarkdownContextHelpUtils mchu = new MarkdownContextHelpUtils("context_help_test");
        
        Map<MarkdownContextHelpItemKey, MarkdownContextHelpItem> m = mchu.getAvailableMarkdownHelpFiles();
        
        Assert.assertEquals( false, m.isEmpty() );

        Optional<Entry<MarkdownContextHelpItemKey, MarkdownContextHelpItem>> findAny = m.entrySet().stream().findAny();
        MarkdownContextHelpItem val = findAny.get().getValue();
        String html = mchu.renderMarkdownFile( val.getMarkDownFilename() );
        Assert.assertEquals( true, html.contains( "h1" ) );
    }
    
}
