/**
 * 
 */
package eu.fusepool.platform.enhancer.engine.patent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.NonLiteral;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.commons.io.IOUtils;
import org.apache.stanbol.enhancer.contentitem.inmemory.InMemoryContentItemFactory;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.ContentItemFactory;
import org.apache.stanbol.enhancer.servicesapi.EngineException;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.impl.StringSource;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.apache.clerezza.rdf.ontologies.FOAF;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openjena.atlas.logging.Log;

import eu.fusepool.platform.enhancer.engine.patent.PatentEnhancementEngine;
import eu.fusepool.platform.enhancer.engine.patent.testutil.MockComponentContext;
import eu.fusepool.platform.enhancer.engine.patent.testutil.MockLogService;

/**
 * @author giorgio
 *
 */
public class PatentEnhancementEngineTest {

	
	static PatentEnhancementEngine engine ;
	static MockComponentContext ctx ;
	
	private static ContentItemFactory ciFactory = InMemoryContentItemFactory.getInstance();

	private static ContentItem ci = null ;
	
	// The file used for these tests must not be changed. Results, such as number of entities and enhancements, depend on this file.
	// If another file is used the following values must be updated accordingly
	private static final String TEST_FILE = "EP-1000000-A1.xml";
	private static final int PERSONS_NUMBER = 3; // number of entities of type foaf:Person extracted from the test file.
	private static final int PERSONS_ENTITIES_REFERENCES = 3; // number of entity references (fise:entity-reference) created, must be equal to the number of person.
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put("CLEAN_ON_STARTUP", false) ;
		properties.put(EnhancementEngine.PROPERTY_NAME, "PatentEngine") ;
		ctx = new MockComponentContext(properties) ;
		
		engine = new PatentEnhancementEngine() ;
		engine.logService = new MockLogService() ;
		engine.parser = Parser.getInstance() ;
		Set<String> supportedFormats = engine.parser.getSupportedFormats() ;
		engine.activate(ctx) ;
		
		// creates a content item from the document and compute the enhancements
		createContentItemFromFile(TEST_FILE);
		
	}

	
	@AfterClass 
	public static void clean() {
		engine.deactivate(ctx) ;
		File test_data_folder = new File(MockComponentContext.BUNDLE_TEST_DATA_FOLDER) ;
		if(test_data_folder.exists() && test_data_folder.isDirectory())
			test_data_folder.delete() ;
	}
	
	
	/*
	 * Test if subjects of type person are found after the transformation and prints them
	 */
	
	@Test
	public void testEntities() {
		
		int personsNumber = 0;
		
		if (! ci.getMetadata().isEmpty()) {
			
			// Filter triples for persons
			Iterator<Triple> ipersons = ci.getMetadata().filter(null, RDF.type, FOAF.Person) ;
			
			while (ipersons.hasNext()){
				personsNumber += 1;
				Triple triple = ipersons.next();
				String subjectUri = triple.getSubject().toString();
				System.out.println("Filtered subject of type foaf:Person = " + subjectUri);
				
			}
			
			
		}
		else {
			System.out.println("Enhancement graph empty !");
		}
		
		assertTrue("Subjects of type foaf:Person found in the document " + personsNumber, personsNumber == PERSONS_NUMBER);
		
	}
	
	/*
	 * Test if entity-reference annotations have been added for each entity of type person.
	 */
	
	@Test
	public void testAnnotations() {
		
		int personEntityReferences = 0;
		
		if (! ci.getMetadata().isEmpty()) {
			
			// Filter triples for entities annotations
			Iterator<Triple> ireferences = ci.getMetadata().filter(null, OntologiesTerms.fiseEntityReference, null);
			while (ireferences.hasNext()) {
				personEntityReferences += 1;
				Triple triple = ireferences.next();
				String enhancement = triple.getSubject().toString();
				String entity = triple.getObject().toString();
				System.out.println("Filtered entity references: " + enhancement + " entity reference: " + entity);
			}
			
		}
		else {
			System.out.println("Enhancement graph empty !");
		}
		
		assertTrue("Subjects of type foaf:Person found in the document " + personEntityReferences, personEntityReferences == PERSONS_ENTITIES_REFERENCES);
	
	
	}
	
		
	private static void createContentItemFromFile(String fileName) {
		
		String filePath = "/test/data/" + fileName;
		try {
			InputStream in = PatentEnhancementEngineTest.class.getResourceAsStream(filePath) ;
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer);
			String theString = writer.toString();
			//System.out.println(theString);
			ci = ciFactory.createContentItem(new UriRef("urn:test:content-item:") + fileName, new StringSource(theString)) ;
			engine.computeEnhancements(ci) ;
		}
		catch (IOException e) {
			System.out.println("Error while creating content item from file " + filePath);
		} catch (EngineException e) {
			fail("Engine should not throw exception: "+e.getMessage());
		}
		
	}
	
}
