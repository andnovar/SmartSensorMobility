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

package edu.sharif.ce.dml.mobisim.evaluationanalyzer.ui;


import edu.sharif.ce.dml.common.data.EvaluationLoadingHandler;
import edu.sharif.ce.dml.common.data.configfilter.ConfigFileFilter;
import edu.sharif.ce.dml.common.data.trace.filter.FileFilters;
import edu.sharif.ce.dml.common.data.trace.filter.TraceFilter;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.common.logic.worker.MultiTaskSwingWorker;
import edu.sharif.ce.dml.common.logic.worker.ProcessInstance;
import edu.sharif.ce.dml.common.parameters.data.ParameterableConfigLoader;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterList;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterableSelect;
import edu.sharif.ce.dml.common.ui.components.FileSelector;
import edu.sharif.ce.dml.common.ui.forms.FrameTemplate;
import edu.sharif.ce.dml.common.util.FileManager;
import edu.sharif.ce.dml.common.util.io.loader.FileLoader;
import edu.sharif.ce.dml.common.util.io.loader.LoadingHandler;
import edu.sharif.ce.dml.common.util.io.loader.User;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkLoader;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkLoadingHandler;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkUser;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationAnalyzer;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.LearnableAnalyzer;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 2, 2007
 * Time: 4:20:52 PM<br>
 * A frame application which uses an {@link edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationAnalyzer}
 * to analyze an AccuracyEvaluator file.
 * /todo has error when columns of learn file and test file are not equal
 */
public class EvaluationAnalyzerFrame extends FrameTemplate {
    /**
     * UI component to select an analyzer.
     */
    protected ParameterableSelect analyzerSelect;
    public static final String EVALUATION_FILE_KEY = "evaluatorsconfig";
    public static final String EVALUATION_ANALYZER_FILE_KEY = "evalanalyzeconfig";
    /**
     * UI component to select some factors in AccuracyEvaluator to use in analyze
     */
    protected ParameterList<String> factorSelectList;

