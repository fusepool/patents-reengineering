<!--
    Author: Sarven Capadisli <info@csarven.ca>
    Author URI: http://csarven.ca/#i

    Description: XSLT for ECLA Cooperative Patent Classification
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
    xmlns:schema="http://schema.org/"

    xpath-default-namespace="http://www.epo.org/eclaexport"
    exclude-result-prefixes="xsl fn"
    >

    <xsl:import href="common.xsl"/>

    <xsl:output encoding="utf-8" indent="yes" method="xml" omit-xml-declaration="no"/>

    <xsl:param name="pathToProvDocument"/>

    <xsl:strip-space elements="*"/>

    <xsl:variable name="xslDocument" select="'https://github.com/fusepool/patents-reengineering/src/main/resources/scripts/ecla.xsl'"/>

    <xsl:template match="/class-scheme">
        <rdf:RDF>
            <rdf:Description rdf:about="{$creator}">
                <rdf:type rdf:resource="{$prov}Agent"/>
            </rdf:Description>

            <xsl:apply-templates/>
        </rdf:RDF>
    </xsl:template>

    <xsl:template match="classification-item">
        <xsl:variable name="level" select="number(normalize-space(@level))"/>
        <xsl:variable name="levelPath">
            <xsl:if test="$level &lt; 7">
                <xsl:value-of select="concat($level, '/')"/>
            </xsl:if>
        </xsl:variable>
        <xsl:variable name="conceptID" select="normalize-space(classification-symbol)"/>
        <xsl:variable name="conceptURI" select="concat($concept, 'ecla/', $levelPath, $conceptID)"/>

<!--<xsl:message>-->
<!--<xsl:value-of select="@level"/>-->
<!--</xsl:message>-->

        <rdf:Description rdf:about="{$conceptURI}">
            <xsl:choose>
                <xsl:when test="$level &lt; 7">
                    <rdf:type rdf:resource="{$skos}Collection"/>
                    <rdf:type rdf:resource="{$xkos}ClassificationLevel"/>
                    <xkos:depth><xsl:value-of select="$level"/></xkos:depth>

                    <xsl:for-each select="classification-item">
                        <skos:member>
                            <xsl:apply-templates select="."/>
                        </skos:member>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <rdf:type rdf:resource="{$skos}Concept"/>
                    <xsl:variable name="broaderConceptID" select="../../classification-symbol"/>

                    <xsl:if test="$broaderConceptID">
                        <xsl:variable name="broaderConceptURI" select="concat($concept, $broaderConceptID)"/>
                        <skos:topConceptOf>
                            <rdf:Description rdf:about="{concat($concept, 'ecla')}">
                                <skos:hasTopConcept rdf:resource="{$conceptURI}"/>
                            </rdf:Description>
                        </skos:topConceptOf>
                        <skos:inScheme rdf:resource="{concat($concept, 'ecla')}"/>
                        <skos:broader rdf:resource="{$broaderConceptURI}"/>
                    </xsl:if>

                    <xsl:for-each select="classification-item">
                        <skos:narrower>
                            <xsl:apply-templates select="."/>
                        </skos:narrower>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:call-template name="class-title">
                <xsl:with-param name="conceptURI" select="$conceptURI" tunnel="yes"/>
            </xsl:call-template>
            <xsl:call-template name="notes-and-warnings">
                <xsl:with-param name="conceptURI" select="$conceptURI" tunnel="yes"/>
            </xsl:call-template>
        </rdf:Description>
    </xsl:template>

    <xsl:template name="class-title">
        <xsl:for-each select="class-title">
            <xsl:apply-templates select="title-part"/>
            <xsl:apply-templates select=".//class-ref"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="notes-and-warnings">
        <xsl:for-each select="notes-and-warnings">
            <xsl:apply-templates select="note"/>
            <xsl:apply-templates select=".//class-ref"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="title-part">
        <xsl:apply-templates select="text" mode="skos:prefLabel"/>
        <xsl:apply-templates select="explanation"/>
    </xsl:template>

    <xsl:template match="explanation">
        <xsl:apply-templates select="text" mode="skos:definition"/>
        <xsl:apply-templates select="comment"/>
    </xsl:template>

    <xsl:template match="comment">
        <xsl:apply-templates select="text" mode="skos:scopeNote"/>
    </xsl:template>

    <xsl:template match="note">
        <xsl:apply-templates select="note-paragraph" mode="skos:note"/>
    </xsl:template>

    <xsl:template match="text" mode="skos:prefLabel">
        <skos:prefLabel><xsl:call-template name="langTextNode"/></skos:prefLabel>
    </xsl:template>

    <xsl:template match="text" mode="skos:definition">
        <skos:definition><xsl:call-template name="langTextNode"/></skos:definition>
    </xsl:template>

    <xsl:template match="text" mode="skos:scopeNote">
        <skos:scopeNote><xsl:call-template name="langTextNode"/></skos:scopeNote>
    </xsl:template>

    <xsl:template match="note-paragraph" mode="skos:note">
        <skos:note><xsl:call-template name="langTextNode"/></skos:note>
    </xsl:template>

    <xsl:template match="class-ref">
        <dcterms:references rdf:resource="{concat($concept, 'ecla/', normalize-space(.))}"/>
    </xsl:template>


<!--<xsl:message>-->
<!--<xsl:text>-->
<!--</xsl:text>-->
<!--<xsl:value-of select="local-name()"/>-->
<!--</xsl:message>-->
</xsl:stylesheet>
