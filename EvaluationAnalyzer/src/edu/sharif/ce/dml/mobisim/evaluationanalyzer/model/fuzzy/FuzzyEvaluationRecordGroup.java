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

package edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.fuzzy;

import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationRecordGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 20, 2009
 * Time: 8:05:54 PM
 */
public class FuzzyEvaluationRecordGroup extends EvaluationRecordGroup {
    private Map<EvaluationRecord, Double> recordsMembershipMap = new HashMap<EvaluationRecord, Double>();

    public void updateRecord(EvaluationRecord record, double value) {
        recordsMembershipMap.put(record, value);
    }

    public int getRecordsSize() {
        return recordsMembershipMap.size();
    }

    public Map<EvaluationRecord, Double> getRecordsMembershipMap() {
        return recordsMembershipMap;
    }

    public double getRecordMembership(EvaluationRecord r) {
        Double d = recordsMembershipMap.get(r);
        if (d != null) return d;
        return 0;
    }

    private static class MaximumMembership {
        EvaluationRecordGroup group;
        double membership = -1;

        void setMembership(EvaluationRecordGroup group, double membership) {
            if (this.membership < membership) {
                this.group = group;
                this.membership = membership;
            }
        }
    }

    public static void classifyBasedonMemberShip(Collection<FuzzyEvaluationRecordGroup> classes) {
        Map<EvaluationRecord, MaximumMembership> maximumMembership = new HashMap<EvaluationRecord, MaximumMembership>();
        EvaluationRecordWrapper testRecordWrapper = new EvaluationRecordWrapper(null, null);
        for (FuzzyEvaluationRecordGroup aClass : classes) {
            Map<EvaluationRecord, Double> map = aClass.getRecordsMembershipMap();
            for (EvaluationRecord record : map.keySet()) {
                testRecordWrapper.setEvaluationRecord(record);
                Double membership = map.get(record);
                if (!maximumMembership.containsKey(record)) {
                    maximumMembership.put(record, new MaximumMembership());
                }
                maximumMembership.get(record).setMembership(aClass, membership);
            }
        }
        for (EvaluationRecord evaluationRecord : maximumMembership.keySet()) {
            EvaluationRecordGroup evaluationRecordGroup = maximumMembership.get(evaluationRecord).group;
            evaluationRecordGroup.getRecords().add(evaluationRecord);
        }
    }

}