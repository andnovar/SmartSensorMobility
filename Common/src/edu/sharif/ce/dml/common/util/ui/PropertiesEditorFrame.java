package edu.sharif.ce.dml.common.util.ui;

import edu.sharif.ce.dml.common.ui.forms.DialogTemplate;
import edu.sharif.ce.dml.common.util.PropertyManager;
import edu.sharif.ce.dml.common.util.PublicConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: 12/28/10
 * Time: 9:17 PM
 */
public class PropertiesEditorFrame extends DialogTemplate {
    private JTable propertiesTable;
    private JButton saveBtn;
    private JButton cancelBtn;
    private boolean storeNeeded = false;

    public PropertiesEditorFrame(JFrame owner, String title, final PropertyManager propertyManager) throws HeadlessException {
        super(owner, title, true);
        //prepare data
        Map<String, String> properties = propertyManager.getAllProperties();
        final String[][] data = new String[properties.size()][2];
        int i = 0;
        for (String name : properties.keySet()) {
            data[i][0] = name;
            data[i][1] = properties.get(name);
            i++;
        }

        //create ui
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        propertiesTable = new JTable();
        propertiesTable.setModel(new DefaultTableModel(data, new Object[]{"Name", "Value"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        });
        TableColumn valueColumn = propertiesTable.getColumnModel().getColumn(1);
        valueColumn.setCellEditor(new DefaultCellEditor(new JTextField()));
        valueColumn.setMinWidth(150);
        propertiesTable.setDragEnabled(true);
        JScrollPane propertiesTablePane = new JScrollPane(propertiesTable);
        propertiesTablePane.setPreferredSize(new Dimension(300,200));
        mainPanel.add(propertiesTablePane, BorderLayout.CENTER);

        Box h = Box.createHorizontalBox();
        mainPanel.add(h, BorderLayout.PAGE_END);
        h.add(Box.createHorizontalGlue());

        saveBtn = new JButton("Save");
        h.add(saveBtn);
        h.add(Box.createHorizontalStrut(10));
        saveBtn.setDefaultCapable(true);
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TableModel model = propertiesTable.getModel();
                int rowCount = model.getRowCount();
                for (int j = 0; j < rowCount; j++) {
                    propertyManager.addProperty(model.getValueAt(j, 0).toString(), model.getValueAt(j, 1).toString());
                }
                storeNeeded = true;
                dispose();
            }
        });

        cancelBtn = new JButton("Cancel");
        h.add(cancelBtn);
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                storeNeeded = false;
                dispose();
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    public boolean isStoreNeeded() {
        return storeNeeded;
    }
}

