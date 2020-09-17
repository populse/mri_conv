package philips;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import MRIFileManager.FileManagerFrame;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class TablePhilipsSequence extends PrefParam {

	private Object[][] data;

	public TablePhilipsSequence(String repertory) throws IOException {

		List<String> listSeq = new SearchPhilips(repertory).getList();
		ArrayList<Object[]> listData = new ArrayList<>();

		if (!listSeq.isEmpty()) {
			String prefixSeq=("000000").substring(0, String.valueOf(listSeq.size()).length());

			data = new Object[listSeq.size()][ParamMRI2.headerListSeq.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Philips sequence found";

			FileManagerFrame.dlg.setVisible(true);
			String title = "Loading : sequence ";
			ListPhilipsSequence lps;
			int iterNoSeq=0;

			for (int i = 0; i < listSeq.size(); i++) {
				FileManagerFrame.dlg.setTitle(title + i * 100 / data.length + " %");
				lps = new ListPhilipsSequence(repertory + separator + listSeq.get(i),(prefixSeq + iterNoSeq).substring(String.valueOf(iterNoSeq).length()));
				if (ListPhilipsSequence.hasAcqImg) {
					listData.add(lps.ListSeqPhilipsAcq());
					iterNoSeq++;
				}
				if (ListPhilipsSequence.hasCalcImg) {
					listData.add(lps.ListSeqPhilipsCal());
					iterNoSeq++;
				}
			}
			data = new Object[listData.size()][ParamMRI2.headerListSeq.length];
			data = listData.toArray(data);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			FileManagerFrame.dlg.setVisible(false);
			
			listSeq.clear();
			listData.clear();
			lps = null;

		} else {
			data = new Object[1][ParamMRI2.headerListSeq.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Philips sequence found";
		}
	}

	public Object[][] getSequence() {
		return data;
	}
}