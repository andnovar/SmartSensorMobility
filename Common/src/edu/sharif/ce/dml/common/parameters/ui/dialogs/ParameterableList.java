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


import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 29, 2007
 * Time: 5:33:24 PM<br/>
 * List of {@link edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable} objects to select by user.
 */
public class ParameterableList extends ParameterList<Parameterable> {

    public ParameterableList(java.util.List<Parameterable> parameterables,boolean showInternalParameterables) {
        this(parameterables,null,showInternalParameterables);
    }

    public ParameterableList(java.util.List<Parameterable> parameterables,JFrame owner, boolean showInternalParameterables) {
        super(parameterables,owner );
        TableColumn configColumn = parameterTable.getColumnModel().getColumn(2);
        configColumn.setCellEditor(new ButtonEditor(showInternalParameterables));
        configColumn.setCellRenderer(new ButtonRenderer());
    }

    /**
     * adds a config button for the Parameterable object.
     * @return
     */
    protected String[] getColumns() {
        String[] superColumns = super.getColumns();
        java.util.List<String> columnsList = new ArrayList<String>(superColumns.length);
        columnsList.addAll(Arrays.asList(superColumns));
        columnsList.add("Config");
        return columnsList.toArray(new String[columnsList.size()]);
    }

    protected Object[] getInitData(Parameterable o) {
        Object[] objects = super.getInitData(o);
        java.util.List<Object> objectsList = new ArrayList<Object>(objects.length);
        objectsList.addAll(Arrays.asList(objects));
        objectsList.add("");
        return objectsList.toArray();
    }
}


class ButtonEditor extends AbstractCellEditor
        implements TableCellEditor,
        ActionListener {
    //        Color currentColor;
    private JButton button;
    private JDialog dialog;
    protected static final String EDIT = "edit";

    boolean showInternalParameterables;


    public ButtonEditor(boolean showInternalParameterables) {
        button = new JButton("Config");
        button.setForeground(Color.blue);
        button.setActionCommand(EDIT);

        button.addActionListener(this);
//            button.setBorderPainted(false);
        this.showInternalParameterables=showInternalParameterables;
    }

    public void actionPerformed(ActionEvent e) {
        //The user has clicked the cell, so
        //bring up the dialog.
//                button.setBackground(currentColor);
//                colorChooser.setColor(currentColor);
        dialog.setVisible(true);

        fireEditingStopped(); //Make the renderer reappear.

        /*} else { //User pressed dialog's "OK" button.
//                currentColor = colorChooser.getColor();
        }*/
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return "";
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
//            currentColor = (Color)value;
        Parameterable parameterable = (Parameterable) table.getValueAt(row, 1);
        dialog = new TreeParameterableConfigDialog( parameterable,false);
        return button;
    }
}
class ButtonRenderer extends JButton
        implements TableCellRenderer {
    public ButtonRenderer() {
        setText("Config");
    }

    public Component getTableCellRendererComponent(
            JTable table, Object color,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        return this;
    }

}

