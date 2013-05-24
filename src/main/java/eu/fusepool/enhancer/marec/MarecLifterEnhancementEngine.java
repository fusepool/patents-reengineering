package eu.fusepool.enhancer.marec;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.indexedgraph.IndexedMGraph;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EngineException;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.ServiceProperties;
import org.apache.stanbol.enhancer.servicesapi.impl.AbstractEnhancementEngine;
import org.apache.stanbol.entityhub.servicesapi.Entityhub;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

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


	//private static final Logger log = LoggerFactory.getLogger(MarecLifterEnhancementEngine.class);
	private TCServiceLocator serviceLocator ;
	
	/*****
	 * graph uri for the triplestore
	 */
	private static final String graphUri = "urn:fusepool-graph1" ;
	
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

	//protected TripleCollection tripleCollection ;
	

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
		
		try { // TODO: errore nell'attivazione ?
			serviceLocator = new TCServiceLocator(ce.getBundleContext(), "graph.uri=" + graphUri) ;
					//"graph.uri=om.go5th.yard.clerezza.01") ;	
//			if(CLEAN_ON_STARTUP) {
//				TripleCollection collection = serviceLocator.getTripleCollection() ;
//				if(collection!=null) {
//					collection.clear() ;
//				}
//			}
		} catch (InvalidSyntaxException e) {
			logService.log(LogService.LOG_ERROR,"Invalid graph.uri syntax", e) ;
		} catch (Exception e) {
			logService.log(LogService.LOG_ERROR, "Error creating service locator", e) ;
		}
		logService.log(LogService.LOG_INFO, "activating "+this.getClass().getName());

	}

	protected void deactivate(ComponentContext ce) {
		super.deactivate(ce);
		catalogBuilder.cleanupFiles() ;
	}

	@Override
	public int canEnhance(ContentItem ci) throws EngineException {
		return ENHANCE_SYNCHRONOUS;
	}

	@Override
	public void computeEnhancements(ContentItem ci) throws EngineException {
		UriRef contentItemId = ci.getUri();
		logService.log(LogService.LOG_INFO, "UriRef: "+contentItemId.getUnicodeString()) ;



		

		//TODO: check if mimetype is supported
//		String mimeType = ci.getMimeType() ;
//		if(!isSupported(mimeType)) {
//			throw new EngineException("Cannot enhance mimetype: "+mimeType) ;
//		}
		
		// Load rdf graph 
		MGraph rdfGraph = new IndexedMGraph();
		
		try {
	
			ci.getLock().writeLock().lock();
			XMLProcessor processor = new PatentXSLTProcessor() ;
			InputStream rdfIs = null ;
			try {
				rdfIs = processor.processPatentXML(ci.getStream()) ;
				parser.parse(rdfGraph, rdfIs, SupportedFormat.RDF_XML) ;
				rdfIs.close() ;
			} catch (Exception e) {
				logService.log(LogService.LOG_ERROR, "Wrong data format for the "+this.getName()+" enhancer", e) ;
				return ;
			}
		
		  
		/*	
		RdfValueFactory valueFactory = RdfValueFactory.getInstance();
		Map<String,Representation> representations = new HashMap<String,Representation>();
		Set<NonLiteral> processed = new HashSet<NonLiteral>();
		for(Iterator<Triple> st = rdfGraph.iterator();st.hasNext();){
			Triple curTriple = st.next() ;
			NonLiteral resource = curTriple.getSubject();
			if(resource instanceof UriRef){
				representations.put(((UriRef)resource).getUnicodeString(),
						valueFactory.createRdfRepresentation((UriRef)resource, rdfGraph));
			} else {
				logService.log(LogService.LOG_INFO, "skipped: "+curTriple);
				logService.log(LogService.LOG_INFO, "not UriRef: "+resource);	
			}
		}
		
		*/
		
		/* stores the triples in a graph	
		TripleCollection tripleCollection = serviceLocator.getTripleCollection() ;
		if(tripleCollection!=null) {
			if(tripleCollection.addAll(rdfGraph)) {
				logService.log(LogService.LOG_INFO, this.getClass().getName()+" added collection to yard...");
			} else{
				logService.log(LogService.LOG_WARNING, this.getClass().getName()+" collection NOT added to yard...");
			}
		}
		*/

		
		ci.getMetadata().addAll(rdfGraph) ;
		
			
		} catch (Exception e) {
			logService.log(LogService.LOG_ERROR, "", e) ;
			
		} finally {
			ci.getLock().writeLock().unlock();
		}
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
