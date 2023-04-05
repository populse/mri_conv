package brukerParavision;

import java.io.IOException;
import java.util.List;

import MRIFileManager.FileManagerFrame;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class TableBrukerSequence implements ParamMRI2 {

	private Object[][] data;

	public TableBrukerSequence(String repertory) throws IOException {

		List<String> list2dseq = new Search2dseq(repertory).getList2dseq();
		
		String directory = repertory.substring(repertory.lastIndexOf(PrefParam.separator)+1);
		
		if (!list2dseq.isEmpty()) {
			String prefixSeq = ("000000").substring(0, String.valueOf(list2dseq.size()).length());

			data = new Object[list2dseq.size()][headerListSeq.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Bruker sequence found";

			FileManagerFrame.dlg.setVisible(true);
			String title = "Loading : sequence ";
			for (int i = 0; i < data.length; i++) {
				FileManagerFrame.dlg.setTitle(title + i * 100 / data.length + " %");
				data[i] = new ListBrukerSequence(directory, list2dseq.get(i),
						(prefixSeq + i).substring(String.valueOf(i).length())).ListSeqBruker();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			FileManagerFrame.dlg.setVisible(false);

		} else {
			data = new Object[1][headerListSeq.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Bruker sequence found";
		}
	}

	public Object[][] getSequence() {
		return data;
	}
}