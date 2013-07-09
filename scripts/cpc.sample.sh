#!/bin/bash
cpcscheme="../data/cpc-scheme/";
xslDocument="../src/main/resources/xsl/cpc.xsl";

for i in "$cpcscheme"*.xml ; do file=$(basename "$i"); fn=${file%.*}; saxonb-xslt -ext:on -t -tree:linked -s "$i" -xsl "$xslDocument" xmlDocument="$i" > "$cpcscheme""$fn".rdf ; done

#saxonb-xslt -tree:linked -s ../data/ecla/ecla-A.xml -xsl ../xsl/ecla.xsl > ../data/ecla/ecla-A.rdf
#saxonb-xslt -tree:linked -s ../data/ecla/ecla-A01B.xml -xsl ../xsl/ecla.xsl > ../data/ecla/ecla-A01B.rdf
#saxonb-xslt -tree:linked -s ../data/ecla/ecla-A01C.xml -xsl ../xsl/ecla.xsl > ../data/ecla/ecla-A01C.rdf
#saxonb-xslt -tree:linked -s ../data/ecla/ecla-B01J.xml -xsl ../xsl/ecla.xsl > ../data/ecla/ecla-B01J.rdf
