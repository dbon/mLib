package de.dbon.java.vlib;

import javax.swing.table.DefaultTableModel;

public class A_DefaultTableModel extends DefaultTableModel {

  public A_DefaultTableModel(Object[] columnNames, int rowCount) {
    super(columnNames, rowCount);
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return Integer.class;
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }
}
