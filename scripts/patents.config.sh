#!/bin/bash

namespace="http://fusepool.info/";
repoMeta="/home/fusepool/patents-reengineering/data/meta/";
marec="/data/marec/";
cpcscheme="/data/cpc-scheme/";
eclascheme="/data/ecla/";
#cpcscheme="/var/www/patents-reengineering/data/cpc-scheme/";
pathToCPCConcordances="/home/fusepool/patents-reengineering/data/cpc-concordances/CPC-concordance-12102012.xml";
xslCPC="/home/fusepool/patents-reengineering/src/main/resources/xsl/cpc.xsl";
tdbAssembler="/usr/lib/fuseki/tdb.patents.ttl";
JVM_ARGS="-Xmx32000M";
db="/data/tdb/fusepool/";
javatdbloader="java $JVM_ARGS tdb.tdbloader --desc=$tdbAssembler";
