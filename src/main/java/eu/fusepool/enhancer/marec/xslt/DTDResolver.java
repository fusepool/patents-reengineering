/**
 * 
 */
package eu.fusepool.enhancer.marec.xslt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;


/**
 * @author giorgio
 *
 */
public class DTDResolver implements EntityResolver2 {

	
	private Hashtable<String, String> localDTDURL = new Hashtable<String, String>() ;
	
	private Hashtable<String, String> urlFromId = new Hashtable<String, String>() ;
	
	/*
	-//MXW//DTD patent-document XML//EN
	*/
	
	
	/**
	 * 
	 */
	public DTDResolver() {
		localDTDURL.put("[dtd]", "/dtd/patent-document.dtd") ;
		localDTDURL.put("%st32", "/dtd/st32-merges.dtd") ;
		localDTDURL.put("%extdep", "/dtd/ext-dependencies.dtd") ;
		localDTDURL.put("%mathml2", "/dtd/mathml2.dtd") ;
		localDTDURL.put("%mathml-qname.mod", "/dtd/mathml2-qname-1.mod") ;
		localDTDURL.put("%ent-isoamsa", "/dtd/isoamsa.ent") ;
		localDTDURL.put("%ent-isoamsb", "/dtd/isoamsb.ent") ;
		localDTDURL.put("%ent-isoamsc", "/dtd/isoamsc.ent") ;
		localDTDURL.put("%ent-isoamsn", "/dtd/isoamsn.ent") ;
		localDTDURL.put("%ent-isoamso", "/dtd/isoamso.ent") ;
		localDTDURL.put("%ent-isoamsr", "/dtd/isoamsr.ent") ;

		localDTDURL.put("%ent-isoamsr", "/dtd/isoamsr.ent") ;
		localDTDURL.put("%ent-isogrk3", "/dtd/isogrk3.ent") ;
		localDTDURL.put("%ent-isomfrk", "/dtd/isomfrk.ent") ;
		localDTDURL.put("%ent-isomopf", "/dtd/isomopf.ent") ;
		localDTDURL.put("%ent-isomscr", "/dtd/isomscr.ent") ;
		localDTDURL.put("%ent-isotech", "/dtd/isotech.ent") ;
		localDTDURL.put("%ent-isobox", "/dtd/isobox.ent") ;
		localDTDURL.put("%ent-isocyr1", "/dtd/isocyr1.ent") ;
		localDTDURL.put("%ent-isocyr2", "/dtd/isocyr2.ent") ;
		localDTDURL.put("%ent-isodia", "/dtd/isodia.ent") ;
		localDTDURL.put("%ent-isolat1", "/dtd/isolat1.ent") ;
		localDTDURL.put("%ent-isolat2", "/dtd/isolat2.ent") ;
		localDTDURL.put("%ent-isonum", "/dtd/isonum.ent") ;
		localDTDURL.put("%ent-isopub", "/dtd/isopub.ent") ;
		localDTDURL.put("%ent-mmlextra", "/dtd/mmlextra.ent") ;
		localDTDURL.put("%ent-mmlalias", "/dtd/mmlalias.ent") ;
		localDTDURL.put("%calstblx", "/dtd/soextblx.dtd") ;
		
		
		urlFromId.put("-//MXW//DTD patent-document XML//EN", "/dtd/patent-document.dtd") ;
		
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		System.out.println("####################### publicId= "+publicId);
		System.out.println("####################### systemId= "+systemId);
		return null;
	}

	@Override
	public InputSource getExternalSubset(String name, String baseUri)
			throws SAXException, IOException {
		System.out.println("####################### name= "+name);
		System.out.println("####################### baseUri= "+baseUri);
		return null;
	}

	
	public InputSource resolveEntityOld(String name, String publicId, 
			String baseURI, String systemId) throws SAXException, IOException {
		
//		System.out.println("####################### publicId= "+publicId);
//		System.out.println("####################### systemId= "+systemId);
//		System.out.println("####################### name= "+name);
//		System.out.println("####################### baseUri= "+baseURI);
		
		
		//chiamata per patent-document
		if(systemId!=null && systemId.endsWith("patent-document.dtd")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/patent-document.dtd") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		if(systemId!=null && systemId.endsWith("st32-merges.dtd")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/st32-merges.dtd") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("mathml2.dtd")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/mathml2.dtd") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("ext-dependencies.dtd")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/ext-dependencies.dtd") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("mathml2-qname-1.mod")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/mathml2-qname-1.mod") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}

		if(systemId!=null && systemId.endsWith("isoamsa.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isoamsa.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("isoamsb.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isoamsb.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		if(systemId!=null && systemId.endsWith("isoamsc.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isoamsc.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		if(systemId!=null && systemId.endsWith("isoamsn.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isoamsn.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("isoamso.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isoamso.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}

		if(systemId!=null && systemId.endsWith("isoamsr.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isoamsr.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		
		}
		if(systemId!=null && systemId.endsWith("isogrk3.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isogrk3.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		if(systemId!=null && systemId.endsWith("isomfrk.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isomfrk.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		if(systemId!=null && systemId.endsWith("isomopf.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isomopf.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("isomscr.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isomscr.ent") ;
			System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("isotech.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isotech.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("isobox.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isobox.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		if(systemId!=null && systemId.endsWith("isobox.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isobox.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}

		if(systemId!=null && systemId.endsWith("isocyr1.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isocyr1.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("isocyr2.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isocyr2.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("isodia.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isodia.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("isolat1.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isolat1.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("isolat2.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isolat2.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("isonum.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isonum.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("isopub.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/isopub.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}

		
		if(systemId!=null && systemId.endsWith("mmlextra.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/mmlextra.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("mmlalias.ent")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/mmlalias.ent") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		if(systemId!=null && systemId.endsWith("soextblx.dtd")) {
			InputStream is = this.getClass().getResourceAsStream("/dtd/soextblx.dtd") ;
			//System.out.println("ritornato documento");
			return new InputSource(is) ;
		}
		
		
		
		return null;
	}

	
	@Override
	public InputSource resolveEntity(String name, String publicId, 	
			String baseURI, String systemId) throws SAXException, IOException {
		
		InputSource toRet = null ;
		
		String url = localDTDURL.get(name) ;
		if(url!=null) {
			InputStream is = this.getClass().getResourceAsStream(url) ;
			toRet = new InputSource(is) ;
		}
		
		if(toRet==null) {
			url = urlFromId.get(systemId) ;
			if(url!=null) {
				InputStream is = this.getClass().getResourceAsStream(url) ;
				toRet = new InputSource(is) ;
			}
		}
		
		return toRet ;
	}



}
