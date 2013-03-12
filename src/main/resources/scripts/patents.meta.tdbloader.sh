#!/bin/bash
. ./patents.config.sh

"$javatdbloader" --graph="$namespace"graph/meta "$ecla"import/ecla.nt ;

for i in "$repoMeta"* ; do "$javatdbloader" --graph="$namespace"graph/meta "$i" ; done
