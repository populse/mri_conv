package exportFiles;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import abstractClass.PrefParam;

public class UpdateTreeBasket extends PrefParam {

	private JTree tree;

	public UpdateTreeBasket(Object[] arrayPath) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("(your exportation folder)");
		DefaultTreeModel model = new DefaultTreeModel(root);
		tree = new JTree(model);

		for (Object jj : arrayPath) {
			String tmplab = jj.toString();
			tmplab = tmplab.substring(tmplab.indexOf(']') + 1);
			tmplab = tmplab.substring(0, tmplab.indexOf('[')).trim();
			buildTreeFromString(model, tmplab + ".nii");
			buildTreeFromString(model, tmplab + ".json");
		}
	}

	private void buildTreeFromString(final DefaultTreeModel model, String str) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode node = root;

		String[] strings = str.split("\\"+separator);

		for (String s : strings) {
			int index = childIndex(node, s);

			if (index < 0) {
				DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(s);
				node.insert(newChild, node.getChildCount());
				node = newChild;
			} else {
				node = (DefaultMutableTreeNode) node.getChildAt(index);
			}
		}
	}

	/**
	 * Returns the index of a child of a given node, provided its string value.
	 * 
	 * @param node
	 *            The node to search its children
	 * @param childValue
	 *            The value of the child to compare with
	 * @return The index
	 */
	private int childIndex(final DefaultMutableTreeNode node, final String childValue) {
//		Enumeration<DefaultMutableTreeNode> children = node.children();

		Enumeration<TreeNode> children = node.children();
		DefaultMutableTreeNode child = null;
		
		int index = -1;

		while (children.hasMoreElements() && index < 0) {
			child = (DefaultMutableTreeNode) children.nextElement();

			if (child.getUserObject() != null && childValue.equals(child.getUserObject())) {
				index = node.getIndex(child);
			}
		}

		return index;
	}

	public TreeModel returnTreeModel() {
		return tree.getModel();
	}

}
