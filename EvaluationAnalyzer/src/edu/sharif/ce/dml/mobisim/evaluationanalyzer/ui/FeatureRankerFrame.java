/*
 * Copyright (c) 2005-2009 by Masoud Moshref Javadi <moshref@ce.sharif.edu>, http://ce.sharif.edu/~moshref
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
import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.data.trace.filter.FileFilters;
import edu.sharif.ce.dml.common.data.trace.filter.TraceFilter;
import edu.sharif.ce.dml.common.data.trace.plaintext.TextTraceWriter;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.common.logic.worker.MultiTaskSwingWorker;
import edu.sharif.ce.dml.common.logic.worker.ProcessInstance;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterableList;
import edu.sharif.ce.dml.common.parameters.ui.primitives.IntegerUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.primitives.StringUIParameter;
import edu.sharif.ce.dml.common.ui.components.FileSelector;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import edu.sharif.ce.dml.common.util.io.loader.FileLoader;
import edu.sharif.ce.dml.common.util.io.loader.LoadingHandler;
import edu.sharif.ce.dml.common.util.io.loader.User;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkLoader;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkLoadingHandler;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkUser;
import edu.sharif.ce.dml.common.util.io.output.BufferOutputWriter;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationAnalyzer;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationRecordGroup;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluatorClassifier;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.LearnableAnalyzer;
import edu.sharif.ce.dml.mobisim.evaluator.model.Evaluator;
import edu.sharif.ce.dml.mobisim.evaluator.model.SimulationAnalyzer;
import edu.sharif.ce.dml.mobisim.evaluator.model.analyze.AccuracyEvaluator;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 17, 2009
 * Time: 2:57:38 PM
 */
public class FeatureRankerFrame extends EvaluationAnalyzerFrame implements Observer {
    private IntegerUIParameter featureStartLength;
    private IntegerUIParameter featureEndLength;
    private java.util.List<Set<String>> featuresCombinations;
    private FileSelector evaluationAnalyzedFolder;
    private StringUIParameter labelColumn;
    private ParameterableList evaluatorList;

    public FeatureRankerFrame() throws HeadlessException {
        super();
        setTitle("Feature Ranker");
    }

    protected void generateAction(File[] files, File[] outputFile, FileSelector evalFileSelector) {
        featuresCombinations = new LinkedList<Set<String>>();
        for (int i = featureStartLength.getValue(); i <= featureEndLength.getValue(); i++) {
            findFactorstoRun(factorSelectList.getSelectedObjects(), new HashSet<String>(), i, 0);
        }

        new FeatureRankerSwingWorker(files[0], (TraceFilter) evalFileSelector.getSelectedFileFilter(), outputFile[0]).execute();
    }

    private void findFactorstoRun(java.util.List<String> features, Set<String> currentFeatures, int length, int current) {
        //current factor is out
        //need to go furthor
        if (current < features.size() - 1) {
            findFactorstoRun(features, currentFeatures, length, current + 1);
        }
        //current factor is in
        currentFeatures.add(features.get(current));
        if (currentFeatures.size() == length) {
            featuresCombinations.add(new HashSet<String>(currentFeatures));
        } else {
            //need to go furthor
            if (current < features.size() - 1) {
                findFactorstoRun(features, currentFeatures, length, current + 1);
            }
        }
        currentFeatures.remove(features.get(current));
    }

