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
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationRecordGroup;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.center.NearestCenter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
public class GroupSVD extends NearestCenter {
    private Map<Integer, Matrix> indexSingularMatrix = new HashMap<Integer, Matrix>();
    private Map<Integer, Matrix> indexVMatrix;
    private Map<Integer, EvaluationRecordGroup> indexGroupMap;
    double singularCost = 0;


    @Override
    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("singularcost", new DoubleParameter("singularcost", singularCost));
        return parameters;
    }

    @Override
    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        singularCost = ((DoubleParameter) parameters.get("singularcost")).getValue();
    }

    public void setEvaluationData(Evaluation evaluation) {

        if (indexSingularMatrix.size() == 0) {
            indexVMatrix = new HashMap<Integer, Matrix>(groupClusterCenter.size());
            indexGroupMap = new HashMap<Integer, EvaluationRecordGroup>();
            int index = 0;
            for (EvaluationRecordGroup evaluationRecordGroup : groupClusterCenter.keySet()) {
                //calculate matrix A which is position of nodes regarding to group's center
                indexGroupMap.put(index, evaluationRecordGroup);
                final Collection<EvaluationRecord> records = evaluationRecordGroup.getRecords();
                double[][] aArray = new double[records.size()][coordinateSize];
                NearestCenter.Coordination center = groupClusterCenter.get(evaluationRecordGroup);
                int r = 0;
                for (EvaluationRecord record : records) {
                    List<Double> values = record.getConsideringValues();
                    int c = 0;
                    for (Double value : values) {
                        aArray[r][c] = value - center.getCoordination(c);
                        c++;
                    }
                    r++;
                }
                //decompose matrix
                Matrix aMatrix = new Matrix(aArray);
                SingularValueDecomposition svd = new SingularValueDecomposition(aMatrix);
                indexVMatrix.put(index, svd.getV().transpose());
                double[] singularValues = svd.getSingularValues();

                double[][] singularValuesA = new double[singularValues.length][1];
                int zeroValues = 0;
                for (int i3 = 0; i3 < singularValues.length; i3++) {
                    double v = singularValues[i3];
                    if (v == 0) {
                        zeroValues++;
                        singularValuesA[i3] = new double[]{singularCost};
                    } else {
                        singularValuesA[i3] = new double[]{1 / v};
                    }
                }
                if (zeroValues > 0)
                    System.out.println("zero singular values=" + zeroValues);
                indexSingularMatrix.put(index, new Matrix(singularValuesA));

                index++;
            }
        }

        for (EvaluationRecord evaluationRecord : evaluation.getRecords()) {
            List<Double> originalPose = evaluationRecord.getConsideringValues();

            int groupIndex = -1;
            double minLength = 0;
            for (Integer index : indexGroupMap.keySet()) {
                double[][] distanceArray = new double[coordinateSize][1];

                //get distance of the current record to each center
                int r = 0;
                Coordination center = groupClusterCenter.get(indexGroupMap.get(index));
                for (double centerCoordinate : center.getCoordinations()) {
                    distanceArray[r][0] = centerCoordinate - originalPose.get(r);
                    r++;
                }
                Matrix matrix1 = new Matrix(distanceArray);
                Matrix poseInGroup = indexVMatrix.get(index).times(matrix1).arrayTimes(indexSingularMatrix.get(index));
                Double length = poseInGroup.transpose().times(poseInGroup).get(0, 0);
                if (length < minLength || groupIndex < 0) {
                    minLength = length;
                    groupIndex = index;
                }
            }

            indexGroupMap.get(groupIndex).getRecords().add(evaluationRecord);
        }
    }


    @Override
    public void reset() {
        super.reset();
        indexSingularMatrix.clear();
    }

    public String toString() {
        return "Group SVD";
    }
}
