<?xml version="1.0" encoding="UTF-8"?>
<!--
  **************************************************-
  InGrid Portal Apps
  ==================================================
  Copyright (C) 2014 - 2019 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml"/>

<xsl:template match="/">
    <div class="row search-filtered">
    <xsl:for-each select="chapter">
        <div class="xsmall-24 large-8 xlarge-6 columns">
            <div class="accordion accordion-filter-group" data-accordion="" data-allow-all-closed="true" role="tablist" data-e="5rmu1i-e">
                <div class="accordion-item accordion-item-filter-group" data-accordion-item="">
                    <a href="#" class="accordion-title accordion-title-filter-group hide-for-large" role="tab" aria-expanded="false" aria-selected="false"><xsl:value-of select="header"/></a>
                    <div class="accordion-content filter-wrapper" data-tab-content="" role="tabpanel" aria-labelledby="" aria-hidden="true" id="">
                        <div class="boxes">
	                        <ul class="accordion filter-group help-group" data-accordion="" data-multi-expand="true" data-allow-all-closed="true" role="tablist" data-e="">
	                            <li class="accordion-item is-active" data-accordion-item="">
	                                <a class="accordion-title" aria-controls="type-accordion" role="tab" id="type-accordion-label" aria-expanded="false" aria-selected="false">
	                                    <span class="text"><xsl:value-of select="header"/></span>
	                                </a>
	                                <div class="accordion-content" data-tab-content="" role="tabpanel"  aria-hidden="true">
	                                    <div class="boxes">
	                                         <xsl:for-each select="section">
	                                             <div class="form-element boxed">
	                                                 <a class="nav-item">
	                                                     <xsl:attribute name="href">#<xsl:value-of select="@help-key" /></xsl:attribute>
	                                                     <xsl:value-of select="header"/>
	                                                 </a>
	                                             </div>
	                                         </xsl:for-each>
	                                     </div>
	                                 </div>
	                             </li>
	                        </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </xsl:for-each>
    <xsl:for-each select="chapter">
        <div class="xsmall-24 large-16 xlarge-18 columns" style="padding-left: 50px;">
           <h2><xsl:value-of select="header"/></h2>
           <xsl:for-each select="section">
               <a class="anchor"><xsl:attribute name="name"><xsl:value-of select="@help-key" /></xsl:attribute><span class="ic-ic-arrow"></span><a/></a>
               <xsl:if test="header/@display != 'false' or not(header/@display)">
                   <h3><xsl:value-of select="header"/></h3>
               </xsl:if>
               <xsl:apply-templates select="content"/>
               <xsl:for-each select="section">
                   <a><xsl:attribute name="name"><xsl:value-of select="@help-key" /></xsl:attribute><a/></a>
                   <h3><xsl:value-of select="header"/></h3>
                   <xsl:apply-templates select="content"/>
               </xsl:for-each>
           </xsl:for-each>
         </div>
    </xsl:for-each>
    </div>
</xsl:template>

<xsl:template match="content">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="*">
  <xsl:copy-of select="." />
</xsl:template>

</xsl:stylesheet>
