#!/bin/bash

. ./patents.config.sh

find "$marec" -name *.rdf | while read i ; do rapper -g "$i" >> "$marec"import/marec.nt ; done ;

sort -u "$marec"import/marec.nt -o "$marec"import/marec.nt ;
