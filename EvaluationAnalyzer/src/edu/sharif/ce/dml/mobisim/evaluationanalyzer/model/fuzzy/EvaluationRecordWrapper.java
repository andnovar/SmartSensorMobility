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

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 20, 2009
 * Time: 8:05:16 PM
 */
public class EvaluationRecordWrapper implements Comparable<EvaluationRecordWrapper> {
    private EvaluationRecord evaluationRecord;
    /**
     * actual group in training set
     */
    private FuzzyEvaluationRecordGroup group;
    private double distance = 0;

    public EvaluationRecordWrapper(EvaluationRecord evaluationRecord, FuzzyEvaluationRecordGroup group) {
        this(evaluationRecord, group, 0);
    }

    public EvaluationRecordWrapper(EvaluationRecord evaluationRecord, FuzzyEvaluationRecordGroup group,
                                 double distance) {
        this.evaluationRecord = evaluationRecord;
        this.group = group;
        this.distance = distance;
    }

    public void setEvaluationRecord(EvaluationRecord evaluationRecord) {
        this.evaluationRecord = evaluationRecord;
    }

    public void setGroup(FuzzyEvaluationRecordGroup group) {
        this.group = group;
    }

    public int compareTo(EvaluationRecordWrapper o) {
        return (int) ((this.distance - o.distance) * 10000);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvaluationRecordWrapper)) return false;

        EvaluationRecordWrapper that = (EvaluationRecordWrapper) o;

        if (evaluationRecord != null ? !evaluationRecord.equals(that.evaluationRecord) : that.evaluationRecord != null)
            return false;

        return true;
    }

    public int hashCode() {
        return (evaluationRecord != null ? evaluationRecord.hashCode() : 0);
    }

    public EvaluationRecord getEvaluationRecord() {
        return evaluationRecord;
    }

    public FuzzyEvaluationRecordGroup getGroup() {
        return group;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
