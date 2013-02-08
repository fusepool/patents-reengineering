package eu.fusepool.enahncer.marec;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.NonLiteral;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.TripleCollection;
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
import org.apache.stanbol.entityhub.model.clerezza.RdfValueFactory;
import org.apache.stanbol.entityhub.servicesapi.Entityhub;
import org.apache.stanbol.entityhub.servicesapi.model.Representation;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fusepool.enencher.marec.xslt.XSLTProcessor;



@Component(immediate = true, metatype = true)
@Service
@Properties(value={
		@Property(name=EnhancementEngine.PROPERTY_NAME, value="marecEngine"),
		@Property(name=Constants.SERVICE_RANKING,intValue=MarecLifterEnhancementEngine.DEFAULT_SERVICE_RANKING)
})
public class MarecLifterEnhancementEngine 
extends AbstractEnhancementEngine<IOException,RuntimeException> 
implements EnhancementEngine, ServiceProperties {

	public static final String DEFAULT_ENGINE_NAME = "marecEngine";
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


	private static final Logger log = LoggerFactory.getLogger(MarecLifterEnhancementEngine.class);

	private TCServiceLocator serviceLocator ;
	
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
		
		
		
		
		try {
			serviceLocator = new TCServiceLocator(ce.getBundleContext(), "graph.uri=om.go5th.yard.clerezza.01") ;
			//Filter filter = ce.getBundleContext().createFilter("(graph.uri=om.go5th.yard.clerezza.01)") ;
//			ServiceReference[] se = ce.getBundleContext().
//												getAllServiceReferences(org.apache.clerezza.rdf.core.TripleCollection.class.getName(), 
//														"(graph.uri=om.go5th.yard.clerezza.01)") ;
//			for(ServiceReference s : se) {
//				System.out.println(s.getBundle().getSymbolicName()+ " - "+s.getBundle().getBundleId()+
//						" - "+s.getBundle().getLocation());
//			}
//			if(se != null && se.length>0) {
//				System.out.println("Registering triplestore reference...");
//				tripleCollection = (TripleCollection) ce.getBundleContext().getService(se[0]) ;
//			}
			
			
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("activating "+this.getClass().getName());

	}

	protected void deactivate(ComponentContext ce) {
		super.deactivate(ce);
	}

	@Override
	public int canEnhance(ContentItem ci) throws EngineException {
		return ENHANCE_SYNCHRONOUS;
	}

	@Override
	public void computeEnhancements(ContentItem ci) throws EngineException {
		UriRef contentItemId = ci.getUri();
		logService.log(LogService.LOG_INFO, "UriRef: "+contentItemId.getUnicodeString()) ;


		MGraph g = ci.getMetadata();
		String mimeType = ci.getMimeType() ;
		

		// check if mimetype is supported
//		if(!isSupported(mimeType)) {
//			throw new EngineException("Cannot enhance mimetype: "+mimeType) ;
//		}
		
		

		// Load rdf graph 
		MGraph rdfGraph = new IndexedMGraph();
		
		XSLTProcessor processor = new XSLTProcessor() ;
		InputStream rdfIs = null ;
		try {
			rdfIs = processor.processPatentXML(ci.getStream()) ;
			parser.parse(rdfGraph, rdfIs, SupportedFormat.RDF_XML) ;
			rdfIs.close() ;
		} catch (Exception e) {
			throw new EngineException(e) ;
		}
		

		RdfValueFactory valueFactory = RdfValueFactory.getInstance();
		Map<String,Representation> representations = new HashMap<String,Representation>();
		Set<NonLiteral> processed = new HashSet<NonLiteral>();
		for(Iterator<Triple> st = rdfGraph.iterator();st.hasNext();){
			Triple curTriple = st.next() ;
			NonLiteral resource = curTriple.getSubject();
			if(resource instanceof UriRef && processed.add(resource)){
				//build a new representation
				//UriRef ref = new UriRef(unicodeString)
				representations.put(((UriRef)resource).getUnicodeString(),
						valueFactory.createRdfRepresentation((UriRef)resource, rdfGraph));
			} else {
				System.out.println("skipped: "+curTriple);
				System.out.println("not UriRef: "+resource);			
			}
		}
		
		
		TripleCollection tripleCollection = serviceLocator.getTripleCollection() ;
		if(tripleCollection!=null) {
			if(tripleCollection.addAll(rdfGraph)) {
				System.out.println("\n\nadded collection to yard...");
			} else{
				System.out.println("\n\nCannot add collection to yard...");
			}
		}
		// now it should be written to the entityhub

		//Entity entity = updateOrCreateEntity("*", representations, true, true) ;

		ci.getLock().writeLock().lock();

		/*
		 * Enhancement phase
		 */
		try {
			MGraph graph = ci.getMetadata();
			graph.addAll(rdfGraph) ;
			/*
			UriRef textEnhancement = EnhancementEngineHelper.createTextEnhancement(ci, this);
			graph.add(new TripleImpl(textEnhancement,
					new UriRef("http://example.org/likes"), 
					new UriRef("http://fusepool.eu")));
		   */

		} finally {
			ci.getLock().writeLock().unlock();
		}

		//entityHub.store(representation)
	}

	@Override
	public Map<String, Object> getServiceProperties() {
		return Collections.unmodifiableMap(Collections.singletonMap(
				ENHANCEMENT_ENGINE_ORDERING, (Object) defaultOrder));
	}


	//@Activate
	public void registered(ServiceReference ref) {
		logService.log(LogService.LOG_INFO, "DummyEngine registered") ;
		//log.debug("registered "+this.getClass().getName()) ;
	}

	//@Deactivate
	public void unregistered(ServiceReference ref) {
		logService.log(LogService.LOG_INFO, "DummyEngine unregistered") ;

		//log.debug("unregistered "+this.getClass().getName()) ;
	}


	/**
	 * n.d.G.: credo verrà eliminata
	 * 
	 * @param is
	 * @return
	 */
//	public String convertStreamToString(InputStream is) { 
//		BufferedReader reader = new BufferedReader(new InputStreamReader(is)); 
//		StringBuilder sb = new StringBuilder(); 
//
//		String line = null; 
//
//		try { 
//			while ((line = reader.readLine()) != null) { 
//				sb.append(line + "\n"); 
//			} 
//		} catch (IOException e) { 
//			e.printStackTrace(); 
//		} finally { 
//			try { 
//				is.close(); 
//			} catch (IOException e) { 
//				e.printStackTrace(); 
//			} 
//		}
//
//		return sb.toString(); 
//	}

	/**
	 * Converts the type and the subtype of the parsed media type to the
	 * string representation as stored in {@link #supportedMediaTypes} and than
	 * checks if the parsed media type is contained in this list.
	 * @param mediaType the MediaType instance to check
	 * @return <code>true</code> if the parsed media type is not 
	 * <code>null</code> and supported. 
	 */
	private boolean isSupported(String mediaType){
		return mediaType == null ? false : supportedMediaTypes.contains(
				mediaType.toLowerCase());
	}


}
