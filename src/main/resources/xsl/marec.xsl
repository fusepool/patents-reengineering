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
    xmlns:property="http://fusepool.eu/ontologies/general#"
    xmlns:schema="http://schema.org/"
    xmlns:uuid="java:java.util.UUID"

    exclude-result-prefixes="xsl fn uuid"
    >

<!--    xpath-default-namespace=""-->

    <xsl:import href="common.xsl"/>

    <xsl:output encoding="utf-8" indent="yes" method="xml" omit-xml-declaration="no"/>

    <xsl:param name="pathToProvDocument"/>
    <xsl:param name="pathToCPCConcordances"/>
    <xsl:variable name="cpcConcordances" select="document($pathToCPCConcordances)/conversion-list/section/subclass"/>

    <xsl:strip-space elements="*"/>

    <xsl:variable name="xslDocument" select="'https://github.com/fusepool/patents-reengineering/src/main/resources/xsl/marec.xsl'"/>

    <xsl:template match="/patent-document">
        <rdf:RDF>
            <rdf:Description rdf:about="{$creator}">
                <rdf:type rdf:resource="{$prov}Agent"/>
            </rdf:Description>

            <xsl:variable name="ucid" select="@ucid"/>

            <xsl:variable name="patentURI">
                <xsl:value-of select="$patent"/>
                <xsl:value-of select="$ucid"/>
            </xsl:variable>

            <xsl:call-template name="provActivity">
                <xsl:with-param name="provUsedA" select="resolve-uri(tokenize(document-uri(/), '/')[last()], $xmlDocumentBaseURI)"/>
                <xsl:with-param name="provGenerated" select="$patentURI"/>
                <xsl:with-param name="id" select="$ucid"/>
            </xsl:call-template>

            <rdf:Description rdf:about="{$patentURI}">
                <rdf:type rdf:resource="{$pmo}PatentPublication"/>

                <xsl:call-template name="family-id">
                    <xsl:with-param name="ucid" select="$ucid" tunnel="yes"/>
                </xsl:call-template>
            </rdf:Description>

            <xsl:call-template name="bibliographic-data">
                <xsl:with-param name="ucid" select="$ucid" tunnel="yes"/>
            </xsl:call-template>

            <xsl:call-template name="abstract">
                <xsl:with-param name="ucid" select="$ucid" tunnel="yes"/>
            </xsl:call-template>

            <xsl:call-template name="description">
                <xsl:with-param name="ucid" select="$ucid" tunnel="yes"/>
            </xsl:call-template>

            <xsl:call-template name="claims">
                <xsl:with-param name="ucid" select="$ucid" tunnel="yes"/>
            </xsl:call-template>
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
            <xsl:call-template name="technical-data"/>
            <xsl:call-template name="parties"/>
<!--        <xsl:apply-templates select="international-convention-data"/>-->
        </xsl:for-each>

    </xsl:template>


    <xsl:template name="publication-reference">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:for-each select="publication-reference">
            <rdf:Description rdf:about="{$patent}{$ucid}">
                <xsl:call-template name="generalParameterEntities"/>

                <xsl:call-template name="document-id">
                    <xsl:with-param name="ucid" select="@ucid" tunnel="yes"/>
                </xsl:call-template>
            </rdf:Description>
        </xsl:for-each>
    </xsl:template>


    <xsl:template name="application-reference">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:for-each select="application-reference">
            <rdf:Description rdf:about="{$patent}{$ucid}">
                <dcterms:references>
                    <rdf:Description rdf:about="{$patent}{@ucid}">
                        <rdf:type rdf:resource="{$pmo}PatentPublication"/>

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
                    <rdf:Description rdf:about="{$patent}{@ucid}">
<!--
XXX: Normally we don't really want to define other patents from here. In case this patent is not declared anywhere else, there is at least this type.
-->
                        <rdf:type rdf:resource="{$pmo}PatentPublication"/>

                        <xsl:call-template name="document-id">
                            <xsl:with-param name="ucid" select="@ucid" tunnel="yes"/>
                        </xsl:call-template>

                    </rdf:Description>
                </property:priority-claim>
            </rdf:Description>
        </xsl:for-each>
    </xsl:template>


    <xsl:template name="document-id">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:variable name="referenceType">
            <xsl:choose>
                <xsl:when test="local-name() = 'publication-reference'
                                or local-name() = 'patcit'">
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

<!--                    <rdf:type rdf:resource="{$pmo}PatentPublication"/>-->

