#!/bin/bash

namespace="http://fusepool.info/";
repoMeta="/home/fusepool/patents-reengineering/src/main/resources/data/meta/";
marec="/data/marec/";
ecla="/data/ecla/";
tdbAssembler="/usr/lib/fuseki/tdb.patents.ttl";
JVM_ARGS="-Xmx16000M";
db="/data/tdb/db/patents";
javatdbloader="java $JVM_ARGS tdb.tdbloader --desc=$tdbAssembler";
