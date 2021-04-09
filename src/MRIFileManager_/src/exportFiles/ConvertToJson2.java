package exportFiles;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.HashMap;
//import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import nifti.Nifti1Dataset;

public class ConvertToJson2 extends PrefParam implements ParamMRI2 {

	public ConvertToJson2(String pathJson, String nameJson, Object linebasket, String SeqName) {

		JSONObject obj = new JSONObject();
		JSONObject subobj;
		JSONArray list, list2;

		String tmp;
		
		if (formatCurrent.contains("[Dicom]")) {
			HashMap<String, String> listValues = listBasket_hmInfo.get(linebasket);
			tmp = listValues.get("Image Orientation Patient");
			tmp = tmp.replace("\\", " ");
			listValues.put("Image Orientation Patient", tmp);
			listBasket_hmInfo.put((String) linebasket, listValues);
			tmp = listValues.get("Image Position Patient");
			tmp = tmp.replace("\\", " ");
			listValues.put("Image Position Patient", tmp);
			listBasket_hmInfo.put((String) linebasket, listValues);
		}
		
		for (String kn : dictionaryJsonSystem.keySet()) { // kn listLabelMRI

			try {
				if (listBasket_hmInfo.get(linebasket).get(kn) != null) {

					subobj = new JSONObject();
					list = new JSONArray();

					if (dictionaryJsonSystem.get(kn).get("type").contains(("string"))) {

//						if (kn.contentEquals("Sequence Name") && !SeqName.isEmpty())
//							list.add(SeqName);
//
//						else
						list.add(listBasket_hmInfo.get(linebasket).get(kn));
					}

					if (dictionaryJsonSystem.get(kn).get("type").contains(("float"))) {
//						if (listBasket_hmInfo.get(linebasket).get(kn).split(" +").length > 1) {
						for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +")) {
							list2 = new JSONArray();
							list2.add(Float.parseFloat(hj));
							list.add(list2);
						}
//						} else
//							list.add(Float.parseFloat(listBasket_hmInfo.get(linebasket).get(kn)));

//						for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +"))
//							list.add(Float.parseFloat(hj));
					}

					if (dictionaryJsonSystem.get(kn).get("type").contains(("int"))) {
//						if (listBasket_hmInfo.get(linebasket).get(kn).split(" +").length > 1) {
						for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +")) {
							list2 = new JSONArray();
							list2.add(Integer.parseInt(hj));
							list.add(list2);
						}
//						} else
//							list.add(Integer.parseInt(listBasket_hmInfo.get(linebasket).get(kn)));

//						for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +"))
//							list.add(Integer.parseInt(hj));
					}

					subobj.put("value", list);
					subobj.put("type", dictionaryJsonSystem.get(kn).get("type"));
					subobj.put("format", dictionaryJsonSystem.get(kn).get("format"));
					subobj.put("units", dictionaryJsonSystem.get(kn).get("units"));
					subobj.put("description", dictionaryJsonSystem.get(kn).get("description"));
					obj.put(dictionaryJsonSystem.get(kn).get("tagJson").toString(), subobj);

//					obj.put(dictionaryJsonSystem.get(kn).get("tagJson").toString(), list);
				}
			} catch (Exception e) {

			}
		}

		for (String kn : dictionaryJsonUser.keySet()) {

			try {
				if (listBasket_hmInfo.get(linebasket).get(kn) != null) {
					subobj = new JSONObject();
					list = new JSONArray();

					if (dictionaryJsonUser.get(kn).get("type").contains(("string"))) {
						list.add(listBasket_hmInfo.get(linebasket).get(kn));
					}

					if (dictionaryJsonUser.get(kn).get("type").contains(("float"))) {
//						if (listBasket_hmInfo.get(linebasket).get(kn).split(" +").length > 1) {
						for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +")) {
							list2 = new JSONArray();
							list2.add(Float.parseFloat(hj));
							list.add(list2);
						}
//						} else
//							list.add(Float.parseFloat(listBasket_hmInfo.get(linebasket).get(kn)));

//						for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +"))
//							list.add(Float.parseFloat(hj));
					}

					if (dictionaryJsonUser.get(kn).get("type").contains(("int"))) {

//						if (listBasket_hmInfo.get(linebasket).get(kn).split(" +").length > 1) {
						for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +")) {
							list2 = new JSONArray();
							list2.add(Integer.parseInt(hj));
							list.add(list2);
						}
//						} else
//							list.add(Integer.parseInt(listBasket_hmInfo.get(linebasket).get(kn)));

//						for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +"))
//							list.add(Integer.parseInt(hj));

					}

					subobj.put("value", list);
					subobj.put("type", dictionaryJsonUser.get(kn).get("type"));
					subobj.put("format", dictionaryJsonUser.get(kn).get("format"));
					subobj.put("units", dictionaryJsonUser.get(kn).get("units"));
					subobj.put("description", dictionaryJsonUser.get(kn).get("description"));
					obj.put(dictionaryJsonUser.get(kn).get("tagJson").toString(), subobj);

//					obj.put(dictionaryJsonUser.get(kn).get("tagJson").toString(), list);
				}
			} catch (Exception e) {

			}
		}

		/************************
		 * add MetadataRaw Tag in Json
		 ************************************************/
		// list = new JSONArray();
		// list.add(listBasket_hmInfo.get(linebasket).get("MetadataRaw"));
		// obj.put("MetadataRaw", list);

		/************************
		 * header Nifti parameters to Json
		 ************************************************/
		Nifti1Dataset niftiParam = new Nifti1Dataset(pathJson + PrefParam.separator + nameJson + ".nii");
		HashMap<String, HashMap<String, String>> niftiHdrField = new HashMap<>();
		HashMap<String, String> listField;

		for (String jj[] : DictionaryHeaderNifti.listTagHeaderNifti) {

			listField = new HashMap<>();

			listField.put("type", jj[1]);
			listField.put("format", jj[2]);
			listField.put("units", jj[3]);
			listField.put("description", jj[4]);

			niftiHdrField.put(jj[0], listField);
		}

		try {
			niftiParam.readHeader();
		} catch (Exception e) {
		}
		String hdr = niftiParam.getHeader();

		String line, key = "", value = "";
		BufferedReader reader = new BufferedReader(new StringReader(hdr));
		try {
			while ((line = reader.readLine()) != null) {

				if (line.length() > 0 && line.contains(":")) {
					line = line.replace("//", "/");
					line = line.replace("\\\\", "\\");
					try {
						key = line.substring(0, line.indexOf(":")).toString().trim();
						value = line.substring(line.indexOf(":") + 1).toString().trim();
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (niftiHdrField.containsKey(key)) {

						subobj = new JSONObject();
						list = new JSONArray();

						if (niftiHdrField.get(key).get("type").contentEquals("string")) {
							list.add(value);
//							obj.put(key, list);
						}

						else if (niftiHdrField.get(key).get("type").contentEquals("float")) {
							if (value.split(" +").length > 1) {
								for (String hj : value.split(" +")) {
									list2 = new JSONArray();
									list2.add(Float.parseFloat(hj));
									list.add(list2);
								}

							} else
								list.add(Float.parseFloat(value));
//							obj.put(key, list);
						}

						else if (niftiHdrField.get(key).get("type").contentEquals("int")) {
							if (value.split(" +").length > 1) {
								for (String hj : value.split(" +")) {
									list2 = new JSONArray();
									list2.add(Integer.parseInt(hj));
									list.add(list2);
								}

							} else
								list.add(Integer.parseInt(value));

//							obj.put(key, list);
						}

						subobj.put("value", list);
						subobj.put("type", niftiHdrField.get(key).get("type"));
						subobj.put("format", niftiHdrField.get(key).get("format"));
						subobj.put("units", niftiHdrField.get(key).get("units"));
						subobj.put("description", niftiHdrField.get(key).get("description"));
						obj.put(key, subobj);
					}
				}
			} // end while
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		// scanner.close();

		/************************************************
		 * add Json_Version Tag in Json
		 ************************************************/
		list = new JSONArray();
		list.add("Irmage2018");
		obj.put("Json_Version", list);

		/************************************************
		 * add Anonymized Tag in Json
		 ************************************************/
		list = new JSONArray();
		subobj = new JSONObject();
		list.add(listBasket_hmInfo.get(linebasket).get("DataAnonymized"));
//		obj.put("DataAnonymized", list);
		subobj.put("value", list);
		subobj.put("type", "string");
		subobj.put("format", "");
		subobj.put("units", "");
		subobj.put("description", "");
		obj.put("DataAnonymized", subobj);

		/************************************************
		 * add bvals, bvecs if diffusion
		 ************************************************/

		if (listBasket_hmInfo.get(linebasket).get("bvals") != null) {
			list = new JSONArray();
			subobj = new JSONObject();
			list.add(listBasket_hmInfo.get(linebasket).get("bvals"));
//		obj.put("DataAnonymized", list);
			subobj.put("value", list);
			subobj.put("type", "string");
			subobj.put("format", "");
			subobj.put("units", "");
			subobj.put("description", "");
			obj.put("bvals", subobj);
		}
		if (listBasket_hmInfo.get(linebasket).get("bvecs") != null) {
			list = new JSONArray();
			subobj = new JSONObject();
			list.add(listBasket_hmInfo.get(linebasket).get("bvecs"));
//		obj.put("DataAnonymized", list);
			subobj.put("value", list);
			subobj.put("type", "string");
			subobj.put("format", "");
			subobj.put("units", "");
			subobj.put("description", "");
			obj.put("bvecs", subobj);
		}

		
		/************************************************
		 * create Json
		 ************************************************/

		try {
			FileWriter writer = new FileWriter(pathJson + PrefParam.separator + nameJson + ".json");
			writer.write(obj.toJSONString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());

			System.out.println(
					"Error: impossible to create file '" + pathJson + PrefParam.separator + nameJson + ".json" + "'");
		}
	}
}