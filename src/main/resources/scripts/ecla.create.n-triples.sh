#!/bin/bash
data="/data/ecla/";

for in in "$data"*.rdf ; do rapper -g "$i" > "$data"import/ecla.nt ; done
