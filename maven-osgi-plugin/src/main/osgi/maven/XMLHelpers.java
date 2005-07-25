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
    
    // regard new lines...
    // WARNING: keep in mind that these two methods add tabs to the content!!!
    //  -> no problems if outside of tags...
    protected static String emitMultilineTagNL(String tagName, String content){
        return emitMultilineTagNL(tagName, content, 0);
    }
    
    // regard new lines...
    // WARNING: keep in mind that these two methods modify the content!!!
    //  -> no problems if new lines outside of text-tags...
    protected static String emitMultilineTagNL(String tagName, String content, int level) {
        String leveller = new String();
        for (int i = 0; i < level; i++) {
    		leveller = leveller + "\t";    		
    	}
        StringBuffer result = new StringBuffer();
        int index;
        int pos = 0;
        while ((index = content.indexOf("\n", pos)) != -1){
            result.append(content.substring(pos, index));
            result.append("\n" + leveller + "\t");
            pos = index + 1;
        }
        if (pos < content.length()) result.append(content.substring(pos, content.length()));
        return leveller + "<" + tagName + ">\n" + leveller + "\t" + result.toString() + "\n" + leveller + "</" + tagName  + ">\n";
    }
    
    protected static String insertNL(String data, int lineLength){
        StringBuffer result = new StringBuffer(data);
        int pos = lineLength - 1;
        while (pos < result.length()){
            result.insert(pos, '\n');
            pos += lineLength;
        }
        return result.toString();
    }

}