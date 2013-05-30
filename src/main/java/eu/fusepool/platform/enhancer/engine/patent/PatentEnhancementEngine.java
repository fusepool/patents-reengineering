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
import org.apache.clerezza.rdf.core.access.TcManager;
import org.apache.clerezza.rdf.core.impl.PlainLiteralImpl;
import org.apache.clerezza.rdf.core.impl.TripleImpl;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;
import org.apache.clerezza.rdf.ontologies.DCTERMS;
import org.apache.clerezza.rdf.ontologies.FOAF;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.indexedgraph.IndexedMGraph;
import org.apache.stanbol.enhancer.contentitem.inmemory.InMemoryContentItemFactory;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.ContentItemFactory;
import org.apache.stanbol.enhancer.servicesapi.ContentSink;
import org.apache.stanbol.enhancer.servicesapi.EngineException;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.InvalidContentException;
import org.apache.stanbol.enhancer.servicesapi.ServiceProperties;
import org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper;
import org.apache.stanbol.enhancer.servicesapi.impl.AbstractEnhancementEngine;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fusepool.platform.enhancer.engine.patent.xslt.CatalogBuilder;
import eu.fusepool.platform.enhancer.engine.patent.xslt.XMLProcessor;
import eu.fusepool.platform.enhancer.engine.patent.xslt.impl.PatentXSLTProcessor;

/**
 * Transform an XML patent document into RDF mapping elements and attributes to terms in ontologies.
 * @author giorgio
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

	
	private static final ContentItemFactory ciFactory = InMemoryContentItemFactory.getInstance();
	

	//private static final Logger log = LoggerFactory.getLogger(MarecLifterEnhancementEngine.class);
	private TCServiceLocator serviceLocator ;
	
	// MIME TYPE of the patent document
	public static final String MIME_TYPE_XML = "application/xml";
	
	// Patent properties used as variables for query and its result
	public static final String PATENT_URI = "patent";
	public static final String PATENT_TITLE = "title";
	public static final String PATENT_ABSTRACT = "abstract";
	
	
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
	
	@Reference
    private TcManager tcManager;
	

	//@SuppressWarnings("unchecked")
	protected void activate(ComponentContext ce) throws IOException, ConfigurationException {
		super.activate(ce);
		this.componentContext = ce ;
		
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
            
            // Currently 2013 May 27 the ECS accepts only text/plain as mime type so in order to test the engine within the default chain
            // used by the ECS the following check is commented.
            
            if (! ci.getMimeType().equals(this.MIME_TYPE_XML)) {
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
						
			// Add a part to the content item as a text/plain representation of the XML document 
			addPartToContentItem(ci);
			
			// Create enhancements to each entity extracted from the XML
			MGraph enhancements = addEnhancements(ci, xml2rdf);
			
			// Add all the RDF triples to the content item metadata
			ci.getMetadata().addAll(xml2rdf);
			ci.getMetadata().addAll(enhancements);
			
			
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
		
		MGraph mapping = null;
		
		XMLProcessor processor = new PatentXSLTProcessor() ;
		InputStream rdfIs = null ; 
	
		//System.out.println("Starting transformation from XML to RDF");
		
		try {
			
			mapping = new IndexedMGraph();
			rdfIs = processor.processPatentXML(ci.getStream()) ;
			parser.parse(mapping, rdfIs, SupportedFormat.RDF_XML) ;
			rdfIs.close() ;
			
			
		} catch (Exception e) {
			logger.error("Wrong data format for the " + this.getName() + " enhancer.", e) ;
		}
		
		//System.out.println("Finished transformation from XML to RDF");
		
		return mapping;
		
	}
	
	/*
	 *  Add a part to the content item as a text/plain representation of the XML document. This is the part of the content that will be indexed
	 *  by the Enhanced Content Store (ECS). The part can contain the full document or just some relevant elements such as title and abstract.
	 */
	public void addPartToContentItem(ContentItem ci)  {
		
		//System.out.println("Start adding plain text representation");
		
		try {
		
			InputStream toCopy = ci.getStream() ;
			
			UriRef partUri = new UriRef("urn:fusepool-patent-engine:plain-text:" + randomUUID());
			ContentSink plainTextSink = ciFactory.createContentSink("text/plain");
			
			ci.addPart(partUri, plainTextSink.getBlob());
		
		}
		catch (IOException e) {
			logger.error("Error adding text/plain part", e) ;
			//e.printStackTrace();
		}
		
		
		//System.out.println("Finished adding plain text representation");
	}
	
	/*
	 * Create an entity annotation for each entity found by the transformation of the XML document. 
	 * Each annotation is referred to its entity. The types of entities are
	 * 
	 */
	public MGraph addEnhancements(ContentItem ci, MGraph mapping) {
		
		MGraph annotations = new IndexedMGraph();
		
		//System.out.println("Start adding annotation");
		
		if (! mapping.isEmpty()) {
			
			// Create an enhancement for each entity of type foaf:Person
			Iterator<Triple> ipersons = mapping.filter(null, RDF.type, FOAF.Person) ;
			createAnnotations(ci, annotations, ipersons);
			
			
			// Create one enhancement for one entity of type pmo:PatentPublication that is directly related to the input XML patent document.	
			UriRef patentUri = getPatentUri(mapping);
			if( patentUri != null) {
				UriRef patentEnhancement = EnhancementEngineHelper.createEntityEnhancement(ci, this) ;
				// add a triple to link the enhancement to the entity			
				annotations.add( new TripleImpl(patentEnhancement, OntologiesTerms.fiseEntityReference, patentUri) );
				annotations.add( new TripleImpl(patentEnhancement, OntologiesTerms.fiseConfidence, patentUri) );
			}
			
			
			
		}
		
		//System.out.println("Finished adding annotation");
		
		return annotations;
		
	}
	
	/*
	 * Creates enhancements from triples.
	 */
	private void createAnnotations(ContentItem ci, MGraph annotations, Iterator<Triple> itriple) {
		
		while (itriple.hasNext()) {
			// create an entity annotation
			UriRef enhancement = EnhancementEngineHelper.createEntityEnhancement(ci, this) ;
			
			Triple statement = itriple.next();
			NonLiteral subject = statement.getSubject(); 
			
			// add a triple to link the enhancement to the entity
			annotations.add( new TripleImpl(enhancement, OntologiesTerms.fiseEntityReference, subject) );
			//System.out.println("entity reference: " + entityReference.toString());
			
			// add a confidence value
			annotations.add( new TripleImpl(enhancement, OntologiesTerms.fiseConfidence, new PlainLiteralImpl("1.0")) );
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
