/**
 * 
 */
package eu.fusepool.enhancer.marec.xslt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * @author giorgio
 * 
 */
public class XSLTProcessor {
	
	private static TransformerFactory tFactory ;
	
	
	public XSLTProcessor() {
		if(tFactory==null) {
			tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", this.getClass().getClassLoader());
		}
	}
	
	
	

	public InputStream processPatentXML(InputStream is) throws Exception {
	    URIResolver defResolver = tFactory.getURIResolver() ;
	    ResourceURIResolver customResolver = new ResourceURIResolver(defResolver) ;
	    tFactory.setURIResolver(customResolver) ;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		InputStream xslIs = this.getClass().getResourceAsStream("/xsl/marec.xsl") ;
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslIs));
		StreamSource sSource = new StreamSource(is) ;
		StreamResult sRes = new StreamResult(outputStream) ;
		transformer.transform(sSource, sRes) ; 
		InputStream toRet = new ByteArrayInputStream(outputStream.toByteArray());
		return toRet ;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	       try {  
	    	   System.setProperty("javax.xml.transform.TransformerFactory",
	    			   "net.sf.saxon.TransformerFactoryImpl");
	    	      TransformerFactory tFactory = TransformerFactory.newInstance();
	    	      URIResolver defResolver = tFactory.getURIResolver() ;
	    	      ResourceURIResolver customResolver = new ResourceURIResolver(defResolver) ;
	    	      tFactory.setURIResolver(customResolver) ;
	    	      InputStream xslIs = XSLTProcessor.class.getResourceAsStream("/xsl/marec.xsl") ;
	    	      Transformer transformer = tFactory.newTransformer(new StreamSource(xslIs));
	    	      transformer.transform(new StreamSource("data/marec/00/00/EP-1000000-A1.xml"), new StreamResult(new FileOutputStream("output.rdf")));
	    	      System.out.println("************* The result is in output.rdf *************");
	    	        } catch (Throwable t) {
	    	          t.printStackTrace();
   	        }
	}

	
	
}
