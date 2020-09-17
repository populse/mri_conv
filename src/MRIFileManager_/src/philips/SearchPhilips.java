package philips;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchPhilips {

	private String rep;
	private List<String> listSeq = new ArrayList<>();

	public SearchPhilips(String rep) {
		this.rep = rep;
		search();
	}

	public void search() {

		String[] listOfFiles = new File(rep).list();

		for (String lg : listOfFiles) {
			if (lg.toUpperCase().endsWith(".REC"))
				listSeq.add(lg);
		}
		
		listOfFiles=null;
	}
	
	public List<String> getList () {
		return listSeq;
	}
}