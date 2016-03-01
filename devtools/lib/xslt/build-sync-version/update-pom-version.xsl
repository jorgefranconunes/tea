<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (c) 2016 PDMFC, All Rights Reserved.

XSLT for replacing the artifact version and parent version in a POM file.
-->

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:t="http://maven.apache.org/POM/4.0.0">

  <!-- These parameters must be set by the tool running the XSLT
       transformation. -->
  <xsl:param name="NEW_POM_VERSION"/>
  <xsl:param name="NEW_PARENT_VERSION"/>

  <xsl:output method="xml" encoding="UTF-8" />
  
  <!-- This is an identity template. It copies everything that does
       not match another template -->
  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  
  <!-- Replace the project version with the value of the
       NEW_POM_VERSION parameter. -->
  <xsl:template match="/t:project/t:version/text()"><xsl:value-of select="$NEW_POM_VERSION"/></xsl:template>

  <!-- Replace the project parent version with the value of the
       NEW_PARENT_VERSION parameter. -->
  <xsl:template match="/t:project/t:parent/t:version/text()">
    <xsl:choose>
      <xsl:when test="$NEW_PARENT_VERSION!=''"><xsl:value-of select="$NEW_PARENT_VERSION"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
    
</xsl:stylesheet>
