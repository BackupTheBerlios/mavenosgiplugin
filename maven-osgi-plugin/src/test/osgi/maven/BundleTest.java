/*
 * Created on Apr 19, 2004
 *
 */
package osgi.maven;

import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import javassist.NotFoundException;
import junit.framework.TestCase;

/**
 * @author andfrei
 * 
 */
public class BundleTest extends TestCase {
	
//	public void testUnzip() {
//		Bundle bundle = new Bundle(".",".", "oro", "oro", "2.0.7", true);
//		
//		bundle.unzip("./tmp");
//	}
	
	
	public void testAppendMainAttributes()
	{
		Bundle bundle;
		
		try {
			
			String atts = "export,import";
//			Vector attv = new Vector();
//			
//			if (atts.matches(".*(export)+.*"))
//				attv.add("Export-Package: " );
//			if (atts.matches(".*(import)+.*"))
//				attv.add("Import-Package: " );
//			String[] attar = new String[attv.size()];
//			attv.copyInto(attar);
			
			bundle = new Bundle(".",".", "batik-total", "batik-total", "1.0.0", true);
			bundle.createOSGiBundle(false, "test", atts);
//			bundle.parseBundle("./log4j-1.2.8.jar");

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
}
