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

import Jama.Matrix;
import Jama.SingularValueDecomposition;
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
 * <br/> creates the distances between the center of groups. Then, decomposes the matrix
 * using SVD and multiplies it by U/S which remains U. U/S here is transform which
 * gives the projection of each vector on the eigenvectors of group's center matrix <br/>
 * see SVD on wikipedia
 */
public class NearestSVD extends NearestCenter {
    private Matrix singularMatrix;
    private Matrix groupNewCenters;
    private Matrix uMatrix;
    private Map<Integer, EvaluationRecordGroup> indexGroupMap;

    public void setEvaluationData(Evaluation evaluation) {

        if (singularMatrix == null) {
            //calculate matrix A which is distance between centers
            double[][] aArray = new double[groupClusterCenter.size()][groupClusterCenter.size()];
            indexGroupMap = new HashMap<Integer, EvaluationRecordGroup>();
            int i = 0;
            for (EvaluationRecordGroup evaluationRecordGroup : groupClusterCenter.keySet()) {
                Coordination center1 = groupClusterCenter.get(evaluationRecordGroup);
                int j = 0;
                for (EvaluationRecordGroup recordGroup : groupClusterCenter.keySet()) {
                    Coordination center2 = groupClusterCenter.get(recordGroup);
                    aArray[i][j] = center1.getLength(center2);
                    j++;
                }
                indexGroupMap.put(i, evaluationRecordGroup);
                i++;
            }
            //decompose matrix
            Matrix aMatrix = new Matrix(aArray);
            SingularValueDecomposition svd = new SingularValueDecomposition(aMatrix);
            uMatrix = svd.getU().transpose();
            double[] singularValues = svd.getSingularValues();

            double[][] singularValuesA = new double[singularValues.length][1];
            int zeroValues=0;
            for (int i3 = 0; i3 < singularValues.length; i3++) {
                double v = singularValues[i3];
                if (v == 0) {
                    zeroValues++;
                    singularValuesA[i3]=new double[]{v};
                }else{
                    singularValuesA[i3] = new double[]{1 / v};
                }
            }
            if (zeroValues>0)
                System.out.println("zero singular values="+zeroValues);
            singularMatrix = new Matrix(singularValuesA);


            //fill group new centers matrix
            groupNewCenters = new Matrix(aArray);
            groupNewCenters = uMatrix.times(groupNewCenters);
            /*for (int i2 = 0; i2 < aArray.length; i2++) {
                double[] doubles = aArray[i2];
                double[][] temp = new double[doubles.length][1];
                for (int i1 = 0; i1 < doubles.length; i1++) {
                    double aDouble = doubles[i1];
                    temp[i1][0] = aDouble;
                }
                Matrix tempMatrix = uMatrix.times(new Matrix(temp));
                groupNewCenters[i2] = tempMatrix;
            }*/

        }

        int i = 0;
        for (EvaluationRecord evaluationRecord : evaluation.getRecords()) {
            double[][] distanceArray = new double[groupClusterCenter.size()][1];
            i = 0;
            //get distance of the current record to each center
            for (Coordination center : groupClusterCenter.values()) {
                distanceArray[i][0] = center.getLength(evaluationRecord.getConsideringValues());
                i++;
            }
            Matrix matrix1 = new Matrix(distanceArray);
            Matrix newPose = uMatrix.times(matrix1);

            //newPose = newPose.arrayTimes(singularMatrix);

            int groupIndex = -1;

            double minLength = 0;
            for (int i1 = 0; i1 < groupNewCenters.getRowDimension(); i1++) {
                Matrix groupNewCenter = groupNewCenters.getMatrix(0,groupNewCenters.getColumnDimension()-1,i1,i1);
                Matrix distance = newPose.minus(groupNewCenter).arrayTimes(singularMatrix);
                double length=distance.transpose().times(distance).get(0,0);
                if (length < minLength || groupIndex < 0) {
                    minLength = length;
                    groupIndex = i1;
                }
            }

            indexGroupMap.get(groupIndex).getRecords().add(evaluationRecord);
        }
    }

    @Override
    public void reset() {
        super.reset();
        singularMatrix=null;
    }

    public String toString() {
        return "Nearest SVD";
    }
}
