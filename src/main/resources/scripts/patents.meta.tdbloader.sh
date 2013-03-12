#!/bin/bash
. ./patents.config.sh

java "$JVM_ARGS" tdb.tdbloader --desc="$tdbAssembler" --graph="$namespace"graph/meta "$data"ecla/import/ecla.nt ;

for i in "$repoMeta"* ; do "$JVM_ARGS" java tdb.tdbloader --desc="$tdbAssembler" --graph="$namespace"graph/meta "$i" ; done
