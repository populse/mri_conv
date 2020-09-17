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

public class ConvertToJson extends PrefParam implements ParamMRI2 {

	public ConvertToJson(String pathJson, String nameJson, Object linebasket) {

		JSONObject obj = new JSONObject();
		JSONArray list, list2;

		for (String kn : dictionaryJsonSystem.keySet()) { // kn listLabelMRI
			try {
				if (listBasket_hmInfo.get(linebasket).get(kn) != null) {
					if (dictionaryJsonSystem.get(kn).get("type").contains(("string"))) {
						list = new JSONArray();
//						if (dictionaryJsonSystem.get(kn).get("format") != null)
//							list.add(new Dateformatmodif(listBasket_hmInfo.get(linebasket).get(kn),
//									dictionaryMRISystem.get(kn).get("format"),
//									dictionaryJsonSystem.get(kn).get("format")).getNewFormatDate());
//						else
						list.add(listBasket_hmInfo.get(linebasket).get(kn));
						obj.put(dictionaryJsonSystem.get(kn).get("tagJson").toString(), list);
					}

					if (dictionaryJsonSystem.get(kn).get("type").contains(("float"))) {
						list = new JSONArray();

						if (listBasket_hmInfo.get(linebasket).get(kn).split(" +").length > 1) {
							for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +")) {
								list2 = new JSONArray();
								list2.add(Float.parseFloat(hj));
								list.add(list2);
							}

						} else
							list.add(Float.parseFloat(listBasket_hmInfo.get(linebasket).get(kn)));
						obj.put(dictionaryJsonSystem.get(kn).get("tagJson").toString(), list);
					}

					if (dictionaryJsonSystem.get(kn).get("type").contains(("int"))) {
						list = new JSONArray();

						if (listBasket_hmInfo.get(linebasket).get(kn).split(" +").length > 1) {
							for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +")) {
								list2 = new JSONArray();
								list2.add(Integer.parseInt(hj));
								list.add(list2);
							}

						} else
							list.add(Integer.parseInt(listBasket_hmInfo.get(linebasket).get(kn)));

						obj.put(dictionaryJsonSystem.get(kn).get("tagJson").toString(), list);
					}
				}
			} catch (Exception e) {

			}
		}
		for (String kn : dictionaryJsonUser.keySet()) {

			try {
				if (listBasket_hmInfo.get(linebasket).get(kn) != null) {

					if (dictionaryJsonUser.get(kn).get("type").contains(("string"))) {
						list = new JSONArray();
						list.add(listBasket_hmInfo.get(linebasket).get(kn));
						obj.put(dictionaryJsonUser.get(kn).get("tagJson").toString(), list);
					}

					if (dictionaryJsonUser.get(kn).get("type").contains(("float"))) {
						list = new JSONArray();

						if (listBasket_hmInfo.get(linebasket).get(kn).split(" +").length > 1) {
							for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +")) {
								list2 = new JSONArray();
								list2.add(Float.parseFloat(hj));
								list.add(list2);
							}

						} else
							list.add(Float.parseFloat(listBasket_hmInfo.get(linebasket).get(kn)));
						obj.put(dictionaryJsonUser.get(kn).get("tagJson").toString(), list);
					}

					if (dictionaryJsonUser.get(kn).get("type").contains(("int"))) {
						list = new JSONArray();

						if (listBasket_hmInfo.get(linebasket).get(kn).split(" +").length > 1) {
							for (String hj : listBasket_hmInfo.get(linebasket).get(kn).split(" +")) {
								list2 = new JSONArray();
								list2.add(Integer.parseInt(hj));
								list.add(list2);
							}

						} else
							list.add(Integer.parseInt(listBasket_hmInfo.get(linebasket).get(kn)));

						obj.put(dictionaryJsonUser.get(kn).get("tagJson").toString(), list);
					}

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
		HashMap<String, String> niftiHdrType = new HashMap<>();

		for (String jj[] : DictionaryHeaderNifti.listTagHeaderNifti) {
			// System.out.println(jj[0]+" , "+jj[1]);
			niftiHdrType.put(jj[0], jj[1]);
		}

		try {
			niftiParam.readHeader();
		} catch (Exception e) {
		}
		String hdr = niftiParam.getHeader();
		// Scanner scanner = new Scanner(hdr);
		// try {
		// while (scanner.hasNextLine()) {
		// String line = scanner.nextLine();
		// System.out.println(line);
		// if (!line.isEmpty()) {
		// list = new JSONArray();
		// list.add(line.substring(line.indexOf(":")+1).trim().toString());
		// obj.put(line.substring(0, line.indexOf(":")).toString(),list);
		// }
		// }
		// }

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

					if (niftiHdrType.containsKey(key)) {

						if (niftiHdrType.get(key).contentEquals("string")) {
							list = new JSONArray();
							list.add(value);
							obj.put(key, list);
						}

						else if (niftiHdrType.get(key).contentEquals("float")) {
							list = new JSONArray();

							if (value.split(" +").length > 1) {
								for (String hj : value.split(" +")) {
									list2 = new JSONArray();
									list2.add(Float.parseFloat(hj));
									list.add(list2);
								}

							} else
								list.add(Float.parseFloat(value));
							obj.put(key, list);
						}

						else if (niftiHdrType.get(key).contentEquals("int")) {
							list = new JSONArray();

							if (value.split(" +").length > 1) {
								for (String hj : value.split(" +")) {
									list2 = new JSONArray();
									list2.add(Integer.parseInt(hj));
									list.add(list2);
								}

							} else
								list.add(Integer.parseInt(value));

							obj.put(key, list);
						}

						// list = new JSONArray();
						// list.add(line.substring(line.indexOf(":") + 1).trim().toString());
						// obj.put(line.substring(0, line.indexOf(":")).toString(), list);
					}
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		// scanner.close();

		/************************
		 * add Json_Version Tag in Json
		 ************************************************/
		list = new JSONArray();
		list.add("Irmage2018");
		obj.put("Json_Version", list);

		/************************
		 * create Json
		 ************************************************/

		try {
			FileWriter writer = new FileWriter(pathJson + PrefParam.separator + nameJson + ".json");
			writer.write(obj.toJSONString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			new GetStackTrace(e);
//			FileManagerFrame.getBugText().setText(
//					FileManagerFrame.getBugText().getText() + "\n----------------\n" + GetStackTrace.getMessage());
			System.out.println("Error: unable to create file '" + pathJson + PrefParam.separator + nameJson
					+ ".json" + "'");
		}
	}
}