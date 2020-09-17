package philips;

import javax.swing.JPopupMenu;

import MRIFileManager.FileManagerFrame;
import abstractClass.PrefParam;
import abstractClass.SelectionData;

public class SelectionDataPhilips extends SelectionData {

	private String dataSelected;

	public SelectionDataPhilips(FileManagerFrame wind) throws Exception {
		super();
		if (PrefParam.DirectoryDataOnly.isEmpty())
			dataSelected = wind.getListPath().getSelectedItem().toString() + PrefParam.separator
					+ wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(), 1);
		else
			dataSelected = wind.getListPath().getSelectedItem().toString();
		dataSelected = dataSelected.substring(12);
	}

	@Override
	public void popMenuData(JPopupMenu popMenu) {
	}

	@Override
	public void popMenuDataExport(JPopupMenu popMenuExport) {

	}
}