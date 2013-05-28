package eu.fusepool.platform.enhancer.engine.patent;

import org.apache.clerezza.rdf.core.UriRef;

/*
 * This class contains terms from ontologies that are not yet available
 * in org.apache.clerezza.rdf.ontologies.*
 */
public class OntologiesTerms {
	
	// FISE
	final static String FISE_NS =  "http://fise.iks-project.eu/ontology/";
	final static UriRef fiseEntityReference = new UriRef("http://fise.iks-project.eu/ontology/entity-reference");
	final static UriRef fiseConfidence = new UriRef("http://fise.iks-project.eu/ontology/confidence");

}
