package MRIFileManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Dateformatmodif {

	private String resul;

	public Dateformatmodif(String date, String oldFormat, String newFormat) {
		
//		System.out.println(this + ":" + date + " , " + oldFormat + " , " + newFormat);

		resul = date.replace(":", ".");

		if (!date.isEmpty()) {
			SimpleDateFormat dt;
			Date df = null;
			SimpleDateFormat dt1 = new SimpleDateFormat(newFormat);
			
			for (String jj : oldFormat.split("or")) {
				dt = new SimpleDateFormat(jj.trim(),new Locale("EN","en")); 
				try {
					df = dt.parse(date);
					resul = dt1.format(df);
					break;
				}
				catch (ParseException e) {
				} 
			}
		}
	}

	public String getNewFormatDate() {
		return resul;
	}
}