#!/bin/bash
data="/data/ecla/";
xslDocument="/usr/lib/patents-reengineering/src/main/resources/xsl/ecla.xsl";

for i in "$data"*.xml ; do file=$(basename "$i"); fn=${file%.*}; saxonb-xslt -t -tree:linked -s "$i" -xsl "$xslDocument" xmlDocument="$i" > "$data""$fn".rdf ; done
