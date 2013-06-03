package eu.fusepool.platform.enhancer.engine.patent;


import static org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper.randomUUID;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.NonLiteral;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.PlainLiteralImpl;
import org.apache.clerezza.rdf.core.impl.TripleImpl;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;
import org.apache.clerezza.rdf.ontologies.DCTERMS;
import org.apache.clerezza.rdf.ontologies.FOAF;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.indexedgraph.IndexedMGraph;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.ContentSource;
import org.apache.stanbol.enhancer.servicesapi.EngineException;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.InvalidContentException;
import org.apache.stanbol.enhancer.servicesapi.ServiceProperties;
import org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper;
import org.apache.stanbol.enhancer.servicesapi.impl.AbstractEnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.impl.ByteArraySource;
import org.apache.stanbol.enhancer.servicesapi.rdf.TechnicalClasses;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fusepool.platform.enhancer.engine.patent.xslt.CatalogBuilder;
import eu.fusepool.platform.enhancer.engine.patent.xslt.XMLProcessor;
import eu.fusepool.platform.enhancer.engine.patent.xslt.impl.PatentXSLTProcessor;

/**
 * Transform an XML patent document into RDF mapping elements and attributes to terms in ontologies.
 * @author Giorgio Costa
 * @author Luigi Selmi
 *
 */

