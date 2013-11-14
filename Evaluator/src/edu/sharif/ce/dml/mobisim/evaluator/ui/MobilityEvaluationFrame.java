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

package edu.sharif.ce.dml.mobisim.evaluator.ui;

import edu.sharif.ce.dml.common.data.configfilter.ConfigFileFilter;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.data.trace.filter.FileFilters;
import edu.sharif.ce.dml.common.data.trace.filter.TraceFilter;
import edu.sharif.ce.dml.common.data.trace.plaintext.TextTraceWriter;
import edu.sharif.ce.dml.common.logic.entity.SnapShot;
import edu.sharif.ce.dml.common.logic.worker.MultiTaskSwingWorker;
import edu.sharif.ce.dml.common.logic.worker.ProcessInstance;
import edu.sharif.ce.dml.common.parameters.data.ParameterableConfigLoader;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterList;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterableList;
import edu.sharif.ce.dml.common.ui.components.IOComponent;
import edu.sharif.ce.dml.common.ui.forms.FrameTemplate;
import edu.sharif.ce.dml.common.util.FileManager;
import edu.sharif.ce.dml.common.util.io.TraceUsingFactory;
import edu.sharif.ce.dml.common.util.io.loader.FileLoader;
import edu.sharif.ce.dml.common.util.io.loader.User;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkStreamUsingHandlerAdapter;
import edu.sharif.ce.dml.common.util.io.output.BufferOutputWriter;
import edu.sharif.ce.dml.mobisim.evaluator.model.Evaluator;
import edu.sharif.ce.dml.mobisim.evaluator.model.SimulationAnalyzer;
import edu.sharif.ce.dml.mobisim.evaluator.model.position.MobilityEvaluator;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 28, 2007
 * Time: 12:48:57 AM
 */
public class MobilityEvaluationFrame extends FrameTemplate {
    public static final String EVALUATORS_CONFIG_FILE_NAME = "evaluatorsconfig";
    static MobilityEvaluationFrame frame;
    protected final List<String> selectedCopyConfigs = new LinkedList<String>();
    protected ParameterableList evaluatorsList;
    private ParameterList<String> parameterList;


