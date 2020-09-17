package MRIFileManager;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

public class ExtractTxtfromFile {
	
	private String filePath;
	
	public ExtractTxtfromFile(String filePath) {
		
		this.filePath=filePath;
	}
	
	public String getTxt() {
		String tm="";
		
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));
			StringWriter out = new StringWriter();
			int b;
			while ((b=in.read()) != -1)
	           out.write(b);
			out.flush();
			out.close();
			in.close();
			tm=out.toString();
		}
	    catch (IOException e) {
	    	new GetStackTrace(e);
	    }
		return tm;
	}
}