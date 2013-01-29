<!--
    Author: Sarven Capadisli <info@csarven.ca>
    Author URI: http://csarven.ca/#i
-->
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:skos="http://www.w3.org/2004/02/skos/core#"
    xmlns:prov="http://www.w3.org/ns/prov#"
    xmlns:fn="http://270a.info/xpath-function/"

    exclude-result-prefixes="xsl fn"
    >

    <xsl:output encoding="utf-8" indent="yes" method="xml" omit-xml-declaration="no"/>

    <xsl:variable name="pathToConfig"><xsl:text>./config.rdf</xsl:text></xsl:variable>
    <xsl:variable name="Config" select="document($pathToConfig)/rdf:RDF"/>
    <xsl:variable name="xmlDocumentBaseURI" select="fn:getConfig('xmlDocumentBaseURI')"/>
    <xsl:variable name="xslDocument" select="fn:getConfig('xslDocument')"/>
    <xsl:variable name="baseURI" select="fn:getConfig('baseURI')"/>
    <xsl:variable name="now" select="fn:now()"/>
    <xsl:variable name="rdf" select="fn:getConfig('rdf')"/>
    <xsl:variable name="rdfs" select="fn:getConfig('rdfs')"/>
    <xsl:variable name="owl" select="fn:getConfig('owl')"/>
    <xsl:variable name="xsd" select="fn:getConfig('xsd')"/>
    <xsl:variable name="qb" select="fn:getConfig('qb')"/>
    <xsl:variable name="skos" select="fn:getConfig('skos')"/>
    <xsl:variable name="xkos" select="fn:getConfig('xkos')"/>
    <xsl:variable name="sdmx" select="fn:getConfig('sdmx')"/>
    <xsl:variable name="prov" select="fn:getConfig('prov')"/>
    <xsl:variable name="provenance" select="fn:getConfig('provenance')"/>
    <xsl:variable name="lang" select="fn:getConfig('lang')"/>
    <xsl:variable name="creator" select="fn:getConfig('creator')"/>
    <xsl:variable name="pso" select="fn:getConfig('pso')"/>
    <xsl:variable name="pulo" select="fn:getConfig('pulo')"/>
    <xsl:variable name="sumo" select="fn:getConfig('sumo')"/>
    <xsl:variable name="pmo" select="fn:getConfig('pmo')"/>
    <xsl:variable name="uriThingSeparator" select="fn:getConfig('uriThingSeparator')"/>
    <xsl:variable name="uriDimensionSeparator" select="fn:getConfig('uriDimensionSeparator')"/>
    <xsl:variable name="concept" select="concat($baseURI, 'concept')"/>
    <xsl:variable name="code" select="concat($baseURI, 'code/')"/>
    <xsl:variable name="class" select="concat($baseURI, 'class', $uriThingSeparator)"/>
    <xsl:variable name="property" select="concat($baseURI, 'property', $uriThingSeparator)"/>
    <xsl:variable name="dataset" select="concat($baseURI, 'dataset', $uriThingSeparator)"/>
    <xsl:variable name="patent" select="concat($baseURI, 'patent', $uriThingSeparator)"/>
    <xsl:variable name="patentFamily" select="concat($baseURI, 'family', $uriThingSeparator)"/>
    <xsl:variable name="patentPublication" select="concat($baseURI, 'publication')"/>
    <xsl:variable name="patentApplication" select="concat($baseURI, 'application')"/>
    <xsl:variable name="claim" select="concat($baseURI, 'claim')"/>


    <!--Copied from https://github.com/csarven/sdmx-to-qb/blob/master/scripts/common.xsl -->
    <xsl:function name="fn:getConfig">
        <xsl:param name="label"/>

        <xsl:value-of select="$Config/rdf:Description/rdf:value/rdf:Description[rdfs:label = $label]/rdf:value"/>
    </xsl:function>


    <!--Copied from https://github.com/csarven/sdmx-to-qb/blob/master/scripts/common.xsl -->
    <xsl:template name="langTextNode">
        <xsl:if test="@xml:lang">
            <xsl:copy-of select="@*[name() = 'xml:lang']"/>
        </xsl:if>
        <xsl:if test="$lang">
            <xsl:attribute name="xml:lang" select="$lang"/>
        </xsl:if>
        <xsl:value-of select="text()"/>
    </xsl:template>


    <!--Copied from https://github.com/csarven/sdmx-to-qb/blob/master/scripts/common.xsl -->
    <xsl:template name="rdfDatatypeXSD">
        <xsl:param name="type"/>

        <xsl:attribute name="rdf:datatype"><xsl:text>http://www.w3.org/2001/XMLSchema#</xsl:text><xsl:value-of select="$type"/></xsl:attribute>
    </xsl:template>


    <!--Copied from https://github.com/csarven/sdmx-to-qb/blob/master/scripts/common.xsl -->
    <xsl:template name="provActivity">
        <xsl:param name="provUsedA"/>
        <xsl:param name="provUsedB"/>
        <xsl:param name="provGenerated"/>

        <xsl:variable name="now" select="format-dateTime(current-dateTime(), '[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01]Z')"/>

        <rdf:Description rdf:about="{$provenance}activity{$uriThingSeparator}{replace($now, '\D', '')}">
            <rdf:type rdf:resource="{$prov}Activity"/>
<!--dcterms:title-->
            <prov:startedAtTime rdf:datatype="{$xsd}dateTime"><xsl:value-of select="$now"/></prov:startedAtTime>
            <prov:wasAssociatedWith rdf:resource="{$creator}"/>
            <prov:used rdf:resource="{$provUsedA}"/>
            <xsl:if test="$provUsedB">
                <prov:used rdf:resource="{$provUsedB}"/>
            </xsl:if>
            <prov:used rdf:resource="{$xslDocument}"/>
            <prov:generated>
                <rdf:Description rdf:about="{$provGenerated}">
                    <prov:wasDerivedFrom rdf:resource="{$provUsedA}"/>
                </rdf:Description>
            </prov:generated>
        </rdf:Description>
    </xsl:template>

    <!--Copied from https://github.com/csarven/sdmx-to-qb/blob/master/scripts/common.xsl -->
    <xsl:function name="fn:now">
        <xsl:value-of select="format-dateTime(current-dateTime(), '[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01]Z')"/>
    </xsl:function>


    <xsl:template name="dateNormalized">
        <xsl:param name="date"/>

        <xsl:analyze-string select="$date" regex="([0-9]{{4}})([0-9]{{2}})([0-9]{{2}})">
            <xsl:matching-substring>                
                <xsl:call-template name="rdfDatatypeXSD">
                    <xsl:with-param name="type" select="'date'"/>
                </xsl:call-template>

                <xsl:value-of select="regex-group(1)"/><xsl:text>-</xsl:text><xsl:value-of select="regex-group(2)"/><xsl:text>-</xsl:text><xsl:value-of select="regex-group(3)"/>
            </xsl:matching-substring>

            <xsl:non-matching-substring>
                <xsl:value-of select="$date"/>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>
</xsl:stylesheet>
