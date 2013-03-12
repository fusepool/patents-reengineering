#!/bin/bash
data="/data/ecla/";

for i in "$data"*.rdf ; do rapper -g "$i" >> "$data"import/ecla.nt ; done