    public EvaluationAnalyzerFrame() throws HeadlessException {
        super("Evaluation Analyze");
        JPanel mainPanel = new JPanel();
        this.setContentPane(mainPanel);
        mainPanel.setLayout(new BorderLayout());
        final FileSelector evalFileSelector = new FileSelector(false, "Evaluation", new FileFilter[]{FileFilters.getEvaluationFilter()});
        final FileSelector evalAnalyzeFileSelector = new FileSelector(false, "Evaluation Analyze", new FileFilter[]{FileFilters.getEvaluationFilter()});
        {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
            mainPanel.add(p, BorderLayout.NORTH);
            p.add(evalFileSelector);
            p.add(evalAnalyzeFileSelector);
            createParameterableUIs(p);
        }
        Box h = Box.createHorizontalBox();
        mainPanel.add(h, BorderLayout.SOUTH);
        h.add(Box.createHorizontalGlue());
//      select evaluatorAnalyzer
        JButton selectFactorsBtn = new JButton("Select Features");
        h.add(selectFactorsBtn);
        h.add(Box.createHorizontalStrut(10));
        JButton generateBtn = new JButton("Generate");
        h.add(generateBtn);

        selectFactorsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (evalFileSelector.isFileSelected()) {
                    File file = evalFileSelector.getSelectedFiles()[0];
                    FileLoader<EvaluationRecord> fileLoader = new BulkLoader<EvaluationRecord>(file, new EvaluationLoadingHandler());
                    fileLoader.loadConfiguration();
                    factorSelectList = new ParameterList<String>(EvaluationLoadingHandler.convertConfigurations(fileLoader.getConfigurations()));
                    factorSelectList.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(thisFrame, "Please select input evaluation file", "No Evaluation File", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        generateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (evalFileSelector.isFileSelected()) {
                    File[] files = evalFileSelector.getSelectedFiles();
                    if (evalAnalyzeFileSelector.isFileSelected()) {
                        File[] outputFile = evalAnalyzeFileSelector.getSelectedFiles();
                        if (factorSelectList != null) {
                            generateAction(files, outputFile, evalFileSelector);
                        } else {
                            JOptionPane.showMessageDialog(thisFrame, "Please select analyzing factors", "No analyzing factor", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(thisFrame, "Please select output file", "file error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(thisFrame, "Please select input file", "file error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    protected void generateAction(File[] files, File[] outputFile, FileSelector evalFileSelector) {
        new EvaluationAnalyzeSwingWorker(files[0], (TraceFilter) evalFileSelector.getSelectedFileFilter(), outputFile[0]).execute();
    }

    /**
     * loads AccuracyEvaluator analyzers.
     *
     * @param mainPanel
     */
    protected void createParameterableUIs(JPanel mainPanel) {
        try {
            java.util.List<Parameterable> loaddedParameterables = ParameterableConfigLoader.load(
                    FileManager.getInstance().getFile(EVALUATION_ANALYZER_FILE_KEY + ".xml", EVALUATION_ANALYZER_FILE_KEY, true
                            , new javax.swing.filechooser.FileFilter[]{
                                    ConfigFileFilter.getXMLInstance()}, true)).instantiate();
            analyzerSelect = new ParameterableSelect("Evaluator Analyzer", loaddedParameterables,true);
            mainPanel.add(analyzerSelect);
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Error occurred while loading Evaluators config file",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * performs actual work on selected AccuracyEvaluator file.
     */
    private class EvaluationAnalyzeSwingWorker extends MultiTaskSwingWorker {

        /**
         * selected file
         */
        private File file;
        /**
         * AccuracyEvaluator file type
         */
        private TraceFilter traceFilter;
        /**
         * analyzing output file.
         */
        private File outputFile;

        protected int groupFactorIndex = -1;

        public EvaluationAnalyzeSwingWorker(File file, TraceFilter traceFilter, File outputFile) {
            super(1);
            this.file = file;
            this.traceFilter = traceFilter;
            this.outputFile = outputFile;
        }

        protected void doWork() throws Exception {
            boolean success = false;
//            for (File file : files) {
            Long initTime = System.currentTimeMillis();
            try {
                EvaluationAnalyzer evaluationAnalyzer = (EvaluationAnalyzer) analyzerSelect.getSelectedParameter();
                evaluationAnalyzer.reset();
                EvaluationAnalyzerUsingHandler dataHandler;
                if (evaluationAnalyzer instanceof LearnableAnalyzer) {
                    //load learning file
                    LoadEvaluationGroupsUsingHandler learnUsingHandler = new LoadEvaluationGroupsUsingHandler(-1);
                    LearnableAnalyzer learnableAnalyzer = ((LearnableAnalyzer) evaluationAnalyzer);
                    LoadingHandler<EvaluationRecord> trackLoadingHandler = learnableAnalyzer.getLearnFileFilter().
                            getLoadingHandlerClass(learnableAnalyzer.getLearnFile()).getConstructor().newInstance();
                    //todo
                    User<EvaluationRecord> user = null;
                    {
//                        if (trackLoadingHandler instanceof StreamLoadingHandler) {
                        //                            user = new BulkUser<EvaluationRecord>(dataHandler, file, (BulkLoadingHandler<EvaluationRecord>) trackLoadingHandler);
                        //                        } else
                        if (trackLoadingHandler instanceof BulkLoadingHandler) {
                            user = new BulkUser<EvaluationRecord>(learnUsingHandler, learnableAnalyzer.getLearnFile(),
                                    (BulkLoadingHandler<EvaluationRecord>) trackLoadingHandler);
                        }
                    }
                    assert user != null;
                    user.run();
                    //end loading learn file
                    dataHandler = new LearningEvaluationAnalyzerUsingHandler(factorSelectList.getSelectedObjects(),
                            file, outputFile, evaluationAnalyzer, learnUsingHandler, null);
                } else {
                    dataHandler = new EvaluationAnalyzerUsingHandler(factorSelectList.getSelectedObjects(),
                            file, outputFile, evaluationAnalyzer, null);
                }
                LoadingHandler<EvaluationRecord> trackLoadingHandler = (LoadingHandler<EvaluationRecord>) traceFilter.
                        getLoadingHandlerClass(file).getConstructor().newInstance();
                //todo
                User<EvaluationRecord> user = null;
                {
//                        if (trackLoadingHandler instanceof StreamLoadingHandler) {
//                            user = new BulkUser<EvaluationRecord>(dataHandler, file, (BulkLoadingHandler<EvaluationRecord>) trackLoadingHandler);
//                        } else
                    if (trackLoadingHandler instanceof BulkLoadingHandler) {
                        user = new BulkUser<EvaluationRecord>(dataHandler, file, (BulkLoadingHandler<EvaluationRecord>) trackLoadingHandler);
                    }
                }
                assert user != null;
                user.run();

                success = true;
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
            publish(new ProcessInstance(file.getName(),
                    success ? ProcessInstance.ProcessResult.success : ProcessInstance.ProcessResult.failure,
                    System.currentTimeMillis() - initTime));
        }


    }


    static JFrame frame;

    public static void createGUI() {
        frame = new EvaluationAnalyzerFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        createGUI();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}
