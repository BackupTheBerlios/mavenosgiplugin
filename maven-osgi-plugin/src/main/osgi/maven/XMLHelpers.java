package osgi.maven;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author rjan
 */
public class XMLHelpers {

	protected static String getTagContent(BufferedReader reader, String firstLine, String tagName) throws IOException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(firstLine);	
		
		if (firstLine.indexOf("/" + tagName) == -1) {
			String line = new String();
			do  {
				line = reader.readLine();
				buffer.append(line);    						
			} while (line.indexOf("/" + tagName) == -1);
			
		}
	
		return getTagContent(buffer.toString(), tagName);	    				
    }
    
    protected static String getTagContent(String buffer, String tagName) {
    	String content = buffer.substring(buffer.indexOf(tagName), buffer.lastIndexOf(tagName));
    	return content.substring(content.indexOf(">") + 1, content.lastIndexOf("<"));
    }
    
    protected static String emitTag(String tagName, String content) {
    	return emitTag(tagName, content, 0);
    }
    
    protected static String emitTag(String tagName, String content, int level) {
    	String leveller = new String();
    	for (int i = 0; i < level; i++) {
    		leveller = leveller + "\t";    		
    	}
    	return leveller + "<" + tagName + ">" + content + "</" + tagName  + ">\n";
    }
    
    protected static String emitMultilineTag(String tagName, String content) {
    	return emitMultilineTag(tagName, content, 0);
    }
    
    protected static String emitMultilineTag(String tagName, String content, int level) {
    	String leveller = new String();
    	for (int i = 0; i < level; i++) {
    		leveller = leveller + "\t";    		
    	}
    	return leveller + "<" + tagName + ">\n" + leveller + "\t" + content + "\n" + leveller + "</" + tagName  + ">\n";    	
    }
	
}
