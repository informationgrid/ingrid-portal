/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
import java.util.List;

import de.ingrid.portal.search.DisplayTreeNode;

public class IngridFacet {

    private String id;
    private String parentId;
    private String name;
    private String shortName;
    private String mobileName;
    private String query;
    private String queryType;
    private String field;
    private String codelist;
    private String codelistField;
    private String codelistId;
    private String codelistEntryId; 
    private String dependency;
    private String hidden;
    private String facetValue;
    private String sort;
    private String icon;
    private String shortcut;
    private String url;
    private String wildcard;
    private String info;
    private String infoResultSelect;
    private String selectGroup;
    private String selectFacet;
    private boolean hierarchyTree;
    private boolean hierarchyTreeLeaf;
    private String hierarchyTreeTypes;
    private int hierarchyTreeLevel;
    private DisplayTreeNode hierarchyTreeNode;
    private int showOnMoreThan;
    private int colNum;
    private int listLength;
    private boolean isDependencySelect = false;
    private boolean isHiddenSelect = false;
    private boolean isSelect = false;
    private boolean isOpen = false;
    private boolean isDisplay = false;
    private boolean categoryOnly = false;
    private IngridFacet toggle;
    private ArrayList<ArrayList<String>> toggleGroups;
    private ArrayList<String> options;
    
    /* Only for partner restriction */
    private boolean isParentHidden = false;
    
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
    public List<IngridFacet> getFacets() {
        return facets;
    }
    public void setFacets(List<IngridFacet> facets) {
        this.facets = (ArrayList<IngridFacet>) facets;
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
    public boolean isOpen() {
        return isOpen;
    }
    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
    public boolean isDisplay() {
        return isDisplay;
    }
    public void setDisplay(boolean isDisplay) {
        this.isDisplay = isDisplay;
    }
    public boolean isCategoryOnly() {
        return categoryOnly;
    }
    public void setCategoryOnly(boolean categoryOnly) {
        this.categoryOnly = categoryOnly;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getParentId() {
        return parentId;
    }
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public String getShortcut() {
        return shortcut;
    }
    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getShowOnMoreThan() {
        return showOnMoreThan;
    }
    public void setShowOnMoreThan(int showOnMoreThan) {
        this.showOnMoreThan = showOnMoreThan;
    }
    public String getWildcard() {
        return wildcard;
    }
    public void setWildcard(String wildcard) {
        this.wildcard = wildcard;
    }
    public int getColNum() {
        return colNum;
    }
    public void setColNum(int colNum) {
        this.colNum = colNum;
    }
    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    public String getMobileName() {
        return mobileName;
    }
    public void setMobileName(String mobileName) {
        this.mobileName = mobileName;
    }
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public String getInfoResultSelect() {
        return infoResultSelect;
    }
    public void setInfoResultSelect(String infoResultSelect) {
        this.infoResultSelect = infoResultSelect;
    }
    public String getSelectGroup() {
        return selectGroup;
    }
    public void setSelectGroup(String selectGroup) {
        this.selectGroup = selectGroup;
    }
    public String getSelectFacet() {
        return selectFacet;
    }
    public void setSelectFacet(String selectFacet) {
        this.selectFacet = selectFacet;
    }
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getCodelist() {
        return codelist;
    }
    public void setCodelist(String codelist) {
        this.codelist = codelist;
    }
    public String getCodelistField() {
        return codelistField;
    }
    public void setCodelistField(String codelistField) {
        this.codelistField = codelistField;
    }
    public IngridFacet getToggle() {
        return toggle;
    }
    public void setToggle(IngridFacet toggle) {
        this.toggle = toggle;
    }
    public int getListLength() {
        return listLength;
    }
    public void setListLength(int listLength) {
        this.listLength = listLength;
    }
    public ArrayList<ArrayList<String>> getToggleGroups() {
        return toggleGroups;
    }
    public void setToggleGroups(ArrayList<ArrayList<String>> toggleGroups) {
        this.toggleGroups = toggleGroups;
    }
    public boolean isHierarchyTree() {
        return hierarchyTree;
    }
    public void setHierarchyTree(boolean hierarchyTree) {
        this.hierarchyTree = hierarchyTree;
    }
    public boolean isHierarchyTreeLeaf() {
        return hierarchyTreeLeaf;
    }
    public void setHierarchyTreeLeaf(boolean hierarchyTreeLeaf) {
        this.hierarchyTreeLeaf = hierarchyTreeLeaf;
    }
    public String getHierarchyTreeTypes() {
        return hierarchyTreeTypes;
    }
    public void setHierarchyTreeTypes(String hierarchyTreeTypes) {
        this.hierarchyTreeTypes = hierarchyTreeTypes;
    }
    public int getHierarchyTreeLevel() {
        return hierarchyTreeLevel;
    }
    public void setHierarchyTreeLevel(int hierarchyTreeLevel) {
        this.hierarchyTreeLevel = hierarchyTreeLevel;
    }
    public DisplayTreeNode getHierarchyTreeNode() {
        return hierarchyTreeNode;
    }
    public void setHierarchyTreeNode(DisplayTreeNode hierarchyTreeNode) {
        this.hierarchyTreeNode = hierarchyTreeNode;
    }
    public ArrayList<String> getOptions() {
        return options;
    }
    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }
}
