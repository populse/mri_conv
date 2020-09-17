package MRIFileManager;
import java.util.HashMap;

public class User {

	private HashMap<String, HashMap<String, HashMap<String , String>>> dictionaryMRI;
	private HashMap<String, HashMap<String, HashMap<String , String[]>>> listProtocols;


	public HashMap<String, HashMap<String, HashMap<String , String>>> getDictionaryMRI() {
		return dictionaryMRI;
	}

	public void setDictionaryMRI(HashMap<String, HashMap<String, HashMap<String , String>>> dictionaryMRI) {
		this.dictionaryMRI = dictionaryMRI;
	}
	
	public HashMap<String, HashMap<String, HashMap<String , String[]>>> getlistProtocols() {
		return listProtocols;
	}
	
}