<!--                    <dcterms:isPartOf rdf:resource="{$patent}{$ucid}/{$referenceType}"/>-->

<!--            <xsl:apply-templates select="@format"/>-->

            <xsl:if test="country">
                <pmo:countryOfFiling><xsl:value-of select="country"/></pmo:countryOfFiling>
                <property:filing-office rdf:resource="{$code}filing-office{$uriThingSeparator}{country}"/>
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

            <pmo:kindCode><xsl:value-of select="kind"/></pmo:kindCode>
            
            <xsl:if test="country = 'EP'">
                <xsl:if test="string-length(kind) = 2">
                    <rdf:type rdf:resource="{$pso}{kind}Document"/>
                </xsl:if>

                <xsl:if test="kind = 'B1'
                            or kind = 'B2'
                            or kind = 'B8'
                            or kind = 'B9'">
                    <rdf:type rdf:resource="{$pmo}GrantedPatent"/>
                </xsl:if>
            </xsl:if>

            <xsl:if test="country = 'US'">
                <xsl:if test="kind = 'A'
                            or kind = 'S'
                            or kind = 'P'
                            or kind = 'H'
                            or kind = 'E'
                            or kind = 'I'
                            or kind = 'B1'
                            or kind = 'B2'
                            or kind = 'P2'
                            or kind = 'P3'
                            or kind = 'S1'
                            or kind = 'E1'
                            or kind = 'H1'">
                    <rdf:type rdf:resource="{$pmo}GrantedPatent"/>
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
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:for-each select="technical-data">
            <xsl:call-template name="classifications"/>

            <rdf:Description rdf:about="{$patent}{$ucid}">
                <xsl:apply-templates select="invention-title"/>
            </rdf:Description>

            <xsl:call-template name="citations"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="classifications">
        <xsl:param name="ucid" tunnel="yes"/>

        <rdf:Description rdf:about="{$patent}{$ucid}">
            <xsl:for-each select="classification-ecla/classification-symbol">

<!--
XXX: This removes the codes after + or : in ECLA. There might be a particular use even though spec says it is not generally needed.
-->
<!-- XXX: This works with ECLA fine                <xsl:variable name="id" select="fn:substring-before-if-contains(fn:substring-before-if-contains(normalize-space(replace(., '\s+', '')), '+'), ':')"/>
-->

                <xsl:variable name="classificationIdentifier" select="fn:substring-before-if-contains(replace(normalize-space(replace(., '\s+', '')), ':', '/'), '+')"/>

                <xsl:for-each select="$cpcConcordances/item[EC/text() = $classificationIdentifier]">
                    <xsl:variable name="id" select="CPC"/>
                    <xsl:variable name="level" select="number(normalize-space(level))"/>
                    <xsl:variable name="levelPath">
<!--                        <xsl:if test="$level &lt; 7">-->
                            <xsl:value-of select="concat($level, '/')"/>
<!--                        </xsl:if>-->
                    </xsl:variable>

                    <xsl:variable name="conceptURI" select="concat($concept, 'cpc/', $levelPath, $id)"/>

                    <pmo:classifiedAs>
    <!--
    XXX: Maybe switch to a code list
    -->
                        <rdf:Description rdf:about="{$conceptURI}">
    <!--                        <rdf:type rdf:resource="{$pmo}PatentClassificationCategory"/>-->
    <!--                        <rdf:type rdf:resource="{$pmo}IPCCategory"/>-->
                            <rdf:type rdf:resource="{$skos}Concept"/>

                            <pmo:classifiedPatent rdf:resource="{$patent}{$ucid}"/>

    <!--                        <skos:inScheme rdf:resource="{$concept}ipc"/>-->
    <!--                        <skos:topConceptOf>-->
    <!--                            <rdf:Description rdf:about="{$concept}ipc">-->
    <!--                                <skos:hasTopConcept rdf:resource="{$concept}{$id}"/>-->
    <!--                                <pmo:classifiedPatent rdf:resource="{$concept}{$id}"/>-->
    <!--                            </rdf:Description>-->
    <!--                        </skos:topConceptOf>-->

                            <pmo:classificationCode><xsl:value-of select="$id"/></pmo:classificationCode>
                            <skos:notation><xsl:value-of select="$id"/></skos:notation>
                        </rdf:Description>
                    </pmo:classifiedAs>
                </xsl:for-each>
            </xsl:for-each>
        </rdf:Description>
    </xsl:template>

    <xsl:template match="invention-title">
        <dcterms:title><xsl:call-template name="langTextNode"/></dcterms:title>
    </xsl:template>

    <xsl:template name="citations">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:for-each select="citations/patent-citations/patcit">
            <rdf:Description rdf:about="{$patent}{$ucid}">
                <pmo:cites>
                    <rdf:Description rdf:about="{$patent}{@ucid}">
                        <rdf:type rdf:resource="{$pmo}PatentPublication"/>
                        <pmo:citedBy rdf:resource="{$patent}{$ucid}"/>

                        <xsl:call-template name="document-id">
                            <xsl:with-param name="ucid" select="@ucid" tunnel="yes"/>
                        </xsl:call-template>
