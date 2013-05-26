/**
 * 
 */
package eu.fusepool.platform.enhancer.engine.patent.xslt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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


	final static String BASE_DTD = "dtd"+File.separator ;
	final static String BASE_ZIP = "zip"+File.separator ;


	private final BundleContext bundleContext ;

	//private String dtdAbsPath ;

	public CatalogBuilder(BundleContext ctx) {
		bundleContext = ctx ;
	}


	public void build() throws Exception {
//		if(catalogPath!=null)
//			return ;
		
		createCatalog() ;
		File dtdDir = bundleContext.getDataFile(BASE_DTD) ;
		if(dtdDir!=null && !dtdDir.exists())
			makeDTDDir() ;
			
		
		
		
		copyZipFile("dtd-patent.zip") ;
		unZipIt(BASE_ZIP+"dtd-patent.zip", BASE_DTD) ;
		
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
			File fCatalog = bundleContext.getDataFile("catalog"+File.separator+CATALOG_FILE) ;
			catalogPath = fCatalog.getAbsolutePath() ;
			os = new FileOutputStream(fCatalog.getAbsolutePath()) ;
			IOUtils.copy(catalogIs, os) ;
		} finally {
			if(os!=null)
				os.close() ;
		}
	}


	private void copyZipFile(String zipRes) throws Exception {
		String zipPath = BASE_ZIP ;
		File zipDir = bundleContext.getDataFile(zipPath) ;
		if(!zipDir.exists()) {
			zipDir.mkdirs() ;
		}

		InputStream is = getClass().getResourceAsStream("/"+zipRes) ;
		File destFile = bundleContext.getDataFile(zipPath+zipRes) ;
		OutputStream os = new FileOutputStream(destFile) ;
		IOUtils.copy(is, os) ;
		os.close() ;
	}


	private void makeBaseDirs(String base) {
		File baseDir = bundleContext.getDataFile(base) ;
		if(!baseDir.exists())
			baseDir.mkdirs() ;
	}

	private void makeBaseDir(String base) {
		File baseDir = bundleContext.getDataFile(base) ;
		baseDir.mkdir() ;
	}

	private void makeDTDDir() {
		File baseDir = bundleContext.getDataFile(BASE_DTD) ;
		baseDir.mkdir() ;
		//dtdAbsPath = baseDir.getAbsolutePath() ;
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

	/**
	 * Unzip it
	 * @param zipFile input zip file
	 * @param output zip file output folder
	 */
	public void unZipIt(String zipFile, String outputFolder)	{

		try{
			File srcFile = bundleContext.getDataFile(zipFile) ; 
			ZipFile zf = new ZipFile(srcFile) ;	

			Enumeration<? extends ZipEntry> entries = zf.entries();
//			ZipInputStream zipInput = null;

			while (entries.hasMoreElements()) {
				ZipEntry zipEntry=entries.nextElement();
				if(zipEntry.isDirectory())
					continue ;
				
				String fileName = zipEntry.getName();

				// target
				File newFile = bundleContext.getDataFile(outputFolder+fileName) ; 
				//create all non existing folders
				//else you will hit FileNotFoundException for compressed folder
				
				File parent = newFile.getParentFile() ;
				if(!parent.exists() && !parent.mkdirs()){
					throw new IllegalStateException("Couldn't create dir: " + parent);
				}
				
				
				FileOutputStream fos = new FileOutputStream(newFile);     
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));

				// source
				InputStream inputs=zf.getInputStream(zipEntry);
				BufferedReader br = new BufferedReader(new InputStreamReader(inputs, "UTF-8"));
				
				String line ;
				while((line = br.readLine()) != null)	{
					out.write(line);
					out.newLine();
					out.flush();
				}
//				zipInput.closeEntry();
				out.close() ;
			
				//System.out.println("file unzip : "+ newFile.getAbsoluteFile());
			}
			zf.close() ;
		}catch(IOException ex){
			ex.printStackTrace(); 
		}
	}    



}



