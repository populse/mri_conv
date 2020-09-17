package exportFiles;

//import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class LogJsonMIA extends PrefParam implements ParamMRI2 {

	private JSONObject obj = new JSONObject();

	public LogJsonMIA(String pathLog, Object linebasket, String nameFile, boolean error) {

		String status;
		String[] listLabel = { "Patient Name", "Study Name", "Creation Date", "noSeq", "Sequence Name", "Protocol" };
		String[] listTag = { "PatientName", "StudyName", "CreationDate", "SeqNo", "SequenceName", "Protocol" };

		if (error)
			status = "Export failure";
		else
			status = "Export ok";

		for (int i = 0; i < listLabel.length; i++) {

			try {

				if (listBasket_hmInfo.get(linebasket).get(listLabel[i]).toString().contentEquals(""))
					obj.put(listTag[i], "''''");
				else
					obj.put(listTag[i], listBasket_hmInfo.get(linebasket).get(listLabel[i]));
			} catch (Exception e) {
				obj.put(listTag[i], "''''");
			}
		}

		try {
			if (listBasket_hmInfo.get(linebasket).get("Protocol").isEmpty())
				obj.put("Protocol", listBasket_hmInfo.get(linebasket).get("Manufacturer's Model"));
		} catch (Exception e) {

		}
		obj.put("StatusExport", status);
		obj.put("NameFile", nameFile);
	}

	public String getJson() {
		return obj.toJSONString();
	}
}