<!--TODO:-->
<!--            <sources>-->
<!--              <source category="A" name="SEA" created-by-npl="N"/>-->
<!--            </sources>-->
                    </rdf:Description>
                </pmo:cites>
            </rdf:Description>
        </xsl:for-each>

<!-- TODO -->
<!--        <non-patent-citations>-->
<!--          <nplcit status="new">-->
<!--            <text>See references of WO     9906345A1</text>-->
<!--            <sources>-->
<!--              <source name="SEA" created-by-npl="N"/>-->
<!--            </sources>-->
<!--          </nplcit>-->
<!--        </non-patent-citations>-->
    </xsl:template>

    <xsl:template name="parties">
        <xsl:for-each select="parties">
            <xsl:apply-templates select="applicants/applicant"/>
            <xsl:apply-templates select="inventors/inventor"/>
            <xsl:apply-templates select="assignees/assignee"/>
            <xsl:apply-templates select="agents/agent"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="applicants/applicant">
        <xsl:call-template name="party-members"/>
    </xsl:template>

    <xsl:template match="inventors/inventor">
        <xsl:call-template name="party-members"/>
    </xsl:template>

    <xsl:template match="assignees/assignee">
        <xsl:call-template name="party-members"/>
    </xsl:template>

    <xsl:template match="agents/agent">
        <xsl:call-template name="party-members"/>
    </xsl:template>

    <xsl:template name="party-members">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:variable name="party-type" select="local-name()"/>
        <xsl:variable name="id" select="@mxw-id"/>

        <xsl:variable name="party-type-property">
            <xsl:choose>
                <xsl:when test="local-name() = 'applicant' or
                                local-name() = 'inventor' or
                                local-name() = 'assignee'
                                ">
                    <xsl:value-of select="concat('pmo:', $party-type)"/>
                </xsl:when>
                <xsl:when test="local-name() = 'agent'">
                    <xsl:value-of select="'property:agent'"/>
                </xsl:when>
                <xsl:otherwise>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <rdf:Description rdf:about="{$patent}{$ucid}">
            <xsl:for-each select="addressbook">
                <xsl:element name="{$party-type-property}">
                    <xsl:call-template name="addressbook">
                        <xsl:with-param name="id" select="$id"/>
                        <xsl:with-param name="party-type" select="$party-type"/>
                    </xsl:call-template>
                </xsl:element>
            </xsl:for-each>
        </rdf:Description>
    </xsl:template>

    <xsl:template name="addressbook">
        <xsl:param name="ucid" tunnel="yes"/>
        <xsl:param name="id"/>
        <xsl:param name="party-type"/>

        <rdf:Description rdf:about="{concat($entityID, uuid:randomUUID())}">
            <xsl:choose>
                <xsl:when test="$party-type = 'applicant'">
                    <rdf:type rdf:resource="{$sumo}CognitiveAgent"/>
                    <rdf:type rdf:resource="{$foaf}Agent"/>
                    <pmo:applicantOf rdf:resource="{$patent}{$ucid}"/>
                </xsl:when>
                <xsl:when test="$party-type = 'inventor'">
                    <rdf:type rdf:resource="{$sumo}Human"/>
                    <rdf:type rdf:resource="{$foaf}Person"/>
                    <pmo:inventorOf rdf:resource="{$patent}{$ucid}"/>
                </xsl:when>
                <xsl:when test="$party-type = 'assignee'">
                    <rdf:type rdf:resource="{$sumo}CognitiveAgent"/>
                    <rdf:type rdf:resource="{$foaf}Agent"/>
                    <pmo:assigneeOf rdf:resource="{$patent}{$ucid}"/>
                </xsl:when>
                <xsl:when test="$party-type = 'agent'">
                    <rdf:type rdf:resource="{$sumo}Agent"/>
                    <rdf:type rdf:resource="{$foaf}Agent"/>
                    <property:agentOf rdf:resource="{$patent}{$ucid}"/>
                </xsl:when>
                <xsl:otherwise>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:if test="name">
                <foaf:name><xsl:value-of select="name"/></foaf:name>
                <rdfs:label><xsl:value-of select="name"/></rdfs:label>
            </xsl:if>

            <xsl:if test="orgname">
                <foaf:name><xsl:value-of select="orgname"/></foaf:name>
                <rdfs:label><xsl:value-of select="orgname"/></rdfs:label>
            </xsl:if>

            <xsl:if test="last-name">
                <rdfs:label><xsl:value-of select="last-name"/></rdfs:label>
                <xsl:choose>
                    <xsl:when test="$party-type = 'inventor'">
                        <foaf:lastName><xsl:value-of select="last-name"/></foaf:lastName>
                    </xsl:when>
                    <xsl:otherwise>
                        <foaf:name><xsl:value-of select="last-name"/></foaf:name>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>

            <xsl:if test="first-name">
                <foaf:firstName><xsl:value-of select="first-name"/></foaf:firstName>
                <rdfs:label><xsl:value-of select="first-name"/></rdfs:label>
            </xsl:if>

            <xsl:if test="middle-name">
            </xsl:if>

            <xsl:if test="prefix">
                <foaf:honorificPrefix><xsl:value-of select="prefix"/></foaf:honorificPrefix>
            </xsl:if>
            <xsl:if test="suffix">
                <foaf:honorificSuffix><xsl:value-of select="suffix"/></foaf:honorificSuffix>
            </xsl:if>

            <xsl:if test="iid">
            </xsl:if>

            <xsl:if test="role">
                <foaf:title><xsl:value-of select="role"/></foaf:title>
            </xsl:if>

            <xsl:if test="department">
            </xsl:if>

            <xsl:if test="synonym">
            </xsl:if>

            <xsl:if test="registered-number">
                <property:registered-number><xsl:value-of select="registered-number"/></property:registered-number>
            </xsl:if>

            <xsl:apply-templates select="address"/>

            <xsl:apply-templates select="phone"/>
            <xsl:apply-templates select="fax"/>
            <xsl:apply-templates select="email"/>
            <xsl:apply-templates select="url"/>
            <xsl:apply-templates select="ead"/>
        </rdf:Description>
    </xsl:template>

    <xsl:template match="phone">
        <foaf:phone rdf:resource="tel:{replace(normalize-space(phone), ' ', '-')}"/>
    </xsl:template>
    <xsl:template match="fax">
        <schema:faxNumber><xsl:value-of select="fax"/></schema:faxNumber>
    </xsl:template>
    <xsl:template match="email">
        <foaf:mbox rdf:resource="mailto:{normalize-space(email)}"/>
    </xsl:template>
    <xsl:template match="url">
        <foaf:page rdf:resource="{normalize-space(url)}"/>
    </xsl:template>
    <xsl:template match="ead">
        <property:ead><xsl:value-of select="ead"/></property:ead>
    </xsl:template>

    <xsl:template match="address">
        <schema:address>
            <rdf:Description rdf:about="{concat($entityID, uuid:randomUUID())}">
                <rdf:type rdf:resource="{$schema}PostalAddress"/>
                <xsl:if test="address-1">
                    <schema:streetAddress><xsl:value-of select="address1"/></schema:streetAddress>
                </xsl:if>
                <xsl:if test="address-2">
                    <schema:streetAddress><xsl:value-of select="address2"/></schema:streetAddress>
                </xsl:if>
                <xsl:if test="address-3">
                    <schema:streetAddress><xsl:value-of select="address3"/></schema:streetAddress>
                </xsl:if>
                <xsl:if test="mailcode">
                </xsl:if>
                <xsl:if test="pobox">
                    <schema:postOfficeBoxNumber><xsl:value-of select="pobox"/></schema:postOfficeBoxNumber>
                </xsl:if>
                <xsl:if test="room">
                </xsl:if>
                <xsl:if test="address-floor">
                </xsl:if>
                <xsl:if test="building">
                </xsl:if>
                <xsl:if test="street">
                    <schema:streetAddress><xsl:value-of select="street"/></schema:streetAddress>
                </xsl:if>
                <xsl:if test="city">
                    <schema:addressLocality><xsl:value-of select="city"/></schema:addressLocality>
                </xsl:if>
                <xsl:if test="state">
                    <schema:addressRegion><xsl:value-of select="state"/></schema:addressRegion>
                </xsl:if>
                <xsl:if test="postcode">
                    <schema:postalCode><xsl:value-of select="postcode"/></schema:postalCode>
                </xsl:if>
                <xsl:if test="country">
                    <schema:addressCountry rdf:resource="{$code}country{$uriThingSeparator}{normalize-space(country)}"/>
                </xsl:if>
            </rdf:Description>
        </schema:address>
    </xsl:template>


    <xsl:template name="generalParameterEntities">
        <xsl:apply-templates select="@ucid"/>

