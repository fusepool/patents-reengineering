# MAREC-RDF

XSLT 2.0 templates to transform MAREC XML to RDF/XML, ECLA to RDF/XML and supporting data.

## Requirements

An XSLT 2.0 processor to transform, and some configuring to change defaults.

## What can it do?
Currently it does two types of transformations: patents and classifications. The patents are based on the MAREC standard which is a superset of the patents from EP, US, JP, WO offices. The classifications are based on ECLA (and soon to be on CPC).

From patents, it transforms bibliogrpahic-data, publication and application references, priority-claims, technical-data (classifications), parties (applicants, inventors, assignees), claims.

There is also a small script that converts a list of filing offices from the EPO websites into N-Triples.

Classifications and filing offices are both used in patents.

## What is inside?

It comes with scripts and sample data. Tested with `saxonb-xslt` tool for Debian from command-line.

### Scripts
The `scripts/` directory contains Bash script to test on sample data.

### XSL
The `xsl/` directory is for MAREC and ECLA transformations.

### Data
There is some sample data under `data/`.

## How-to
Either use the provided Bash script for sample data, or take `marec.xsl`, `common.xsl` to your application. Run marec.xsl (it imports `common.xsl`). Same goes for `ecla.xsl`.
