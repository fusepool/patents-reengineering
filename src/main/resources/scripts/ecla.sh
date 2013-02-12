#!/bin/bash

saxonb-xslt -tree:linked -s ../data/ecla/ecla-A.xml -xsl ../xsl/ecla.xsl > ../data/ecla/ecla-A.rdf
saxonb-xslt -tree:linked -s ../data/ecla/ecla-A01B.xml -xsl ../xsl/ecla.xsl > ../data/ecla/ecla-A01B.rdf
saxonb-xslt -tree:linked -s ../data/ecla/ecla-A01C.xml -xsl ../xsl/ecla.xsl > ../data/ecla/ecla-A01C.rdf

