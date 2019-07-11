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
package de.ingrid.mdek.dwr.services;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import de.ingrid.external.gemet.GEMETService;
import de.ingrid.mdek.MdekUtils.IdcEntityOrderBy;
import de.ingrid.mdek.MdekUtils.IdcQAEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.IdcWorkEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.beans.TreeNodeBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.object.MdekDataSimpleBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultSimpleBean;
import de.ingrid.mdek.beans.query.ObjectStatisticsResultBean;
import de.ingrid.mdek.beans.query.ThesaurusStatisticsResultBean;
import de.ingrid.mdek.dwr.services.sns.SNSService;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;
import de.ingrid.mdek.handler.ObjectRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekObjectUtils;

public class ObjectServiceImpl implements ObjectService, BeanFactoryAware {

    private static final Logger log = Logger.getLogger(ObjectServiceImpl.class);	

	// Injected by Spring
	private ObjectRequestHandler objectRequestHandler;

    // Spring Bean Factory for lookup of needed services
    private BeanFactory beanFactory;
    private SNSService snsService;
    private CatalogService catalogService;

	private static final String OBJECT_APPTYPE = "O";
	private static final String OBJECT_INITIAL_DOCTYPE = "Class0_B";

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.snsService = (SNSService) this.beanFactory.getBean("snsService");
        this.catalogService = (CatalogService) this.beanFactory.getBean("catalogService");
    }

	public boolean canCopyObject(String uuid) {
		log.debug("Query if node can be copied: "+uuid);

		try {
			objectRequestHandler.canCopyObject(uuid);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while checking if node can be cut.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error checking if node can be copied.", e);
		}
		return true;
	}
	
	public boolean canCopyObjects(String[] uuids) {
	    boolean success = true;
        try {
            for (String uuid : uuids) {
                success = success && canCopyObject(uuid);
            }
        } catch (MdekException e) {
            // Wrap the MdekException in a RuntimeException so dwr can convert it
            log.error("MdekException while checking if one of several nodes can be cut.", e);
            throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
        } catch (Exception e) {
            log.error("Error checking if one of several nodes can be copied.", e);
        }
        return success;
    }

	
	public boolean canCutObject(String uuid) {
		log.debug("Query if node can be cut: "+uuid);

		try {
			objectRequestHandler.canCutObject(uuid);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while checking if node can be cut.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error checking if node can be cut.", e);
		}
		return true;
	}

	public TreeNodeBean copyNode(String nodeUuid, String dstNodeUuid, Boolean includeChildren) {
		log.debug("Copying node with ID: "+nodeUuid+" to ID: "+dstNodeUuid);

		try {
			TreeNodeBean copyResult = objectRequestHandler.copyObject(nodeUuid, dstNodeUuid, includeChildren);
			if (copyResult != null) {
				TreeServiceImpl.addTreeNodeObjectInfo(copyResult);
			}
			return copyResult;
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while copying node.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.error("Error copying node.", e);
			throw new RuntimeException(e);
		}
	}

	public TreeNodeBean[] copyNodes(String[] nodeUuids, String dstNodeUuid, Boolean includeChildren) {
	    TreeNodeBean[] result = new TreeNodeBean[nodeUuids.length];
	    int i = 0;
	    for (String uuid : nodeUuids) {
	        result[i++] = copyNode(uuid, dstNodeUuid, includeChildren);
	    }
	    return result;
	}
	
	public MdekDataBean createNewNode(String parentUuid) {
		log.debug("creating new node with parent id = "+parentUuid);		
		MdekDataBean data = null;
		try {
			data = objectRequestHandler.getInitialObject(parentUuid);
		} catch (MdekException e) {
			log.error("Error while getting node data.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}

		MdekObjectUtils.setInitialValues(data);

		data.setNodeAppType(OBJECT_APPTYPE);
		data.setNodeDocType(OBJECT_INITIAL_DOCTYPE);
		data.setUuid("newNode");
		return data;
	}

	
	public void deleteNode(String uuid, Boolean forceDeleteReferences, Boolean markOnly) {
		try {
			objectRequestHandler.deleteObject(uuid, forceDeleteReferences);
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("Error deleting object.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.error("Error deleting object.", e);
			throw new RuntimeException(e);
		}
	}

	
	public MdekDataBean deleteObjectWorkingCopy(String uuid, Boolean forceDeleteReferences, Boolean markOnly) {
		try {
			boolean wasFullyDeleted = objectRequestHandler.deleteObjectWorkingCopy(uuid, forceDeleteReferences);
			if (!wasFullyDeleted) {
				return objectRequestHandler.getObjectDetail(uuid);
			}
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("Error deleting working Copy.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.error("Error deleting working Copy.", e);
			throw new RuntimeException(e);
		}

		return null;
	}

	public MdekDataBean getNodeData(String nodeUuid, String nodeType,
			Boolean useWorkingCopy) {
		
		MdekDataBean data = null; 

		try {
			data = objectRequestHandler.getObjectDetail(nodeUuid);
		} catch (RuntimeException e) {
			log.error("Error while getting node data.", e);
			throw e;
		}

		return data;
	}

	public MdekDataBean getPublishedNodeData(String nodeUuid) {
		MdekDataBean data = null; 

		try {
			data = objectRequestHandler.getPublishedObjectDetail(nodeUuid);
		} catch (RuntimeException e) {
			log.error("Error while getting published node data.", e);
			throw e;
		}

		return data;
	}

	
	public List<String> getPathToObject(String uuid) {
		return objectRequestHandler.getPathToObject(uuid);
	}

	public void moveNode(String nodeUuid, String oldParentUuid, String newParentUuid, boolean forcePublicationCondition) {
		log.debug("Moving node with ID: "+nodeUuid+" and parent: "+oldParentUuid+" to ID: "+newParentUuid);

		try {
			objectRequestHandler.moveObjectSubTree(nodeUuid, oldParentUuid, newParentUuid, forcePublicationCondition);
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while moving node.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.error("Error moving node.", e);
			throw new RuntimeException(e);
		}
	}
	
	public void moveNodes(String[] nodeUuids, String[] oldParentUuids, String newParentUuid, boolean forcePublicationCondition) {
	    int i = 0;
	    for (String uuid : nodeUuids) {
            moveNode(uuid, oldParentUuids[i++], newParentUuid, forcePublicationCondition);
        }
	}

	public MdekDataBean saveNodeData(MdekDataBean data, Boolean useWorkingCopy, Boolean forcePublicationCondition) {
		log.debug("saveNodeData()");

        // TODO: REMOVE THIS UGLY FIX WHEN BETTER GEMET SERVICE ??????? ADDITIONAL LOCALIZATION OF GEMET TERMS WHEN SAVING !
		fixGEMETTerms(data);

		if (useWorkingCopy) {
			log.debug("Saving node with ID: "+data.getUuid());

			try { return objectRequestHandler.saveObject(data); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.error("MdekException while saving node.", e);
				throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
			}
			catch (RuntimeException e) {
				log.error("Error while saving node", e);
				throw new RuntimeException(e);
			}
		} else {
			log.debug("Publishing node with ID: "+data.getUuid());

			try { return objectRequestHandler.publishObject(data, forcePublicationCondition); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.error("MdekException while publishing node.", e);
				throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
			}
			catch (RuntimeException e) {
				log.error("Error while publishing node", e);
				throw e;
			}
		}
	}
	
	/**
	 * Process GEMET terms to add additional localized name if wanted and not yet present !
	 * This is an ugly fix cause backend cannot add additional localized term when requesting
	 * findTermsFromQueryTerm (search in navigator) for every found term, cause it is too slow !!!
	 * see https://dev.informationgrid.eu/redmine/issues/363
	 * @param data DataBean where SNSTopics (of source GEMET) will be fixed !
	 */
	private void fixGEMETTerms(MdekDataBean data) {
        if (snsService.getThesaurusService() instanceof GEMETService && ((GEMETService) snsService.getThesaurusService()).getAlternateLanguage() != null) {

            // fetch catalog language
            Locale catalogLocale = new Locale( catalogService.getCatalogData().getLanguageShort() );

            // check GEMET terms for additional localization and fetch again if not set yet !
            List<SNSTopic> topics = data.getThesaurusTermsTable();
            for (int i=0; i<topics.size(); i++) {
                SNSTopic topic = topics.get( i );
                if (SNSTopic.Source.GEMET.equals( topic.getSource() ) && topic.getAlternateTitle() == null) {
                    // fetch again from backend WITH alternate title (additional localization)
                    // alternateTitle is always set when fetching single term via ID (in GEMET service !)
                    topics.set( i, snsService.getPSI( topic.getTopicId(), catalogLocale ));
                }
            }
        }
	}

	public MdekDataBean saveObjectPublicationCondition(String uuid, Boolean useWorkingCopy, Integer newPublicationCondition, boolean forcePublicationCondition) {
		log.debug("saveObjectPublicationCondition()");

		// first load object
		MdekDataBean data = getNodeData(uuid, "O", useWorkingCopy);
		
		// then change publication condition
		data.setExtraInfoPublishArea(newPublicationCondition);

		// then save
		return saveNodeData(data, useWorkingCopy, forcePublicationCondition);
	}

	public void updateObjectTitle(String uuid, String newTitle) {
		try {
			objectRequestHandler.updateObjectTitle(uuid, newTitle);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while updating object title.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public MdekDataBean assignObjectToQA(MdekDataBean data) {
		log.debug("Assigning node with ID to QA: "+data.getUuid());

		try { return objectRequestHandler.assignObjectToQA(data); }
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while assigning node to QA.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (RuntimeException e) {
			log.error("Error while assigning node to QA", e);
			throw e;
		}
	}		

	public MdekDataBean reassignObjectToAuthor(MdekDataBean data) {
		log.debug("Reassigning node with ID to author: "+data.getUuid());

		try { return objectRequestHandler.reassignObjectToAuthor(data); }
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while reassigning node to Author.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (RuntimeException e) {
			log.error("Error while reassigning node to Author", e);
			throw e;
		}
	}		
	
	public ObjectSearchResultSimpleBean getWorkObjects(IdcWorkEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits) {
		try {
			ObjectSearchResultBean workObjects = objectRequestHandler.getWorkObjects(selectionType, orderBy, orderAsc, startHit, numHits);
			Iterator<MdekDataBean> iterator = workObjects.getResultList().iterator();
			ObjectSearchResultSimpleBean workObjectsSimple = new ObjectSearchResultSimpleBean();
			workObjectsSimple.setTotalNumHits( workObjects.getTotalNumHits() );
			workObjectsSimple.setNumHits( workObjects.getNumHits() );
			while (iterator.hasNext()) {
                MdekDataBean bean = iterator.next();
                MdekDataSimpleBean simpleBean = new MdekDataSimpleBean();
                simpleBean.setUuid( bean.getUuid() );
                simpleBean.setModificationTime( bean.getModificationTime() );
                simpleBean.setNodeDocType( bean.getNodeDocType() );
                simpleBean.setObjectClass( bean.getObjectClass() );
                simpleBean.setRelationType( bean.getRelationType() );
                simpleBean.setTitle( bean.getTitle() );
                simpleBean.setWorkState( bean.getWorkState() );
                simpleBean.setWritePermission( bean.getWritePermission() );
                
                workObjectsSimple.getResultList().add( simpleBean );
            }
			return workObjectsSimple;

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while fetching Work objects.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
	
		} catch (RuntimeException e) {
			log.error("Error while fetching Work objects", e);
			throw e;
		}
	}
	
	public ObjectSearchResultBean getWorkObjectsQA(IdcWorkEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits) {
        try {
            return objectRequestHandler.getWorkObjects(selectionType, orderBy, orderAsc, startHit, numHits);

        } catch (MdekException e) {
            // Wrap the MdekException in a RuntimeException so dwr can convert it
            log.error("MdekException while fetching Work objects.", e);
            throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
    
        } catch (RuntimeException e) {
            log.error("Error while fetching Work objects", e);
            throw e;
        }
    }

	public ObjectSearchResultBean getQAObjects(WorkState workState, IdcQAEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits) {
		try {
			return objectRequestHandler.getQAObjects(workState, selectionType, orderBy, orderAsc, startHit, numHits);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while fetching QA objects.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));

		} catch (RuntimeException e) {
			log.error("Error while fetching QA objects", e);
			throw e;
		}
	}

	public ObjectStatisticsResultBean getObjectStatistics(String objUuid) {
		try {
			return objectRequestHandler.getObjectStatistics(objUuid);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while fetching objects for statistics.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));

		} catch (RuntimeException e) {
			log.error("Error while fetching objects for statistics", e);
			throw e;
		}		
	}

	public ThesaurusStatisticsResultBean getObjectThesaurusStatistics(String objUuid, boolean thesaurusTerms, int startHit, int numHits) {
    	try {
    		return objectRequestHandler.getObjectThesaurusStatistics(objUuid, thesaurusTerms, startHit, numHits);
    
    	} catch (MdekException e) {
    		// Wrap the MdekException in a RuntimeException so dwr can convert it
    		log.error("MdekException while fetching objects for thesaurus statistics.", e);
    		throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
    
    	} catch (RuntimeException e) {
    		log.error("Error while fetching objects for thesaurus statistics", e);
    		throw e;
    	}		
    }

	public ObjectRequestHandler getObjectRequestHandler() {
		return objectRequestHandler;
	}


	public void setObjectRequestHandler(ObjectRequestHandler objectRequestHandler) {
		this.objectRequestHandler = objectRequestHandler;
	}
	
	public String getIsoXml(String id, boolean publishedVersion) {
	    return objectRequestHandler.getIsoXml( id, publishedVersion );
	}
}