    public MobilityEvaluationFrame() {
        super("Mobility Evaluation");
        JPanel mainPanel = new JPanel(new BorderLayout());
        this.setContentPane(mainPanel);
        final IOComponent ioComponent = new IOComponent(new FileFilter[]{FileFilters.getEvaluationFilter()});
        mainPanel.add(ioComponent, BorderLayout.NORTH);
        Box h = Box.createHorizontalBox();
        mainPanel.add(h, BorderLayout.SOUTH);
        h.add(Box.createHorizontalGlue());
        JButton copyConfigsButton = new JButton("Copy Configs");
        h.add(copyConfigsButton);
        h.add(Box.createHorizontalStrut(10));
        JButton configEvaluatorsButton = new JButton("Config Evaluators");
        h.add(configEvaluatorsButton);
        h.add(Box.createHorizontalStrut(10));
        JButton generateBtn = new JButton("Generate");
        h.add(generateBtn);
        generateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File[] files = ioComponent.getInputFiles();
                if (files != null) {
                    File outputFolder = ioComponent.getOutputFile();
                    if (outputFolder != null && evaluatorsList != null) {
                        getEvaluateWorkerInstance(files, (TraceFilter) ioComponent.getSelectedFilter(), outputFolder).execute();
                    }
                }
            }
        });
        copyConfigsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File[] files = ioComponent.getInputFiles();
                if (files != null && files.length > 0) {
                    Set<String> configs = null;
                    for (File file : files) {
                        FileLoader<SnapShotData> fileLoader = new TraceUsingFactory<SnapShotData>((TraceFilter<SnapShotData>) ioComponent.getSelectedFilter()).getFileLoader(file);
                        if (configs == null) {
                            configs = new HashSet<String>(fileLoader.getConfigurations().keySet());
                        } else {
                            configs.retainAll(fileLoader.getConfigurations().keySet());
                        }
                    }
                    parameterList = new ParameterList<String>(new LinkedList<String>(configs));
                    parameterList.setSelectedObjects(selectedCopyConfigs);//try to select the old selected evaluators
                    parameterList.setVisible(true);
                    selectedCopyConfigs.clear();
                    selectedCopyConfigs.addAll(parameterList.getSelectedObjects());
                }
            }
        });

        configEvaluatorsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (evaluatorsList == null) {
                    try {
                        java.util.List<Parameterable> loadedParameterables = ParameterableConfigLoader.load(
                                FileManager.getInstance().getFile(EVALUATORS_CONFIG_FILE_NAME + ".xml", EVALUATORS_CONFIG_FILE_NAME, true,
                                        new javax.swing.filechooser.FileFilter[]{
                                                ConfigFileFilter.getXMLInstance()}, true)).instantiate();
                        evaluatorsList = new ParameterableList(loadedParameterables, true);

                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(null, "Error occured while loading Evaluators config file",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                        System.exit(1);
                    }
                }
                evaluatorsList.setVisible(true);
            }
        });
    }

    public static void createGUI() {
        frame = new MobilityEvaluationFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        createGUI();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    protected MultiTaskSwingWorker getEvaluateWorkerInstance(File[] files, TraceFilter traceFilter, File outputFile) {
        return new NetworkEvaluatorWorker(files, traceFilter, outputFile);
    }

    private class NetworkEvaluatorWorker extends MultiTaskSwingWorker {
        protected File[] files;
        protected TraceFilter<SnapShotData> traceFilter;
        protected File outputFile;
        protected SimulationAnalyzer<SnapShotData> simulationAnalyzer;

        private NetworkEvaluatorWorker(File[] files, TraceFilter traceFilter, File outputFile) {
            super(files.length);
            this.files = files;
            this.traceFilter = traceFilter;
            this.outputFile = outputFile;
            List<Parameterable> selectedP = evaluatorsList.getSelectedObjects();
            List<Evaluator<SnapShotData>> selectedE = new LinkedList<Evaluator<SnapShotData>>();
            for (Parameterable parameterable : selectedP) {
                selectedE.add((MobilityEvaluator) parameterable);
            }
            simulationAnalyzer = new SimulationAnalyzer<SnapShotData>(selectedE);
        }

        protected void doWork() throws Exception {
            boolean success = false;
            List<String> labels = new LinkedList<String>();
            labels.add("FileName");
            labels.addAll(simulationAnalyzer.getLabels());
            labels.addAll(selectedCopyConfigs);
            TraceWriter evaluationsWriter = new TextTraceWriter(new LinkedList<StringDataParameter>(), labels.toArray(new String[labels.size()]),
                    BufferOutputWriter.createRandomWriter(), outputFile.getPath());
            boolean isSorting = simulationAnalyzer.isSorting();
            PowerEvaluatorUsingHandler dataHandler = new PowerEvaluatorUsingHandler(simulationAnalyzer, evaluationsWriter, isSorting);
            for (File file : files) {
                dataHandler.setString(file.getName());
                Long initTime = System.currentTimeMillis();
                User<SnapShotData> user = new TraceUsingFactory<SnapShotData>(traceFilter).getDataUser(file, dataHandler);
                user.run();
                success = true;
                publish(new ProcessInstance(file.getName(),
                        success ? ProcessInstance.ProcessResult.success : ProcessInstance.ProcessResult.failure,
                        System.currentTimeMillis() - initTime));
            }
            if (isSorting) {
                simulationAnalyzer.sort(1, dataHandler.getMemory());
                dataHandler.writeMemory();
            }
            evaluationsWriter.flushAndClose();
        }
    }

    private class PowerEvaluatorUsingHandler extends BulkStreamUsingHandlerAdapter<SnapShotData> {
        protected SimulationAnalyzer<SnapShotData> simulationAnalyzer;
        Map<String, String> fileConfigs;
        private SnapShot snapshot;
        private TraceWriter evaluationsWriter;
        private List<List> memory;
        private String fileName = "";

        public PowerEvaluatorUsingHandler(SimulationAnalyzer<SnapShotData> eSimulationAnalyzer, TraceWriter evaluationsWriter, boolean sorting) {
            this.simulationAnalyzer = eSimulationAnalyzer;
            this.evaluationsWriter = evaluationsWriter;
            if (sorting) {
                memory = new LinkedList<List>();
            }
        }

        public void loadData(SnapShotData data) {
            if (snapshot == null) {
                snapshot = new SnapShot(data.getNodeShadowsLength());
            }
            this.snapshot.setNodesData(data.getNodeShadows());
            this.snapshot.setTime(data.getTime());
        }

        public void useData() {
            simulationAnalyzer.evaluate(snapshot);
        }

        public void stopLoading() {

        }

        public List<List> getMemory() {
            return memory;
        }

        public void endLoading() {
            List traces = new LinkedList();
            traces.add(fileName);
            traces.addAll(simulationAnalyzer.print());
            for (String selectedCopyConfig : selectedCopyConfigs) {
                traces.add(fileConfigs.get(selectedCopyConfig));
            }
            if (memory != null) {
                memory.add(traces);
            } else {
                evaluationsWriter.writeTrace(traces);
            }
            simulationAnalyzer.reset();

            snapshot = null;
        }

        public void startLoading() {

        }

        void setString(String name) {
            fileName = name;
        }

        public void setConfiguration(Map<String, String> conf) {
            fileConfigs = conf;
        }

        public void writeMemory() {
            for (List traces : memory) {
                evaluationsWriter.writeTrace(traces);
            }
        }
    }
}