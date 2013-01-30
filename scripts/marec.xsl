<!--
    Author: Sarven Capadisli <info@csarven.ca>
    Author URI: http://csarven.ca/#i

    Description: XSLT for MAREC data
-->
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:fn="http://270a.info/xpath-function/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:foaf="http://xmlns.com/foaf/0.1/"
    xmlns:prov="http://www.w3.org/ns/prov#"
    xmlns:skos="http://www.w3.org/2004/02/skos/core#"
    xmlns:xkos="http://purl.org/linked-data/xkos#"
    xmlns:pso="http://www.patexpert.org/ontologies/pso.owl#"
    xmlns:pulo="http://www.patexpert.org/ontologies/pulo.owl#"
    xmlns:sumo="http://www.owl-ontologies.com/sumo.owl#"
    xmlns:pmo="http://www.patexpert.org/ontologies/pmo.owl#"
    xmlns:property="http://example.org/property/"

    exclude-result-prefixes="xsl fn"
    >

<!--    xpath-default-namespace=""-->

<!--
TODO: ignore DTD check. saxonb-xslt breaks if offline
-->
<!--
TODO:
fp vocab
-->


    <xsl:import href="common.xsl"/>

    <xsl:output encoding="utf-8" indent="yes" method="xml" omit-xml-declaration="no"/>

    <xsl:param name="xmlDocument"/>

    <xsl:strip-space elements="*"/>

    <xsl:template match="/patent-document">
        <rdf:RDF>
            <rdf:Description rdf:about="{$creator}">
                <rdf:type rdf:resource="{$prov}Agent"/>
            </rdf:Description>

<!--<patent-document ucid="EP-1000000-A1" country="EP" doc-number="1000000" kind="A1" lang="EN" family-id="19768124" status="new" date-produced="20090516" date="20000517">-->

            <xsl:variable name="ucid" select="@ucid"/>
<!--            <xsl:variable name="country" select="@country"/>-->
<!--            <xsl:variable name="doc-number" select="@doc-number"/>-->
<!--            <xsl:variable name="kind" select="@kind"/>-->
<!--            <xsl:variable name="lang" select="@lang"/>-->
<!--            <xsl:variable name="family-id" select="@family-id"/>-->
<!--            <xsl:variable name="status" select="@status"/>-->
<!--            <xsl:variable name="date-produced" select="@date-produced"/>-->
<!--            <xsl:variable name="date" select="@date"/>-->

            <xsl:variable name="patentURI">
                <xsl:value-of select="$patent"/>
                <xsl:value-of select="$ucid"/>
            </xsl:variable>

            <xsl:call-template name="provActivity">
                <xsl:with-param name="provUsedA" select="resolve-uri(tokenize($xmlDocument, '/')[last()], $xmlDocumentBaseURI)"/>
                <xsl:with-param name="provGenerated" select="$patentURI"/>
            </xsl:call-template>

            <rdf:Description rdf:about="{$patentURI}">
                <rdf:type rdf:resource="{$sumo}Patent"/>

                <xsl:call-template name="generalParameterEntities"/>

                <xsl:call-template name="family-id">
                    <xsl:with-param name="ucid" select="$ucid" tunnel="yes"/>
                </xsl:call-template>
            </rdf:Description>

            <xsl:call-template name="bibliographic-data">
                <xsl:with-param name="ucid" select="$ucid" tunnel="yes"/>
            </xsl:call-template>
<!--            <xsl:apply-templates select="description"/>-->
<!--            <xsl:call-template name="claims">-->
<!--                <xsl:with-param name="patentURI" select="$patentURI" tunnel="yes"/>-->
<!--            </xsl:call-template>-->
<!--            <xsl:apply-templates select="drawings"/>-->
            <xsl:call-template name="copyright">
                <xsl:with-param name="ucid" select="$ucid" tunnel="yes"/>
            </xsl:call-template>
        </rdf:RDF>
    </xsl:template>

    <xsl:template name="bibliographic-data">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:for-each select="bibliographic-data">
            <xsl:call-template name="publication-reference"/>
            <xsl:call-template name="application-reference"/>
            <xsl:call-template name="priority-claims"/>
<!--        <xsl:apply-templates select="dates-of-public-availability"/>-->
        <xsl:apply-templates select="technical-data"/>
