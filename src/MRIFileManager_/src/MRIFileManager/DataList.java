package MRIFileManager;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import abstractClass.ParamMRI2;

public class DataList implements ParamMRI2 {

	private JTable dataList;

	public DataList(Object[][] data) {

		String[] columnNames = headerListData;

		dataList = new JTable(data, columnNames);
		dataList.setFillsViewportHeight(true);

//		dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		dataList.getTableHeader().setReorderingAllowed(false);
		// for (int i=5;i<ParamMRI.headerListData.length;i++) {
		// dataList.getColumnModel().getColumn(i).setMinWidth(0);
		// dataList.getColumnModel().getColumn(i).setMaxWidth(0);
		// dataList.getColumnModel().getColumn(i).setWidth(0);
		// }
		dataList.setEnabled(false);
		dataList.getTableHeader().setReorderingAllowed(false);
	}

	public JTable getTableData() {
		return dataList;
	}
}