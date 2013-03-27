#!/bin/bash

. ./patents.config.sh

marec="/data/IREC/EP/000000/00/";
marecirdf="/data/marec/";

find "$marec" -name *.xml | while read i ; do path=${i%/*} ; file=$(basename $i) ; fn=${file%.*}; saxonb-xslt -ext:on -tree:linked -s "$i" -xsl ../src/main/resources/xsl/marec.xsl pathToCPCConcordances="$pathToCPConcordances" > "$marecrdf""$fn".rdf ; done

#ls -1 ../data/*.xml | sed 's/\.xml//' | while read i; do saxonb-xslt -t -tree:linked -s "$i".xml -xsl generic.xsl > "$i".rdf; echo "Created $i.rdf"; done
#for i in MAREC/*/*/*.xml ; do filename=$(basename $i) ; path=${i%/*} ; xmllint --format "$i" > /tmp/"$filename" ; mv /tmp/"$filename" "$path""/""$filename" ; done
#grep -RE "kind=\"[^\"]*\"" MAREC/*/*/*.xml | perl -pe 's/.*kind=\"([^\"]*)\".*/$1/' | sort -u

#Data from #http://www.ifs.tuwien.ac.at/imp/marec.shtml

#perl -pe 's/ xmlns:xsi="http:\/\/www.w3.org\/2001\/XMLSchema-instance" xsi:noNamespaceSchemaLocation="C:\\xml\\listren.xsd"//g' CPC-concordance-12102012.xml > c.xml

