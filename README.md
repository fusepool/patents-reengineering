# MAREC to RDF

XSLT 2.0 templates to transform MAREC XML to RDF/XML.

## Requirements

An XSLT 2.0 processor to transform, and some configuring using the provided config.rdf file.

## What can it do?
Currently it handles bibliogrpahic-data: publication and application references, priority-claims, technical-data (classifications), parties (applicants, inventors, assignees).

## What is inside?

It comes with scripts and sample data. Tested with `saxonb-xslt` tool for Debian from command-line.

### Scripts
The `scripts/` directory contains XSLT 2.0 templates and a simple Bash script to test it on provided sample data.

### Data
There is some sample data under `data/`.

### How-to
Either use the provided Bash script for sample data, or take `marec.xsl`, `common.xsl`, and `config.rdf` to place for your application. Run marec.xsl (it imports `common.xsl` and `config.rdf`) and make sure to pass in parameter `xmlDocument=path/to/source/patent-document.xml`.

If you want to change the config, either edit `config.rdf` in place, or use `config.ttl` for a simplicty, but make sure to generate an abbreviated RDF/XML. It is used by XSL to grab some of the values. Don't touch `marec.xsl` or `common.xsl` unless you are developing!
