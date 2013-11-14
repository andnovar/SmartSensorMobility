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

package edu.sharif.ce.dml.mobisim.evaluator.model.analyze;

import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationRecordGroup;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.fuzzy.FuzzyEvaluationRecordGroup;
import edu.sharif.ce.dml.mobisim.evaluator.model.Evaluator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 25, 2009
 * Time: 12:51:33 AM
 */


public class MembershipPerClass extends Evaluator<EvaluationRecordGroup> {

    private Map<EvaluationRecordGroup, Double> classMemberships = new HashMap<EvaluationRecordGroup, Double>();
    private List<String> labels;

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    protected void evaluate(EvaluationRecordGroup group) {
        //find fuzzy class
        FuzzyEvaluationRecordGroup fuzzyClass = (FuzzyEvaluationRecordGroup) group;

        double totalMembership = 0;
        int totalNumber = 0;
        Map<EvaluationRecord, Double> fuzzyRecords = fuzzyClass.getRecordsMembershipMap();
        for (EvaluationRecord fuzzyEvaluationRecord : fuzzyRecords.keySet()) {
            //discard learning records
            totalMembership += fuzzyRecords.get(fuzzyEvaluationRecord);
            totalNumber++;
        }
        classMemberships.put(group, totalMembership / totalNumber);
    }

    public void reset() {
        classMemberships.clear();
    }

    /*
   data for this specific object not internal objects
    */
    public List print() {
        return new LinkedList(classMemberships.values());
    }

    public List<String> getLabels() {
        return labels;
    }

    public String toString() {
        return "Membership per Group";
    }
}
