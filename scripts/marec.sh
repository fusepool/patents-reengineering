#!/bin/bash

#ls -1 ../data/*.xml | sed 's/\.xml//' | while read i; do saxonb-xslt -t -tree:linked -s "$i".xml -xsl generic.xsl > "$i".rdf; echo "Created $i.rdf"; done
#for i in MAREC/*/*/*.xml ; do filename=$(basename $i) ; path=${i%/*} ; xmllint --format "$i" > /tmp/"$filename" ; mv /tmp/"$filename" "$path""/""$filename" ; done
#grep -RE "kind=\"[^\"]*\"" MAREC/*/*/*.xml | perl -pe 's/.*kind=\"([^\"]*)\".*/$1/' | sort -u

#Data from #http://www.ifs.tuwien.ac.at/imp/marec.shtml

saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/EP/00/06/EP-1000006-B1.xml -xsl ../src/main/resources/xsl/marec.xsl > ../data/marec/EP/00/06/EP-1000006-B1.rdf

saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/EP/00/00/EP-1000000-A1.xml -xsl ../src/main/resources/xsl/marec.xsl > ../data/marec/EP/00/00/EP-1000000-A1.rdf

saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/EP/00/00/EP-1000000-B1.xml -xsl ../src/main/resources/xsl/marec.xsl > ../data/marec/EP/00/00/EP-1000000-B1.rdf

saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/EP/00/06/EP-1000006-A1.xml -xsl ../src/main/resources/xsl/marec.xsl > ../data/marec/EP/00/06/EP-1000006-A1.rdf

saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/US/000000/00/00/H1/US-H1-H.xml -xsl ../src/main/resources/xsl/marec.xsl > ../data/marec/US/000000/00/00/H1/US-H1-H.rdf

saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/JP/000010/00/00/01/JP-10000001-A.xml -xsl ../src/main/resources/xsl/marec.xsl > ../data/marec/JP/000010/00/00/01/JP-10000001-A.rdf

saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/WO/001978/00/00/01/WO-1978000001-A1.xml -xsl ../src/main/resources/xsl/marec.xsl > ../data/marec/WO/001978/00/00/01/WO-1978000001-A1.rdf

saxonb-xslt -ext:on -t -tree:linked -s ../data/marec/WO/002004/04/72/28/WO-2004047228-B1.xml -xsl ../src/main/resources/xsl/marec.xsl > ../data/marec/WO/002004/04/72/28/WO-2004047228-B1.rdf
