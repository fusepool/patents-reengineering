/**
 * 
 */
package eu.fusepool.enhancer.marec;

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
import org.apache.stanbol.enhancer.servicesapi.rdf.NamespaceEnum;
import org.apache.stanbol.enhancer.servicesapi.rdf.Properties;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.apache.clerezza.rdf.ontologies.FOAF;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fusepool.enhancer.marec.testutil.MockComponentContext;
import eu.fusepool.enhancer.marec.testutil.MockLogService;

/**
 * @author giorgio
 *
 */
public class MarecLifterEnhancementEngineTest {

	
	static MarecLifterEnhancementEngine engine ;
	static MockComponentContext ctx ;
	
	private static final ContentItemFactory ciFactory = InMemoryContentItemFactory.getInstance();

	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put("CLEAN_ON_STARTUP", false) ;
		properties.put(EnhancementEngine.PROPERTY_NAME, "PatentEngine") ;
		ctx = new MockComponentContext(properties) ;
		
		engine = new MarecLifterEnhancementEngine() ;
		engine.logService = new MockLogService() ;
		engine.parser = Parser.getInstance() ;
		Set<String> supportedFormats = engine.parser.getSupportedFormats() ;
		engine.activate(ctx) ;
	}

	
	@AfterClass 
	public static void clean() {
		engine.deactivate(ctx) ;
		File test_data_folder = new File(MockComponentContext.BUNDLE_TEST_DATA_FOLDER) ;
		if(test_data_folder.exists() && test_data_folder.isDirectory())
			test_data_folder.delete() ;
	}
	
	
	@Test
	public void testCanEnhance() {
		ContentItem ci = null ;
		try {
			
			ci = createContentItemFromFile("EP-1000000-A1.xml");
			
			engine.canEnhance(ci);
		} 
		catch (IOException e) {
			fail("No data. Exception  thrown: "+e.getMessage());
		}
		catch (EngineException e) {
			
			fail("Engine should not throw exception: "+e.getMessage());
		}	
				
	}
	
	
	/*
	 * Test if subjects of type person are found after the transformation
	 */
	
	@Test
	public void testEntities() {
		
		ContentItem ci01 = null ;
		int personsNumber = 0;
		
		try {
			ci01 = createContentItemFromFile("EP-1000000-A1.xml");
		} catch (IOException e) {
			fail("No data. Exception  thrown: "+e.getMessage());
		}
		try {
			engine.computeEnhancements(ci01) ;
		} catch (EngineException e) {
			
			fail("Engine should not throw exception: "+e.getMessage());
		}
		
		MGraph rdfGraph = ci01.getMetadata() ;
		
		if (! rdfGraph.isEmpty()) {
			System.out.println("Enhancement graph has data !");
			
			Iterator<Triple> ipersons = rdfGraph.filter(null, RDF.type, FOAF.Person) ;
			
			while (ipersons.hasNext()){
				Triple triple = ipersons.next();
				String subjectUri = triple.getSubject().toString();
				System.out.println("Filtered subject of type foaf:Person = " + subjectUri);
				personsNumber += 1;
			}
		}
		else {
			System.out.println("Enhancement graph empty !");
		}
		
		assertTrue("Three subjects of type foaf:Person found in the document.", personsNumber == 3);
		
	}
	
	
	/*
	 * Test if annotations have been created for each entity.
	 */
	@Test
	public void testEntityAnnotations() {
		
		try {
			ContentItem ci = createContentItemFromFile("EP-1000000-A1.xml");
			
			engine.computeEnhancements(ci);
			
			if( ! ci.getMetadata().isEmpty()) {
				Iterator<Triple> ipersons = ci.getMetadata().filter(null, RDF.type, FOAF.Person);
				if(ipersons != null) {
					while(ipersons.hasNext()) {
						NonLiteral subject = ci.getMetadata().filter(null, RDF.type, FOAF.Person).next().getSubject();
						Iterator<Triple> ireference = ci.getMetadata().filter(subject, Properties.ENHANCER_ENTITY_REFERENCE, null);
						
//						while(ireference.hasNext()) {
//							Triple reference = ireference.next();
//							String subRef = reference.getSubject().toString();
//							String predRef = reference.getPredicate().toString();
//							String objRef = reference.getObject().toString();
//							System.out.println(subRef + " " + predRef + " " + objRef);
//						}
					}
				}
				else {
					System.out.println("No entities found!");
				}
			}
			else {
				System.out.println("No metadata !");
			}
			
			
		} 
		catch (IOException e) {
			fail("No data. Exception  thrown: " + e.getMessage());
		}
		catch (EngineException e) {
			fail("Engine should not throw exception: " + e.getMessage());
		}
		
		//assertTrue("All entities have been annotated", allEntitiesEnhanced);
	}
	
	/*
	@Test
	public void testFile01() {
		ContentItem ci01 = null ;
		try {
			ci01 = createContentItemFromFile("EP-1000000-A1.xml");
		} catch (IOException e) {
			fail("No data. Exception  thrown: "+e.getMessage());
		}
		try {
			engine.computeEnhancements(ci01) ;
		} catch (EngineException e) {
			
			fail("Engine should not throw exception: "+e.getMessage());
		}	
		
		assertFalse("Metadata should not be empty ",ci01.getMetadata().isEmpty()) ;
		//fail("Facciamo una prova...") ;
	
	}
	*/

	/*
	@Test
	public void testFile02() {
		ContentItem ci02 = null ;
		try {
			ci02 = createContentItemFromFile("EP-1000000-B1.xml");
		} catch (IOException e) {
			fail("No data. Exception  thrown: "+e.getMessage());
		}
		try {
			engine.computeEnhancements(ci02) ;
		} catch (EngineException e) {
			
			fail("Engine should not throw exception: "+e.getMessage());
		}	
		
		assertFalse("Metadata should not be empty ",ci02.getMetadata().isEmpty()) ;
	
	}
	*/
	
	/*
	@Test
	public void testFile03() {
		ContentItem ci02 = null ;
		try {
			ci02 = createContentItemFromFile("wrong-data.rdf");
		} catch (IOException e) {
			fail("No data. Exception  thrown: "+e.getMessage());
		}
		try {
			engine.computeEnhancements(ci02) ;
		} catch (EngineException e) {
			
			fail("Engine should not throw exception: "+e.getMessage());
		}	
		assertTrue("Metadata should be empty ",ci02.getMetadata().isEmpty()) ;
		//assertTrue(true) ;
	
	}
	*/
	
	private ContentItem createContentItemFromFile(String fileName) throws IOException {
		InputStream in = this.getClass().getResourceAsStream("/test/data/"+fileName) ;
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer);
		String theString = writer.toString();
		//System.out.println(theString);
		ContentItem ci = ciFactory.createContentItem(new UriRef("urn:test:content-item:")+fileName, new StringSource(theString)) ;
		return ci ;
	}
	
}
