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
    <xsl:for-each select="chapter">
        <xsl:if test="header/@display != 'false' or not(header/@display)">
            <ul class="accordion filter-group help-group" data-accordion="" data-multi-expand="true" data-allow-all-closed="true" role="tablist" data-e="">
                <li class="accordion-item" data-accordion-item="">
                    <a class="accordion-title" aria-controls="facet-accordion" role="tab" id="help-accordion-label" aria-expanded="false" aria-selected="false">
                        <span class="text"><xsl:value-of select="header"/></span>
                    </a>
                    <div class="accordion-content" data-tab-content="" role="tabpanel">
                        <div class="boxes">
                            <xsl:for-each select="section">
                                <a class="js-anchor-target">
                                    <xsl:attribute name="href">?hkey=<xsl:value-of select="@help-key" />#<xsl:value-of select="@help-key" /></xsl:attribute>
                                    <span class="ic-ic-bullet"><br></br></span>
                                    <span class="text"><xsl:value-of select="header"/></span>
                                </a>
                            </xsl:for-each>
                        </div>
                    </div>
                </li>
            </ul>
        </xsl:if>
    </xsl:for-each>
</xsl:template>

<xsl:template match="chapter">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="*">
  <xsl:copy-of select="." />
</xsl:template>

</xsl:stylesheet>
