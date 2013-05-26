package eu.fusepool.platform.enhancer.engine.patent.xslt;

import java.io.InputStream;

public interface XMLProcessor {

	public abstract InputStream processPatentXML(InputStream is)
			throws Exception;

}