<!--        <xsl:apply-templates selet="@mxw-id"/>-->

<!--        <xsl:apply-templates select="@status"/>-->

<!--        <xsl:apply-templates selet="@load-source"/>-->

<!--        <xsl:apply-templates selet="@ref-ucid"/>-->
    </xsl:template>

    <xsl:template match="@id | @ucid">
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




    <xsl:template name="description">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:for-each select="description">
            <rdf:Description rdf:about="{$patent}{$ucid}">
                <xsl:variable name="lang" select="lower-case(@lang)"/>

                <dcterms:description rdf:parseType="Literal">
                    <div xmlns="http://www.w3.org/1999/xhtml" xml:lang="{$lang}">
                               <xsl:apply-templates/>
                    </div>
                </dcterms:description>
<!--                <pso:hasDescriptionSection>-->
<!--                    <rdf:Description rdf:about="{$patent}{$ucid}/description">-->
<!--                        <rdf:type rdf:resource="{$pso}Description"/>-->

<!--                        <xsl:for-each select="p">-->
<!--                            <dcterms:hasPart>-->
<!--                                <rdf:Description rdf:about="{$patent}{$ucid}/description{$uriThingSeparator}{@num}">-->
<!--                                    <dcterms:isPartOf rdf:resource="{$patent}{$ucid}/description"/>-->
<!--                                    <rdfs:label xml:lang="en"><xsl:value-of select="concat('Paragraph ', @num)"/></rdfs:label>-->
<!--                                    <dcterms:identifier><xsl:value-of select="@num"/></dcterms:identifier>-->
<!--                                    <dcterms:description rdf:parseType="Literal">-->

