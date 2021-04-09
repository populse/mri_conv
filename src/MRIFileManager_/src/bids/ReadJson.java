package bids;

import java.io.FileReader;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import MRIFileManager.GetStackTrace;

public class ReadJson {

	private JSONParser parser = new JSONParser();
	private Object obj;
	private HashMap<String, String> listObject = new HashMap<>();

	public ReadJson(String jsonPath) {

		try {
			obj = parser.parse(new FileReader(jsonPath));
			JSONObject object = (JSONObject) obj;
			listHighObject(object);
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}
	}

	private void listHighObject(JSONObject obj) {
		for (Object sw : obj.keySet().toArray()) {
			listObject.put(sw.toString(), obj.get(sw).toString());
		}
	}

	public HashMap<String, String> getlistObject() {
		return listObject;
	}
}