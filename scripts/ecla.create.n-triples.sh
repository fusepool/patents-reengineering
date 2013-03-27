#!/bin/bash

. ./patents.config.sh

for i in "$eclascheme"*.rdf ; do rapper -g "$i" >> "$eclascheme"import/ecla.nt ; done

sort -u "$eclascheme"import/ecla.nt -o "$eclascheme"import/ecla.nt ;
