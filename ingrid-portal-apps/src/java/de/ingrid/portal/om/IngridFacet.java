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
package de.ingrid.portal.om;

import java.util.ArrayList;

public class IngridFacet {

	private String id;
	private String name;
	private String query;
	private String queryType;
	private String codelistId;
	private String codelistEntryId; 
	private String dependency;
	private String hidden;
	private String facetValue;
	private String sort;
	private boolean isDependencySelect = false;
	private boolean isHiddenSelect = false;
	private boolean isSelect = false;
	/* Only for partner restriction */
	private boolean isParentHidden = false;
	private boolean isOldIPlug = false;
	
	private IngridFacet parent; 
	private ArrayList<IngridFacet> facets;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public ArrayList<IngridFacet> getFacets() {
		return facets;
	}
	public void setFacets(ArrayList<IngridFacet> facets) {
		this.facets = facets;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public IngridFacet getParent() {
		return parent;
	}
	public void setParent(IngridFacet parent) {
		this.parent = parent;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getDependency() {
		return dependency;
	}
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}
	public String getFacetValue() {
		return facetValue;
	}
	public void setFacetValue(String facetValue) {
		this.facetValue = facetValue;
	}
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	public boolean isDependencySelect() {
		return isDependencySelect;
	}
	public void setDependencySelect(boolean isDependencySelect) {
		this.isDependencySelect = isDependencySelect;
	}
	public String getHidden() {
		return hidden;
	}
	public void setHidden(String hidden) {
		this.hidden = hidden;
	}
	public boolean isHiddenSelect() {
		return isHiddenSelect;
	}
	public void setHiddenSelect(boolean isHiddenSelect) {
		this.isHiddenSelect = isHiddenSelect;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public boolean isOldIPlug() {
		return isOldIPlug;
	}
	public void setOldIPlug(boolean isOldIPlug) {
		this.isOldIPlug = isOldIPlug;
	}
	public boolean isParentHidden() {
		return isParentHidden;
	}
	public void setParentHidden(boolean isParentHidden) {
		this.isParentHidden = isParentHidden;
	}
	
	public String getCodelistEntryId() {
	    return codelistEntryId;
	}
	
	public void setCodelistEntryId(String id) {
	    this.codelistEntryId = id;
	}
	
	public String getCodelistId() {
	    return codelistId;
	}
	
	public void setCodelistId(String id) {
	    this.codelistId = id;
	}
}
