package de.ingrid.portal.om;

import java.util.ArrayList;

public class IngridFacet {

	private String id;
	private String name;
	private String query;
	private String queryType;
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
}
