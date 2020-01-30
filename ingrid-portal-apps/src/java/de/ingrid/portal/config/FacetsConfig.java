/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.config;

import de.ingrid.portal.global.UtilsFacete;
import de.ingrid.portal.om.IngridFacet;
import de.ingrid.utils.udk.iso19108.TM_PeriodDuration;
import de.ingrid.utils.udk.iso19108.TM_PeriodDuration.Interval;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class FacetsConfig {

    private static final Logger log = LoggerFactory.getLogger( UtilsFacete.class );
    private static XMLConfiguration instance = null;

    private FacetsConfig() {
        throw new IllegalStateException("FacetsConfig class");
    }

    public static XMLConfiguration getInstance() {
        if (instance == null) {
            try {
                instance = new XMLConfiguration( "facets-config.xml" );
            } catch (ConfigurationException cex) {
                if(log.isErrorEnabled()) {
                    log.error( "Error reading facets config file!" );
                }
            }
        }
        return instance;
    }

    /**
     * Get facet configuration
     * 
     * @return
     */
    public static List<IngridFacet> getFacets() {

        if (instance == null) {
            instance = getInstance();
        }

        return extractFacets( instance.getRoot().getChildren( "facet" ), null );
    }

    /**
     * Extract facet configuration
     * 
     * @param facets
     * @return
     */
    private static ArrayList<IngridFacet> extractFacets(List<ConfigurationNode> facet, IngridFacet parentFacet) {
        ArrayList<IngridFacet> ingridFacets = new ArrayList<>();

        if (facet != null) {
            for (ConfigurationNode facetNode : facet) {
                IngridFacet ingridFacet = new IngridFacet();
                if (!facetNode.getChildren( "id" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "id" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setId( node.getValue().toString() );
                    }
                }

                if (parentFacet != null) {
                    ingridFacet.setParent( parentFacet );
                }

                if (!facetNode.getChildren( "name" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "name" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setName( (String) node.getValue() );
                    }
                }

                if (!facetNode.getChildren( "field" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "field" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setField( (String) node.getValue() );
                    }
                }

                if (!facetNode.getChildren( "shortName" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "shortName" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setShortName( (String) node.getValue() );
                    }
                }

                if (!facetNode.getChildren( "mobileName" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "mobileName" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setMobileName( (String) node.getValue() );
                    }
                }

                if (!facetNode.getAttributes( "sort" ).isEmpty()) {
                    Node subNode = (Node) facetNode.getAttributes( "sort" ).get( 0 );
                    ingridFacet.setSort( subNode.getValue().toString() );
                }
                
                if (!facetNode.getAttributes( "show-on-more-than" ).isEmpty()) {
                    Node subNode = (Node) facetNode.getAttributes( "show-on-more-than" ).get( 0 );
                    ingridFacet.setShowOnMoreThan( new Integer(subNode.getValue().toString()) );
                }
                
                if (!facetNode.getChildren( "query" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "query" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setQuery( (String) node.getValue() );
                    }
                }

                if (!facetNode.getChildren( "codelist-id" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "codelist-id" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setCodelistId( (String) node.getValue() );
                    }
                }

                if (!facetNode.getChildren( "codelist-entry-id" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "codelist-entry-id" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setCodelistEntryId( (String) node.getValue() );
                    }
                }

                if (!facetNode.getChildren( "from" ).isEmpty() && !facetNode.getChildren( "to" ).isEmpty()) {
                    String from = null;
                    String to = null;
                    SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd" );
                    Calendar cal;

                    // from
                    Node node = (Node) facetNode.getChildren( "from" ).get( 0 );
                    cal = new GregorianCalendar();
                    if (node != null) {
                        String value = node.getValue().toString();
                        if (value.equals( "" )) {
                            from = "";
                        } else {
                            TM_PeriodDuration pd = TM_PeriodDuration.parse( value );
                            if(pd != null) {
                                if (pd.getValue( Interval.DAYS ) != null && pd.getValue( Interval.DAYS ).length() > 0) {
                                    cal.add( Calendar.DAY_OF_MONTH, Integer.parseInt( pd.getValue( Interval.DAYS ) ) * (-1) );
                                }
                                
                                if (pd.getValue( Interval.MONTHS ) != null && pd.getValue( Interval.MONTHS ).length() > 0) {
                                    cal.add( Calendar.MONTH, Integer.parseInt( pd.getValue( Interval.MONTHS ) ) * (-1) );
                                }
                                
                                if (pd.getValue( Interval.YEARS ) != null && pd.getValue( Interval.YEARS ).length() > 0) {
                                    cal.add( Calendar.YEAR, Integer.parseInt( pd.getValue( Interval.YEARS ) ) * (-1) );
                                }
                            }
                            from = df.format( cal.getTime() );
                        }
                    }

                    // to
                    node = (Node) facetNode.getChildren( "to" ).get( 0 );
                    cal = new GregorianCalendar();
                    if (node != null) {
                        String value = node.getValue().toString();
                        if (value.equals( "" )) {
                            to = "";
                        } else {
                            TM_PeriodDuration pd = TM_PeriodDuration.parse( value );
                            if(pd != null) {
                                if (pd.getValue( Interval.DAYS ) != null && pd.getValue( Interval.DAYS ).length() > 0) {
                                    cal.add( Calendar.DAY_OF_MONTH, Integer.parseInt( pd.getValue( Interval.DAYS ) ) * (-1) );
                                }
                                
                                if (pd.getValue( Interval.MONTHS ) != null && pd.getValue( Interval.MONTHS ).length() > 0) {
                                    cal.add( Calendar.MONTH, Integer.parseInt( pd.getValue( Interval.MONTHS ) ) * (-1) );
                                }
                                
                                if (pd.getValue( Interval.YEARS ) != null && pd.getValue( Interval.YEARS ).length() > 0) {
                                    cal.add( Calendar.YEAR, Integer.parseInt( pd.getValue( Interval.YEARS ) ) * (-1) );
                                }
                            }
                            to = df.format( cal.getTime() );
                        }
                    }
                    node = (Node) facetNode.getChildren( "query" ).get( 0 );
                    if (node != null) {
                        String value = node.getValue().toString();
                        if (from != null && to != null) {
                            ingridFacet.setQuery( value.replace( "{FROM}", from ).replace( "{TO}", to ) );
                        }
                    }
                }

                if (!facetNode.getChildren( "dependency" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "dependency" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setDependency( node.getValue().toString() );
                    }
                }

                if (!facetNode.getChildren( "isOpen" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "isOpen" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setOpen( Boolean.parseBoolean(node.getValue().toString()) );
                    }
                }

                if (!facetNode.getChildren( "hidden" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "hidden" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setHidden( node.getValue().toString() );
                    }
                }

                if (!facetNode.getChildren( "display" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "display" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setDisplay( Boolean.parseBoolean(node.getValue().toString()) );
                    }
                }

                if (!facetNode.getChildren( "categoryOnly" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "categoryOnly" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setCategoryOnly( Boolean.parseBoolean(node.getValue().toString()) );
                    }
                }
                
                if (!facetNode.getChildren( "parentId" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "parentId" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setParentId( node.getValue().toString() );
                    }
                }
                
                if (!facetNode.getChildren( "icon" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "icon" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setIcon( node.getValue().toString() );
                    }
                }
                
                if (!facetNode.getChildren( "shortcut" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "shortcut" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setShortcut( node.getValue().toString() );
                    }
                }
                
                if (!facetNode.getChildren( "url" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "url" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setUrl( node.getValue().toString() );
                    }
                }
                
                if (!facetNode.getChildren( "wildcard" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "wildcard" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setWildcard( node.getValue().toString() );
                    }
                }
                
                if (!facetNode.getChildren( "colNum" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "colNum" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setColNum( Integer.parseInt(node.getValue().toString()) );
                    }
                }
                
                if (!facetNode.getChildren( "facets" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "facets" ).get( 0 );
                    if (node != null) {
                        if (!node.getAttributes( "@queryType" ).isEmpty()) {
                            Node subNode = (Node) node.getAttributes( "@queryType" ).get( 0 );
                            ingridFacet.setQueryType( subNode.getValue().toString() );
                        }

                        Node facetsNode = (Node) facetNode.getChildren( "facets" ).get( 0 );
                        if (facetNode != null) {
                            List<ConfigurationNode> subFacet = facetsNode.getChildren( "facet" );
                            if (subFacet != null) {
                                ingridFacet.setFacets( extractFacets( subFacet, ingridFacet ) );
                            }
                        }
                    }
                }

                if (!facetNode.getChildren( "info" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "info" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setInfo( node.getValue().toString() );
                    }
                }

                if (!facetNode.getChildren( "infoResultSelect" ).isEmpty()) {
                    Node node = (Node) facetNode.getChildren( "infoResultSelect" ).get( 0 );
                    if (node != null) {
                        ingridFacet.setInfoResultSelect( node.getValue().toString() );
                    }
                }

                ingridFacets.add( ingridFacet );
            }
        }
        return ingridFacets;
    }
}
