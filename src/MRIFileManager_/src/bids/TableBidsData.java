package bids;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;

import MRIFileManager.FileManagerFrame;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class TableBidsData extends PrefParam implements ParamMRI2 {

	private Object[][] data;
	private String[] listSousRep;

	public TableBidsData(String repertory) throws IOException {

		listSousRep = new ListUnderRep().listesousrep(repertory);

		if (listSousRep != null) {
			HashMap<String, String[]> listSt = new HashMap<>();
			
			FileManagerFrame.dlg.setVisible(true);
			
			int sizeAllSub = 0;
			for (String hh : listSousRep) {
				String[] tmp = listStudy(repertory+separator+hh);
				if (tmp != null && tmp.length>0) {
					listSt.put(hh, tmp);
					sizeAllSub+=tmp.length-1;
				}
			}

			data = new Object[listSousRep.length+sizeAllSub][headerListData.length];
//			for (int i = 4; i < data[0].length; i++)
//				data[0][i] = "";

			int off = 0;
			for (int i = 0; i < listSousRep.length; i++) {
				FileManagerFrame.dlg.setTitle("Loading : data " + i * 100 / data.length + " %");
				String[] tmp = listSt.get(listSousRep[i]);
				if (tmp != null)
					for (String hh:tmp) {
						data[off][0] = iconBids;
						data[off][1] = repertory.substring(repertory.lastIndexOf(PrefParam.separator) + 1);
						data[off][2] = listSousRep[i];
						data[off][3] = hh;
						for (int j=4;j<data[0].length;j++)
							data[off][j] = "";
						off++;
					}
				else {
					data[off][0] = iconBids;
					data[off][1] = repertory.substring(repertory.lastIndexOf(PrefParam.separator) + 1);
					data[off][2] = listSousRep[i];
					data[off][3] = "";
					for (int j=4;j<data[0].length;j++)
						data[off][j] = "";
					off++;
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			FileManagerFrame.dlg.setVisible(false);
			
		} else {
			data = new Object[1][headerListData.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Bids structure found";
		}
	}

	private String[] listStudy(String path) {
//		String listProtocol = "anat,fmap,tmap,func,dti,dwi,meg,beh,pcasl,derivatives,.datalad";

		File repertory = new File(path);
		String[] listrep = repertory.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (new File(dir, name).isDirectory() && !listProtocolsForBids.contains(name));
			}
		});
		return listrep;
	}

	public Object[][] getData() {
		return data;
	}
}