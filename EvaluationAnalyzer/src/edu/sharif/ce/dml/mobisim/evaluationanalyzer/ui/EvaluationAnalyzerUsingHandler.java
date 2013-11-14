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

import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.data.trace.plaintext.TextTraceWriter;
import edu.sharif.ce.dml.common.logic.entity.evaluation.Evaluation;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationMeasure;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkUsingHandler;
import edu.sharif.ce.dml.common.util.io.output.BufferOutputWriter;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationAnalyzer;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationRecordGroup;
import edu.sharif.ce.dml.mobisim.evaluator.model.SimulationAnalyzer;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 17, 2009
 * Time: 7:33:40 PM
 */

/**
 * The using handler that when each AccuracyEvaluator record has been read. uses it (passes to analyzer).
 */
class EvaluationAnalyzerUsingHandler implements BulkUsingHandler<EvaluationRecord> {
    protected EvaluationMeasure[] fileEvaluators;
    protected Map<String, Integer> labelNumber = new HashMap<String, Integer>();
    protected List<String> factorsString;
    protected File file;
    protected File outputFile;



    protected EvaluationAnalyzer evaluationAnalyzer;

    protected SimulationAnalyzer<EvaluationRecordGroup> classificationAnalyzer;

    protected EvaluationAnalyzerUsingHandler(List<String> factorsString, File file, File outputFile,
                                             EvaluationAnalyzer evaluationAnalyzer, SimulationAnalyzer<EvaluationRecordGroup> classificationAnalyzer) {
        this.factorsString = factorsString;
        this.file = file;
        this.outputFile = outputFile;
        this.evaluationAnalyzer = evaluationAnalyzer;
        this.classificationAnalyzer = classificationAnalyzer;
    }

    public void use(EvaluationRecord[] data) {
        Evaluation evaluation = new Evaluation(file.getName(), Arrays.asList(data), fileEvaluators);

        //find labels indexes
        List<Integer> factorsIndexes;
        {
            factorsIndexes = new ArrayList<Integer>(factorsString.size());
            for (String factor : factorsString) {
                factorsIndexes.add(labelNumber.get(factor));
            }
        }
        evaluation.setConsideringFactors(factorsIndexes);
        evaluationAnalyzer.setConsideringFactorsSize(factorsIndexes.size());

        List<Double> mins = new ArrayList<Double>();
        List<Double> maxs = new ArrayList<Double>();
        List<Integer> normalizationFactors = new ArrayList<Integer>();
        evaluation.normalize(mins, maxs, normalizationFactors);
        evaluationAnalyzer.setEvaluationData(evaluation);
        Collection<EvaluationRecordGroup> groups = evaluationAnalyzer.getEvaluationGroups();

        evaluation.unnormalize(mins, maxs, normalizationFactors);

        //write groups to file
        //add new label
        writeOutput(groups);
    }

    protected void writeOutput(Collection<EvaluationRecordGroup> groups) {
        List<String> labels = new LinkedList<String>();
        for (EvaluationMeasure fileEvaluator : fileEvaluators) {
            labels.add(fileEvaluator.getName());
        }
        labels.addAll(evaluationAnalyzer.getLabels());

        LinkedList<StringDataParameter> parameters = new LinkedList<StringDataParameter>();
        if (classificationAnalyzer != null) {
            for (EvaluationRecordGroup group : groups) {
                classificationAnalyzer.evaluate(group);
            }
            List<String> classAnalyzeLabels = classificationAnalyzer.getLabels();
            List classAnalyzeValues = classificationAnalyzer.print();
            for (int i = 0; i < classAnalyzeLabels.size(); i++) {
                parameters.add(new StringDataParameter(classAnalyzeLabels.get(i), TraceWriter.valueToString(classAnalyzeValues.get(i))));
            }
        }

        TraceWriter evaluationsWriter = new TextTraceWriter(parameters, labels.toArray(new String[labels.size()]),
                BufferOutputWriter.createRandomWriter(), outputFile.getPath());

        //write groups
        for (EvaluationRecordGroup group : groups) {
            for (EvaluationRecord evaluationRecord : group.getRecords()) {
                List traces = new LinkedList();
                traces.addAll(evaluationRecord.print());
//                traces.add(group.getName());
                evaluationsWriter.writeTrace(traces);
            }
        }


        evaluationsWriter.flushAndClose();
    }

    public void stopLoading() {

    }

    public void endLoading() {

    }

    public void startLoading() {

    }

    public void setConfiguration(Map<String, String> conf) {
        Collection<String> labelsNum = conf.keySet();
        fileEvaluators = new EvaluationMeasure[labelsNum.size()];

        for (String iS : labelsNum) {
            String name = conf.get(iS);
            int i = Integer.parseInt(iS);
            fileEvaluators[i] = new EvaluationMeasure(name);
            labelNumber.put(name, i);
        }
    }


}