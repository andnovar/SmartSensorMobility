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

import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.ui.forms.DialogTemplate;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 22, 2007
 * Time: 11:42:48 PM<br>
 * A dialog that user can add some objects from a set of parameterables.
 */
public class VariableParameterableList extends DialogTemplate {
    /**
     * internal table object
     */
    protected JTable parameterTable;
    /**
     * user selection
     */
    protected List<Parameterable> selectedObjects;
    /**
     * last user selection. to handle cancel button operation.
     */
    protected List<Parameterable> lastSeletedObjects;

    private ParameterableSelect parameterableSelect;

    private OkBtnObservable okBtnObservable = new OkBtnObservable();

    /**
     * default name for new objects
     */
    private static String DEFAULT_NAME_TEXT = "Untitled";

    public VariableParameterableList(String name, List<Parameterable> choices, List<Parameterable> selected,
                                     boolean showInternalParameterables) {
        this(name, choices, selected, null,showInternalParameterables);
    }

    public VariableParameterableList(String name, List<Parameterable> choices, List<Parameterable> selected, JFrame parent,
                                     boolean showInternalParameterables) {
        super(parent, name, true);
        final String[] columns = getColumns();
        Object[][] datas = new Object[0][columns.length];
        parameterTable = new JTable(new ListTableModel(datas,columns){
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }
        });
        Box v = Box.createVerticalBox();
        this.getContentPane().add(v);
        v.add(new JScrollPane(parameterTable), BorderLayout.CENTER);
        parameterTable.setPreferredScrollableViewportSize(new Dimension(300, 300));
        parameterTable.setDragEnabled(false);

        JTextField nameTextField = new JTextField();
        parameterTable.getColumnModel().getColumn(1).setCellEditor(new NameEditor());

        /////////////
        {
            Box h = Box.createHorizontalBox();
            v.add(h);
            parameterableSelect = new ParameterableSelect("Choices", choices,true);
            h.add(parameterableSelect);
            h.add(Box.createHorizontalStrut(5));
            final JTextField nameTxt = new JTextField(DEFAULT_NAME_TEXT, 15);
            h.add(nameTxt);
            h.add(Box.createHorizontalStrut(5));
            JButton addBtn = new JButton("Add");
            h.add(Box.createHorizontalStrut(5));
            h.add(addBtn);
            JButton deleteBtn = new JButton("Delete Selected");
            h.add(Box.createHorizontalStrut(5));
            h.add(deleteBtn);
            addBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //instantiate
                    try {
                        //check name
                        {
                            String newName = nameTxt.getText();
                            for (Parameterable selectedObject : selectedObjects) {
                                if (newName.equals(selectedObject.toString())){
                                    nameTxt.setText(newName+(int)(Math.random()*100));
                                    break;
                                }
                            }
                        }
                        Parameterable parameterableInstance = ParameterableImplement.cloneParameterable(parameterableSelect.getSelectedParameter(), nameTxt.getText());
                        nameTxt.setText(DEFAULT_NAME_TEXT);
                        //add new to table
                        Object[][] datas = ((ListTableModel) parameterTable.getModel()).data;
                        //add to selected
                        selectedObjects.add(parameterableInstance);
                        Collections.sort(selectedObjects);
                        List<Object[]> datasList = new ArrayList<Object[]>(datas.length + 1);
                        int i = 0;
                        for (Parameterable o : selectedObjects) {
                            datasList.add(getInitData(o));
                        }
                        ((ListTableModel) parameterTable.getModel()).setData(datasList.toArray(new Object[datasList.size()][getColumns().length]));
                        parameterTable.updateUI();
                    } catch (InvalidParameterInputException e1) {
                        e1.showMessage(thisDialog);
                    }
                }
            });

            deleteBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //remove from selected
                    for (int k = 0; k < parameterTable.getRowCount(); k++) {
                        if ((Boolean) parameterTable.getValueAt(k, 0)) {
                            selectedObjects.remove((Parameterable) parameterTable.getValueAt(k, 1));
                        }
                    }

                    //remove from table
                    Object[][] datas = new Object[selectedObjects.size()][columns.length];
                    int i = 0;
                    for (Parameterable o : selectedObjects) {
                        datas[i++] = getInitData(o);
                    }
                    ((ListTableModel) parameterTable.getModel()).setData(datas);
                    parameterTable.updateUI();
                }
            });
        }
        /////////////
        v.add(Box.createVerticalGlue());
        v.add(new JSeparator());
        Box h = Box.createHorizontalBox();
        v.add(h);
        h.add(Box.createHorizontalGlue());
        JButton cancelBtn = new JButton("Cancel");
        h.add(cancelBtn);
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JButton okBtn = new JButton("OK");
        h.add(Box.createHorizontalStrut(10));
        h.add(okBtn);

        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //save changes
                lastSeletedObjects = selectedObjects;
                okBtnObservable.change(selectedObjects);
                dispose();
            }
        });

        setSelectedObjects(selected);
        TableColumn configColumn = parameterTable.getColumnModel().getColumn(2);
        configColumn.setCellEditor(new ButtonEditor(showInternalParameterables));
        configColumn.setCellRenderer(new ButtonRenderer());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            setSelectedObjects(selectedObjects);
        }
        super.setVisible(b);
    }

    public void addOKBtnObserver(Observer o) {
        okBtnObservable.addObserver(o);
    }

    public List<Parameterable> getSelectedObjects() {
        return lastSeletedObjects;
    }

    /**
     * @param selected default selected objects
     */
    public void setSelectedObjects(Collection<Parameterable> selected) {
        String[] columns = getColumns();
        selectedObjects = new LinkedList<Parameterable>();
        //remove rows
        Object[][] datas = new Object[selected.size()][columns.length];
        int i = 0;
        selectedObjects = new LinkedList<Parameterable>(selected);
        Collections.sort(selectedObjects);
        for (Parameterable selectedObject : selectedObjects) {
            datas[i++] = getInitData(selectedObject);
        }
        lastSeletedObjects = new LinkedList<Parameterable>(selectedObjects);
        ((ListTableModel) parameterTable.getModel()).setData(datas);
    }

    /**
     * override this method to change table structure.
     *
     * @return columns of the table.
     */
    protected String[] getColumns() {
        return new String[]{"Selection", "Name", "Config"};

    }

    /**
     * override this method to change table structure.
     *
     * @param o
     * @return table objects from object <tt>E</tt>
     */
    protected Object[] getInitData(Parameterable o) {
        return new Object[]{false, o, ""};
    }


    private class NameEditor extends AbstractCellEditor implements TableCellEditor,ActionListener {
        private JTextField nameTextField=new JTextField();
        private Parameterable parameterable;

        private NameEditor() {
            nameTextField.addActionListener(this);
        }

        public Object getCellEditorValue() {
            return parameterable;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            parameterable= (Parameterable) value;
            nameTextField.setText(value.toString());

            return nameTextField;
        }

        public void actionPerformed(ActionEvent e) {
            parameterable.setName(nameTextField.getText());
            fireEditingStopped();
        }
    }
}

