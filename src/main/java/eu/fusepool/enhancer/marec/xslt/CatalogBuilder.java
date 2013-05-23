/**
 * 
 */
package eu.fusepool.enhancer.marec.xslt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;

/**
 * @author giorgio
 *
 */
public class CatalogBuilder {

	
	private final static String CATALOG_FILE = "catalog.xml" ;
	
	private static String catalogPath ;
	

	
	private final BundleContext bundleContext ;
	
	
	public CatalogBuilder(BundleContext ctx) {
		bundleContext = ctx ;
	}
	
	
	public void build() throws Exception {
		if(catalogPath!=null)
			return ;
		
		createCatalog() ;
		populateDTDS() ;
		populateISO8879() ;
		populateISO957313() ;
		populateMathML() ;
		createProps() ;
	}
	
	
	private void createProps() {
		try {

		File catalogProps = bundleContext.getDataFile("CatalogManager.properties") ;
		OutputStream os = new FileOutputStream(catalogProps) ;
		InputStream cfgIs = this.getClass().getResourceAsStream("/rough-cm.properties") ;
		String roughConfig = IOUtils.toString(cfgIs, "UTF-8");
		roughConfig = StringUtils.replace(roughConfig, "[CATALOG_PATH]", catalogPath) ;
		os.write(roughConfig.getBytes(Charset.forName("UTF-8"))) ;
		os.flush() ;
		os.close() ;
		} catch (Exception e) {
			e.printStackTrace() ;
		}
		
		// queste istruzioni mandano in tilt saxon
		//Properties props = new Properties() ;
		//props.put("xml.catalog.files", catalogPath) ;
		//System.setProperties(props) ;
	}
	
	private void createCatalog() throws Exception {
		OutputStream os = null ;
		InputStream catalogIs = this.getClass().getResourceAsStream("/catalog/"+CATALOG_FILE) ;
		try {
			makeBaseDir("catalog/") ;
			File fCatalog = bundleContext.getDataFile("catalog/"+CATALOG_FILE) ;
			catalogPath = fCatalog.getAbsolutePath() ;
			os = new FileOutputStream(fCatalog.getAbsolutePath()) ;
			IOUtils.copy(catalogIs, os) ;
		} finally {
			if(os!=null)
				os.close() ;
		}
	}
	
	private void populateDTDS() throws Exception {
		final String BASE = "dtd/" ;
		final String[] files = {
								"ext-dependencies.dtd",
								"mathml2-qname-1.mod",
								"mathml2.dtd",
								"st32-merges.dtd",
								"mathml2.dtd",
								"patent-document.dtd",
								"soextblx.dtd"
								} ;
		
		
		makeBaseDir(BASE) ;
		
		for(int i=0; i<files.length;i++) {
			OutputStream os = null ;
			try{
			InputStream resIS = this.getClass().getResourceAsStream("/dtd/"+files[i]) ;
			File created = bundleContext.getDataFile(BASE+files[i]) ;
			os = new FileOutputStream(created) ;
			IOUtils.copy(resIS, os) ;
			} finally {
				if(os!=null) {
					os.close() ;
					os=null ;
				}
			}
		}

		
	}
	
	private void populateISO8879() throws Exception {
		final String BASE = "dtd/iso8879/" ;
		
		final String[] files = {"isobox.ent",
								"isocyr1.ent",
								"isocyr2.ent",
								"isodia.ent",
								"isolat1.ent",
								"isolat2.ent",
								"isonum.ent",
								"isopub.ent"
								} ;
		
		makeBaseDirs(BASE) ;
		
		for(int i=0; i<files.length;i++) {
			OutputStream os = null ;
			try{
			InputStream resIS = this.getClass().getResourceAsStream("/dtd/"+files[i]) ;
			File created = bundleContext.getDataFile(BASE+files[i]) ;
			os = new FileOutputStream(created) ;
			IOUtils.copy(resIS, os) ;
			} finally {
				if(os!=null) {
					os.close() ;
					os=null ;
				}
			}
		}
	}
	

	private void populateISO957313() throws Exception {
		final String BASE = "dtd/iso9573-13/" ;
		
		final String[] files = {
							"isoamsa.ent",
							"isoamsb.ent",
							"isoamsc.ent",
							"isoamsn.ent",
							"isoamso.ent",
							"isoamsr.ent",
							"isogrk3.ent",
							"isomfrk.ent",
							"isomopf.ent",
							"isomscr.ent",
							"isotech.ent"
								} ;
		
		makeBaseDirs(BASE) ;
		
		for(int i=0; i<files.length;i++) {
			OutputStream os = null ;
			try{
			InputStream resIS = this.getClass().getResourceAsStream("/dtd/"+files[i]) ;
			File created = bundleContext.getDataFile(BASE+files[i]) ;
			os = new FileOutputStream(created) ;
			IOUtils.copy(resIS, os) ;
			} finally {
				if(os!=null) {
					os.close() ;
					os=null ;
				}
			}
		}
	}	
	
	private void populateMathML() throws Exception {
		final String BASE = "dtd/mathml/" ;
		
		final String[] files = {
								"mmlalias.ent",
								"mmlextra.ent"
								} ;
		makeBaseDirs(BASE) ;
		
		for(int i=0; i<files.length;i++) {
			OutputStream os = null ;
			try{
			InputStream resIS = this.getClass().getResourceAsStream("/dtd/"+files[i]) ;
			File created = bundleContext.getDataFile(BASE+files[i]) ;
			os = new FileOutputStream(created) ;
			IOUtils.copy(resIS, os) ;
			} finally {
				if(os!=null) {
					os.close() ;
					os=null ;
				}
			}
		}
	}	
	
	private void makeBaseDirs(String base) {
		File baseDir = bundleContext.getDataFile(base) ;
		baseDir.mkdirs() ;
	}
	
	private void makeBaseDir(String base) {
		File baseDir = bundleContext.getDataFile(base) ;
		baseDir.mkdir() ;
	}

	
	public void cleanupFiles() {
		File rootFolder = bundleContext.getDataFile("") ;
		
	    if( rootFolder.exists() ) {
		      File[] files = rootFolder.listFiles();
		      for(int i=0; i<files.length; i++) {
		         if(files[i].isDirectory()) {
		           deleteDirectory(files[i]);
		         }
		         else {
		           files[i].delete();
		         }
		      }
		    }
	}
	
	
	
	private boolean deleteDirectory(File path) {
		    if( path.exists() ) {
		      File[] files = path.listFiles();
		      for(int i=0; i<files.length; i++) {
		         if(files[i].isDirectory()) {
		           deleteDirectory(files[i]);
		         }
		         else {
		           files[i].delete();
		         }
		      }
		    }
		    return( path.delete() );
		  }

	/**
	 * @return the catalogPath
	 */
	public static String getCatalogPath() {
		return catalogPath;
	}
	
	
	
	
	
}



