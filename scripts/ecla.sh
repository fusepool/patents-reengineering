#!/bin/bash

saxonb-xslt -tree:linked -s ../data/ecla/ecla-A.xml -xsl ecla.xsl > ../data/ecla/ecla-A.rdf
saxonb-xslt -tree:linked -s ../data/ecla/ecla-A01B.xml -xsl ecla.xsl > ../data/ecla/ecla-A01B.rdf
saxonb-xslt -tree:linked -s ../data/ecla/ecla-A01C.xml -xsl ecla.xsl > ../data/ecla/ecla-A01C.rdf

