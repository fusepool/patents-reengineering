package eu.fusepool.enhancer.marec.xslt;

import java.io.InputStream;

public interface XMLProcessor {

	public abstract InputStream processPatentXML(InputStream is)
			throws Exception;

}