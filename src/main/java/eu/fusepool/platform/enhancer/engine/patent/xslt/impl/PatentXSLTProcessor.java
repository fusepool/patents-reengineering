/**
 * 
 */
package eu.fusepool.platform.enhancer.engine.patent.xslt.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xml.resolver.tools.ResolvingXMLFilter;
import org.xml.sax.InputSource;

import eu.fusepool.platform.enhancer.engine.patent.xslt.MarecXMLReader;
import eu.fusepool.platform.enhancer.engine.patent.xslt.ResourceURIResolver;
import eu.fusepool.platform.enhancer.engine.patent.xslt.XMLProcessor;


/**
 * @author giorgio
 * 
 */
public class PatentXSLTProcessor implements XMLProcessor {
	
	private TransformerFactory tFactory ;
	
	
	public PatentXSLTProcessor() {
		
		
		if(tFactory==null) {
			
			tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", //null) ; 
												this.getClass().getClassLoader());
			
			//tFactory.setAttribute(FeatureKeys.SOURCE_PARSER_CLASS, MarecXMLReader.class.getName()) ;
			//tFactory.setAttribute(FeatureKeys.STYLE_PARSER_CLASS, MarecXMLReader.class.getName()) ;
		
		}
	}
	
	
	

	/* (non-Javadoc)
	 * @see eu.fusepool.enhancer.marec.xslt.PatentXMLProcessor#processPatentXML(java.io.InputStream)
	 */
	@Override
	public InputStream processPatentXML(InputStream is) throws Exception {
	    URIResolver defResolver = tFactory.getURIResolver() ;
	    ResourceURIResolver customResolver = new ResourceURIResolver(defResolver) ;
	    tFactory.setURIResolver(customResolver) ;
	    
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		InputStream xslIs = this.getClass().getResourceAsStream("/xsl/marec.xsl") ;
		StreamSource xlsSS = new StreamSource(xslIs) ;
		Transformer transformer = tFactory.newTransformer(xlsSS);
//		Controller controller = (Controller)transformer ;
		
		InputSource inputSource = new InputSource(is) ;
		
		ResolvingXMLFilter filter = new ResolvingXMLFilter(new MarecXMLReader());
		
		SAXSource saxSource = new SAXSource(filter, inputSource) ;
		StreamResult sRes = new StreamResult(outputStream) ;
		transformer.transform(saxSource, sRes) ; 
		
		InputStream toRet = new ByteArrayInputStream(outputStream.toByteArray());
		return toRet ;
	}
	
	/**
	 * @param args
	 */
	/*
	public static void main(String[] args) {
	       try {  
	    	   System.setProperty("javax.xml.transform.TransformerFactory",
	    			   "net.sf.saxon.TransformerFactoryImpl");
	    	      //TransformerFactory tFactory = TransformerFactory.newInstance();
	    	      TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null) ;
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
	*/
	
	
}
