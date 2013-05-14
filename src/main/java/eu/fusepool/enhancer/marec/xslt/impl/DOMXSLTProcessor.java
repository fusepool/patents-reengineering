/**
 * 
 */
package eu.fusepool.enhancer.marec.xslt.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import eu.fusepool.enhancer.marec.xslt.DTDResolver;
import eu.fusepool.enhancer.marec.xslt.PatentXMLProcessor;
import eu.fusepool.enhancer.marec.xslt.ResourceURIResolver;


/**
 * @author giorgio
 * 
 */
public class DOMXSLTProcessor implements PatentXMLProcessor {
	
	private TransformerFactory tFactory ;
	
	
	public DOMXSLTProcessor() {
		
		
		if(tFactory==null) {
			
			tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", //null) ; 
												this.getClass().getClassLoader());
			//tFactory.setAttribute(FeatureKeys.SOURCE_PARSER_CLASS, MarecXMLReader.class.getName()) ;
			//tFactory.setAttribute(FeatureKeys.STYLE_PARSER_CLASS, MarecXMLReader.class.getName()) ;

		    URIResolver defResolver = tFactory.getURIResolver() ;
		    ResourceURIResolver customResolver = new ResourceURIResolver(defResolver) ;
		    tFactory.setURIResolver(customResolver) ;
			
		}
	}
	
	
	

	/* (non-Javadoc)
	 * @see eu.fusepool.enhancer.marec.xslt.PatentXMLProcessor#processPatentXML(java.io.InputStream)
	 */
	@Override
	public InputStream processPatentXML(InputStream is) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

	    DocumentBuilder db = dbFactory.newDocumentBuilder();
	    //db.setEntityResolver(new DTDResolver()) ;

	    //System.out.println("BUILDING DOM");
	    long from =System.currentTimeMillis() ;
	    Document doc = db.parse(is);
	    long to = System.currentTimeMillis() ;
	    System.out.println("DOM_BUILT in "+(to-from)+ "ms");
	    
	    
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	    Transformer transformer = tFactory.newTransformer(
	        new StreamSource(this.getClass().getResourceAsStream("/xsl/marec.xsl")));

	    transformer.transform(
	            new DOMSource(doc.getDocumentElement()),
	            new StreamResult(outputStream));

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
