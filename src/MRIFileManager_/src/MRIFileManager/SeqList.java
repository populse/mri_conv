package MRIFileManager;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import abstractClass.ParamMRI2;

public class SeqList {
	
	private JTable seqList;
	
	public SeqList(Object[][] data){
		
		String[] columnN = ParamMRI2.headerListSeq;
		
		seqList = new JTable(data, columnN);
		seqList.setFillsViewportHeight(false);
		seqList.setAutoCreateColumnsFromModel(true);
		seqList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		seqList.getTableHeader().setReorderingAllowed(false);
//		for (int i=5;i<columnN.length;i++) {
//			seqList.getColumnModel().getColumn(i).setMinWidth(0);
//			seqList.getColumnModel().getColumn(i).setMaxWidth(0);
//			seqList.getColumnModel().getColumn(i).setWidth(0);
//		}
		
		seqList.setEnabled(false);
	}
	
	public JTable getTableSeq() {
		return seqList;
	}
}