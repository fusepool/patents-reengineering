#!/bin/bash
. ./patents.config.sh

find ../data/marec/ -name *.rdf | while read i ; do java "$JVM_ARGS" tdb.tdbloader --desc="$tdbAssembler" --graph="$namespace"graph/patents "$i" ; done
