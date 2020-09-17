package abstractClass;

import javax.swing.JPopupMenu;

public interface SelectionSeq {

	public abstract void goSelectionSeq() throws Exception;

	public abstract void popMenuSeq(JPopupMenu popMenu);

	public abstract void openImage();
	
	public abstract void showParamFile(String chemFile, String keyword);

	public abstract void fillBasket();

}