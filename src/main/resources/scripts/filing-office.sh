#!/bin/bash

perl -pe 's/([^\s]*)\s+(.*)/<http:\/\/fusepool.info\/code\/filing-office\/$1> <http:\/\/www.w3.org\/1999\/02\/22-rdf-syntax-ns#type> <http:\/\/www.w3.org\/2004\/02\/skos\/core#Concept> .
<http:\/\/fusepool.info\/code\/filing-office\/$1> <http:\/\/www.w3.org\/2004\/02\/skos\/core#topConceptOf> <http:\/\/fusepool.info\/code\/filing-office> .
<http:\/\/fusepool.info\/code\/filing-office> <http:\/\/www.w3.org\/2004\/02\/skos\/core#hasTopConcept> <http:\/\/fusepool.info\/code\/filing-office\/$1> .
<http:\/\/fusepool.info\/code\/filing-office\/$1> <http:\/\/www.w3.org\/2004\/02\/skos\/core#inScheme> <http:\/\/fusepool.info\/code\/filing-office> .
<http:\/\/fusepool.info\/code\/filing-office\/$1> <http:\/\/www.w3.org\/2004\/02\/skos\/core#notation> "$1" .
<http:\/\/fusepool.info\/code\/filing-office\/$1> <http:\/\/www.w3.org\/2004\/02\/skos\/core#prefLabel> "$2" ./' ../data/meta/filing-office.txt > ../data/meta/filing-office.nt

echo '<http://fusepool.info/code/filing-office>  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2004/02/skos/core#ConceptScheme> .
<http://fusepool.info/code/filing-office> <http://www.w3.org/2004/02/skos/core#prefLabel> "Patent filing offices"@en .
<http://fusepool.info/code/filing-office> <http://www.w3.org/2004/02/skos/core#definition> "Country codes consist of two letters (e.g. GB) indicating the country or organisation where the patent application was filed or granted."@en .
<http://fusepool.info/code/filing-office> <http://purl.org/dc/terms/source> <http://ep.espacenet.com/help?topic=countrycodes&locale=en_ep&method=handleHelpTopic> .' >> ../data/meta/filing-office.nt
