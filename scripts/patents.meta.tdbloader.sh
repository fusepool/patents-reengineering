#!/bin/bash
. ./patents.config.sh

#java "$JVM_ARGS" tdb.tdbloader --desc="$tdbAssembler" --graph="$namespace"graph/meta "$ecla"import/ecla.nt ;

java "$JVM_ARGS" tdb.tdbloader --desc="$tdbAssembler" --graph="$namespace"graph/meta "$cpcscheme"import/cpc.nt ;

for i in "$repoMeta"* ; do java "$JVM_ARGS" tdb.tdbloader --desc="$tdbAssembler" --graph="$namespace"graph/meta "$i" ; done
