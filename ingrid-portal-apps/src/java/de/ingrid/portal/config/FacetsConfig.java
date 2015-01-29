/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.global.UtilsFacete;
import de.ingrid.portal.om.IngridFacet;
import de.ingrid.utils.udk.iso19108.TM_PeriodDuration;
import de.ingrid.utils.udk.iso19108.TM_PeriodDuration.Interval;

public class FacetsConfig {

	private final static Logger log = LoggerFactory.getLogger(UtilsFacete.class);
	private static XMLConfiguration instance = null;

	public static XMLConfiguration getInstance() {
		
		if(instance == null){
			try {
				instance = new XMLConfiguration("facets-config.xml");
			} catch(ConfigurationException cex) {
				log.error("Error reading facets config file!");
			}
		}
		return instance;
	}

	/**
	 * Get facet configuration
	 * 
	 * @return
	 */
	public static ArrayList<IngridFacet> getFacets(){
		
		if(instance == null){
			instance = getInstance();
		}
		
		return extractFacets(instance.getRoot().getChildren("facet"), null);
	}
	
	/**
	 * Extract facet configuration
	 * 
	 * @param facets
	 * @return
	 */
	private static ArrayList<IngridFacet> extractFacets(List<ConfigurationNode> facet, IngridFacet parentFacet){
		ArrayList<IngridFacet> ingridFacets = new ArrayList<IngridFacet>();
		
		if(facet != null){
			for(ConfigurationNode facetNode : facet){
				IngridFacet ingridFacet = new IngridFacet();
				if(facetNode.getChildren("id").size() > 0){
					Node node = (Node) facetNode.getChildren("id").get(0);
					if(node  != null){
						ingridFacet.setId(node.getValue().toString());
					}
				}
				
				if(parentFacet != null){
					ingridFacet.setParent(parentFacet);
				}
				
				if(facetNode.getChildren("name").size() > 0){
					Node node = (Node) facetNode.getChildren("name").get(0);
					if(node  != null){
						ingridFacet.setName((String) node.getValue());
					}
				}
				
				if(facetNode.getChildren("[@sort]").size() > 0){
					Node subNode = (Node) facetNode.getChildren("[@sort]").get(0); 
					ingridFacet.setSort(subNode.getValue().toString());
				}
				
				
				if(facetNode.getChildren("query").size() > 0){
					Node node = (Node) facetNode.getChildren("query").get(0);
					if(node  != null){
						ingridFacet.setQuery((String) node.getValue());
					}
				}
				
				if(facetNode.getChildren("codelist-id").size() > 0){
				    Node node = (Node) facetNode.getChildren("codelist-id").get(0);
				    if(node  != null){
				        ingridFacet.setCodelistId((String) node.getValue());
				    }
				}
				
				if(facetNode.getChildren("codelist-entry-id").size() > 0){
				    Node node = (Node) facetNode.getChildren("codelist-entry-id").get(0);
				    if(node  != null){
				        ingridFacet.setCodelistEntryId((String) node.getValue());
				    }
				}
				
				if(facetNode.getChildren("from").size() > 0 && facetNode.getChildren("to").size() > 0){
					String from = null;
					String to = null;
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
					Calendar cal;
			        
					//from
					Node node = (Node) facetNode.getChildren("from").get(0);
					cal = new GregorianCalendar();
					if(node  != null){
						String value = node.getValue().toString();
						if(value.equals("")){
							from = "";
						}else{
							TM_PeriodDuration pd = TM_PeriodDuration.parse(value);
						    if(pd.getValue(Interval.DAYS) != null && pd.getValue(Interval.DAYS).length() > 0){
						    	cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(pd.getValue(Interval.DAYS)) * (-1));
					        }
	
					        if(pd.getValue(Interval.MONTHS) != null && pd.getValue(Interval.MONTHS).length() > 0){
					        	cal.add(Calendar.MONTH, Integer.parseInt(pd.getValue(Interval.MONTHS)) * (-1));
					        }
					        
					        if(pd.getValue(Interval.YEARS) != null && pd.getValue(Interval.YEARS).length() > 0){
					        	cal.add(Calendar.YEAR, Integer.parseInt(pd.getValue(Interval.YEARS)) * (-1));
					        }
					        from = df.format(cal.getTime());
						}
					}
			        
					//to
					node = (Node) facetNode.getChildren("to").get(0);
					cal = new GregorianCalendar();
					if(node  != null){
						String value = node.getValue().toString();
						if(value.equals("")){
							to = "";
						}else{
							TM_PeriodDuration pd = TM_PeriodDuration.parse(value);
						    if(pd.getValue(Interval.DAYS) != null && pd.getValue(Interval.DAYS).length() > 0){
						    	cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(pd.getValue(Interval.DAYS)) * (-1));
					        }

					        if(pd.getValue(Interval.MONTHS) != null && pd.getValue(Interval.MONTHS).length() > 0){
					        	cal.add(Calendar.MONTH, Integer.parseInt(pd.getValue(Interval.MONTHS)) * (-1));
					        }
					        
					        if(pd.getValue(Interval.YEARS) != null && pd.getValue(Interval.YEARS).length() > 0){
					        	cal.add(Calendar.YEAR, Integer.parseInt(pd.getValue(Interval.YEARS)) * (-1));
					        }
					        to = df.format(cal.getTime());
						}
					}
					if(from != null && to != null){
						ingridFacet.setQuery("t01_object.mod_time:[" + from +  "0* TO " + to + "9*]");
					}
				}
				
				if(facetNode.getChildren("dependency").size() > 0){
					Node node = (Node) facetNode.getChildren("dependency").get(0);
					if(node  != null){
						ingridFacet.setDependency(node.getValue().toString());
					}
				}
				
				if(facetNode.getChildren("hidden").size() > 0){
					Node node = (Node) facetNode.getChildren("hidden").get(0);
					if(node  != null){
						ingridFacet.setHidden(node.getValue().toString());
					}
				}
				
				if(facetNode.getChildren("facets").size() > 0){
					Node node = (Node) facetNode.getChildren("facets").get(0);
					if(node != null){
						if(node.getChildren("[@queryType]").size() > 0){
							Node subNode = (Node) node.getChildren("[@queryType]").get(0); 
							ingridFacet.setQueryType(subNode.getValue().toString());
						}
						
						Node facetsNode = (Node) facetNode.getChildren("facets").get(0);
						if(facetNode != null){
							List<ConfigurationNode> subFacet = facetsNode.getChildren("facet"); 
							if(subFacet != null){
								ingridFacet.setFacets(extractFacets(subFacet, ingridFacet));							
							}
						}
					}
				}
				
				ingridFacets.add(ingridFacet);
			}
		}
		return ingridFacets;
	}
}