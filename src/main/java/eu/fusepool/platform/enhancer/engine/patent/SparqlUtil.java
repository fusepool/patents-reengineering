package eu.fusepool.platform.enhancer.engine.patent;

import java.util.HashMap;
import java.util.Map;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.Resource;
import org.apache.clerezza.rdf.core.access.TcManager;
import org.apache.clerezza.rdf.core.sparql.ParseException;
import org.apache.clerezza.rdf.core.sparql.QueryParser;
import org.apache.clerezza.rdf.core.sparql.ResultSet;
import org.apache.clerezza.rdf.core.sparql.SolutionMapping;
import org.apache.clerezza.rdf.core.sparql.query.SelectQuery;
import org.apache.clerezza.rdf.jena.sparql.JenaSparqlEngine;

/*
 * Provides SPARQL queries to be used by other classes.
 */

public class SparqlUtil {
	
	// Patent properties used as variables for query and its result
		public static final String PATENT_URI = "patent";
		public static final String PATENT_TITLE = "title";
		public static final String PATENT_ABSTRACT = "abstract";
	
	/*
	 * Retrieves the patent publication uri that refers to the original document. As this publication can refer to other publication the first one must be
	 * selected using properties that are filled that are not for the mentioned patent as title and abstract. These properties can also be used for the plain text
	 * representation of the document to be indexed instead of the full XML document. 
	 */
	public Map getPatentData(MGraph mapping, TcManager tcManager) {
		
		Map<String, Resource> patentData = new HashMap<String, Resource>();
		
		String sparqlQuery = "PREFIX pmo: <http://www.patexpert.org/ontologies/pmo.owl#> " +
				 "PREFIX dcterms: <http://purl.org/dc/terms/> " +
				 "SELECT * WHERE { " +
				 "   ?" + PATENT_URI + " rdf:type pmo:PatentPublication ; " +
				 "           dcterms:title ?" + PATENT_TITLE + " ; " +
				 "	         OPTIONAL {          " +
				 "              ?" + PATENT_URI + " dcterms:abstract ?" + PATENT_ABSTRACT + " .  " +
				 "           } " +
				 "           FILTER (lang(?title) = \"en\") " +
				 "} " ;

		SelectQuery query = null;

		try {
		
			query = (SelectQuery) QueryParser.getInstance().parse(sparqlQuery);
		
		} 
		catch (ParseException e) {
		
			System.out.println("Cannot parse the query " + sparqlQuery);
		 
		}
		
		ResultSet queryResult = tcManager.executeSparqlQuery(query, mapping);
		
		if(queryResult != null) {
		
			// There must be only one patent publication that represents the document and has a title
			while(queryResult.hasNext()) {
			
				SolutionMapping isolution = queryResult.next();
	
				Resource patentUri = (Resource) isolution.get(PATENT_URI);
				patentData.put(PATENT_URI, patentUri);
				
				Resource title = (Resource) isolution.get(PATENT_TITLE);
				patentData.put(PATENT_TITLE, title);
				
				Resource abstract_ = (Resource) isolution.get(PATENT_ABSTRACT);
				patentData.put(PATENT_ABSTRACT, abstract_);
			}
		
		}
		
		return patentData;
	
	}

}
