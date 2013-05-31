#!/bin/bash

. ./patents.config.sh

pathToCPCConcordances="/var/www/patents-reengineering/data/cpc-concordances/ECLAtoCPC.xml";

#Data from #http://www.ifs.tuwien.ac.at/imp/marec.shtml

#perl -pe 's/ xmlns:xsi="http:\/\/www.w3.org\/2001\/XMLSchema-instance" xsi:noNamespaceSchemaLocation="C:\\xml\\listren.xsd"//g' CPC-concordance-12102012.xml > c.xml

java "$JVM_ARGS" -classpath "$saxonb:$xmlresolver" -Dxml.catalog.files="$pathToCatalog" -Dxml.catalog.verbosity=2 net.sf.saxon.Transform -r:org.apache.xml.resolver.tools.CatalogResolver -x org.apache.xml.resolver.tools.ResolvingXMLReader -y org.apache.xml.resolver.tools.ResolvingXMLReader -ext:on -t -tree:linked -s ../data/marec/EP/00/00/EP-1000000-A1.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/EP/00/06/EP-1000000-A1.rdf
java "$JVM_ARGS" -classpath "$saxonb:$xmlresolver" -Dxml.catalog.files="$pathToCatalog" -Dxml.catalog.verbosity=2 net.sf.saxon.Transform -r:org.apache.xml.resolver.tools.CatalogResolver -x org.apache.xml.resolver.tools.ResolvingXMLReader -y org.apache.xml.resolver.tools.ResolvingXMLReader -ext:on -t -tree:linked -s ../data/marec/EP/00/00/EP-1000000-B1.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/EP/00/06/EP-1000000-B1.rdf
java "$JVM_ARGS" -classpath "$saxonb:$xmlresolver" -Dxml.catalog.files="$pathToCatalog" -Dxml.catalog.verbosity=2 net.sf.saxon.Transform -r:org.apache.xml.resolver.tools.CatalogResolver -x org.apache.xml.resolver.tools.ResolvingXMLReader -y org.apache.xml.resolver.tools.ResolvingXMLReader -ext:on -t -tree:linked -s ../data/marec/EP/00/06/EP-1000006-A1.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/EP/00/06/EP-1000006-A1.rdf
java "$JVM_ARGS" -classpath "$saxonb:$xmlresolver" -Dxml.catalog.files="$pathToCatalog" -Dxml.catalog.verbosity=2 net.sf.saxon.Transform -r:org.apache.xml.resolver.tools.CatalogResolver -x org.apache.xml.resolver.tools.ResolvingXMLReader -y org.apache.xml.resolver.tools.ResolvingXMLReader -ext:on -t -tree:linked -s ../data/marec/EP/00/06/EP-1000006-B1.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/EP/00/06/EP-1000006-B1.rdf

java "$JVM_ARGS" -classpath "$saxonb:$xmlresolver" -Dxml.catalog.files="$pathToCatalog" -Dxml.catalog.verbosity=2 net.sf.saxon.Transform -r:org.apache.xml.resolver.tools.CatalogResolver -x org.apache.xml.resolver.tools.ResolvingXMLReader -y org.apache.xml.resolver.tools.ResolvingXMLReader -ext:on -t -tree:linked -s ../data/marec/US/000000/00/00/H1/US-H1-H.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/US/000000/00/00/H1/US-H1-H.rdf
java "$JVM_ARGS" -classpath "$saxonb:$xmlresolver" -Dxml.catalog.files="$pathToCatalog" -Dxml.catalog.verbosity=2 net.sf.saxon.Transform -r:org.apache.xml.resolver.tools.CatalogResolver -x org.apache.xml.resolver.tools.ResolvingXMLReader -y org.apache.xml.resolver.tools.ResolvingXMLReader -ext:on -t -tree:linked -s ../data/marec/JP/000010/00/00/01/JP-10000001-A.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/JP/000010/00/00/01/JP-10000001-A.rdf
java "$JVM_ARGS" -classpath "$saxonb:$xmlresolver" -Dxml.catalog.files="$pathToCatalog" -Dxml.catalog.verbosity=2 net.sf.saxon.Transform -r:org.apache.xml.resolver.tools.CatalogResolver -x org.apache.xml.resolver.tools.ResolvingXMLReader -y org.apache.xml.resolver.tools.ResolvingXMLReader -ext:on -t -tree:linked -s ../data/marec/WO/002004/04/72/28/WO-2004047228-B1.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/WO/002004/04/72/28/WO-2004047228-B1.rdf


#saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/EP/00/06/EP-1000006-A1.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/EP/00/06/EP-1000006-A1.rdf

#saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/US/000000/00/00/H1/US-H1-H.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/US/000000/00/00/H1/US-H1-H.rdf

#saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/JP/000010/00/00/01/JP-10000001-A.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/JP/000010/00/00/01/JP-10000001-A.rdf

#saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/WO/001978/00/00/01/WO-1978000001-A1.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/WO/001978/00/00/01/WO-1978000001-A1.rdf

#saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/WO/002004/04/72/28/WO-2004047228-B1.xml -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPCConcordances" > ../data/marec/WO/002004/04/72/28/WO-2004047228-B1.rdf
