/**
 * 
 */
package eu.fusepool.enhancer.marec.xslt;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * @author giorgio
 *
 */
public class ResourceURIResolver implements URIResolver {

	URIResolver defaultResolver ;
	
	public ResourceURIResolver(URIResolver resolver) {
		defaultResolver = resolver ;
	}
	
	
	@Override
	public Source resolve(String href, String base)
			throws TransformerException {
		StreamSource sSource = null ;
		InputStream xslIs = this.getClass().getResourceAsStream("/xsl/"+href) ;
		if(xslIs!=null) {
			sSource = new StreamSource(xslIs) ;
			return  sSource ;
		} else {
			xslIs = this.getClass().getResourceAsStream(href) ;
			if(xslIs!=null) {
				sSource = new StreamSource(xslIs) ;
				return  sSource ;
			} else {
				return defaultResolver.resolve(href, base) ;
			}
		}
	}

}
