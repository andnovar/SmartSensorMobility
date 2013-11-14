/*
 * Copyright (c) 2005-2008 by Masoud Moshref Javadi <moshref@ce.sharif.edu>, http://ce.sharif.edu/~moshref
 * The license.txt file describes the conditions under which this software may be distributed.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package edu.sharif.ce.dml.common.parameters.ui.dialogs;

import edu.sharif.ce.dml.common.ui.forms.DialogTemplate;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 2, 2007
 * Time: 10:07:47 PM<br/>
 * Shows a list of objects in a table to select.
 */
public class ParameterList<E> extends DialogTemplate {
    /**
     * table object
     */
    protected JTable parameterTable;
    /**
     * selected objects
     */
    protected List<E> selectedObjects;

    protected OkBtnObservable okBtnObservable = new OkBtnObservable();

    protected java.util.List<E> objects;
    Comparator<? super E> comparator;

    public ParameterList(java.util.List<E> objects) {
        this(objects, null);
    }

    public ParameterList(java.util.List<E> objects, JFrame parent, Comparator<? super E> c) {
        super(parent, "Config", true);
        this.objects = objects;
        this.comparator = c;
        if (c != null) {
            Collections.sort(objects, c);
        } else if (objects.size() > 0 && objects.get(0) instanceof Comparable) {
            Collections.sort((List<? extends Comparable>) objects);
            //todo
        }
        String[] columns = getColumns();
        Object[][] datas = new Object[objects.size()][columns.length];

        int i = 0;
        for (E o : objects) {
            datas[i++] = getInitData(o);
        }
        parameterTable = new JTable(new ListTableModel(datas, columns));

        Box v = Box.createVerticalBox();
        this.getContentPane().add(v);
        v.add(new JScrollPane(parameterTable), BorderLayout.CENTER);
        parameterTable.setPreferredScrollableViewportSize(
                new Dimension(300, 300));
        parameterTable.setDragEnabled(false);

        /*UIParameter sampleTimeParameter = new UIParameter<Integer>("SampleTime: ", 0);
        v.add(sampleTimeParameter);*/
        v.add(Box.createVerticalGlue());
        v.add(new JSeparator());
        Box h = Box.createHorizontalBox();
        v.add(h);
        JButton selectAllBtn = new JButton("Select All");
        h.add(selectAllBtn);
        selectAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TableModel tableModel = parameterTable.getModel();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    tableModel.setValueAt(true, i, 0);
                }
            }
        });
        h.add(Box.createHorizontalStrut(10));
        JButton deSelectAllBtn = new JButton("Deselect All");
        h.add(deSelectAllBtn);
        deSelectAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TableModel tableModel = parameterTable.getModel();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    tableModel.setValueAt(false, i, 0);
                }
            }
        });

        h.add(Box.createHorizontalGlue());
        JButton cancelBtn = new JButton("Cancel");
        h.add(cancelBtn);
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedObjects = new LinkedList<E>();
                dispose();
            }
        });
        JButton okBtn = new JButton("OK");
        h.add(Box.createHorizontalStrut(10));
        h.add(okBtn);

        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedObjects = new LinkedList<E>();
                for (int k = 0; k < parameterTable.getRowCount(); k++) {
                    if ((Boolean) parameterTable.getValueAt(k, 0)) {
                        selectedObjects.add((E) parameterTable.getValueAt(k, 1));
                    }
                }
                okBtnObservable.change(selectedObjects);
                dispose();
            }
        });


        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
    }

    public void addOkBtnObserver(Observer o) {
        okBtnObservable.addObserver(o);
    }

    public ParameterList(java.util.List<E> objects, JFrame parent) {
        this(objects, parent, null);
    }

    /**
     * @return user selected objects
     */
    public List<E> getSelectedObjects() {
        return selectedObjects;
    }

    /**
     * @param selected default select objects
     */
    public void setSelectedObjects(Collection<E> selected) {
        selectedObjects = new LinkedList<E>();
        for (int i = 0; i < parameterTable.getRowCount(); i++) {
            boolean found = false;
            for (E e : selected) {
                if (e.equals(parameterTable.getValueAt(i, 1))) {
                    parameterTable.setValueAt(true, i, 0);
                    parameterTable.setValueAt(e, i, 1);
                    selectedObjects.add((E) parameterTable.getValueAt(i, 1));
                    found = true;
                    break;
                }
            }
            if (!found) {
                parameterTable.setValueAt(false, i, 0);
            }
        }
    }

    /**
     * override this method to change table structure.
     *
     * @return columns of the table.
     */
    protected String[] getColumns() {
        return new String[]{"Selection", "Name"};
    }

    /**
     * override this method to change table structure.
     *
     * @param o
     * @return table objects from object <tt>E</tt>
     */
    protected Object[] getInitData(E o) {
        return new Object[]{false, o};
    }
}

/**
 * the table model class
 */
class ListTableModel extends AbstractTableModel {

    public String[] columnNames;
    public Object[][] data;

    /**
     * @param data
     * @param columnNames header of the table.
     */
    public ListTableModel(Object[][] data, String[] columnNames) {
        this.columnNames = columnNames;
        this.data = data;
    }

    public void setData(Object[][] data) {
        this.data = data;
    }

    public Object[][] getData() {
        return data;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 1;
    }

    /*
    * JTable uses this method to determine the default renderer/
    * editor for each cell.
    */

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        /*if (col == 1 && value.equals(Boolean.FALSE)) {
            edu.sharif.ce.dml.common.data[row][3] = value;
        } else if (col == 3 && value.equals(Boolean.TRUE)) {
            edu.sharif.ce.dml.common.data[row][2] = value;
        }*/
        fireTableCellUpdated(row, col);
    }
}


