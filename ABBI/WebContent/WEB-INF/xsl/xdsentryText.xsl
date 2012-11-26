<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:fhir="http://hl7.org/fhir"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="fhir xhtml xsl"
>
  <xsl:output indent="yes" omit-xml-declaration="yes"/>
  <xsl:template match="fhir:XdsEntry">
    <xsl:copy>
      <xsl:copy-of select="*" />
      <text xmlns="http://hl7.org/fhir">
        <status>generated</status>
        <div xmlns="http://www.w3.org/1999/xhtml">
          <table cols="2">
            <xsl:apply-templates select="*"/>
          </table>
        </div>
      </text>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="*" >
    <xsl:variable name="name">
      <xsl:choose>
        <xsl:when test="self::fhir:contact">
          <xsl:value-of select="./fhir:system"/>
        </xsl:when>
        <xsl:otherwise><xsl:value-of select="local-name()"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <tr>
    <th align="right">
      <xsl:value-of select="translate(substring($name,1,1),
        'abcdefghijklmnopqrstuvwzyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
      <xsl:value-of select="substring($name,2)"/>
      <xsl:text>:</xsl:text>
    </th>
    <td>
      <xsl:choose>
        <xsl:when test="self::fhir:contact">
          <xsl:value-of select="fhir:value"/>
        </xsl:when>
        <xsl:when test="./fhir:code">
          <xsl:value-of select="./fhir:display"></xsl:value-of>
          <xsl:text> (</xsl:text>
          <xsl:value-of select="./fhir:code"/>
          <xsl:text>)</xsl:text>
        </xsl:when>
        <xsl:when test="./fhir:start|./fhir:stop">
          <xsl:value-of select="./fhir:start"/>
          <xsl:if test="./fhir:stop"><xsl:text> - </xsl:text></xsl:if>
          <xsl:value-of select="./fhir:stop"/>
        </xsl:when>
        <xsl:when test="./fhir:name/fhir:part">
          <xsl:for-each select="./fhir:name/fhir:part">
            <xsl:value-of select="./fhir:value"/>
            <xsl:text> </xsl:text>
          </xsl:for-each>
          <xsl:if test="./fhir:id">
            <xsl:text>(</xsl:text>
            <xsl:value-of select="./fhir:id/fhir:id"/>
            <xsl:text>)</xsl:text>
          </xsl:if>
        </xsl:when>
        <xsl:when test="./fhir:id">
          <xsl:value-of select="./fhir:id"/>
        </xsl:when>
        <xsl:when test="self::fhir:attachment">
          <xsl:value-of select="./fhir:url"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </td>
    </tr>
    <xsl:if test="self::fhir:author/*[not(self::fhir:id|self::fhir:name)]">
      <tr><td>&#xA0;</td>
        <td>
          <table rows="2">
            <xsl:apply-templates select="./*[not(self::fhir:id|self::fhir:name)]"/>
          </table>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>