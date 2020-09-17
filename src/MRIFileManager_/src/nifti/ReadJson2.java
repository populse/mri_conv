package nifti;

import java.io.FileReader;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import MRIFileManager.GetStackTrace;

public class ReadJson2 {

	private JSONParser parser = new JSONParser();
	private Object obj;
	private HashMap<String, HashMap<String, String>> listObject = new HashMap<>();
	private boolean jsonversion;

	public ReadJson2(String jsonPath) {

		try {
			obj = parser.parse(new FileReader(jsonPath));
			JSONObject object = (JSONObject) obj;
			if (object.get("Json_Version").toString().contains("Irmage2018")) {
				listObject(object);
				jsonversion = true;
			} else {
//				listHighObject(object);
				jsonversion = false;
			}

		} catch (Exception e) {
			new GetStackTrace(e);
		}
	}

	private void listObject(JSONObject object) {
		for (Object sw : object.keySet().toArray()) {
			try {
				obj = parser.parse(object.get(sw).toString());
				listUnderObject((JSONObject) object.get(sw), sw.toString());
			} catch (Exception e) {
			}
		}
	}

	private void listUnderObject(JSONObject underObject, String key) {
		HashMap<String, String> listField = new HashMap<>();
		for (Object sx : underObject.keySet().toArray()) {
			try {
				listField.put(sx.toString(), underObject.get(sx).toString());
			} catch (Exception e) {
			}
		}
		listObject.put(key, listField);
	}

//	private void listHighObject(JSONObject obj) {
//		HashMap<String, String> listField = new HashMap<>();
//		for (Object sw : obj.keySet().toArray()) {
//			listObject.put(sw.toString(), obj.get(sw).toString());
//		}
//	}

	public HashMap<String, HashMap<String, String>> getlistObject() {
		return listObject;
	}

	public boolean isGoodJsonVersion() {
		return jsonversion;
	}
}