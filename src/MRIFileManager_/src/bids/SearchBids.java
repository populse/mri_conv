package bids;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class SearchBids {
	
	private String rep;
	private List<String> listSeq = new ArrayList<>();

	public SearchBids(String rep) {
		this.rep = rep;
		search();
	};

	public void search() {

		File root = new File(rep);
		String extensionName = ".nii.gz";
		try {
			boolean recursive = true;
			Collection files = FileUtils.listFiles(root, null, recursive);
			for (Iterator iterator = files.iterator();iterator.hasNext();) {
				File file = (File) iterator.next();
				if (file.getName().endsWith(extensionName))
					listSeq.add(file.toString());
			}
		} catch (Exception e) {
		}
	}

	public List<String> getList() {
		return listSeq;
	}
}