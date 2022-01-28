package MRIFileManager;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import abstractClass.ParamMRI2;

public class BasketList implements ParamMRI2 {

	public BasketList(FileManagerFrame wind) {

		String tmp;

		int tmp_length = listinBasket.size();

		if (tmp_length == 0)
			tmp_length = 5;

		Object[][] obj_bask = new Object[tmp_length][headerListBasket.length];

		if (listinBasket.size() != 0) {
			for (int i = 0; i < obj_bask.length; i++) {
				obj_bask[i][0] = listinBasket.get(i).toString().split(" +")[0];
				obj_bask[i][1] = listinBasket.get(i).toString().split(" +")[1];
				tmp = listinBasket.get(i).toString().split(" +")[2];
				tmp = tmp.substring(tmp.indexOf("[") + 1, tmp.indexOf("Mo]"));
				obj_bask[i][2] = Float.valueOf(tmp);
				for (int j = 3; j < headerListBasket.length; j++)
					obj_bask[i][j] = "";
			}
		} else {
			for (int i = 0; i < tmp_length; i++)
				for (int j = 0; j < headerListBasket.length; j++)
					obj_bask[i][j] = "";
		}

		TableModEditable model = new TableModEditable(obj_bask, headerListBasket);
		TableRowSorter<TableModEditable> sorter = new TableRowSorter<>(model);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);

		wind.getTabBasket().removeAll();
		wind.getTabBasket().updateUI();
		wind.getTabBasket().setModel(model);
		wind.getTabBasket().setRowSorter(sorter);
		wind.getTabBasket().setDefaultRenderer(String.class, centerRenderer);
		wind.getTabBasket().setDefaultRenderer(Float.class, centerRenderer);
//		wind.getTabBasket().setFont(new Font("Serif", Font.BOLD, 14));
//		wind.getTabBasket().setRowHeight(25);
		
		wind.getTabBasket().getColumnModel().getColumn(0).setMaxWidth(100);
		wind.getTabBasket().getColumnModel().getColumn(0).setWidth(80);
		wind.getTabBasket().getColumnModel().getColumn(1).setMinWidth(500);
		wind.getTabBasket().getColumnModel().getColumn(1).setWidth(800);
		wind.getTabBasket().getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
		wind.getTabBasket().getColumnModel().getColumn(2).setMinWidth(100);
		wind.getTabBasket().getColumnModel().getColumn(2).setMaxWidth(200);
		wind.getTabBasket().getColumnModel().getColumn(2).setWidth(150);
		wind.getTabBasket().setEnabled(true);

	}

}