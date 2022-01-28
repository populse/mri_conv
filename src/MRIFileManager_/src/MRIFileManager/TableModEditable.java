package MRIFileManager;

import javax.swing.table.AbstractTableModel;


public class TableModEditable extends AbstractTableModel{
	
	private static final long serialVersionUID = 1L;
	private String[] columnNames;
	private Object[][] data;
	
	public TableModEditable(Object [][] data,String[] columNames) {
		this.data = data;
		this.columnNames = columNames;
	}

	@Override
	public int getColumnCount() {
		return data[0].length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public String getColumnName(int col) {
        return columnNames[col];
    }

	@Override
	public Object getValueAt(int row, int col) {
		 return data[row][col];
	}

    @Override
	public Class<? extends Object> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	if (columnIndex > 2)
    		return true;
    	return false;
    }
    
    public void setValueAt(Object value, int row, int column) {
    	if (column > 2) 
    		data[row][column] = value;
      }
    
}