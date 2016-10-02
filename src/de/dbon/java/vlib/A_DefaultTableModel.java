package de.dbon.java.vlib;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

public class A_DefaultTableModel extends DefaultTableModel {

  public A_DefaultTableModel(Object[] columnNames, int rowCount) {
    super(columnNames, rowCount);
    System.out.println("A_DefaultTableModel()");
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == Interface.columnNames.get(Interface.COLUMN_NAME_FILERATING)) {
      return ImageIcon.class;
    } else if (columnIndex == Interface.columnNames.get(Interface.COLUMN_NAME_FILESIZE)) {
      return Integer.class;
    } else if (columnIndex == Interface.columnNames.get(Interface.COLUMN_NAME_NUMBER)) {
      return Integer.class;
    } else if (columnIndex == Interface.columnNames.get(Interface.COLUMN_NAME_REVIEWED)) {
      return Integer.class;
    } else if (columnIndex == Interface.columnNames.get(Interface.COLUMN_NAME_TOBEDELTED)) {
      return Integer.class;
    } else {
      return String.class;
    }
  }


  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }
}
