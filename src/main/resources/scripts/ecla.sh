#!/bin/bash
for i in "$ecla"*.xml ; do file=$(basename "$i"); fn=${file%.*}; saxonb-xslt -t -tree:linked -s "$i" -xsl "$xslECLA" xmlDocument="$i" > "$ecla""$fn".rdf ; done
