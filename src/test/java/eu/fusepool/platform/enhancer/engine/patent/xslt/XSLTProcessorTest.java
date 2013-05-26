/**
 * 
 */
package eu.fusepool.platform.enhancer.engine.patent.xslt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.fusepool.platform.enhancer.engine.patent.xslt.XMLProcessor;
import eu.fusepool.platform.enhancer.engine.patent.xslt.impl.PatentXSLTProcessor;
import eu.fusepool.platform.enhancer.engine.patent.xslt.impl.XSLOLDTProcessor;



/**
 * @author giorgio
 *
 */
public class XSLTProcessorTest {

	

	private final String sparqlQuery =	"PREFIX pmo: <http://www.patexpert.org/ontologies/pmo.owl#>\n"+
											"PREFIX dcterms: <http://purl.org/dc/terms/>\n"+
											"PREFIX sumo: <http://www.owl-ontologies.com/sumo.owl#>\n"+
											"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"+
											"SELECT ?inventor\n"+ 
											"WHERE { \n"+
											 	"?invention pmo:inventor ?inventor_uri .\n"+
											 	"?inventor_uri foaf:name ?inventor .\n"+
											 "}" ;	
									
	

	private XMLProcessor processor ;
	
	
	@Before
	public void setup() {
		processor = new PatentXSLTProcessor() ;
	}
	
	/**
	 * Test method for {@link eu.fusepool.platform.enhancer.engine.patent.xslt.impl.PatentXSLTProcessor#processPatentXML(java.io.InputStream)}.
	 */
	@Test
	public void testProcessPatentXML01() {
		//processor = new XSLTProcessor() ;
		InputStream xslIs = this.getClass().getResourceAsStream("/test/data/EP-1000000-A1.xml") ;
		try {
			@SuppressWarnings("unused")
			InputStream rdfIs = processor.processPatentXML(xslIs) ;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage()) ;
		}
		//fail("Not yet implemented");
		assertTrue("Test processing XML data",true) ;
	}
	
	@Test
	public void testProcessPatentXML02() {
		//processor = new XSLTProcessor() ;
		InputStream xslIs = this.getClass().getResourceAsStream("/test/data/EP-1000000-B1.xml") ;
		
		try {
			InputStream rdfIs = processor.processPatentXML(xslIs) ;
			Model model = getJenaModel() ;
			model.read(rdfIs, null) ;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage()) ;
		}
		assertTrue("Extracted incorrect RDF data",true) ;
	}
	
	

	@Test
	public void testProcessPatentXML03() {
		//processor = new XSLTProcessor() ;
		
		String fileToParse = "/test/data/EP-1000000-B1.xml" ;
		String inventorName = null ;
		try {
			InputStream xmlIs = this.getClass().getResourceAsStream(fileToParse) ;
			
		    NodeList nodes = getInventorNamesfromXML(xmlIs) ;
		    assertNotNull(nodes) ; // one inventor name at least
		    assertEquals(1, nodes.getLength()) ; // only one inventor in this case

		    Node curNode = nodes.item(0) ;
		    inventorName = curNode.getTextContent();
		    xmlIs = this.getClass().getResourceAsStream(fileToParse) ;
		    InputStream rdfIs = processor.processPatentXML(xmlIs) ;
		    
			Model model = getJenaModel() ;
			model.read(rdfIs, null) ;
			
			

			Query query = QueryFactory.create(sparqlQuery);
			
			// Execute the query and obtain results
			QueryExecution qe = QueryExecutionFactory.create(query, model);
			ResultSet results = qe.execSelect();
			
			boolean inventorFound = false ;
			// we MUST have one inventor name at least
			assertTrue("Test data not found",results.hasNext()) ;
		
			while(results.hasNext()) {
				QuerySolution solution = results.next() ;
				Literal invNameLiteral = solution.getLiteral("inventor") ;
				String iName = invNameLiteral.getString() ;
				inventorFound = inventorName.equals(iName) ;
				if(inventorFound)
					break ;
			}
			
			// Important - free up resources used running the query
			qe.close();

			// we have the correct inventor name
			assertTrue("Checking rdf data against processed XML failed", inventorFound) ;
						
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage()) ;
		}

		assertTrue(true) ;
	}
	
	/**
	 * Jena Model factory method. It's used for rdf correctness checking
	 * 
	 * @return
	 */
	protected Model getJenaModel() {
		Model m = ModelFactory.createMemModelMaker().createDefaultModel();
		return m ;
	}

	
	/**
	 * DocumentBuilder factory
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 */
	protected DocumentBuilder getDocBuilder() throws ParserConfigurationException {
	    DocumentBuilderFactory domFactory = 
	    	    DocumentBuilderFactory.newInstance();
	    	          domFactory.setNamespaceAware(true); 
	    	    DocumentBuilder builder = domFactory.newDocumentBuilder();
	    	    return builder ;
	}
	
	
	
	/**
	 * Extract the inventor name(s) from XML
	 * 
	 * @param xmlIs
	 * @return
	 * @throws Exception
	 */
	protected NodeList getInventorNamesfromXML(InputStream xmlIs) throws Exception {
		DocumentBuilder docBuilder = getDocBuilder() ;
		Document doc = docBuilder.parse(xmlIs);
		XPath xpath = XPathFactory.newInstance().newXPath();
	       // XPath Query 
	    XPathExpression expr = xpath.compile("//patent-document/bibliographic-data/parties/inventors/inventor[@format='epo']/addressbook/name");
	    Object result = expr.evaluate(doc, XPathConstants.NODE);
	    NodeList nodes = (NodeList) result;
	    return nodes ;
	}
	
	
}
