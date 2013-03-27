#!/bin/bash

. ./patents.config.sh

for i in "$cpcscheme"*.rdf ; do rapper -g "$i" >> "$cpcscheme"import/cpc.nt ; done