<!--                                    </dcterms:description>-->
<!--                                </rdf:Description>-->
<!--                            </dcterms:hasPart>-->
<!--                        </xsl:for-each>-->
<!--                    </rdf:Description>-->
<!--                </pso:hasDescriptionSection>-->
            </rdf:Description>
        </xsl:for-each>
    </xsl:template>


    <xsl:template name="abstract">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:for-each select="abstract">
            <rdf:Description rdf:about="{$patent}{$ucid}">
                <dcterms:abstract rdf:parseType="Literal">
<!--                    <div>-->
                    <div xmlns="http://www.w3.org/1999/xhtml" xml:lang="{$lang}">
                               <xsl:apply-templates/>
                    </div>
                </dcterms:abstract>
            </rdf:Description>
        </xsl:for-each>
    </xsl:template>


    <xsl:template match="*">
        <xsl:element name="{local-name()}" namespace="http://www.w3.org/1999/xhtml">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>


    <xsl:template match="claim-text">
        <div xmlns="http://www.w3.org/1999/xhtml"><xsl:apply-templates/></div>
    </xsl:template>
    <xsl:template match="sl">
        <ul xmlns="http://www.w3.org/1999/xhtml"><xsl:apply-templates/></ul>
    </xsl:template>

    <xsl:template name="claims">
        <xsl:param name="ucid" tunnel="yes"/>

        <xsl:for-each select="claims">
            <xsl:variable name="lang" select="lower-case(@lang)"/>

            <xsl:for-each select="claim">
                <rdf:Description rdf:about="{$patent}{$ucid}">
                    <property:claim>
                        <rdf:Description rdf:about="{$patent}{$ucid}/claim/{@num}">
                            <rdf:type rdf:resource="{$pso}Claim"/>
                            <rdf:type rdf:resource="{$skos}Concept"/>
                            <skos:prefLabel xml:lang="en"><xsl:value-of select="concat('Claim ', @num)"/></skos:prefLabel>

                            <skos:notation><xsl:value-of select="@num"/></skos:notation>
                            <skos:definition rdf:parseType="Literal">
<!--                                <xsl:for-each select="claim-text">-->
<!--                                    <div>-->
                                    <div xmlns="http://www.w3.org/1999/xhtml" xml:lang="{$lang}">
                                       <xsl:apply-templates/>
                                    </div>
<!--                                </xsl:for-each>-->
                            </skos:definition>
                        </rdf:Description>
                    </property:claim>
                </rdf:Description>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>


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
