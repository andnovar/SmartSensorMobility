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

import edu.sharif.ce.dml.common.logic.entity.evaluation.Evaluation;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationAnalyzer;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationRecordGroup;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.LearnableAnalyzer;
import edu.sharif.ce.dml.mobisim.evaluator.model.SimulationAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 17, 2009
 * Time: 7:43:41 PM
 */
class LearningEvaluationAnalyzerUsingHandler extends EvaluationAnalyzerUsingHandler {
    private LoadEvaluationGroupsUsingHandler usingHandler;

    public void use(EvaluationRecord[] data) {
        Evaluation evaluation = new Evaluation(file.getName(), Arrays.asList(data), fileEvaluators);


        List<Integer> factorsIndexs;
        {
            factorsIndexs = new ArrayList<Integer>(factorsString.size());
            for (String factor : factorsString) {
                factorsIndexs.add(labelNumber.get(factor));
            }
        }
        evaluation.setConsideringFactors(factorsIndexs);
        evaluationAnalyzer.setConsideringFactorsSize(factorsIndexs.size());
        usingHandler.setConsideringFactors(factorsString);

        List<Double> mins = new ArrayList<Double>();
        List<Double> maxs = new ArrayList<Double>();
        List<Integer> normalizationFactors = new ArrayList<Integer>();

        usingHandler.normalize(mins, maxs, normalizationFactors);

        ((LearnableAnalyzer) evaluationAnalyzer).learn(usingHandler.getGroups());
        //end learning

        evaluation.doNormalize(mins, maxs, normalizationFactors);
        evaluationAnalyzer.setEvaluationData(evaluation);


        Collection<EvaluationRecordGroup> groups = evaluationAnalyzer.getEvaluationGroups();
        evaluation.unnormalize(mins, maxs, normalizationFactors);
        usingHandler.unNormalize(mins, maxs, normalizationFactors);

        List<EvaluationRecord> learningRecords = usingHandler.getLarningRecords();
        for (EvaluationRecordGroup group : groups) {
            group.getRecords().removeAll(learningRecords);
        }

        writeOutput(groups);

    }

    LearningEvaluationAnalyzerUsingHandler(List<String> factorsString, File file, File outputFile,
                                           EvaluationAnalyzer evaluationAnalyzer, LoadEvaluationGroupsUsingHandler usingHandler,
                                           SimulationAnalyzer<EvaluationRecordGroup> classificationAnalyzer) {
        super(factorsString, file, outputFile, evaluationAnalyzer, classificationAnalyzer);
        this.usingHandler = usingHandler;
    }
}
