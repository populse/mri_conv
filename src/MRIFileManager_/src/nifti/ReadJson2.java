package nifti;

import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;

public class ReadJson2 implements ParamMRI2 {

	private JSONParser parser = new JSONParser();
	private Object obj;
	private HashMap<String, HashMap<String, String>> listObject = new HashMap<>();
	private boolean jsonversion;

	public ReadJson2(String jsonPath) {

		try {
			obj = parser.parse(new FileReader(jsonPath));
			JSONObject object = (JSONObject) obj;
			jsonversion = true;
			if (object.containsKey("Json_Version")) {
				if (object.get("Json_Version").toString().contains("Irmage2018")) {
					listObject(object);
				}
			}
			else if (object.containsKey("ConversionSoftware")) {
				if (object.get("ConversionSoftware").toString().contentEquals("dcm2niix")) {
					listObject_dcm2niix(object, true);
				}
				else if (object.get("ConversionSoftware").toString().contentEquals("handmade")){
					listObject_dcm2niix(object, false);
				}
			}
			else {
				jsonversion = false;
			}

		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}
	}

	private void listObject_dcm2niix(JSONObject object, Boolean timefactor) {
		HashMap<String, String> listField;
		for (Object sw : object.keySet().toArray()) {
			listField = new HashMap<String, String>() {
				private static final long serialVersionUID = 1L;
			{
				put("format", "");
				put("description", "");
				put("units", "");
				put("type", "");
			}};

			try {
				if (Arrays.asList("RepetitionTime","EchoTime","InversionTime").contains(sw.toString()) && timefactor) {
					float val = Float.valueOf(object.get(sw).toString());
					val *= 1000.0;
					listField.put("value", String.valueOf(val));
				}
				else
					listField.put("value", object.get(sw).toString());
				listObject.put(sw.toString(), listField);
			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
			}
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

	public HashMap<String, HashMap<String, String>> getlistObject() {
		return listObject;
	}

	public boolean isGoodJsonVersion() {
		return jsonversion;
	}
}