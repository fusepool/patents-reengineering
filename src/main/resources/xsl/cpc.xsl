<!--
    Author: Sarven Capadisli <info@csarven.ca>
    Author URI: http://csarven.ca/#i

    Description: XSLT for CPC (Cooperative Patent Classification)
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
    xmlns:property="urn:x-temp:/property/"
    xmlns:schema="http://schema.org/"

    exclude-result-prefixes="xsl fn"
    >

<!--    xpath-default-namespace="http://www.epo.org/eclaexport"-->

    <xsl:import href="common.xsl"/>

    <xsl:output encoding="utf-8" indent="yes" method="xml" omit-xml-declaration="no"/>

    <xsl:param name="pathToProvDocument"/>

    <xsl:strip-space elements="*"/>

    <xsl:variable name="xslDocument" select="'https://github.com/fusepool/patents-reengineering/src/main/resources/xsl/cpc.xsl'"/>

    <xsl:template match="/class-scheme">
        <rdf:RDF>
            <rdf:Description rdf:about="{$creator}">
                <rdf:type rdf:resource="{$prov}Agent"/>
            </rdf:Description>

            <rdf:Description rdf:about="{$concept}cpc">
                <rdf:type rdf:resource="{$pmo}PatentClassification"/>
                <rdf:type rdf:resource="{$skos}ConceptScheme"/>
                <skos:notation>CPC</skos:notation>
                <skos:prefLabel xml:lang="en">Cooperative Patent Classification</skos:prefLabel>
                <foaf:page rdf:resource="http://www.cooperativepatentclassification.org/"/>
            </rdf:Description>

            <xsl:apply-templates/>
        </rdf:RDF>
    </xsl:template>

    <xsl:template match="classification-item">
        <xsl:variable name="level" select="number(normalize-space(@level))"/>
        <xsl:variable name="levelPath">
<!--            <xsl:if test="$level &lt; 7">-->
                <xsl:value-of select="concat($level, '/')"/>
<!--            </xsl:if>-->
        </xsl:variable>
        <xsl:variable name="conceptID" select="normalize-space(classification-symbol)"/>
        <xsl:variable name="conceptURI" select="concat($concept, 'cpc/', $levelPath, $conceptID)"/>

<!--<xsl:message>-->
<!--<xsl:value-of select="@level"/>-->
<!--</xsl:message>-->

        <rdf:Description rdf:about="{$conceptURI}">
            <skos:notation><xsl:value-of select="$conceptID"/></skos:notation>

            <dcterms:isFormatOf rdf:resource="http://worldwide.espacenet.com/classification#!/CPC={$conceptID}"/>

            <xsl:choose>
                <xsl:when test="$level &lt; 7">
                    <rdf:type rdf:resource="{$skos}Collection"/>
                    <rdf:type rdf:resource="{$xkos}ClassificationLevel"/>
<!--                    <rdf:type rdf:resource="{$pmo}ECLACategory"/>-->
                    <xkos:depth rdf:datatype="{$xsd}integer"><xsl:value-of select="$level"/></xkos:depth>

                    <xsl:for-each select="classification-item">
                        <skos:member>
                            <xsl:apply-templates select="."/>
                        </skos:member>
                    </xsl:for-each>

                    <xsl:for-each select="classification-item[number(normalize-space(@level)) &lt; 7]">
                        <xsl:variable name="level" select="number(normalize-space(@level))"/>
                        <xsl:variable name="subConceptID" select="normalize-space(classification-symbol)"/>
                        <xsl:variable name="subConceptURI" select="concat($concept, 'cpc/', $level, '/', $subConceptID)"/>

                        <pmo:subCategory>
                            <rdf:Description rdf:about="{$subConceptURI}">
                                <pmo:parentCategory rdf:resource="{$conceptURI}"/>
                            </rdf:Description>
                        </pmo:subCategory>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <rdf:type rdf:resource="{$skos}Concept"/>
                    <xsl:variable name="broaderConceptID" select="../classification-symbol"/>

                    <xsl:if test="$broaderConceptID">
                        <xsl:variable name="broaderConceptLevel">
                            <xsl:if test="../@level &lt; 7">
                                <xsl:value-of select="concat(../@level, '/')"/>
                            </xsl:if>
                        </xsl:variable>
                        <xsl:variable name="broaderConceptURI" select="concat($concept, 'cpc/', $broaderConceptLevel, $broaderConceptID)"/>
                        <skos:topConceptOf>
                            <rdf:Description rdf:about="{concat($concept, 'cpc')}">
                                <skos:hasTopConcept rdf:resource="{$conceptURI}"/>
                            </rdf:Description>
                        </skos:topConceptOf>
                        <skos:inScheme rdf:resource="{concat($concept, 'cpc')}"/>
                        <skos:broader rdf:resource="{$broaderConceptURI}"/>
                    </xsl:if>

                    <xsl:for-each select="classification-item">
                        <skos:narrower>
                            <xsl:apply-templates select="."/>
                        </skos:narrower>
                    </xsl:for-each>

                    <pmo:classificationCode><xsl:value-of select="$conceptID"/></pmo:classificationCode>
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
        <xsl:apply-templates select="explanation | reference"/>
        <xsl:apply-templates select="comment | CPC-specific-text" mode="fn:title-part"/>
    </xsl:template>

    <xsl:template match="explanation | reference">
        <xsl:apply-templates select="text" mode="skos:definition"/>
        <xsl:apply-templates select="comment" mode="skos:scopeNote"/>
    </xsl:template>

    <xsl:template match="comment" mode="skos:scopeNote">
        <xsl:apply-templates select="text" mode="skos:scopeNote"/>
    </xsl:template>

    <xsl:template match="comment | CPC-specific-text" mode="fn:title-part">
        <xsl:apply-templates select="text" mode="skos:prefLabel"/>
        <xsl:apply-templates select="explanation | reference"/>
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
        <xsl:variable name="classRef" select="normalize-space(.)"/>
        <xsl:if test="not(contains($classRef, ' '))">
            <dcterms:references>
                <rdf:Description rdf:about="{concat($concept, 'cpc/', $classRef)}">
                    <skos:notation><xsl:value-of select="$classRef"/></skos:notation>
                </rdf:Description>
            </dcterms:references>
        </xsl:if>
    </xsl:template>

<!--<xsl:message>-->
<!--<xsl:text>-->
<!--</xsl:text>-->
<!--<xsl:value-of select="local-name()"/>-->
<!--</xsl:message>-->
</xsl:stylesheet>
