#!/bin/bash
data="../data/ecla/";
xslDocument="../xsl/ecla.xsl";

for i in "$data"*.xml ; do file=$(basename "$i"); fn=${file%.*}; saxonb-xslt -t -tree:linked -s "$i" -xsl "$xslDocument" xmlDocument="$i" > "$data""$fn".rdf ; done

#saxonb-xslt -tree:linked -s ../data/ecla/ecla-A.xml -xsl ../xsl/ecla.xsl > ../data/ecla/ecla-A.rdf
#saxonb-xslt -tree:linked -s ../data/ecla/ecla-A01B.xml -xsl ../xsl/ecla.xsl > ../data/ecla/ecla-A01B.rdf
#saxonb-xslt -tree:linked -s ../data/ecla/ecla-A01C.xml -xsl ../xsl/ecla.xsl > ../data/ecla/ecla-A01C.rdf
#saxonb-xslt -tree:linked -s ../data/ecla/ecla-B01J.xml -xsl ../xsl/ecla.xsl > ../data/ecla/ecla-B01J.rdf
