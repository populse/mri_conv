package exportFiles;

import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import MRIFileManager.GetStackTrace;
import abstractClass.PrefParam;

public class Description_Bids_Json {
	
	public Description_Bids_Json(String pathJson) {
		
		JSONObject obj = new JSONObject();
		JSONArray list;
	
		list = new JSONArray();
		list.add("[]");
		
		obj.put("Name", "");
		obj.put("License", "");
		obj.put("Authors", list);
		obj.put("Funding", "");
		obj.put("DatasetDOI","");
		obj.put("BIDSVersion" , "1.2.1");
		
		try {
			FileWriter writer = new FileWriter(pathJson + PrefParam.separator + "dataset_description.json");
			writer.write(obj.toJSONString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			new GetStackTrace(e);
			System.out.println("Error: unable to create file '" + pathJson + PrefParam.separator + "dataset_description.json" + "'");
		}
	}
}