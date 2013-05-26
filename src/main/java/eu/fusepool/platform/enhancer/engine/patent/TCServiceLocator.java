/**
 * 
 */
package eu.fusepool.platform.enhancer.engine.patent;

import org.apache.clerezza.rdf.core.TripleCollection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * @author giorgio
 * 
 */
public class TCServiceLocator implements ServiceListener {

	private String graphUri ;
	BundleContext ctx ;
	
	TripleCollection tripleCollection ;
	
	protected LogService logService ;
	
	
	
	public TCServiceLocator(BundleContext ctx, String graphUri) throws Exception {
		this.ctx = ctx ;
		this.graphUri = graphUri ; 
		getLogService() ;
		if("".equals(graphUri)||graphUri==null) {
			throw new Exception(this.getClass().getSimpleName()+": graph URI can't be null") ;
		}
		ctx.addServiceListener(this, "("+graphUri+")") ;
		logService.log(LogService.LOG_INFO, "Added listener to service: "+graphUri) ;
	}
	
	
	
	
	private boolean lookupCollection() throws InvalidSyntaxException {
		//Filter filter = ce.getBundleContext().createFilter("(graph.uri=om.go5th.yard.clerezza.01)") ;
		ServiceReference[] se = ctx.getAllServiceReferences(org.apache.clerezza.rdf.core.TripleCollection.class.getName(), 
													"("+graphUri+")") ;

		if(se != null && se.length>0) {
			logService.log(LogService.LOG_DEBUG, "Registering triplestore reference: "+graphUri);
			tripleCollection = (TripleCollection) ctx.getService(se[0]) ;
			
			return true ;
		}
		return false ;

	}
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
	 */
	@Override
	public void serviceChanged(ServiceEvent event) {
		ServiceReference ref = event.getServiceReference() ;
		switch (event.getType()) {
		case ServiceEvent.MODIFIED:
		case ServiceEvent.REGISTERED:
			{
				tripleCollection = (TripleCollection) ctx.getService(ref) ;
				logService.log(LogService.LOG_DEBUG, "TripleCollection ("+graphUri+") registered or changed");
				break ;
			}
		case ServiceEvent.UNREGISTERING:
			{
				tripleCollection = null ;
				logService.log(LogService.LOG_DEBUG, "TripleCollection ("+graphUri+") unregistered");
			}
			
		}

	}



	public TripleCollection getTripleCollection() {
		if(tripleCollection==null) {
			try {
				lookupCollection() ;
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
		}
		return tripleCollection;
	}



	public String getGraphUri() {
		return graphUri;
	}

	private void getLogService() {
		ServiceReference ref = ctx.getServiceReference(LogService.class.getName()) ;
		if(ref!=null) {
			logService = (LogService)ctx.getService(ref) ;
		}
	}
	
	
}