@Component(immediate = true, metatype = true)
@Service
@Properties(value={
		@Property(name=EnhancementEngine.PROPERTY_NAME, value=PatentEnhancementEngine.DEFAULT_ENGINE_NAME),
		@Property(name=Constants.SERVICE_RANKING,intValue=PatentEnhancementEngine.DEFAULT_SERVICE_RANKING),
		@Property(name="CLEAN_ON_STARTUP", boolValue=PatentEnhancementEngine.DEF_CLEAN)
})
public class PatentEnhancementEngine 
extends AbstractEnhancementEngine<IOException,RuntimeException> 
implements EnhancementEngine, ServiceProperties {

	public static final String DEFAULT_ENGINE_NAME = "PatentEngine";
	/**
	 * Default value for the {@link Constants#SERVICE_RANKING} used by this engine.
	 * This is a negative value to allow easy replacement by this engine depending
	 * to a remote service with one that does not have this requirement
	 */
	public static final int DEFAULT_SERVICE_RANKING = 101;
	/**
	 * The default value for the Execution of this Engine. Currently set to
	 * {@link ServiceProperties#ORDERING_EXTRACTION_ENHANCEMENT}
	 */
	public static final Integer defaultOrder = ORDERING_EXTRACTION_ENHANCEMENT;

	
	
	// MIME TYPE of the patent document
	private static final String MIME_TYPE_XML = "application/xml";
	
	
	//@SuppressWarnings("unused")
	public static final boolean DEF_CLEAN = false ;
	public static boolean CLEAN_ON_STARTUP = false ;
	
	
	/**
	 * The literal factory
	 */
	//private final LiteralFactory literalFactory = LiteralFactory.getInstance();

	public static final Set<String> supportedMediaTypes;
	static {
		Set<String> types = new HashSet<String>();
		//ensure everything is lower case
		types.add(SupportedFormat.N3.toLowerCase());
		types.add(SupportedFormat.N_TRIPLE.toLowerCase());
		types.add(SupportedFormat.RDF_JSON.toLowerCase());
		types.add(SupportedFormat.RDF_XML.toLowerCase());
		types.add(SupportedFormat.TURTLE.toLowerCase());
		types.add(SupportedFormat.X_TURTLE.toLowerCase());
		supportedMediaTypes = Collections.unmodifiableSet(types);
	}
	
	protected ComponentContext componentContext ;

	protected CatalogBuilder catalogBuilder ;
	

	
	final Logger logger = LoggerFactory.getLogger(this.getClass()) ;
	
	@Reference
	protected Parser parser ;
	


	protected void activate(ComponentContext ce) throws IOException, ConfigurationException {
		super.activate(ce);
		this.componentContext = ce ;
		
		@SuppressWarnings("rawtypes")
		Dictionary dict = ce.getProperties() ;
		Object o = dict.get("CLEAN_ON_STARTUP") ;
		if(o!=null)  {
			CLEAN_ON_STARTUP = (Boolean) o ;
		}
		
		catalogBuilder = new CatalogBuilder(ce.getBundleContext()) ;
		try {
			catalogBuilder.build() ;
		} catch (Exception e1) {
			logger.error("Error building dtd catalog", e1) ;
		}
		
		logger.info("PatentEngine being activated " + this.getClass().getName());
	}

	protected void deactivate(ComponentContext ce) {
		super.deactivate(ce);
		catalogBuilder.cleanupFiles() ;
	}

	/*
	 * Check if content is present and mime type is correct (application/xml).
	 * 
	 */
	public int canEnhance(ContentItem ci) throws EngineException {
		 
        try {
            if ((ci.getBlob() == null)
                    || (ci.getBlob().getStream().read() == -1)) {
                return CANNOT_ENHANCE;
            }
            
            // MIME Type of the input patent document must be application/xml
            if (! ci.getMimeType().equals(MIME_TYPE_XML)) {
            	return CANNOT_ENHANCE;
            }
            
            
        } catch (IOException e) {
        	logger.warn("Failed to get the text for "
                    + "enhancement of content: " + ci.getUri() + " or wrong MIME TYPE (must be application/xml)");
            throw new InvalidContentException(this, ci, e);
        }
        // no reason why we should require to be executed synchronously
        return ENHANCE_ASYNC;
        
        // or there are some reasons ?
		//return ENHANCE_SYNCHRONOUS;
	}

	@Override
	public void computeEnhancements(ContentItem ci) throws EngineException {
		UriRef contentItemId = ci.getUri();
		logger.info("UriRef: "+contentItemId.getUnicodeString()) ;			
		
		 
		try {
				
			//ci.getLock().writeLock().lock();
			
			// Transform the patent XML file into RDF
			MGraph xml2rdf = transformXML(ci);
			
			// Create enhancements to each entity extracted from the XML
			MGraph enhancements = addEnhancements(ci, xml2rdf);
			
			// Add all the RDF triples to the content item metadata
			ci.getMetadata().addAll(xml2rdf);
			ci.getMetadata().addAll(enhancements);
			
			// Add a part to the content item as a text/plain representation of the XML document for indexing
			addPartToContentItem(ci);
			
			
		} catch (Exception e) {
			logger.error( "", e) ;			
		} 
		/*
		finally {
			ci.getLock().writeLock().unlock();
		}
		*/
		
	}
	
	/*
	 * Transform patent XML documents into RDF using an XSLT transformation and add the graph to the content item metadata.
	 */
	public MGraph transformXML(ContentItem ci) throws EngineException {
		
		MGraph xml2rdf = null;
		
		XMLProcessor processor = new PatentXSLTProcessor() ;
		InputStream rdfIs = null ; 
	
		logger.debug("Starting transformation from XML to RDF");
		
		try {
			
			xml2rdf = new IndexedMGraph();
			rdfIs = processor.processPatentXML(ci.getStream()) ;
			parser.parse(xml2rdf, rdfIs, SupportedFormat.RDF_XML) ;
			rdfIs.close() ;
			
			
		} catch (Exception e) {
			logger.error("Wrong data format for the " + this.getName() + " enhancer.", e) ;
		}
		
		logger.debug("Finished transformation from XML to RDF");
		
		return xml2rdf;
		
	}
	
	/*
	 *  Add a part to the content item as a text/plain representation of the XML document. This is the part of the content that will be indexed
	 *  by the Enhanced Content Store (ECS). The part text is constructed from triples properties values so 
	 *  this method must be called after the xml to rdf transformation.
	 */
	
	public void addPartToContentItem(ContentItem ci) {
		
		logger.debug("Start adding plain text representation");
		
		try {
			
			UriRef partUri = new UriRef("urn:fusepool-patent-engine:part-01:" + randomUUID()); // part uri with index 1 (part with index 0 is reserved to the input data)
			// Add the same content of the document as text/plain. This part can contain some
			// text extracted from the full content for indexing as title and abstract
			
			// full document with xml tags
			//byte [] content = IOUtils.toByteArray(ci.getBlob().getStream());
			
			// construct the text for the part from triples properties values
			@SuppressWarnings("deprecation")
			byte [] content = IOUtils.toByteArray(constructText(ci.getMetadata()));
			// Add some content to the new part as plain text 
			ContentSource source = new ByteArraySource(content, "text/plain");
			ci.addPart(ci.getUri(), source);
			
			// Add metadata about the new part of the content item
			ci.getMetadata().add(new TripleImpl(ci.getUri(), DCTERMS.hasPart, partUri));
			
		}
		catch (IOException e) {
			
			logger.error("Error adding text/plain part", e) ;
			
		}
		
		
		logger.debug("Finished adding plain text representation");
	}
	
	/*
	 * Create an entity annotation for each entity found by the transformation of the XML document. 
	 * Each annotation is referred to its entity. The types of entities are: foaf:Person, foaf:Agent,
	 * pmo:PatentPublication, schema:PostalAddress
	 * 
	 */
	public MGraph addEnhancements(ContentItem ci, MGraph mapping) {
		
		MGraph enhancements = new IndexedMGraph();
		
		//System.out.println("Start adding annotation");
		
		if (! mapping.isEmpty()) {
			
			// Create an enhancement for each entity of type foaf:Person
			Iterator<Triple> ipersons = mapping.filter(null, RDF.type, FOAF.Person) ;
			createAnnotations(ci, enhancements, ipersons);
			
			// Create an enhancement for each entity of type foaf:Agent
			Iterator<Triple> iagents = mapping.filter(null, RDF.type, FOAF.Agent) ;
			createAnnotations(ci, enhancements, iagents);
			
			
			// Create one enhancement for one entity of type pmo:PatentPublication that is directly related to the input XML patent document.	
			UriRef patentUri = getPatentUri(mapping);
			if( patentUri != null) {
				UriRef patentEnhancement = EnhancementEngineHelper.createEntityEnhancement(ci, this) ;
				// add a triple to link the enhancement to the entity			
				enhancements.add( new TripleImpl(patentEnhancement, OntologiesTerms.fiseEntityReference, patentUri) );
				// add the confidence level
				enhancements.add( new TripleImpl(patentEnhancement, TechnicalClasses.FNHANCER_CONFIDENCE_LEVEL, new PlainLiteralImpl("1.0")) );
			}
			
			
			
		}
		
		//System.out.println("Finished adding annotation");
		
		return enhancements;
		
	}
	
	/*
	 * Creates enhancements from triples.
	 */
	private void createAnnotations(ContentItem ci, MGraph annotations, Iterator<Triple> itriple) {
		
		while (itriple.hasNext()) {
			// create an entity annotation
			UriRef enhancement = EnhancementEngineHelper.createEntityEnhancement(ci, this) ;
			
			Triple statement = itriple.next();
			NonLiteral entity = statement.getSubject(); 
			
			// add a triple to link the enhancement to the entity
			annotations.add( new TripleImpl(enhancement, OntologiesTerms.fiseEntityReference, entity) );
			//System.out.println("entity reference: " + entityReference.toString());
			
			// add a confidence value
			annotations.add( new TripleImpl(enhancement, TechnicalClasses.FNHANCER_CONFIDENCE_LEVEL, new PlainLiteralImpl("1.0")) );
			//System.out.println("confidence: " + confidence.toString());
			
		}
		
	}
	
	/*
	 * Retrieves the patent publication uri that refers to the original document. As this publication can refer to other publication the first one must be
	 * selected using properties that are filled that are not for the mentioned patent as title and abstract. These properties can also be used for the plain text
	 * representation of the document to be indexed instead of the full XML document. 
	 */
	public UriRef getPatentUri(MGraph mapping) {
		
		UriRef patentUri = null;
		
		Iterator<Triple> ipatents = mapping.filter(null, RDF.type, OntologiesTerms.pmoPatentPublication) ;
		
		while(ipatents.hasNext()) {
			
			UriRef patentUriTemp = (UriRef) ipatents.next().getSubject();
			// Filter triple with a pmo:PatentPublication as subject and with dcterms:title property filled. There exist only one such triple in each 
			// document.
			Iterator<Triple> ipatentWithTitle = mapping.filter(patentUriTemp, DCTERMS.title, null);
			while(ipatentWithTitle.hasNext()) {
				patentUri = (UriRef) ipatentWithTitle.next().getSubject(); 
			}
			
		}
				
		return patentUri;
	
	}
	
	/*
	 * Creates a string filled with values from properties:
	 * foaf:name of inventors and applicants
	 * dcterms:title of the patent publication
	 * dcterms:abstract of the patent publication
	 * The text is used for indexing. The graph passed as argument must contain the RDF triples created after the transformation.
	 * 
	 */
	public String constructText(MGraph graph) {
		String text = "";
		
		UriRef patentUri = getPatentUri(graph);
		
		// Get the titles. There might be three titles for en, fr, de.
		Iterator<Triple> ititles = graph.filter(patentUri, DCTERMS.title, null);
		String title = "";
		while(ititles.hasNext()) {
			title = ititles.next().getObject().toString() + " ";
			text += title;
		}
		
		
		// Get the abstracts. There might be three abstracts for en, fr, de.
		Iterator<Triple> iabstracts = graph.filter(patentUri, DCTERMS.abstract_, null);
		String abstract_ = " ";
		while(iabstracts.hasNext()) {
			title = iabstracts.next().getObject().toString() + " ";
			text += abstract_;
		}
		
		// Get all the foaf:name of entities of type foaf:Person.
		Iterator<Triple> inames = graph.filter(null, FOAF.name, null);
		String name = "";
		while(inames.hasNext()) {
			title = inames.next().getObject().toString() + " ";
			text += name;
		}
		
		logger.info("Text to be indexed" + text);
		
		return text;
	}
	
	

	@Override
	public Map<String, Object> getServiceProperties() {
		return Collections.unmodifiableMap(Collections.singletonMap(
				ENHANCEMENT_ENGINE_ORDERING, (Object) defaultOrder));
	}


	//@Activate
	public void registered(ServiceReference ref) {
		logger.info(this.getClass().getName()
				+" registered") ;
	}

	//@Deactivate
	public void unregistered(ServiceReference ref) {
		logger.info( this.getClass().getName()+" unregistered") ;
	}



	/**
	 * Converts the type and the subtype of the parsed media type to the
	 * string representation as stored in {@link #supportedMediaTypes} and then
	 * checks if the parsed media type is contained in this list.
	 * @param mediaType the MediaType instance to check
	 * @return <code>true</code> if the parsed media type is not 
	 * <code>null</code> and supported. 
	 */
	@SuppressWarnings("unused")
	private boolean isSupported(String mediaType){
		return mediaType == null ? false : supportedMediaTypes.contains(
				mediaType.toLowerCase());
	}


}
