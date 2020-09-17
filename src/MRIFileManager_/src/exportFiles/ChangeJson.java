package exportFiles;

import java.io.FileReader;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import abstractClass.ParamMRI2;

public class ChangeJson implements ParamMRI2 {
	
	private String returnObj;
	
	private Object obj;
	private JSONObject obj1;
	private JSONParser parser = new JSONParser();
//	private HashMap<String, HashMap<String, String>> listObject = new HashMap<>();
	private String[] listField = {"PatientName","StudyName","PatientBirthDate","PatientWeight","PatientSex"};
	private String[] listField2 = {"Patient Name","Study Name","Patient BirthDate","Patient Weight","Patient Sex"};
	private HashMap<String,String> listinBasket;
	
	public ChangeJson(String pathFileJson, Object linebasket) {
		
		try {
			obj = parser.parse(new FileReader(pathFileJson));
			obj1 = (JSONObject) obj;
			listinBasket = listBasket_hmInfo.get(linebasket);
			for (int i=0;i<listField.length;i++)
				changefield(listField[i], listField2[i]);
			changeTagDataAnonymized();
			returnObj=obj1.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void changefield(String field, String field2) {
		JSONObject subobj = new JSONObject();
		JSONArray list = new JSONArray();
		
		list.add(listinBasket.get(field2));
		subobj.put("value", list);
		subobj.put("type", dictionaryJsonSystem.get(field2).get("type"));
		subobj.put("format", dictionaryJsonSystem.get(field2).get("format"));
		subobj.put("units", dictionaryJsonSystem.get(field2).get("units"));
		subobj.put("description", dictionaryJsonSystem.get(field2).get("description"));
		obj1.put(field, subobj);
	}
	
	private void changeTagDataAnonymized() {
		JSONObject subobj = new JSONObject();
		JSONArray list = new JSONArray();
		
		list.add("yes");
		subobj.put("value", list);
		subobj.put("type", "string");
		subobj.put("format", "");
		subobj.put("units", "");
		subobj.put("description", "");
		obj1.put("DataAnonymized", subobj);
	}
	
	public String newObj() {
		return returnObj;
	}
}