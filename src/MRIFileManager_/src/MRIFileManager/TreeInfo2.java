package MRIFileManager;

import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import abstractClass.ParamMRI2;

public class TreeInfo2 implements ParamMRI2 {

	private JTree tree;

	public TreeInfo2(HashMap<String, List<String>> listLabel, HashMap<String, String> listValues) {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("IRMaGe");
		DefaultMutableTreeNode param = null;
		String keyVector = "", valueVector = "";

		for (String sd : listLabel.keySet()) {

			param = new DefaultMutableTreeNode(sd);
			keyVector = "";
			valueVector = "";
			// for (int i = 0; i < listLabel.get(sd).length; i++) {
			// keyVector += listLabel.get(sd)[i] + "&&";
			// if (listValues != null)
			// valueVector += listValues.get(sd)[i] + "&&";
			// else
			// valueVector += "&&";
			// }

			for (int i = 0; i < listLabel.get(sd).size(); i++) {
				keyVector += listLabel.get(sd).get(i);
				if (listValues != null) {
					if (listValues.get(listLabel.get(sd).get(i)) != null) {
						valueVector += listValues.get(listLabel.get(sd).get(i));
						if (listLabel.get(sd).get(i).contains("Echo Time")
								|| listLabel.get(sd).get(i).contains("Repetition Time")
								|| listLabel.get(sd).get(i).contains("Inversion Time")
								|| listLabel.get(sd).get(i).contains("Scanning Sequence"))
							if (listValues.get(listLabel.get(sd).get(i)).split(" +").length > 1)
								keyVector += " (" + listValues.get(listLabel.get(sd).get(i)).split(" +").length + ")";
						if (listLabel.get(sd).get(i).contains("Image Type"))
							if (listValues.get(listLabel.get(sd).get(i)).split(";").length > 1)
								keyVector += " (" + listValues.get(listLabel.get(sd).get(i)).split(";").length + ")";
					}
				}
				valueVector += "&&";
				keyVector += "&&";
			}

			keyVector = keyVector.substring(0, keyVector.length());
			valueVector = valueVector.substring(0, valueVector.length());
			param.add(new DefaultMutableTreeNode("TABLE:" + keyVector + "�" + valueVector));
			root.add(param);
		}

		// UIManager.put("Tree.expandedIcon", new WindowsTreeUI.ExpandedIcon());
		// UIManager.put("Tree.collapsedIcon", new WindowsTreeUI.CollapsedIcon());
		// UIManager.put("Tree.font", new Font("Serif", Font.ITALIC, 12));

		tree = new JTree(root);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(false);
		tree.setCellRenderer(new TreeRenderer2());
		
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		// renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
//		renderer.setBackgroundSelectionColor(new Color(200,200,255));

	}

	public JTree getTreeInfo() {
		return tree;
	}
}

// class TreeNodeVector<E> extends Vector<E> {
// /**
// *
// */
// private static final long serialVersionUID = 1L;
// String name;
//
// TreeNodeVector(String name) {
// this.name = name;
// }
//
// TreeNodeVector(String name, E elements[]) {
// this.name = name;
// for (int i = 0, n = elements.length; i < n; i++) {
// add(elements[i]);
// }
// }
//
// public String toString() {
// return "[" + name + "]";
// }
// }

class TreeRenderer2 extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;
	private Map<String, JTable> tables = new HashMap<>();

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		Component c = super.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, hasFocus);

		if (value.toString().startsWith("TABLE:")) {

			c = tables.get(value.toString());

			if (c == null) {

				TableModel model = createModel(value.toString());
				TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);

				JTable table = new JTable(model);
//				 table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				// table.setForeground(Color.LIGHT_GRAY);
				table.setFont(new Font("Dialog.plain", 0, 14));
				table.setModel(model);
				table.setCellSelectionEnabled(true);
				table.setRowSorter(sorter);
				table.getColumnModel().getColumn(0).setPreferredWidth(200);
				table.getColumnModel().getColumn(1).setPreferredWidth(1000);

//				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//				table.setEnabled(true);
//				table.setCellSelectionEnabled(false);

				tables.put(value.toString(), table);

				c = tables.get(value.toString());
				c.setEnabled(true);
				c.setComponentOrientation(tree.getComponentOrientation());

			}
		}

		return c;
	}

	private TableModel createModel(String tableData) {

		tableData = tableData.substring(6, tableData.length());

		String[] colData = tableData.split("�");
		int nCol = colData.length;
		int nRow = colData[0].split("&&").length;

		Object[][] dat = new Object[nRow][nCol];

		for (int i = 0; i < nRow; i++) {
			for (int j = 0; j < nCol; j++)
				try {
					dat[i][j] = colData[j].split("&&")[i];
				} catch (Exception e) {
					dat[i][j] = "";
				}
		}
		MRIFileManager.TableModel model = new MRIFileManager.TableModel(dat, new String[] { "", "" });

		return model;
	}
}