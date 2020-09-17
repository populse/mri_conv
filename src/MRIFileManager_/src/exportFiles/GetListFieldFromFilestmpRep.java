package exportFiles;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetListFieldFromFilestmpRep {

	private String txt, separateChar;
	private String[] listField = { "PatientName", "StudyName", "CreationDate", "AcquisitionDate", "SeqNumber", "SerialNumber", "Protocol", "SequenceName",
	"AcquisitionTime" };
	private String[] listFieldTrue = { "Patient Name", "Study Name", "Creation Date", "Acquisition Date", "Seq Number", "Serial Number", "Protocol",
			"Sequence Name", "Acquisition Time" };
	private HashMap<String, String> dict = new HashMap<>();

	public GetListFieldFromFilestmpRep(String txt) {
		for (int i = 0; i < listField.length; i++)
			dict.put(listField[i], listFieldTrue[i]);
		Pattern pattern = null;
		Matcher matcher;
		int count = 0;
		int nField = 0;
		String hj = txt;

		for (String gh : listField) {
			count = 0;
			pattern = Pattern.compile(gh);
			matcher = pattern.matcher(txt);
			while (matcher.find())
				count++;
			hj = hj.replace(gh, "");
			nField += count;
		}

		if (nField != 1) {
			separateChar = hj.substring(0, hj.length() / (nField - 1));
			txt = txt.replace(hj.substring(0, hj.length() / (nField - 1)), "-");
		}

		this.txt = txt;
	}

	public String[] getListFieldTrue() {
		String[] ls = txt.split("-");
		for (int i = 0; i < ls.length; i++)
			ls[i] = dict.get(ls[i]);
		return ls;
	}

	public String[] getListField() {
		return txt.split("-");
	}

	public String getSeparateChar() {
		return separateChar;
	}
}