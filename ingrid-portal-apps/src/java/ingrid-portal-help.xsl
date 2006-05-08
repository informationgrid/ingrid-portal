<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml"/>

<xsl:template match="/">
    <help>
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
                <a><xsl:attribute name="name"><xsl:value-of select="@help-key" /></xsl:attribute><div/></a>
                <h2><xsl:value-of select="header"/></h2>
                <xsl:apply-templates select="content"/>
            </xsl:for-each>
          </div>
                        
    </xsl:for-each>
    </help>
</xsl:template>

<xsl:template match="content">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="*">
  <xsl:copy-of select="." />
</xsl:template>

</xsl:stylesheet>