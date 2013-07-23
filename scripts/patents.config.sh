#!/bin/bash

namespace="http://fusepool.info/";
repoMeta="/home/fusepool/patents-reengineering/data/meta/";
marec="/data/marec/";
cpcscheme="/data/cpc-scheme/";
eclascheme="/data/ecla/";
#cpcscheme="/var/www/patents-reengineering/data/cpc-scheme/";
pathToCPCConcordances="../data/cpc-concordances/ECLAtoCPC.xml";
xslCPC="/home/fusepool/patents-reengineering/src/main/resources/xsl/cpc.xsl";
tdbAssembler="/usr/lib/fuseki/tdb.fusepool.ttl";
JVM_ARGS="-Xmx16000M";
db="/data/tdb/fusepool/";
javatdbloader="java $JVM_ARGS tdb.tdbloader --desc=$tdbAssembler";

saxonb="/usr/share/java/saxonb.jar";
xmlresolver="/usr/share/java/xml-resolver.jar";
pathToCatalog="../src/main/resources/catalog/catalog.xml";
xmlcatalog="-Dxml.catalog.files=$pathToCatalog -Dxml.catalog.verbosity=2";
rxy="-r:org.apache.xml.resolver.tools.CatalogResolver -x org.apache.xml.resolver.tools.ResolvingXMLReader -y org.apache.xml.resolver.tools.ResolvingXMLReader";
