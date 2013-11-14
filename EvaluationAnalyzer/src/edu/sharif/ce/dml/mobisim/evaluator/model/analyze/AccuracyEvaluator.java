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
import edu.sharif.ce.dml.mobisim.evaluator.model.Evaluator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 17, 2009
 * Time: 9:26:09 PM
 */
public class AccuracyEvaluator extends Evaluator<EvaluationRecordGroup> {
    private double misclass = 0;
    private int totalNum=0;


    public AccuracyEvaluator() {
    }

    protected void evaluate(EvaluationRecordGroup group) {
        for (EvaluationRecord evaluationRecord : group.getRecords()) {
            if (!evaluationRecord.getValueAt(EvaluationRecordGroup.realGroupIndex).equals(group.getName())) {
                misclass++;
            }
            totalNum++;
        }
    }

    public void reset() {
        misclass=0;
        totalNum=0;
    }
    /*
       data for this specific object not internal objects
    */

    public List print() {
        return Arrays.asList(1-(misclass/totalNum));
    }

    public List<String> getLabels() {
        return Arrays.asList("Accuracy");
    }

    public String toString() {
        return "Accuracy";
    }
}
