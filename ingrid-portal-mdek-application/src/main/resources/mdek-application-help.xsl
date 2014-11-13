<?xml version="1.0" encoding="UTF-8"?>
<!--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
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

<xsl:output method="html" encoding="UTF-8" />

<xsl:template match="/">
    <div>
    <xsl:for-each select="chapter">
        <h1><xsl:value-of select="header"/></h1>
          <div class="left">
            <h2>Navigation</h2>
            <div class="helpcontent">
              <ul>
                <xsl:for-each select="section">
                    <li><a><xsl:attribute name="href">#<xsl:value-of select="@help-key" /></xsl:attribute>                    
                    <xsl:value-of select="header"/></a></li>
                </xsl:for-each>
                <!-- <li><a href="javascript:history.back()"><xsl:value-of select="../back"/></a></li> -->
              </ul>
              <br />
            </div>
          </div>
          <div class="right">
            <xsl:for-each select="section">
                <a><xsl:attribute name="name"><xsl:value-of select="@help-key" /></xsl:attribute><div/></a>
                <h2><xsl:value-of select="header"/></h2>
                <xsl:apply-templates select="content"/>
                <xsl:for-each select="section">
                    <a><xsl:attribute name="name"><xsl:value-of select="@help-key" /></xsl:attribute><div/></a>
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