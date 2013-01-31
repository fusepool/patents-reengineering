# MAREC to RDF

XSLT 2.0 templates to transform MAREC XML to RDF/XML.

## Requirements

An XSLT 2.0 processor to transform, and some configuring to change defaults.

## What can it do?
Currently it handles bibliogrpahic-data: publication and application references, priority-claims, technical-data (classifications), parties (applicants, inventors, assignees).

## What is inside?

It comes with scripts and sample data. Tested with `saxonb-xslt` tool for Debian from command-line.

### Scripts
The `scripts/` directory contains XSLT 2.0 templates and a simple Bash script to test it on provided sample data.

### Data
There is some sample data under `data/`.

## How-to
Either use the provided Bash script for sample data, or take `marec.xsl`, `common.xsl` to your application. Run marec.xsl (it imports `common.xsl`).

If you want to change the config, edit `common.xsl` directly becauase apparently having a config in the first place was a bit too confusing for some scientist / engineer extraordinaires - pretty standard practice to have config but away it went (see commit 99f479aa249728acb9aa594614a608d394b22d5b).
