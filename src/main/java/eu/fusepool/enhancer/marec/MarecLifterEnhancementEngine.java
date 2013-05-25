package eu.fusepool.enhancer.marec;


import static org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper.randomUUID;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.NonLiteral;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.PlainLiteralImpl;
import org.apache.clerezza.rdf.core.impl.TripleImpl;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.apache.clerezza.rdf.ontologies.FOAF;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.indexedgraph.IndexedGraph;
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
import org.apache.stanbol.enhancer.servicesapi.rdf.NamespaceEnum;
import org.apache.stanbol.enhancer.servicesapi.rdf.TechnicalClasses;
import org.apache.stanbol.entityhub.servicesapi.Entityhub;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.wymiwyg.commons.mediatypes.MimeType;
import eu.fusepool.enhancer.marec.xslt.CatalogBuilder;
import eu.fusepool.enhancer.marec.xslt.XMLProcessor;
import eu.fusepool.enhancer.marec.xslt.impl.PatentXSLTProcessor;



@Component(immediate = true, metatype = true)
@Service
@Properties(value={
		@Property(name=EnhancementEngine.PROPERTY_NAME, value=MarecLifterEnhancementEngine.DEFAULT_ENGINE_NAME),
		@Property(name=Constants.SERVICE_RANKING,intValue=MarecLifterEnhancementEngine.DEFAULT_SERVICE_RANKING),
		@Property(name="CLEAN_ON_STARTUP", boolValue=MarecLifterEnhancementEngine.DEF_CLEAN)
})
public class MarecLifterEnhancementEngine 
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
	
	
	@Reference
	protected Entityhub entityHub ;

	@Reference
	protected LogService logService ;

	@Reference
	protected Parser parser ;
	

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
			// TODO Auto-generated catch block
			logService.log(LogService.LOG_ERROR, "Error building dtd catalog", e1) ;
		}
		
		
		logService.log(LogService.LOG_INFO, "PatentEngine being activated " + this.getClass().getName());

	}

	protected void deactivate(ComponentContext ce) {
		super.deactivate(ce);
		catalogBuilder.cleanupFiles() ;
	}

	/*
	 * Check if content is present and mime type is correct.
	 * 
	 */
	public int canEnhance(ContentItem ci) throws EngineException {
		 
        try {
            if ((ci.getBlob() == null)
                    || (ci.getBlob().getStream().read() == -1)) {
                return CANNOT_ENHANCE;
            }
            
            if (! ci.getMimeType().equals(this.MIME_TYPE_XML)) {
            	return CANNOT_ENHANCE;
            }
        } catch (IOException e) {
            logService.log(CANNOT_ENHANCE, "Failed to get the text for "
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
		logService.log(LogService.LOG_INFO, "UriRef: "+contentItemId.getUnicodeString()) ;		
		
		try {
	
			ci.getLock().writeLock().lock();
			
			// Transform the patent XML file into RDF
			transformXML(ci);
			
			// Add a part to the content item as a text/plain representation of the XML document 
			addPartToContentItem(ci);
			
			// Add enhancements to the metadata
			addEnhancements(ci);
			
			
		} catch (Exception e) {
			logService.log(LogService.LOG_ERROR, "", e) ;
			
		} 
		finally {
			ci.getLock().writeLock().unlock();
		}
		
	}
	
	/*
	 * Transform patent XML documents into RDF using an XSLT transformation and add the graph to the content item metadata.
	 */
	private void transformXML(ContentItem ci) throws EngineException {
		
		XMLProcessor processor = new PatentXSLTProcessor() ;
		InputStream rdfIs = null ; 
	
		
		try {
			ci.getLock().writeLock().lock();
			MGraph metadata = new IndexedMGraph();
			rdfIs = processor.processPatentXML(ci.getStream()) ;
			parser.parse(metadata, rdfIs, SupportedFormat.RDF_XML) ;
			rdfIs.close() ;
			ci.getMetadata().addAll(metadata) ;
			
		} catch (Exception e) {
			logService.log(LogService.LOG_ERROR, "Wrong data format for the " + this.getName() + " enhancer.", e) ;
			return ;
		}
		finally {
			ci.getLock().writeLock().unlock();
		}
		
	}
	
	/*
	 *  Add a part to the content item as a text/plain representation of the XML document
	 */
	private void addPartToContentItem(ContentItem ci) throws EngineException, IOException {
		ci.getLock().writeLock().lock();
		InputStream toCopy = ci.getStream() ;
		UriRef blobUri = new UriRef("urn:patent-engine:plain-text:" + randomUUID());
		ContentSink plainTextSink = ciFactory.createContentSink("text/plain");
		OutputStream os = plainTextSink.getOutputStream() ;
		IOUtils.copy(toCopy, os) ;
		IOUtils.closeQuietly(toCopy) ;
		IOUtils.closeQuietly(os) ;
		ci.addPart(blobUri, plainTextSink.getBlob());
		
		ci.getLock().writeLock().unlock();
		
	}
	
	/*
	 * Create an enhancement for each entity found by the transformation of the XML document and add them 
	 * to the content item metadata with properties to refer it to the entity and some more
	 */
	private void addEnhancements(ContentItem ci) {
		ci.getLock().writeLock().lock();
		if (! ci.getMetadata().isEmpty()) {
			
			Iterator<Triple> ipersons = ci.getMetadata().filter(null, RDF.type, FOAF.Person) ;
			
			while (ipersons.hasNext()) {
				// create an entity annotation
				UriRef entityAnnotation = EnhancementEngineHelper.createEntityEnhancement(ci, this) ;
				// add a triple to link the enhancement to the entity
				ci.getMetadata().add( new TripleImpl(entityAnnotation, org.apache.stanbol.enhancer.servicesapi.rdf.Properties.ENHANCER_ENTITY_REFERENCE, ipersons.next().getSubject() ));
				// add a confidence value
				ci.getMetadata().add( new TripleImpl(entityAnnotation, org.apache.stanbol.enhancer.servicesapi.rdf.Properties.ENHANCER_CONFIDENCE, new PlainLiteralImpl("1.0") ));
			 
			}
	        
		}
		
		ci.getLock().writeLock().unlock();
		
	}
	
	

	@Override
	public Map<String, Object> getServiceProperties() {
		return Collections.unmodifiableMap(Collections.singletonMap(
				ENHANCEMENT_ENGINE_ORDERING, (Object) defaultOrder));
	}


	//@Activate
	public void registered(ServiceReference ref) {
		logService.log(LogService.LOG_INFO, this.getClass().getName()
									+" registered") ;
	}

	//@Deactivate
	public void unregistered(ServiceReference ref) {
		logService.log(LogService.LOG_INFO, this.getClass().getName()+" unregistered") ;
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
