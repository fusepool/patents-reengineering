package eu.fusepool.platform.enhancer.engine.patent;

import org.apache.clerezza.rdf.core.UriRef;

/*
 * This class contains terms from ontologies that are not yet available
 * in org.apache.clerezza.rdf.ontologies.*
 */
public class OntologiesTerms {
	
	// FISE
	final static String FISE_NS =  "http://fise.iks-project.eu/ontology/";
	final static UriRef fiseEntityReference = new UriRef(FISE_NS + "entity-reference");
	final static UriRef fiseConfidence = new UriRef(FISE_NS + "confidence");

	// Patent Metadata Ontology
	final static String PMO_NS = "http://www.patexpert.org/ontologies/pmo.owl#";
	final static UriRef pmoPatentPublication = new UriRef(PMO_NS + "PatentPublication");
}
