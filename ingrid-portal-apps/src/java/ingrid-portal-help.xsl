<?xml version="1.0" encoding="UTF-8"?>
<!--
  **************************************************-
  InGrid Portal Apps
  ==================================================
  Copyright (C) 2014 - 2025 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.2 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  https://joinup.ec.europa.eu/software/page/eupl
  
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
        <div class="xsmall-24 large-16 xlarge-16 columns">
            <h2><xsl:value-of select="header"/></h2>
            <xsl:for-each select="section">
            <div class="section">
                <xsl:if test="header/@display != 'false' or not(header/@display)">
                <a class="anchor">
                    <xsl:attribute name="name"><xsl:value-of select="@help-key" /></xsl:attribute>
                    <xsl:attribute name="id"><xsl:value-of select="@help-key" /></xsl:attribute>
                    <span></span>
                </a>
                <h3>
                    <span><xsl:value-of select="header"/></span>
                </h3>
                </xsl:if>
                <xsl:apply-templates select="content"/>
                <xsl:for-each select="section">
                   <a><xsl:attribute name="name"><xsl:value-of select="@help-key" /></xsl:attribute><a/></a>
                   <h4><xsl:value-of select="header"/></h4>
                   <xsl:apply-templates select="content"/>
                </xsl:for-each>
            </div>
            </xsl:for-each>
         </div>
    </xsl:for-each>
</xsl:template>

<xsl:template match="content">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="*">
  <xsl:copy-of select="." />
</xsl:template>

</xsl:stylesheet>
