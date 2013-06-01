package eu.fusepool.platform.enhancer.engine.patent;

import org.apache.clerezza.rdf.core.UriRef;

/*
 * This class contains terms from ontologies that are not yet available
 * in org.apache.clerezza.rdf.ontologies.*
 */
public class OntologiesTerms {
	
	// Patent Metadata Ontology
	final static String PMO_NS = "http://www.patexpert.org/ontologies/pmo.owl#";
	final static UriRef pmoPatentPublication = new UriRef(PMO_NS + "PatentPublication");
}