    protected void createParameterableUIs(JPanel mainPanel) {
        super.createParameterableUIs(mainPanel);


        mainPanel.add(Box.createVerticalStrut(10));
        evaluationAnalyzedFolder = new FileSelector(false, "Classification output folder", new FileFilter[]{FileFilters.getDirectoryFilter()});
        mainPanel.add(evaluationAnalyzedFolder);
        mainPanel.add(Box.createVerticalStrut(10));
        labelColumn = new StringUIParameter("Label Column", "models");
        mainPanel.add(labelColumn);
        Box h = Box.createHorizontalBox();

        mainPanel.add(Box.createVerticalStrut(10));
        JButton configRankingEvaluatorsButton = new JButton("Config Ranking Evaluators");
        h.add(configRankingEvaluatorsButton);
        h.add(Box.createHorizontalGlue());
        mainPanel.add(Box.createVerticalStrut(10));


         featureStartLength = new IntegerUIParameter("Feature Start Length", 100, 1, 1, 1);
        featureEndLength = new IntegerUIParameter("Feature End Length", 100, 1, 1, 1);
        h = Box.createHorizontalBox();
        h.add(featureStartLength);
        h.add(Box.createHorizontalStrut(10));
        h.add(featureEndLength);
        mainPanel.add(h);
        createEvaluators();
        configRankingEvaluatorsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                evaluatorList.setVisible(true);
            }
        });
        analyzerSelect.addChangeObserver(this);
    }

    static JFrame frame;

    public static void createGUI() {
        frame = new FeatureRankerFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        createGUI();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * informed if analyzer changed to update classification analyzers
     *
     * @param o
     * @param arg
     */
    public void update(Observable o, Object arg) {
        createEvaluators();
    }

    private class FeatureRankerSwingWorker extends MultiTaskSwingWorker {

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


        public FeatureRankerSwingWorker(File file, TraceFilter traceFilter, File outputFile) {
            super(featuresCombinations.size());
            this.file = file;
            this.traceFilter = traceFilter;
            this.outputFile = outputFile;
        }

        protected void doWork() throws Exception {
            boolean success = false;
            EvaluationAnalyzer evaluationAnalyzer = (EvaluationAnalyzer) analyzerSelect.getSelectedParameter();
            LoadEvaluationGroupsUsingHandler learnUsingHandler = null;
            if (evaluationAnalyzer instanceof LearnableAnalyzer) {
                //load learning file
                learnUsingHandler = new LoadEvaluationGroupsUsingHandler(-1);
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

            }

            //find actual label index
            {
                int actualIndex = -1;
                FileLoader<EvaluationRecord> fileLoader = new BulkLoader<EvaluationRecord>(file, new EvaluationLoadingHandler());
                fileLoader.loadConfiguration();
                java.util.List<String> paramList = EvaluationLoadingHandler.convertConfigurations(fileLoader.getConfigurations());
                actualIndex = paramList.indexOf(labelColumn.getValue());
                if (actualIndex < 0) {
                    DevelopmentLogger.logger.fatal("label column not found");
                    System.exit(1);
                }
                EvaluationRecordGroup.realGroupIndex = actualIndex;
            }

            String evalAnalyzeFolder = evaluationAnalyzedFolder.getSelectedFiles()[0].getPath();

            //createEvaluators(evaluationAnalyzer);

            List<Evaluator<EvaluationRecordGroup>> selectedEvaluators = new LinkedList<Evaluator<EvaluationRecordGroup>>();
            for (Parameterable parameterable : evaluatorList.getSelectedObjects()) {
                selectedEvaluators.add((Evaluator<EvaluationRecordGroup>) parameterable);
            }
            SimulationAnalyzer<EvaluationRecordGroup> classificationAnalyzer = new SimulationAnalyzer<EvaluationRecordGroup>(selectedEvaluators);

            List<List> memory = new LinkedList<List>();
            //run classification for each feature set
            for (Set<String> featuresCombination : featuresCombinations) {
                Long initTime = System.currentTimeMillis();
                //create feature set string
                StringBuffer sb = new StringBuffer();
                for (String s : featuresCombination) {
                    sb.append(s).append(",");
                }
                if (sb.length() > 0) {
                    //remove last comma
                    sb.deleteCharAt(sb.length() - 1);
                }
                try {
                    File currentOutputFile = new File(evalAnalyzeFolder + File.separator + sb.toString() + ".txt");
                    currentOutputFile.createNewFile();
                    EvaluationAnalyzerUsingHandler dataHandler;

                    if (evaluationAnalyzer instanceof LearnableAnalyzer) {
                        dataHandler = new LearningEvaluationAnalyzerUsingHandler(new ArrayList<String>(featuresCombination),
                                file, currentOutputFile, evaluationAnalyzer, learnUsingHandler, classificationAnalyzer);
                    } else {
                        dataHandler = new EvaluationAnalyzerUsingHandler(new ArrayList<String>(featuresCombination),
                                file, currentOutputFile, evaluationAnalyzer, classificationAnalyzer);
                    }
                    LoadingHandler<EvaluationRecord> evaluationLoadingHandler = (LoadingHandler<EvaluationRecord>) traceFilter.
                            getLoadingHandlerClass(file).getConstructor().newInstance();
                    User<EvaluationRecord> user = null;
                    {
                        if (evaluationLoadingHandler instanceof BulkLoadingHandler) {
                            user = new BulkUser<EvaluationRecord>(dataHandler, file, (BulkLoadingHandler<EvaluationRecord>) evaluationLoadingHandler);
                        }
                    }
                    assert user != null;
                    user.run();

                    //write classification analyzing values
                    List traces = new LinkedList();
                    traces.add(sb.toString());
                    traces.addAll(classificationAnalyzer.print());

                    memory.add(traces);

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
                classificationAnalyzer.reset();
                evaluationAnalyzer.reset();
                publish(new ProcessInstance(sb.toString(),
                        success ? ProcessInstance.ProcessResult.success : ProcessInstance.ProcessResult.failure,
                        System.currentTimeMillis() - initTime));
            }

            // moved to the end because some evaluators need to evaluation be ran before getting lables!
            List<String> classAnalyzerLabels = new LinkedList<String>();
            classAnalyzerLabels.add("Features");
            classAnalyzerLabels.addAll(classificationAnalyzer.getLabels());
            //create writer
            TraceWriter classificationAnalyzerWriter = new TextTraceWriter(new LinkedList<StringDataParameter>(),
                    classAnalyzerLabels.toArray(new String[classAnalyzerLabels.size()]),
                    BufferOutputWriter.createRandomWriter(), outputFile.getPath());
            boolean isSorting = classificationAnalyzer.isSorting();

            if (isSorting) {
                classificationAnalyzer.sort(1, memory);
            }
            for (List traces : memory) {
                classificationAnalyzerWriter.writeTrace(traces);
            }

            classificationAnalyzerWriter.flushAndClose();
        }

    }

    private EvaluationAnalyzer lastAnalyzer = null;

    private void createEvaluators() {
        EvaluationAnalyzer evaluationAnalyzer = (EvaluationAnalyzer) analyzerSelect.getSelectedParameter();
        if (lastAnalyzer != null || !evaluationAnalyzer.equals(lastAnalyzer)) {
            lastAnalyzer = evaluationAnalyzer;
            //create evaluators
            List<Evaluator<EvaluationRecordGroup>> evaluators = new LinkedList<Evaluator<EvaluationRecordGroup>>();
            evaluators.add(new AccuracyEvaluator());
            if (evaluationAnalyzer instanceof EvaluatorClassifier) {
                //add evaluator which are embedded in the analyzer
                evaluators.addAll(((EvaluatorClassifier) evaluationAnalyzer).getEvaluators());
            }
            List<Parameterable> paramList = new LinkedList<Parameterable>();
            for (Evaluator<EvaluationRecordGroup> evaluator : evaluators) {
                paramList.add(evaluator);
            }
            evaluatorList = new ParameterableList(paramList,true);
            evaluatorList.setSelectedObjects(paramList);
        }
    }
}
