package bids;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class TableBidsSequence extends PrefParam implements ParamMRI2 {

	private Object[][] data;

	public TableBidsSequence(String repertory,String PatientName, String StudyName) throws IOException {

		List<File> listBids = searchBids2(repertory+separator+PatientName+separator+StudyName);

		data = new Object[1][headerListSeq.length];
		for (int i = 0; i < data[0].length; i++)
			data[0][i] = "";
		data[0][0] = "no nii.gz file found";

		if (!listBids.isEmpty()) {
			String prefixSeq = ("000000").substring(0, String.valueOf(listBids.size()).length());
			data = new Object[listBids.size()][headerListSeq.length];
			for (int i = 0; i < data.length; i++) {
				try {
					data[i] = new ListBidsSequence(new String[] {PatientName,StudyName},listBids.get(i).toString(), (prefixSeq + i).substring(String.valueOf(i).length())).ListSeqBids();
				} catch (Exception e) {
					for (int j = 0; j < data[0].length; j++)
						data[i][j] = "";
					data[i][0] = (prefixSeq + i).substring(String.valueOf(i).length());
					data[i][1] = "(error with nii.gz file)";
				}
			}
		}
	}
	
	private List<File> searchBids2(String rep) {
		List<File> listBids = new ArrayList<>();
		File root = new File(rep);
		String extensionName = ".nii.gz";
		try {
			boolean recursive = true;
			Collection files = FileUtils.listFiles(root, null, recursive);
			for (Iterator iterator = files.iterator();iterator.hasNext();) {
				File file = (File) iterator.next();
				if (file.getName().endsWith(extensionName))
					listBids.add(file);
			}
		} catch (Exception e) {
		}
		return listBids;
	}

	public Object[][] getSequence() {
		return data;
	}
}