<!--        <xsl:apply-templates select="parties"/>-->
<!--        <xsl:apply-templates select="international-convention-data"/>-->
        </xsl:for-each>

    </xsl:template>


    <xsl:template name="publication-reference">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:for-each select="publication-reference">
            <rdf:Description rdf:about="{$patent}{$ucid}">
                <dcterms:references>
                    <rdf:Description rdf:about="{$patent}{@ucid}/publication">
                        <rdf:type rdf:resource="{$pmo}IntellectualPropertyDocument"/>

                        <xsl:call-template name="generalParameterEntities"/>

                        <xsl:call-template name="document-id">
                            <xsl:with-param name="ucid" select="@ucid" tunnel="yes"/>
                        </xsl:call-template>
                    </rdf:Description>
                </dcterms:references>
            </rdf:Description>
        </xsl:for-each>
    </xsl:template>


    <xsl:template name="application-reference">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:for-each select="application-reference">
            <rdf:Description rdf:about="{$patent}{$ucid}">
                <dcterms:references>
                    <rdf:Description rdf:about="{$patent}{@ucid}/application">
                        <rdf:type rdf:resource="{$pmo}IntellectualPropertyDocument"/>

                        <xsl:call-template name="generalParameterEntities"/>

                        <xsl:if test="@appl-type">
                            <property:appl-type><xsl:value-of select="@appl-type"/></property:appl-type>
                        </xsl:if>
                        <xsl:if test="@us-series-code">
                            <property:us-series-code><xsl:value-of select="@us-series-code"/></property:us-series-code>
                        </xsl:if>
                        <xsl:if test="@us-art-unit">
                            <property:us-art-unit><xsl:value-of select="@us-art-unit"/></property:us-art-unit>
                        </xsl:if>
                        <xsl:if test="@is-representative">
                            <property:is-representative><xsl:value-of select="@is-representative"/></property:is-representative>
                        </xsl:if>

                        <xsl:call-template name="document-id">
                            <xsl:with-param name="ucid" select="@ucid" tunnel="yes"/>
                        </xsl:call-template>
                    </rdf:Description>
                </dcterms:references>
            </rdf:Description>
        </xsl:for-each>
    </xsl:template>


    <xsl:template name="priority-claims">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:variable name="id">
            <xsl:choose>
                <xsl:when test="date">
                    <xsl:value-of select="replace(normalize-space(date), '\s+', '')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="position()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
<!--
XXX: Skips priority-claims without @ucid
-->
        <xsl:for-each select="priority-claims/priority-claim[@ucid]">
            <rdf:Description rdf:about="{$patent}{$ucid}">
                <property:priority-claim>
                    <rdf:Description rdf:about="{$patent}{@ucid}/publication{$uriThingSeparator}{$id}">
<!--
XXX: Normally we don't really want to define other patents from here. In case this patent is not declared anywhere else, there is at least this type.
-->
                        <rdf:type rdf:resource="{$pmo}PatentPublication"/>
                    </rdf:Description>
                </property:priority-claim>
            </rdf:Description>
        </xsl:for-each>
    </xsl:template>


    <xsl:template name="document-id">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:variable name="referenceType">
            <xsl:choose>
                <xsl:when test="local-name() = 'publication-reference'">
                    <xsl:value-of select="'publication'"/>
                </xsl:when>
                <xsl:when test="local-name() = 'application-reference'">
                    <xsl:value-of select="'application'"/>
                </xsl:when>
                <xsl:when test="local-name() = 'priority-claim'">
                    <xsl:value-of select="'priority-claim'"/>
                </xsl:when>
                <xsl:otherwise>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:for-each select="document-id">
            <dcterms:hasPart>
                <xsl:variable name="id">
                    <xsl:choose>
                        <xsl:when test="date">
                            <xsl:value-of select="replace(normalize-space(date), '\s+', '')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="position()"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <rdf:Description rdf:about="{$patent}{$ucid}/{$referenceType}{$uriThingSeparator}{$id}">
                    <rdf:type rdf:resource="{$pmo}PatentPublication"/>

                    <dcterms:isPartOf rdf:resource="{$patent}{$ucid}/{$referenceType}"/>

                    <xsl:call-template name="generalParameterEntities"/>

                    <xsl:apply-templates select="@format"/>

                    <xsl:if test="country">
                        <property:filing-office rdf:resource="{$code}/filing-office{$uriThingSeparator}{country}"/>
                    </xsl:if>

                    <xsl:if test="doc-number">
                        <xsl:choose>
                            <xsl:when test="$referenceType = 'publication'">
                                <pmo:publicationNumber><xsl:value-of select="doc-number"/></pmo:publicationNumber>
                            </xsl:when>
                            <xsl:when test="$referenceType = 'application'">
                                <pmo:applicationNumber><xsl:value-of select="doc-number"/></pmo:applicationNumber>
                            </xsl:when>
                            <xsl:otherwise>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:if>

                    <xsl:if test="string-length(kind) = 2">
                        <pmo:kindCode><xsl:value-of select="kind"/></pmo:kindCode>
                        
                        <xsl:if test="country = 'EP'">
                            <rdf:type rdf:resource="{$pso}{kind}Document"/>
                        </xsl:if>
                    </xsl:if>

                    <xsl:if test="date">
                        <pmo:dateOfPublication>
                            <xsl:call-template name="dateNormalized">
                                <xsl:with-param name="date" select="date"/>
                            </xsl:call-template>
                        </pmo:dateOfPublication>
                    </xsl:if>

                    <xsl:apply-templates select="lang"/>
                </rdf:Description>
            </dcterms:hasPart>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="@format">
        <pulo:fileFormat><xsl:value-of select="."/></pulo:fileFormat>
    </xsl:template>

    <xsl:template match="lang">
        <dcterms:language><xsl:value-of select="lower-case(.)"/></dcterms:language>
    </xsl:template>


