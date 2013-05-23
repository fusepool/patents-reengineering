package eu.fusepool.enhancer.marec.xslt;

import java.io.InputStream;

public interface PatentXMLProcessor {

	public abstract InputStream processPatentXML(InputStream is)
			throws Exception;

}