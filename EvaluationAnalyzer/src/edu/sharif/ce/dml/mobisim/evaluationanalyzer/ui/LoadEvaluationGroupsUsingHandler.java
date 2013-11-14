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
import edu.sharif.ce.dml.common.logic.entity.evaluation.Evaluation;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterSelect;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkUsingHandler;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationRecordGroup;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 17, 2009
 * Time: 7:29:48 PM
 */

/**
 * if analyzer was an instance of {@link edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.LearnableAnalyzer}
 * this class will be used to manage loadded learn file.
 */
class LoadEvaluationGroupsUsingHandler implements BulkUsingHandler<EvaluationRecord> {
    private Map<String, EvaluationRecordGroup> nameGroups;

    private Evaluation evaluation;

    int learnFactorIndex = -1;
    protected Map<String, Integer> labelNumber = new HashMap<String, Integer>();

    Collection<EvaluationRecordGroup> getGroups() {
        return nameGroups.values();
    }

    LoadEvaluationGroupsUsingHandler(int learnFactorIndex) {
        this.learnFactorIndex = learnFactorIndex;
    }

    public void use(EvaluationRecord[] data) {
        if (learnFactorIndex < 0) {
            DevelopmentLogger.logger.warn("Undefined Grouping Factor to learn");
            return;
        }
        nameGroups = new HashMap<String, EvaluationRecordGroup>();
        for (EvaluationRecord record : data) {
            String name = record.getValueAt(learnFactorIndex);
            EvaluationRecordGroup group = nameGroups.get(name);
            if (group == null) {
                group = new EvaluationRecordGroup();
                group.setName(name);
                nameGroups.put(name, group);
            }
            group.getRecords().add(record);
        }

        evaluation = new Evaluation("", Arrays.asList(data), null);
    }

    public void setConsideringFactors(List<String> factorsString){
        List<Integer> factorsIndexs;
        {
            factorsIndexs = new ArrayList<Integer>(factorsString.size());
            for (String factor : factorsString) {
                factorsIndexs.add(labelNumber.get(factor));
            }
        }
        evaluation.setConsideringFactors(factorsIndexs);
    }

    public void setConfiguration(Map<String, String> conf) {
        if (learnFactorIndex<0){
            learnFactorIndex=fetchLearnFactor(conf);
        }
        Collection<String> labelsNum = conf.keySet();

        for (String iS : labelsNum) {
            String name = conf.get(iS);
            int i = Integer.parseInt(iS);
            labelNumber.put(name, i);
        }
    }


    public void stopLoading() {

    }

    public void endLoading() {

    }

    public void startLoading() {

    }

    public void normalize(List<Double> mins, List<Double> maxs, List<Integer> normalizableFactors) {
        evaluation.normalize(mins, maxs, normalizableFactors);
    }

    public List<EvaluationRecord> getLarningRecords() {
        return evaluation.getRecords();
    }

    public int getLearnFactorIndex() {
        return learnFactorIndex;
    }

    private static int learnfactorStatic =-1;

    public static int fetchLearnFactor(Map<String, String> conf) {
        final List<String> labels = EvaluationLoadingHandler.convertConfigurations(conf);
        //show dialog
        final JDialog selectLearnFactorDialog = new JDialog();
        selectLearnFactorDialog.setTitle("Select Label Column");
        selectLearnFactorDialog.setModal(true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        selectLearnFactorDialog.add(panel);
        final ParameterSelect<String> selectGroupParam = new ParameterSelect<String>("Label Column", labels);
        panel.add(selectGroupParam);
        panel.add(Box.createVerticalStrut(10));
        JButton okBtn = new JButton("Ok");
        panel.add(okBtn);
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                learnfactorStatic = labels.indexOf(selectGroupParam.getSelectedParameter());
                selectLearnFactorDialog.dispose();
            }
        });
        selectLearnFactorDialog.pack();
        selectLearnFactorDialog.setModal(true);
        selectLearnFactorDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        selectLearnFactorDialog.setVisible(true);
        return learnfactorStatic;
    }

    public void unNormalize(List<Double> mins, List<Double> maxs, List<Integer> normalizationFactors) {
        evaluation.unnormalize(mins,maxs,normalizationFactors);
    }
}