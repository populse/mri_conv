package MRIFileManager;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

public class ExtractTxtfromFile {
	
	private String filePath;
	private boolean fileIsExists;
	
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
			fileIsExists = true;
		}
	    catch (IOException e) {
	    	fileIsExists = false;
	    	new GetStackTrace(e, this.getClass().toString());
	    }
		return tm;
	}
	
	public boolean full() {
		return fileIsExists;
	}
}