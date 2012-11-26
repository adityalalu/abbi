<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    wadl.xsl (07-Sep-2012)
    
    Transforms Web Application Description Language (WADL) XML documents into HTML.

    Mark Sawers <mark.sawers@ipc.com>
    
    See example_wadl.xml at http://github.com/ipcsystems/wadl-stylesheet to explore this stylesheet's capabilities
    and the README.txt for other usage information.
    Note that the contents of a doc element is rendered as a:
        * hyperlink if the title attribute contains is equal to 'Example'
        * mono-spaced font ('pre' tag) if content contains the text 'Example'
			
    Limitations:
        * Ignores globally defined methods, referred to from a resource using a method reference element.
          Methods must be embedded in a resource element.
        * Ditto for globally defined representations. Representations must be embedded within request
          and response elements.
        * Ignores type and queryType attributes of resource element.
        * Ignores resource_type element.
        * Ignores profile attribute of representation element.
        * Ignores path attribute and child link elements of param element.

    Copyright (c) 2012 IPC Systems, Inc.

    Parts of this work are adapted from Mark Notingham's wadl_documentation.xsl, at
        https://github.com/mnot/wadl_stylesheets.
    
    This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 License.
    To view a copy of this license, visit 
        http://creativecommons.org/licenses/by-sa/3.0/
    or send a letter to 
        Creative Commons
        543 Howard Street, 5th Floor
        San Francisco, California, 94105, USA
        
    Portions Copyright (c) Keith W. Boone
    Parts of this work have been adapted to fix formatting, and address limitations:
    
        * The resource_type element is not ignored.
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0" xmlns:wadl="http://wadl.dev.java.net/2009/02"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xsl wadl xs"
>

  <xsl:output omit-xml-declaration="yes"  />
  <!-- Global variables -->
  <xsl:variable name="g_resourcesBase"
    select="wadl:application/wadl:resources/@base"/>
  <!-- Template for top-level doc element -->
  <xsl:template match="wadl:application">
    <html>
      <head>
        <meta charset="UTF-8"/>
        <xsl:call-template name="getStyle"/>
        <title>
          <xsl:call-template name="getTitle"/>
        </title>
      </head>
      <body>
        <button class="optionalresource"
          onclick="hideShowOptions('none')">Hide Optional Resources and
          Methods</button>
        <button class="showoptionalresource"
          onclick="hideShowOptions('')">Show Optional Resources and
          Methods</button>
        <script>
          function hideShowOptions(show)
          {
            var hide = show == "none" ? "" : "none";
            document.styleSheets[0].rules[0].style.display = show;
            document.styleSheets[0].rules[1].style.display = show;
            document.styleSheets[0].rules[2].style.display = hide;
          }
        </script>
        <h1>
          <xsl:call-template name="getTitle"/>
        </h1>
        <xsl:apply-templates select="wadl:doc"/>

        <!-- Summary -->
        <h2 id="Summary">Summary</h2>
        <table>
          <tr>
            <th style="width: 12em;">Resource<br/><span
                style="float:right">Method</span></th>
            <th>Conformance</th>
            <th>Description</th>
          </tr>
          <xsl:for-each
            select="wadl:resources/wadl:resource|wadl:resource_type">
            <xsl:call-template name="processResourceSummary">
              <xsl:with-param name="resourceBase"
                select="$g_resourcesBase"/>
              <xsl:with-param name="resourcePath" select="@path"/>
              <xsl:with-param name="lastResource"
                select="position() = last()"/>
            </xsl:call-template>
          </xsl:for-each>
        </table>
        <p/>

        <!-- Grammars -->
        <xsl:if test="wadl:grammars/wadl:include">
          <h2 id="Grammars">Grammars</h2>
          <p>
            <xsl:for-each select="wadl:grammars/wadl:include">
              <xsl:variable name="href" select="@href"/>
              <a href="{$href}">
                <xsl:value-of select="$href"/>
              </a>
              <xsl:if test="position() != last()">
                <br/>
              </xsl:if>
              <!-- Add a spacer -->
            </xsl:for-each>
          </p>
        </xsl:if>

        <!-- Detail -->
        <h2 id="Resources">Resources</h2>
        <xsl:for-each select="wadl:resources">
          <xsl:apply-templates select="wadl:doc"/>
        </xsl:for-each>

        <xsl:for-each
          select="wadl:resources/wadl:resource|wadl:resource_type">
          <xsl:call-template name="processResourceDetail">
            <xsl:with-param name="resourceBase"
              select="$g_resourcesBase"/>
            <xsl:with-param name="resourcePath" select="@path"/>
          </xsl:call-template>
        </xsl:for-each>
      </body>
    </html>
  </xsl:template>

  <!-- Supporting templates (functions) -->

  <xsl:template name="processResourceSummary">
    <xsl:param name="resourceBase"/>
    <xsl:param name="resourcePath"/>
    <xsl:param name="lastResource"/>

    <xsl:if test="wadl:method">
      <tbody>
        <xsl:attribute name="class">
          <xsl:if
            test="wadl:doc[@title='Conformance'][contains('OPTIONAL',string(.))]"
            >optional</xsl:if>
          <xsl:text>resource</xsl:text>
        </xsl:attribute>
        <tr>
          <!-- Resource -->
          <td class="summary">
            <xsl:variable name="id">
              <xsl:call-template name="getId"/>
            </xsl:variable>
            <a href="#{$id}">
              <xsl:choose>
                <xsl:when test="self::wadl:resource_type">
                  <i>
                    <xsl:value-of select="@id"/>
                  </i>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:call-template name="getFullResourcePath">
                    <xsl:with-param name="base"
                      select="substring-after($resourceBase,//wadl:resources/@base)"/>
                    <xsl:with-param name="path" select="$resourcePath"/>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
            </a>
          </td>
          <!-- Method -->
          <td class="summary">
            <xsl:value-of select="wadl:doc[@title='Conformance']"/>
          </td>
          <td class="summary">
            <xsl:apply-templates select="wadl:doc[1]/html:p[1]"/>
          </td>
        </tr>
        <xsl:for-each select="wadl:method">
          <tr>
            <xsl:attribute name="class">
              <xsl:if
                test="wadl:doc[@title='Conformance'][contains('OPTIONAL',string(.))]"
                >optional</xsl:if>
              <xsl:text>method1</xsl:text>
            </xsl:attribute>
            <xsl:variable name="name" select="@name"/>
            <xsl:variable name="id2">
              <xsl:call-template name="getId"/>
            </xsl:variable>
            <td style="text-align: right">
              <a href="#{$id2}">
                <xsl:value-of select="$name"/>
              </a>
            </td>
            <td>
              <xsl:value-of select="wadl:doc[@title='Conformance']"/>
            </td>
            <td>
              <xsl:apply-templates select="wadl:doc[1]/html:p[1]"/>
            </td>
          </tr>
          <!-- Add separator if not the last resource -->
          <xsl:if test="wadl:method and not($lastResource)">
            <tr>
              <td class="summarySeparator"/>
              <td class="summarySeparator"/>
              <td class="summarySeparator"/>
            </tr>
          </xsl:if>
        </xsl:for-each>
        <!-- wadl:method -->
      </tbody>
    </xsl:if>

    <!-- Call recursively for child resources -->
    <xsl:for-each select="wadl:resource">
      <xsl:variable name="base">
        <xsl:call-template name="getFullResourcePath">
          <xsl:with-param name="base" select="$resourceBase"/>
          <xsl:with-param name="path" select="$resourcePath"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:call-template name="processResourceSummary">
        <xsl:with-param name="resourceBase" select="$base"/>
        <xsl:with-param name="resourcePath" select="@path"/>
        <xsl:with-param name="lastResource"
          select="$lastResource and position() = last()"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="processResourceDetail">
    <xsl:param name="resourceBase"/>
    <xsl:param name="resourcePath"/>

    <xsl:if test="wadl:method">
      <xsl:variable name="id">
        <xsl:call-template name="getId"/>
      </xsl:variable>
      <xsl:variable name="full-path">
        <xsl:call-template name="getFullResourcePath">
          <xsl:with-param name="base" select="$resourceBase"/>
          <xsl:with-param name="path" select="$resourcePath"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="local-path">
        <xsl:choose>
          <xsl:when test="self::wadl:resource_type">
            <i><xsl:value-of select="@id"/></i>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of
              select="substring-after($full-path,//wadl:resources/@base)"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <div>
        <xsl:attribute name="class">
          <xsl:if
            test="wadl:doc[@title='Conformance'][contains('OPTIONAL',string(.))]"
            >optional</xsl:if>
          <xsl:text>resource</xsl:text>
        </xsl:attribute>
        <h3>
          <a name="{$id}">
            <xsl:copy-of select="$local-path"/>
          </a>
        </h3>
        <xsl:apply-templates select="wadl:doc"/>

        <h5>Methods</h5>

        <div class="methods">
          <xsl:for-each select="wadl:method">
            <div class="method">
              <xsl:attribute name="class">
                <xsl:text>method </xsl:text>
                <xsl:if
                  test="wadl:doc[@title='Conformance'][contains('OPTIONAL',string(.))]"
                  >optional</xsl:if>
                <xsl:text>method1</xsl:text>
              </xsl:attribute>
              <table class="methodNameTable">
                <tr>
                  <td class="methodNameTd" style="font-weight: bold">
                    <xsl:variable name="name" select="@name"/>
                    <xsl:variable name="id2">
                      <xsl:call-template name="getId"/>
                    </xsl:variable>
                    <a name="{$id2}">
                      <xsl:value-of select="$name"/>
                    </a>
                    <xsl:text> </xsl:text>
                    <xsl:copy-of select="$local-path"/>
                  </td>
                  <td class="methodNameTd" style="text-align: right">
                    <xsl:if test="@id">
                      <xsl:value-of select="@id"/>() </xsl:if>
                  </td>
                </tr>
              </table>
              <xsl:apply-templates select="wadl:doc"/>

              <!-- Request -->
              <h6>request</h6>
              <div style="margin-left: 2em">
                <!-- left indent -->
                <xsl:choose>
                  <xsl:when test="wadl:request">
                    <xsl:for-each select="wadl:request">
                      <xsl:apply-templates select="wadl:doc"/>
                      <xsl:call-template name="getParamBlock">
                        <xsl:with-param name="style" select="'template'"
                        />
                      </xsl:call-template>

                      <xsl:call-template name="getParamBlock">
                        <xsl:with-param name="style" select="'matrix'"/>
                      </xsl:call-template>

                      <xsl:call-template name="getParamBlock">
                        <xsl:with-param name="style" select="'header'"/>
                      </xsl:call-template>

                      <xsl:call-template name="getParamBlock">
                        <xsl:with-param name="style" select="'query'"/>
                      </xsl:call-template>

                      <xsl:call-template name="getRepresentations"/>
                    </xsl:for-each>
                    <!-- wadl:request -->
                  </xsl:when>

                  <xsl:when
                    test="not(wadl:request) and (ancestor::wadl:*/wadl:param)">
                    <xsl:call-template name="getParamBlock">
                      <xsl:with-param name="style" select="'template'"/>
                    </xsl:call-template>

                    <xsl:call-template name="getParamBlock">
                      <xsl:with-param name="style" select="'matrix'"/>
                    </xsl:call-template>

                    <xsl:call-template name="getParamBlock">
                      <xsl:with-param name="style" select="'header'"/>
                    </xsl:call-template>

                    <xsl:call-template name="getParamBlock">
                      <xsl:with-param name="style" select="'query'"/>
                    </xsl:call-template>

                    <xsl:call-template name="getRepresentations"/>
                  </xsl:when>

                  <xsl:otherwise> unspecified </xsl:otherwise>
                </xsl:choose>
              </div>
              <!-- left indent for request -->

              <!-- Response -->
              <h6>responses</h6>
              <div style="margin-left: 2em">
                <!-- left indent -->
                <xsl:choose>
                  <xsl:when test="wadl:response">
                    <xsl:for-each select="wadl:response">
                      <div class="h8">
                        <xsl:choose>
                          <xsl:when test="@status">
                            <xsl:value-of select="@status"/>
                          </xsl:when>
                          <xsl:otherwise> 200 - OK </xsl:otherwise>
                        </xsl:choose>
                      </div>
                      <xsl:apply-templates select="wadl:doc"/>
                      <!-- Get response headers/representations -->
                      <xsl:if test="wadl:param or wadl:representation">
                        <div style="margin-left: 2em">
                          <!-- left indent -->
                          <xsl:if test="wadl:param/@style = 'header'">
                            <div class="h7">headers</div>
                            <table>
                              <col style="width: 10em"/>
                              <col style="width: 15em"/>
                              <xsl:for-each
                                select="wadl:param[@style='header']">
                                <xsl:call-template name="getParams"/>
                              </xsl:for-each>
                            </table>
                          </xsl:if>
                          <xsl:if test="wadl:param/@style = 'query'">
                            <div class="h7">query parameters</div>
                            <table>
                              <col style="width: 10em"/>
                              <col style="width: 15em"/>
                              <xsl:for-each
                                select="wadl:param[@style='query']">
                                <xsl:call-template name="getParams"/>
                              </xsl:for-each>
                            </table>
                          </xsl:if>
                          <xsl:if test="wadl:param/@style = 'plain'">
                            <div class="h7">parameters</div>
                            <table>
                              <col style="width: 10em"/>
                              <col style="width: 15em"/>
                              <xsl:for-each
                                select="wadl:param[@style='plain']">
                                <xsl:call-template name="getParams"/>
                              </xsl:for-each>
                            </table>
                          </xsl:if>

                          <xsl:call-template name="getRepresentations"/>
                        </div>
                        <!-- left indent for response headers/representations -->
                      </xsl:if>
                    </xsl:for-each>
                    <!-- wadl:response -->
                  </xsl:when>
                  <xsl:otherwise> unspecified </xsl:otherwise>
                </xsl:choose>
              </div>
              <!-- left indent for responses -->

            </div>
            <!-- class=method -->
          </xsl:for-each>
          <!-- wadl:method  -->
        </div>
        <!-- class=methods -->
      </div>
    </xsl:if>
    <!-- wadl:method -->
    <!-- Call recursively for child resources -->
    <xsl:for-each select="wadl:resource">
      <xsl:variable name="base">
        <xsl:call-template name="getFullResourcePath">
          <xsl:with-param name="base" select="$resourceBase"/>
          <xsl:with-param name="path" select="$resourcePath"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:call-template name="processResourceDetail">
        <xsl:with-param name="resourceBase" select="$base"/>
        <xsl:with-param name="resourcePath" select="@path"/>
      </xsl:call-template>
    </xsl:for-each>
    <!-- wadl:resource -->
  </xsl:template>

  <xsl:template name="getFullResourcePath">
    <xsl:param name="base"/>
    <xsl:param name="path"/>
    <xsl:choose>
      <xsl:when test="substring($base, string-length($base)) = '/'">
        <xsl:value-of select="$base"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat($base, '/')"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:choose>
      <xsl:when test="starts-with($path, '/')">
        <xsl:value-of select="substring($path, 2)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$path"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="getDoc">
    <xsl:param name="base"/>
    <xsl:for-each select="wadl:doc">
      <xsl:if test="position() > 1">
        <br/>
      </xsl:if>
      <xsl:if test="@title and local-name(..) != 'application'">
        <xsl:value-of select="@title"/>: </xsl:if>
      <xsl:variable name="content" select="."/>
      <xsl:choose>
        <xsl:when test="@title = 'Example'">
          <xsl:variable name="url">
            <xsl:choose>
              <xsl:when test="string-length($base) > 0">
                <xsl:call-template name="getFullResourcePath">
                  <xsl:with-param name="base" select="$base"/>
                  <xsl:with-param name="path" select="text()"/>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="text()"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <a href="{$url}">
            <xsl:value-of select="$url"/>
          </a>
        </xsl:when>
        <xsl:when test="contains($content, 'Example')">
          <div style="white-space:pre-wrap">
            <pre><xsl:value-of select="."/></pre>
          </div>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$content"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="getId">
    <xsl:choose>
      <xsl:when test="@id">
        <xsl:value-of select="@id"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="generate-id()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="getParamBlock">
    <xsl:param name="style"/>
    <xsl:if test="ancestor-or-self::wadl:*/wadl:param[@style=$style]">
      <div class="h7"><xsl:value-of select="$style"/> params</div>
      <table>
        <col style="width: 10em"/>
        <col style="width: 15em"/>
        <xsl:for-each
          select="ancestor-or-self::wadl:*/wadl:param[@style=$style]">
          <xsl:call-template name="getParams"/>
        </xsl:for-each>
      </table>
      <p/>
    </xsl:if>
  </xsl:template>

  <xsl:template name="getParams">
    <tr>
      <td>
        <xsl:if test="wadl:option">
          <xsl:attribute name="rowspan">
            <xsl:value-of select="count(wadl:option)+2"/>
          </xsl:attribute>
        </xsl:if>
        <span style="font-weight: bold">
          <xsl:value-of select="@name"/>
        </span>
        <br/>
        <xsl:if test="not(@type) and not(@fixed)"> unspecified type </xsl:if>
        <xsl:call-template name="getHyperlinkedElement">
          <xsl:with-param name="qname" select="@type"/>
        </xsl:call-template>
        <xsl:if test="@required = 'true'"><br/>(required)</xsl:if>
        <xsl:if test="@repeating = 'true'"><br/>(repeating)</xsl:if>
        <xsl:if test="@default"><br/>default: <tt><xsl:value-of
              select="@default"/></tt></xsl:if>
        <xsl:if test="@type and @fixed">
          <br/>
        </xsl:if>
        <xsl:if test="@fixed">fixed: <tt><xsl:value-of select="@fixed"
            /></tt></xsl:if>
      </td>
      <xsl:if test="wadl:doc">
        <td colspan="2">
          <xsl:apply-templates select="wadl:doc"/>
        </td>
      </xsl:if>
    </tr>
    <xsl:if test="wadl:option">
      <tr>
        <td>
          <b>Values:</b>
          <xsl:if test="wadl:option/@mediaType">
            <br/>
            <tt>
              <b>(Response Type)</b>
            </tt>
          </xsl:if>
        </td>
        <td>
          <b>Description</b>
        </td>
      </tr>
      <xsl:for-each select="wadl:option">
        <tr>
          <td>
            <tt>
              <xsl:value-of select="@value"/>
              <xsl:if test="@mediaType">
                <br/>(<xsl:value-of select="@mediaType"/>)</xsl:if>
            </tt>
          </td>
          <td>
            <xsl:apply-templates select="wadl:doc"/>
          </td>
        </tr>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template name="getHyperlinkedElement">
    <xsl:param name="qname"/>
    <xsl:variable name="prefix" select="substring-before($qname,':')"/>
    <xsl:variable name="ns-uri" select="./namespace::*[name()=$prefix]"/>
    <xsl:variable name="localname" select="substring-after($qname, ':')"/>
    <xsl:choose>
      <xsl:when
        test="$ns-uri='http://www.w3.org/2001/XMLSchema' or $ns-uri='http://www.w3.org/2001/XMLSchema-instance'">
        <a href="http://www.w3.org/TR/xmlschema-2/#{$localname}">
          <xsl:value-of select="$localname"/>
        </a>
      </xsl:when>
      <xsl:when
        test="$ns-uri and starts-with($ns-uri, 'http://www.w3.org/XML/') = false">
        <a href="{$ns-uri}#{$localname}">
          <xsl:value-of select="$localname"/>
        </a>
      </xsl:when>
      <xsl:when test="$qname">
        <xsl:value-of select="$qname"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="wadl:representation">
    <xsl:choose>
      <xsl:when test="@href">
        <xsl:apply-templates select="//wadl:representation[@id = substring-after(current()/@href,'#')]"/>
      </xsl:when>
      <xsl:otherwise>
        <tr>
          <td style="font-weight: bold">
            <xsl:value-of select="@mediaType"/>
            <xsl:choose>
              <xsl:when test="@href or @element">
                <xsl:variable name="href" select="@href"/>
                <xsl:choose>
                  <xsl:when test="@href">
                    <xsl:variable name="localname"
                      select="substring-after(@element, ':')"/>
                    <a href="{$href}">
                      <xsl:value-of select="$localname"/>
                    </a>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="getHyperlinkedElement">
                      <xsl:with-param name="qname" select="@element"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:when>
              <xsl:otherwise> </xsl:otherwise>
            </xsl:choose>
          </td>
          
          <xsl:if test="wadl:doc">
            <td colspan="2">
              <xsl:apply-templates select="wadl:doc"/>
            </td>
          </xsl:if>
        </tr>
        <xsl:call-template name="getRepresentationParamBlock">
          <xsl:with-param name="style" select="'template'"/>
        </xsl:call-template>
        
        <xsl:call-template name="getRepresentationParamBlock">
          <xsl:with-param name="style" select="'matrix'"/>
        </xsl:call-template>
        
        <xsl:call-template name="getRepresentationParamBlock">
          <xsl:with-param name="style" select="'header'"/>
        </xsl:call-template>
        
        <xsl:call-template name="getRepresentationParamBlock">
          <xsl:with-param name="style" select="'query'"/>
        </xsl:call-template>
        
        <xsl:call-template name="getRepresentationParamBlock">
          <xsl:with-param name="style" select="'plain'"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="getRepresentations">
    <xsl:if test="wadl:representation">
      <div class="h7">representations</div>
      <table>
        <xsl:apply-templates select="wadl:representation"/>
      </table>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="getRepresentationParamBlock">
    <xsl:param name="style"/>
    <xsl:if test="wadl:param[@style=$style]">
      <tr>
        <td style="padding: 0em, 0em, 0em, 2em" colspan="2">
          <div class="h7"><xsl:value-of select="$style"/> params</div>
          <table>
            <col style="width: 10em"/>
            <col style="width: 15em"/>
            <xsl:for-each select="wadl:param[@style=$style]">
              <xsl:call-template name="getParams"/>
            </xsl:for-each>
          </table>
          <p/>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template name="getStyle">
    <style type="text/css">
      .optionalresource {
      }
      .optionalmethod1 {
      }
      .showoptionalresource {
        display: none;
      }
      body {
        font-family: sans-serif;
        font-size: 0.85em;
        margin: 2em 2em;
      }
      .methods {
        margin-left: 2em;
        margin-bottom: 2em;
      }
      .method {
        background-color: #eef;
        border: 1px solid #DDDDE6;
        padding: .5em;
        margin-bottom: 1em;
      /* width: 50em */
      }
      .methodNameTable {
        width: 100%;
        border: 0px;
        border-bottom: 2px solid white;
        font-size: 1.4em;
      }
      .methodNameTd {
        background-color: #eef;
      }
      h1 {
        font-size: 2m;
        margin-bottom: 0em;
      }
      h2 {
        border-bottom: 1px solid black;
        margin-top: 1.5em;
        margin-bottom: 0.5em;
        font-size: 1.5em;
      }
      h3 {
        color: #FF6633;
        font-size: 1.35em;
        margin-top: .5em;
        margin-bottom: 0em;
      }
      h5 {
        font-size: 1.2em;
        color: #99a;
        margin: 0.5em 0em 0.25em 0em;
      }
      h6 {
        color: #700000;
        font-size: 1em;
        margin: 1em 0em 0em 0em;
      }
      .h7 {
        margin-top: .75em;
        font-size: 1em;
        font-weight: bold;
        font-style: italic;
        color: blue;
      }
      .h8 {
        margin-top: .75em;
        font-size: 1em;
        font-weight: bold;
        font-style: italic;
        color: black;
      }
      tt {
        font-size: 1em;
      }
      table {
        margin-bottom: 0.5em;
        border: 1px solid #E0E0E0;
        width: 100%;
      }
      th {
        text-align: left;
        font-weight: normal;
        font-size: 1em;
        color: black;
        background-color: #DDDDE6;
        padding: 3px 6px;
        border: 1px solid #B1B1B8;
      }
      td {
        padding: 3px 6px;
        vertical-align: top;
        background-color: #F6F6FF;
        font-size: 0.85em;
      }
      p {
        margin-top: 1.2em;
        margin-bottom: 0em;
      }
      td.summary {
        background-color: white;
      }
      td.summarySeparator {
        padding: 1px;
      }</style>
  </xsl:template>

  <xsl:template name="getTitle">
    <xsl:choose>
      <xsl:when test="wadl:doc/@title">
        <xsl:value-of select="wadl:doc/@title"/>
      </xsl:when>
      <xsl:otherwise> Web Application </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="wadl:doc">
    <xsl:if test="@title">
      <br/>
      <b><xsl:value-of select="@title"/>: </b>
      <br/>
    </xsl:if>
    <xsl:apply-templates select="text()|html:*" mode="doc"/>
  </xsl:template>

  <xsl:template match="html:*" mode="doc">
    <xsl:element name="{local-name()}" namespace="{namespace-uri()}">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="text()|html:*" mode="doc"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="text()" name="text" mode="doc">
    <xsl:param name="text" select="."/>
    <xsl:choose>
      <xsl:when test="contains($text, '[path]')">
        <xsl:value-of select="substring-before($text, '[path]')"/>
        <xsl:apply-templates select="ancestor::wadl:resource"
          mode="path"/>
        <xsl:call-template name="text">
          <xsl:with-param name="text"
            select="substring-after($text, '[path]')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="wadl:resource" mode="path">
    <xsl:text>/</xsl:text>
    <xsl:value-of select="@path"/>
  </xsl:template>
</xsl:stylesheet>
