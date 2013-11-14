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
import edu.sharif.ce.dml.common.data.trace.filter.FileFilters;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.common.ui.components.FileSelector;
import edu.sharif.ce.dml.common.ui.forms.FrameTemplate;
import edu.sharif.ce.dml.common.util.io.loader.FileLoader;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkLoader;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 7, 2007
 * Time: 8:34:18 PM<br>
 * A frame application to create diagram from an evaluation.
 */
public class EvaluationDiagramUI extends FrameTemplate {

    public EvaluationDiagramUI() throws HeadlessException {
        super("Evaluation Diagram Creator");
        JPanel mainPanel = new JPanel(new BorderLayout());
        this.setContentPane(mainPanel);
        final FileSelector evalFileSelector = new FileSelector(false, "Evaluation", new FileFilter[]{FileFilters.getEvaluationFilter()});
        final FileSelector evalAnalyzeFileSelector = new FileSelector(false, "Diagram", new FileFilter[]{FileFilters.getDiagramFilter()});
        {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
            mainPanel.add(p,BorderLayout.NORTH);
            p.add(evalFileSelector);
            p.add(Box.createVerticalStrut(10));
            p.add(evalAnalyzeFileSelector);
        }

        Box h = Box.createHorizontalBox();
        mainPanel.add(h, BorderLayout.SOUTH);
        h.add(Box.createHorizontalGlue());
        JButton nextBtn = new JButton("Next");
        h.add(nextBtn);
        nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                File[] files = evalFileSelector.getSelectedFiles();
                if (files != null && files.length > 0) {
                    if (evalAnalyzeFileSelector.isFileSelected()) {
                        File[] outputFile = evalAnalyzeFileSelector.getSelectedFiles();
                        FileLoader<EvaluationRecord> fileLoader = new BulkLoader<EvaluationRecord>(files[0], new EvaluationLoadingHandler());
                        fileLoader.loadConfiguration();
                        ConfigDiagramDialog configDiagramDialog = new ConfigDiagramDialog(thisFrame, true,
                                EvaluationLoadingHandler.convertConfigurations(fileLoader.getConfigurations()),
                                files[0], outputFile[0]);
                        configDiagramDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        thisFrame.setVisible(false);
                        configDiagramDialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(thisFrame, "Please select output diagram file");
                    }
                } else {
                    JOptionPane.showMessageDialog(thisFrame, "Please select evaluation file");
                }
            }
        });
    }

    static JFrame frame;

    public static void createGUI() {
        frame = new EvaluationDiagramUI();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        createGUI();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
