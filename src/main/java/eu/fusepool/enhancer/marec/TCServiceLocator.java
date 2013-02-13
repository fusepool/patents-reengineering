/**
 * 
 */
package eu.fusepool.enhancer.marec;

import org.apache.clerezza.rdf.core.TripleCollection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * @author giorgio
 *
 */
public class TCServiceLocator implements ServiceListener {

	private String graphUri ;
	BundleContext ctx ;
	
	TripleCollection tripleCollection ;
	
	public TCServiceLocator(BundleContext ctx, String graphUri) throws Exception {
		this.ctx = ctx ;
		this.graphUri = graphUri ;
		if("".equals(graphUri)||graphUri==null) {
			throw new Exception(this.getClass().getSimpleName()+": graph URI can't be null") ;
		}
		if(lookupCollection()) {
			ctx.addServiceListener(this, "("+graphUri+")") ;
			System.out.println("Added listenere to service: "+graphUri);
		} // TODO: else ??
		
	}
	
	
	
	
	private boolean lookupCollection() throws InvalidSyntaxException {
		//Filter filter = ce.getBundleContext().createFilter("(graph.uri=om.go5th.yard.clerezza.01)") ;
		ServiceReference[] se = ctx.getAllServiceReferences(org.apache.clerezza.rdf.core.TripleCollection.class.getName(), 
													"("+graphUri+")") ;
		for(ServiceReference s : se) {
			System.out.println(s.getBundle().getSymbolicName()+ " - "+s.getBundle().getBundleId()+
					" - "+s.getBundle().getLocation());
		}
		if(se != null && se.length>0) {
			System.out.println("Registering triplestore reference: "+graphUri);
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
				System.out.println("TripleCollection ("+graphUri+") registered or changed");
				break ;
			}
		case ServiceEvent.UNREGISTERING:
			{
				tripleCollection = null ;
				System.out.println("TripleCollection ("+graphUri+") unregistered");
			}
			
		}

	}



	public TripleCollection getTripleCollection() {
		return tripleCollection;
	}



	public String getGraphUri() {
		return graphUri;
	}

}
