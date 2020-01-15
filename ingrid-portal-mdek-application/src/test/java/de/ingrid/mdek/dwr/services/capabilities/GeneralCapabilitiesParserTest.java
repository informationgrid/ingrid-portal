/*-
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
package de.ingrid.mdek.dwr.services.capabilities;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vividsolutions.jts.util.Assert;

import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.object.AddressBean;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.handler.ConnectionFacade;

public class GeneralCapabilitiesParserTest {

    @Mock
    private ConnectionFacade connFacade;
    @Mock
    private IMdekCallerQuery callerQuery;
    @Mock
    private SysListCache sysListMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks( this );
        when( sysListMapper.getConnectionFacade() ).thenReturn( connFacade );
    }

    @Test
    public void testExtractName() {
        GeneralCapabilitiesParser cp = new GeneralCapabilitiesParser( null, sysListMapper );
        String[] s = cp.extractName( "Max Mustermann" );
        Assert.equals( "Max", s[0] );
        Assert.equals( "Mustermann", s[1] );

        s = cp.extractName( "Mustermann, Max" );
        Assert.equals( "Max", s[0] );
        Assert.equals( "Mustermann", s[1] );

        s = cp.extractName( "Mustermann" );
        Assert.equals( "", s[0] );
        Assert.equals( "Mustermann", s[1] );

        s = cp.extractName( "Mustermann, Dr. Max" );
        Assert.equals( "Dr. Max", s[0] );
        Assert.equals( "Mustermann", s[1] );

        s = cp.extractName( "Dr. Max Mustermann" );
        Assert.equals( "Dr. Max", s[0] );
        Assert.equals( "Mustermann", s[1] );
    }

    @Test
    public void testSetNameInAddressBean() {
        GeneralCapabilitiesParser cp = new GeneralCapabilitiesParser( null, sysListMapper );
        AddressBean address = new AddressBean();
        cp.setNameInAddressBean( address, "Max Mustermann" );
        Assert.equals( "Max", address.getFirstname() );
        Assert.equals( "Mustermann", address.getLastname() );

        address = new AddressBean();
        cp.setNameInAddressBean( address, "Mustermann, Max" );
        Assert.equals( "Max", address.getFirstname() );
        Assert.equals( "Mustermann", address.getLastname() );

        address = new AddressBean();
        cp.setNameInAddressBean( address, "Mustermann" );
        Assert.equals( "", address.getFirstname() );
        Assert.equals( "Mustermann", address.getLastname() );

        address = new AddressBean();
        cp.setNameInAddressBean( address, "" );
        Assert.equals( "", address.getFirstname() );
        Assert.equals( "", address.getLastname() );

        address = new AddressBean();
        cp.setNameInAddressBean( address, null );
        Assert.isTrue( null == address.getFirstname() );
        Assert.equals( "N/A", address.getLastname() );
    }

}
