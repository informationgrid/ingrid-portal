<?xml version="1.0" encoding="UTF-8"?>
<!--
  **************************************************-
  InGrid Portal Apps
  ==================================================
  Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
    <help>
    <xsl:for-each select="chapter">
        <div class="block">
            <aside class="sidebar js-accordion-multi">
                 <div class="sidebar__title js-accordion-toggle mq-show-l">
                    Navigation
                    <svg class="icon"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#filter"></use></svg>
                    <svg class="icon"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#cross"></use></svg>
                </div>
                <div class="sidebar__content js-accordion-content is-hidden">
                    <form>
                        <div>
                            <xsl:for-each select="section">
                                <a class="sidebar__widget__title filter__title js-accordion-toggle">
                                    <xsl:attribute name="href">#<xsl:value-of select="@help-key" /></xsl:attribute>
                                    <xsl:value-of select="header"/>
                                </a>
                            </xsl:for-each>
                        </div>
                    </form>
                </div>
              </aside>
              <article class="content ob-container ob-box-narrow">
                <h1><xsl:value-of select="header"/></h1>
                <xsl:for-each select="section">
                    <a><xsl:attribute name="name"><xsl:value-of select="@help-key" /></xsl:attribute><a/></a>
                    <xsl:if test="header/@display != 'false' or not(header/@display)">
                        <h2><xsl:value-of select="header"/></h2>
                    </xsl:if>
                    <xsl:apply-templates select="content"/>
                    <xsl:for-each select="section">
                        <a><xsl:attribute name="name"><xsl:value-of select="@help-key" /></xsl:attribute><a/></a>
                        <h3><xsl:value-of select="header"/></h3>
                        <xsl:apply-templates select="content"/>
                    </xsl:for-each>
                </xsl:for-each>
                <div class="sidebar-toggle mq-hide-l mq-show-xb js-sidebar-toggle">
                    <svg class="icon">
                        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#arrow-thick"/>
                    </svg>
                </div>
              </article>
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
