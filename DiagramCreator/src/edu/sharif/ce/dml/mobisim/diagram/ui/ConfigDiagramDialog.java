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

package edu.sharif.ce.dml.mobisim.diagram.ui;

import edu.sharif.ce.dml.common.data.EvaluationLoadingHandler;
import edu.sharif.ce.dml.common.data.configfilter.ConfigFileFilter;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.common.logic.worker.MultiTaskSwingWorker;
import edu.sharif.ce.dml.common.logic.worker.ProcessInstance;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterList;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterSelect;
import edu.sharif.ce.dml.common.ui.forms.DialogTemplate;
import edu.sharif.ce.dml.common.util.FileManager;
import edu.sharif.ce.dml.common.util.InvalidRequiredInputFileException;
import edu.sharif.ce.dml.common.util.PropertyManager;
import edu.sharif.ce.dml.common.util.io.loader.User;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkUser;
import edu.sharif.ce.dml.mobisim.diagram.control.DiagramCreationException;
import edu.sharif.ce.dml.mobisim.diagram.control.EvaluationDiagramUsingHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 7, 2007
 * Time: 11:29:01 PM<br>
 * Dialog to configure diagram application. These configurations depends on input evaluation file
 */
public class ConfigDiagramDialog extends DialogTemplate {
    /**
     * key to find diagram default configs.
     */
    public static final String DIAGRAM_DEFAULT_CONFIGS_KEY = "DiagramConfigs";
    /**
     * input, output files.
     */
    private File inputFile, outputFile;
    /**
     * columns values that used for rows of table in diagram.
     */
    private final ParameterSelect<String> rowNameColumnSelect;
    /**
     * The column which its values used as data in table in diagram.
     */
    private final ParameterSelect<String> evaluationColumnSelect;
    /**
     * columns used to create columns of table in diagram.
     */
    private final ParameterList<String> variableColumns;
    /**
     * columns used to create sheets of diagram.
     */
    private final ParameterList<String> sheetsColumns;
    /**
     * save current settings as default
     */
    private final JCheckBox setAsDefaultChk;
    private WindowAdapter myWindowListener;

    /**
     *
     * @param owner parnet frame (window)
     * @param modal is the dialog modal.
     * @param columns present columns in evaluation data
     * @param inFile evaluation input file
     * @param outFile excel output file.
     */
    public ConfigDiagramDialog(JFrame owner, boolean modal, java.util.List<String> columns, File inFile, File outFile) {
        super(owner, "Config Evaluation Diagram", modal);
        this.inputFile = inFile;
        this.outputFile = outFile;
        variableColumns = new ParameterList<String>(columns);
        sheetsColumns = new ParameterList<String>(columns);
        evaluationColumnSelect = new ParameterSelect<String>("Evaluation Column", columns);
        rowNameColumnSelect = new ParameterSelect<String>("Row Column", columns);
        setAsDefaultChk = new JCheckBox("Set as default");
        initialize(columns);
        JPanel mainPanel = new JPanel();
        this.getContentPane().add(mainPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(rowNameColumnSelect);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(evaluationColumnSelect);
        mainPanel.add(Box.createVerticalStrut(10));
        Box h1 = Box.createHorizontalBox();
        mainPanel.add(h1);
        h1.add(setAsDefaultChk);
        h1.add(Box.createHorizontalGlue());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(Box.createVerticalGlue());
        Box h2 = Box.createHorizontalBox();
        mainPanel.add(h2);
        h2.add(Box.createHorizontalGlue());
        JButton variableBtn = new JButton("Variables");
        h2.add(variableBtn);
        mainPanel.add(Box.createHorizontalStrut(10));
        JButton sheetsBtn = new JButton("Sheets");
        h2.add(sheetsBtn);
        mainPanel.add(Box.createHorizontalStrut(10));
        JButton generateBtn = new JButton("Generate");
        h2.add(generateBtn);

        myWindowListener = new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                getOwner().setVisible(true);
                removeWindowClosed();
            }
        };
        this.addWindowListener(myWindowListener);


        variableBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                variableColumns.setVisible(true);
            }
        });

        sheetsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sheetsColumns.setVisible(true);
            }
        });

        generateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EvaluationDiagramUsingHandler diagramUsingHandler;
                try {
                    diagramUsingHandler = new EvaluationDiagramUsingHandler(
                            FileManager.getInstance().getFile(EvaluationDiagramUsingHandler.TEMPLATE_FILE_KEY + ".xls",
                                    EvaluationDiagramUsingHandler.TEMPLATE_FILE_KEY, true,new javax.swing.filechooser.FileFilter[]{
                    ConfigFileFilter.getXMLInstance()}, true),
                            outputFile, evaluationColumnSelect.getSelectedParameter(), rowNameColumnSelect.getSelectedParameter(),
                            variableColumns.getSelectedObjects(), sheetsColumns.getSelectedObjects());
                    User<EvaluationRecord> user = new BulkUser<EvaluationRecord>(diagramUsingHandler, inputFile, new EvaluationLoadingHandler());
                    user.loadConfigurations();
                    diagramUsingHandler.validate();
                    if (setAsDefaultChk.isSelected()) {
                        saveDefaults();
                    }
                    new DialogWorker(1, user).execute();
                } catch (DiagramCreationException e1) {
                    JOptionPane.showMessageDialog(thisDialog, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (InvalidRequiredInputFileException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
        });
        pack();
    }

    private void removeWindowClosed(){
        this.removeWindowListener(myWindowListener);
    }

    /**
     * initializes according to <tt>columns</tt>
     * @param columns
     */
    private void initialize(java.util.List<String> columns) {
        try {
            PropertyManager propertyManager = new PropertyManager(DIAGRAM_DEFAULT_CONFIGS_KEY + ".properties", DIAGRAM_DEFAULT_CONFIGS_KEY, true);
            String rowNameColumnString = propertyManager.readProperty("rowNameColumnSelect");
            String evaluationColumnString = propertyManager.readProperty("evaluationColumnSelect");
            String variableColumnsString = propertyManager.readProperty("variableColumns");
            String sheetsColumnsString = propertyManager.readProperty("sheetsColumns");
            String saveDefaultString = propertyManager.readProperty("saveDefault");
            setAsDefaultChk.setSelected(Boolean.valueOf(saveDefaultString));
            boolean someError = false;
            if (columns.contains(rowNameColumnString)) {
                rowNameColumnSelect.setSelectedParameter(rowNameColumnString);
            } else {
                someError = true;
            }
            if (columns.contains(evaluationColumnString)) {
                evaluationColumnSelect.setSelectedParameter(evaluationColumnString);
            } else {
                someError = true;
            }
            List<String> strings = getStringList(variableColumnsString);
            if (columns.containsAll(strings)) {
                variableColumns.setSelectedObjects(strings);
            } else {
                someError = true;
            }
            strings = getStringList(sheetsColumnsString);
            if (columns.containsAll(strings)) {
                sheetsColumns.setSelectedObjects(strings);
            } else {
                someError = true;
            }
            if (someError) {
                JOptionPane.showMessageDialog(thisDialog, "Could not set some default values.");
            }
        } catch (InvalidRequiredInputFileException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(thisDialog, "Load default config error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @param variableColumnsString format=[item,...,item]
     * @return parses a String to a list of String objects
     */
    private List<String> getStringList(String variableColumnsString) {
        variableColumnsString = variableColumnsString.replaceAll("^\\[", "").replaceAll("]$", "");
        return Arrays.asList(variableColumnsString.split(", "));
    }

    /**
     * saves current configs in default config file.
     */
    private void saveDefaults() {
        try {
            PropertyManager propertyManager = new PropertyManager(DIAGRAM_DEFAULT_CONFIGS_KEY + ".properties", DIAGRAM_DEFAULT_CONFIGS_KEY, true);
            propertyManager.addProperty("rowNameColumnSelect", rowNameColumnSelect.getSelectedParameter());
            propertyManager.addProperty("evaluationColumnSelect", evaluationColumnSelect.getSelectedParameter());
            propertyManager.addProperty("variableColumns", variableColumns.getSelectedObjects().toString());
            propertyManager.addProperty("sheetsColumns", sheetsColumns.getSelectedObjects().toString());
            propertyManager.addProperty("saveDefault", setAsDefaultChk.isSelected() + "");
            propertyManager.store(null);
        } catch (InvalidRequiredInputFileException e) {
            JOptionPane.showMessageDialog(this, "Can not save default configs");
        }
    }

    /**
     * background worker to create diagram.
     */
    private class DialogWorker extends MultiTaskSwingWorker {
        private final User<EvaluationRecord> user;

        protected DialogWorker(int noOfTasks, User<EvaluationRecord> user) {
            super(noOfTasks);
            this.user = user;
        }

        protected void doWork() throws Exception {
            boolean success = false;
            long initTime = System.currentTimeMillis();
            user.run();
            Desktop.getDesktop().open(outputFile);
            success = true;
            publish(new ProcessInstance(inputFile.getName(),
                    success ? ProcessInstance.ProcessResult.success : ProcessInstance.ProcessResult.failure,
                    System.currentTimeMillis() - initTime));
            getOwner().setVisible(true);
            removeWindowClosed();
            dispose();

        }
    }
}
