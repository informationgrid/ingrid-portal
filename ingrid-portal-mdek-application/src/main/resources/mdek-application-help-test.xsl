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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

<xsl:output method="html" encoding="UTF-8" />

<xsl:template match="/help">

<html>
 <head>
		<style type="text/css">
		div.left { float:left; width:15.75em; padding:0; }
		div.right { float:left; margin-left:2.625em; width:15.75em; padding:0; }
		</style>
 </head>
 <body>

    <xsl:for-each select="chapter">
        <h1><xsl:value-of select="header"/></h1>
          <div class="left">
            <h3>Navigation</h3>
            <div class="helpcontent">
              <ul>
                <xsl:for-each select="section">
                    <li><a><xsl:attribute name="href">#<xsl:value-of select="@help-key" /></xsl:attribute>                    
                    <xsl:value-of select="header"/></a></li>
                </xsl:for-each>
              </ul>
              <br />
            </div>
          </div>
          <div class="right">
            <xsl:for-each select="section">
								<!-- The next line defines the anchor for the current section's 'help-key' -->
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

          <div style="position:relative; clear:both; height:0; line-height:0; height:0; margin:0; padding:0; white-space:nowrap;"></div>
          <hr />

    </xsl:for-each>

	</body>
	</html>
</xsl:template>



<!-- Identity template. This template copies all nodes from input to output -->
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

<!-- Exception for the identity template for nodes of type 'a'. If links start with '?hkey=' they are rewritten as internal links -->
<xsl:template match="a">
	<a>
		<xsl:attribute name="href">
			<xsl:choose>
				<xsl:when test="starts-with(@href,'?hkey=')">#<xsl:value-of select="substring(@href,7)" /></xsl:when>
				<xsl:otherwise><xsl:value-of select="@href" /></xsl:otherwise>
			</xsl:choose>
		</xsl:attribute><xsl:value-of select="." /></a>
</xsl:template>


</xsl:stylesheet>