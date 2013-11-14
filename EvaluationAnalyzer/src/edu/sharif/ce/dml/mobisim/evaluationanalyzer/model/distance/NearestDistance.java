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

package edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.distance;

import edu.sharif.ce.dml.common.logic.entity.evaluation.Evaluation;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationRecordGroup;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.center.NearestCenter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 29, 2009
 * Time: 1:07:42 PM
 * <br/> finds the distances between the groups and creates a new
 * coordination system with this distances. A trace will be labeled as a group member
 * if it has the smallest distance to the center of the group in the new system which means that
 * it has similar distance to the other groups.
 */
public class NearestDistance extends NearestCenter {
    public void setEvaluationData(Evaluation evaluation) {
        //calculate A matrix contains distances of group centers with each other
        double[][] centersArray = new double[groupClusterCenter.size()][groupClusterCenter.size()];
        Map<Integer, EvaluationRecordGroup> indexGroupMap = new HashMap<Integer, EvaluationRecordGroup>();
        int i = 0;// inefficient computation
        for (EvaluationRecordGroup evaluationRecordGroup : groupClusterCenter.keySet()) {
            Coordination center1 = groupClusterCenter.get(evaluationRecordGroup);
            int j = 0;
            for (EvaluationRecordGroup recordGroup : groupClusterCenter.keySet()) {
                Coordination center2 = groupClusterCenter.get(recordGroup);
                centersArray[i][j] = center1.getLength(center2);
                j++;
            }
            indexGroupMap.put(i, evaluationRecordGroup);
            i++;
        }


        for (EvaluationRecord evaluationRecord : evaluation.getRecords()) {
            double[] myPos = new double[groupClusterCenter.size()];
            i = 0;
            //find distance from each center
            for (Coordination center : groupClusterCenter.values()) {
                myPos[i] = center.getLength(evaluationRecord.getConsideringValues());
                i++;
            }

            // add the record to the group having most similar distance ot the other groups
            int groupIndex = -1;
            double minLength = 0;
            for (int i1 = 0; i1 < centersArray.length; i1++) {
                double[] groupNewCenter = centersArray[i1];
                double length = 0;
                for (int i2 = 0; i2 < myPos.length; i2++) {
                    double aDouble = myPos[i2];
                    length += Math.pow(aDouble - groupNewCenter[i2],2);
                }
                if (length < minLength || groupIndex < 0) {
                    minLength = length;
                    groupIndex = i1;
                }
            }

            indexGroupMap.get(groupIndex).getRecords().add(evaluationRecord);
        }

        /*//after each record it updates groups center!!
        for (EvaluationRecord evaluationRecord : evaluation.getRecords()) {
            EvaluationRecordGroup group = getNearestGroup(evaluationRecord);
            group.getRecords().add(evaluationRecord);
            //recalculate center
            groupClusterCenter.put(group, calculateCenter(group.getRecords()));
        }*/
    }

    public String toString() {
        return "Nearest Distance";
    }
}
