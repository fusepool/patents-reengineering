#!/bin/bash

. ./patents.config.sh

for i in "$eclascheme"*.rdf ; do rapper -g "$i" >> "$eclascheme"import/ecla.nt ; done
