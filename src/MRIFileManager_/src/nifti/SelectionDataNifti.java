package nifti;

import javax.swing.JPopupMenu;

import MRIFileManager.FileManagerFrame;
import abstractClass.PrefParam;
import abstractClass.SelectionData;

public class SelectionDataNifti extends SelectionData {

	private String dataSelected;

	public SelectionDataNifti(FileManagerFrame wind) throws Exception {
		super();
		dataSelected = wind.getListPath().getSelectedItem().toString() + PrefParam.separator
				+ wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(), 1);
		dataSelected = dataSelected.substring(12);
	}

	@Override
	public void popMenuData(JPopupMenu popMenu) {
	}
	
	@Override
	public void popMenuDataExport(JPopupMenu popMenuExport) {
	}
}