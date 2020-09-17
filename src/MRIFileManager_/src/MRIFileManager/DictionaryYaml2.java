package MRIFileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import abstractClass.Format;
import abstractClass.ParamMRI2;

public class DictionaryYaml2 implements Format, ParamMRI2 {

	private HashMap<String, HashMap<String, HashMap<String, String>>> manuf = null;
	private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	private String constructor;

	public DictionaryYaml2(String fileYml, String constructor) {
		this.constructor = constructor.trim();
		try {
			User user = mapper.readValue(new File(fileYml), User.class);
			manuf = user.getDictionaryMRI();
		} catch (Exception e) {
			new GetStackTrace(e);
		}
	}

	public void loadDictionarySystem() {
		dictionaryMRISystem.clear();
		dictionaryJsonSystem.clear();
		listParamInfoSystem.clear();
		for (String hh : manuf.keySet()) {
			try {
				if (manuf.get(hh).get("where" + constructor) != null) {
					/****************************************************
					 * where
					 *****************************************************/
					dictionaryMRISystem.put(hh, manuf.get(hh).get("where" + constructor));

					/****************************************************
					 * viewer
					 *****************************************************/
					List<String> listtmp = new ArrayList<>();
					listtmp.add("File path");
					listtmp.add("File Name");
					listtmp.add("File Size (Mo)");
					listParamInfoSystem.put("File Info", listtmp);
					String val = manuf.get(hh).get("viewer").get("category");
					if (val != null) {
						listtmp = new ArrayList<>();
						if (listParamInfoSystem.keySet().contains(val))
							listtmp = listParamInfoSystem.get(val);
						listtmp.add(hh);
						listParamInfoSystem.put(manuf.get(hh).get("viewer").get("category"), listtmp);
					}

					/****************************************************
					 * json
					 *****************************************************/
					if (manuf.get(hh).keySet().contains("json")) {
						if (manuf.get(hh).get("json").get("tagJson") != null) {
							manuf.get(hh).get("json").put("description", manuf.get(hh).get("description").get("info")); // add description in json list
							dictionaryJsonSystem.put(hh, manuf.get(hh).get("json"));
						}
					}
				}

			} catch (Exception e) {
			}
		}
	}

	public void loadDictionaryUser() {
		dictionaryMRIUser.clear();
		dictionaryJsonUser.clear();
		listParamInfoUser.clear();
		for (String hh : manuf.keySet()) {
			try {
				if (manuf.get(hh).get("where" + constructor) != null) {
					/****************************************************
					 * where
					 *****************************************************/

					dictionaryMRIUser.put(hh, manuf.get(hh).get("where" + constructor));

					/****************************************************
					 * viewer
					 *****************************************************/
					String val = manuf.get(hh).get("viewer").get("category");
					if (val != null) {
						List<String> listtmp = new ArrayList<>();
						if (listParamInfoUser.keySet().contains(val))
							listtmp = listParamInfoUser.get(val);
						listtmp.add(hh);
						listParamInfoUser.put(manuf.get(hh).get("viewer").get("category"), listtmp);
					}

					/****************************************************
					 * json
					 *****************************************************/
					if (manuf.get(hh).keySet().contains("json")) {
						if (manuf.get(hh).get("json").get("tagJson") != null) {
							manuf.get(hh).get("json").put("description", manuf.get(hh).get("description").get("info")); // add descriptio in json list
							dictionaryJsonUser.put(hh, manuf.get(hh).get("json"));
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}
}