<!--    <technical-data status="new">-->
<!--      <classification-ipc status="new">-->
<!--        <edition>7</edition>-->
<!--        <main-classification status="new">C07C  45/50</main-classification>-->
<!--        <further-classification status="new">C07F   9/145</further-classification>-->
    <xsl:template name="technical-data">
        <xsl:for-each select="technical-data">
            <xsl:apply-templates select="classification-ipc"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="classification-ipc">
        <xsl:param name="ucid" tunnel="yes"/>

        <rdf:Description rdf:about="{$patent}{$ucid}">
            <xsl:for-each select="*[name() = 'main-classification' or name() = 'further-classification']">
                <xsl:variable name="id" select="normalize-space(replace(., '\s+', ''))"/>
<!--
TODO: Differentiate between main-classification and further-classification -->
-->
                <pmo:classifiedAs>
<!--
XXX: Maybe switch to a code list
-->
                    <rdf:Description rdf:about="{$concept}{$id}">
                        <xsl:call-template name="generalParameterEntities"/>

                        <rdf:type rdf:resource="{$pmo}PatentClassificationCategory"/>
                        <rdf:type rdf:resource="{$pmo}IPCCategory"/>
                        <rdf:type rdf:resource="{$skos}Concept"/>

                        <skos:inScheme rdf:resource="{$concept}ipc"/>
                        <skos:topConceptOf>
                            <rdf:Description rdf:about="{$concept}ipc">
                                <skos:hasTopConcept rdf:resource="{$concept}{$id}"/>
                                <pmo:classifiedPatent rdf:resource="{$concept}{$id}"/>
                            </rdf:Description>
                        </skos:topConceptOf>

                        <pmo:classificationCode><xsl:value-of select="main-classification"/></pmo:classificationCode>
                        <skos:notation><xsl:value-of select="text()"/></skos:notation>
                    </rdf:Description>
                </pmo:classifiedAs>
            </xsl:for-each>
        </rdf:Description>
    </xsl:template>


    <xsl:template name="generalParameterEntities">
        <xsl:apply-templates select="@id"/>

<!--        <xsl:apply-templates selet="@mxw-id"/>-->

        <xsl:apply-templates select="@status"/>

<!--        <xsl:apply-templates selet="@load-source"/>-->

<!--        <xsl:apply-templates selet="@ref-ucid"/>-->
    </xsl:template>

    <xsl:template match="@id">
        <dcterms:identifier><xsl:value-of select="."/></dcterms:identifier>
    </xsl:template>

    <xsl:template match="@status">
        <property:status><xsl:value-of select="."/></property:status>
    </xsl:template>

    <xsl:template name="family-id">
        <xsl:param name="ucid" tunnel="yes"/>

        <pmo:patentFamily>
            <rdf:Description rdf:about="{$patentFamily}{@family-id}">
                <rdf:type rdf:resource="{$pmo}PatentFamily"/>
                <rdf:type rdf:resource="{$skos}Collection"/>
<!--
TODO:
Add members
-->
                <skos:notation><xsl:value-of select="@family-id"/></skos:notation>
            </rdf:Description>
        </pmo:patentFamily>
    </xsl:template>

    <xsl:template match="description">
    </xsl:template>

<!--    <xsl:template name="claims">-->
<!--        <xsl:param name="patentURI" tunnel="yes"/>-->

<!--        <xsl:for-each select="claims">-->
<!--            <xsl:variable name="lang" select="@lang"/>-->

<!--            <xsl:for-each select="claim">-->
<!--                <rdf:Description rdf:about="{$claim}{@num}">-->
<!--                    <rdf:type rdf:resource="{$pso}Claim"/>-->
<!--                    <rdf:type rdf:resource="{$skos}Concept"/>-->

<!--                    <skos:definition xml:lang="{lower-case($lang)}"><xsl:value-of select="claim-text/normalize-space(text())"/></skos:definition>	-->
<!--                </rdf:Description>-->
<!--            </xsl:for-each>-->
<!--        </xsl:for-each>-->
<!--    </xsl:template>-->


    <xsl:template match="drawings">
    </xsl:template>

    <xsl:template name="copyright">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:for-each select="copyright">
            <rdf:Description rdf:about="{$patent}{$ucid}">
                <dcterms:license><xsl:value-of select="normalize-space(.)"/></dcterms:license>
            </rdf:Description>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
