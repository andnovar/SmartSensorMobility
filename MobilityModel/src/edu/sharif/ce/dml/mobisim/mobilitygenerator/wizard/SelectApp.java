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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.wizard;

import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.parameters.data.ParameterableConfigFileException;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.ui.components.FileSelector;
import edu.sharif.ce.dml.common.ui.forms.FrameTemplate;
import edu.sharif.ce.dml.common.util.InvalidRequiredInputFileException;
import edu.sharif.ce.dml.common.util.PublicConfig;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.configcreation.model.Scenario;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.configcreation.model.Scenarios;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.control.SimulationRunner;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.ScenarioConfigFileFilter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.Simulation;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.TreeUIForm;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.shower.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 16, 2007
 * Time: 11:33:35 PM<br>
 */
public class SelectApp extends FrameTemplate {

    public SelectApp() throws HeadlessException {
        super("Mobility Generator");
        final JPanel mainPanel;
        final JPanel appsPanel;
        final JRadioButton batchSimulationRadioButton;
        final JRadioButton createConfigFileRadioButton;
        final JRadioButton graphicalSimulationRadioButton;
        final JButton nextButton;
        final JRadioButton visualizationRadioButton;
        final FileSelector batchSimulationConfigSelector = new FileSelector(false, "Scenarios Config",
                new FileFilter[]{new ScenarioConfigFileFilter()});

        {
            mainPanel = new JPanel(new BorderLayout());
            this.setContentPane(mainPanel);
            mainPanel.add(new JLabel("Select the application that you want to do in the next step"), BorderLayout.NORTH);
            {
                JPanel positionPanel = new JPanel(new BorderLayout());
                appsPanel = new JPanel(new GridLayout(6, 1));
                positionPanel.add(appsPanel, BorderLayout.NORTH);
                mainPanel.add(positionPanel, BorderLayout.CENTER);
                appsPanel.setBorder(BorderFactory.createTitledBorder("Applications"));
                graphicalSimulationRadioButton = new JRadioButton("Graphical Simulation");
                appsPanel.add(graphicalSimulationRadioButton);
                batchSimulationRadioButton = new JRadioButton("Run Scenarios");
                appsPanel.add(batchSimulationRadioButton);
                appsPanel.add(batchSimulationConfigSelector);
                visualizationRadioButton = new JRadioButton("Trace Visualizaiton");
                appsPanel.add(visualizationRadioButton);
                createConfigFileRadioButton = new JRadioButton("Config Scenarios");
                appsPanel.add(createConfigFileRadioButton);
            }
            JPanel buttonsPanel = new JPanel(new BorderLayout());
            nextButton = new JButton("Next");
            buttonsPanel.add(nextButton, BorderLayout.LINE_END);
            mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(graphicalSimulationRadioButton);
            buttonGroup.add(batchSimulationRadioButton);
            buttonGroup.add(visualizationRadioButton);
            buttonGroup.add(createConfigFileRadioButton);
            graphicalSimulationRadioButton.setSelected(true);
        }

        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (batchSimulationRadioButton.isSelected()) {
                        if (batchSimulationConfigSelector.isFileSelected()) {
                            SimulationRunner simulationRunner = new SimulationRunner();
                            simulationRunner.runTextualSimulation(batchSimulationConfigSelector.getSelectedFiles()[0].getPath());
                        } else {
                            JOptionPane.showMessageDialog(null, "Please select a config file!!",
                                    "No Conifg File", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (graphicalSimulationRadioButton.isSelected()) {
                        SimulationRunner simulationRunner = new SimulationRunner();
                        simulationRunner.runGraphical().setVisible(true);
                    } else if (createConfigFileRadioButton.isSelected()) {
                        Simulation simulation = Simulation.getTemplate();
                        Scenario scenario= new Scenario(simulation,1,"Untitiled");
                        Scenarios scenarios = new Scenarios(scenario);
                        TreeUIForm treeUIForm = new TreeUIForm("", scenarios,false);
                        treeUIForm.setVisible(true);
                        //MainFrame.createGUI();
                    } else if (visualizationRadioButton.isSelected()) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Choose a Simulation Track File");
                        fileChooser.setCurrentDirectory(new File(PublicConfig.getInstance().getLastFolderProperty()));
                        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            PublicConfig.getInstance().setLastFolderProperty(fileChooser.getSelectedFile());
                            Simulation simulation = Simulation.getSimulationFromTraceFile(fileChooser.getSelectedFile());
                            java.util.List<AbstractDrawPanel<SnapShotData>> drawPanels = new ArrayList<AbstractDrawPanel<SnapShotData>>();
                            Model model = simulation.getCurrentModel();
                            int height = model.getMap().getHeight();
                            drawPanels.add(new InstanceDrawPanel(height, model));
                            drawPanels.add(new FadeInkyDrawPanel(height, model));
                            drawPanels.add(new InkyDrawPanel(height, model));
                            MiniShower.Init(simulation, thisFrame, drawPanels);
                        }
                    }
                } catch (InvalidRequiredInputFileException e1) {
                    e1.printStackTrace();
                } catch (ParameterableConfigFileException e1) {
                    e1.showMessage(thisFrame);
                } catch (InvalidParameterInputException e1) {
                    e1.showMessage(thisFrame);
                }
            }
        });

    }

    static JFrame frame2;

    public static void createGUI() {
        frame2 = new SelectApp();

        frame2.pack();
        frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame2.setVisible(true);
    }

    public static void main(String[] args) {
        createGUI();
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
