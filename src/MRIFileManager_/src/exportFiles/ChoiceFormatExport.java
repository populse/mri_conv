package exportFiles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.table.TableRowSorter;

import MRIFileManager.BasketList;
import MRIFileManager.FileManagerFrame;
import MRIFileManager.TableMod;
import abstractClass.ParamMRI2;

public class ChoiceFormatExport implements ActionListener, ParamMRI2 {

	private FileManagerFrame wind;

	public ChoiceFormatExport(FileManagerFrame wind) {
		this.wind = wind;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String tmp, tmp2, tmpf;
		for (int i = 0; i < listinBasket.getSize(); i++) {
			tmp = listinBasket.get(i);
			tmp2 = tmp.substring(tmp.indexOf("]") + 1).trim();
			tmp2 = tmp2.substring(0, tmp2.indexOf("[")).trim();
			tmpf = tmp.replace(tmp2, listBasket_hmInfo.get(tmp).get("pathNifti"));

			if (wind.getChoiceExport().getSelectedItem().toString().contentEquals("BIDS")) {
				try {
				tmpf = tmp.replace(tmp2, listBasket_hmInfo.get(tmp).get("pathBids"));
				}
				catch (Exception er) {
					tmpf = tmp.replace(tmp2, listBasket_hmInfo.get(tmp).get("pathNifti"));
					wind.getChoiceExport().setSelectedItem("Nifti-1");
				}
			} 
//			else {
//				tmpf = tmp.replace(tmp2, listBasket_hmInfo.get(tmp).get("pathNifti"));
//			}
			listinBasket.set(i, tmpf);
			listBasket_hmInfo.put(tmpf, listBasket_hmInfo.get(tmp));
			listBasket_hmOrderImage.put(tmpf, listBasket_hmOrderImage.get(tmp));
			listBasket_hmSeq.put(tmpf, listBasket_hmSeq.get(tmp));

		}

		new BasketList(wind);
		
		wind.getTreeBasket().setModel(new UpdateTreeBasket(listinBasket.toArray()).returnTreeModel());
		for (int i = 0; i < wind.getTreeBasket().getRowCount(); i++)
			wind.getTreeBasket().expandRow(i);
	}
}