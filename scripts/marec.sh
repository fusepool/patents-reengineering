#!/bin/bash

#ls -1 ../data/*.xml | sed 's/\.xml//' | while read i; do saxonb-xslt -t -tree:linked -s "$i".xml -xsl generic.xsl > "$i".rdf; echo "Created $i.rdf"; done
#for i in MAREC/*/*/*.xml ; do filename=$(basename $i) ; path=${i%/*} ; xmllint --format "$i" > /tmp/"$filename" ; mv /tmp/"$filename" "$path""/""$filename" ; done
#grep -RE "kind=\"[^\"]*\"" MAREC/*/*/*.xml | perl -pe 's/.*kind=\"([^\"]*)\".*/$1/' | sort -u

#Data from #http://www.ifs.tuwien.ac.at/imp/marec.shtml

saxonb-xslt -t -tree:linked -s ../data/00/06/EP-1000006-B1.xml -xsl marec.xsl > ../data/00/06/EP-1000006-B1.rdf

saxonb-xslt -t -tree:linked -s ../data/00/00/EP-1000000-A1.xml -xsl marec.xsl > ../data/00/00/EP-1000000-A1.rdf

saxonb-xslt -t -tree:linked -s ../data/00/00/EP-1000000-B1.xml -xsl marec.xsl > ../data/00/00/EP-1000000-B1.rdf

saxonb-xslt -t -tree:linked -s ../data/00/06/EP-1000006-A1.xml -xsl marec.xsl > ../data/00/06/EP-1000006-A1.rdf


