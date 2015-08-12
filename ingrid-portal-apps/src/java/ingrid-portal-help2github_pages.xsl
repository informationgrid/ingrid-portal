<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:fn="http://www.w3.org/2005/xpath-functions">

<xsl:output method="text" indent="no"/>

<xsl:template match="/">
<xsl:text><![CDATA[---
layout: default
title: Portal Dokumentation
description: "InGrid: Indexieren, Recherchieren, Visualisieren, Teilen"
---

]]></xsl:text>

    <xsl:for-each select="//chapter">
        <xsl:text>&#xa;</xsl:text>
        <xsl:text>## </xsl:text><xsl:apply-templates select="header" />
        <xsl:for-each select="section">
			<xsl:text>&#xa;</xsl:text>
			<xsl:text><![CDATA[<a name="]]></xsl:text>
			<xsl:value-of select="@help-key"/>
			<xsl:text><![CDATA["></a>]]></xsl:text>
			<xsl:text>&#xa;</xsl:text>
			<xsl:text>&#xa;### </xsl:text><xsl:apply-templates select="header" />
            <xsl:apply-templates select="content"/>
        </xsl:for-each>
    </xsl:for-each>
</xsl:template>

<xsl:template match="content">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="section">
  <xsl:apply-templates/>
</xsl:template>


<xsl:template match="h4">
    <xsl:text>&#xa;</xsl:text>
    <xsl:text>&#xa;#### </xsl:text>
    <xsl:apply-templates/>
    <xsl:text>&#xa;</xsl:text>
</xsl:template>


<xsl:template match="header">
  <xsl:value-of select="normalize-space()" />
  <xsl:text>&#xa;</xsl:text>  
  <xsl:text>&#xa;</xsl:text>  
</xsl:template>


<xsl:template match="p">
  <xsl:text>&#xa;</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>&#xa;</xsl:text>
</xsl:template>

<!-- add spacing for inner text mark up, ignore if mark up follows p tag directly -->
<xsl:template match="p[text() != '']/b">
  <xsl:text> **</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>** </xsl:text>
</xsl:template>
<xsl:template match="p[text() != '']/i">
  <xsl:text> _</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>_ </xsl:text>
</xsl:template>

<xsl:template match="p[text() != '']/u">
  <xsl:text> **</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>** </xsl:text>
</xsl:template>

<xsl:template match="li/b">
  <xsl:text> **</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>** </xsl:text>
</xsl:template>


<!--  ignore underline inside bold -->
<xsl:template match="b/u">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="b">
  <xsl:text>**</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>**</xsl:text>
</xsl:template>

<!--  ignore italic inside code tags -->
<xsl:template match="code/i">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="i">
  <xsl:text>_</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>_</xsl:text>
</xsl:template>

<xsl:template match="u">
  <xsl:text>**</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>**</xsl:text>
</xsl:template>


<xsl:template match="code">
  <xsl:text>&#xa;{% highlight text %}&#xa;</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>&#xa;{% endhighlight %}&#xa;</xsl:text>
</xsl:template>

<xsl:template match="ul">
  <xsl:text>&#xa;</xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="ol">
  <xsl:text>&#xa;</xsl:text>
  <xsl:apply-templates/>
</xsl:template>


<xsl:template match="br">
  <xsl:text>&#xa;</xsl:text>
</xsl:template>

<xsl:template match="li">
  <xsl:text>* </xsl:text>
  <xsl:apply-templates/>
  <xsl:text>&#xa;</xsl:text>
</xsl:template>

<xsl:template match="a[starts-with(@href,'?hkey=')]">
  <xsl:text> [</xsl:text><xsl:value-of select="normalize-space()" /><xsl:text>]</xsl:text>
  <xsl:text>(#</xsl:text><xsl:value-of select="substring-after(@href, '?hkey=')" /><xsl:text>) </xsl:text>
</xsl:template>

<xsl:template match="a">
  <xsl:text> [</xsl:text><xsl:value-of select="normalize-space()" /><xsl:text>]</xsl:text>
  <xsl:text>(</xsl:text><xsl:value-of select="@href" /><xsl:text>) </xsl:text>
</xsl:template>

<xsl:template match="img[starts-with(@src,'../')]">
  <xsl:text> ![</xsl:text>
  <xsl:if test="@title"><xsl:text>[</xsl:text><xsl:value-of select="@alt" /></xsl:if><xsl:text>]</xsl:text>
  <xsl:text>(https://dev.informationgrid.eu/</xsl:text><xsl:value-of select="substring-after(@src, '../')" />
  <xsl:if test="@title"><xsl:text> "</xsl:text><xsl:value-of select="@title" /><xsl:text>"</xsl:text></xsl:if>
  <xsl:text>) </xsl:text>
</xsl:template>

<xsl:template match="node()">
  <xsl:call-template name="string-replace">
        <xsl:with-param name="string" select="normalize-space()" />
        <xsl:with-param name="replace" select="'|'" />
        <xsl:with-param name="with" select="'&amp;#124;'" />
  </xsl:call-template>
</xsl:template>


<xsl:template name="string-replace">
    <xsl:param name="string" />
    <xsl:param name="replace" />
    <xsl:param name="with" />

    <xsl:choose>
        <xsl:when test="contains($string, $replace)">
            <xsl:value-of select="substring-before($string, $replace)" />
            <xsl:value-of select="$with" />
            <xsl:call-template name="string-replace">
                <xsl:with-param name="string" select="substring-after($string,$replace)" />
                <xsl:with-param name="replace" select="$replace" />
                <xsl:with-param name="with" select="$with" />
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$string" />
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


</xsl:stylesheet>

