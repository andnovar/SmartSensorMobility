/*
 * Copyright (c) 2005-2008 by Masoud Moshref Javadi <moshref@ce.sharif.edu>, http://ce.sharif.edu/~moshref
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

package edu.sharif.ce.dml.common.logic.entity.evaluation;


import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 2, 2007
 * Time: 4:07:06 PM<br>
 * A tabular object usally contains an evaluation result
 */
public class Evaluation {
    private String name;

    /**
     * each table row
     */
    private List<EvaluationRecord> records;

    private EvaluationMeasure[] evaluations;


    public String getName() {
        return name;
    }

    public int getNumberOfEvaluators() {
        return evaluations.length;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return each table row
     */
    public List<EvaluationRecord> getRecords() {
        return records;
    }

    public void setRecords(List<EvaluationRecord> records) {
        this.records = records;
    }

    /**
     * @return list of table measures (columns)
     */
    public EvaluationMeasure[] getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(EvaluationMeasure[] evaluations) {
        this.evaluations = evaluations;
    }

    public Evaluation(String name, List<EvaluationRecord> records, EvaluationMeasure[] evaluations) {
        this.name = name;
        this.records = records;
        this.evaluations = evaluations;
    }

    public void setConsideringFactors(List<Integer> indexes){
        for (EvaluationRecord record : records) {
            record.setConsideringIndexes(indexes);
        }
    }

    public void doNormalize(List<Double> mins, List<Double> maxs, List<Integer> normalizableFactors) {
        for (EvaluationRecord record : records) {
            List<String> values = record.getValues();
            for (int i = 0; i < values.size(); i++) {
                if (normalizableFactors.contains(i)) {
                    double element = Double.parseDouble(values.get(i));
                    values.set(i, Double.toString(Math.round((element - mins.get(i)) / (maxs.get(i) - mins.get(i)) * 1000) / 1000.0));
                }
            }
        }
    }

    public void normalize(List<Double> mins, List<Double> maxs, List<Integer> normalizableFactors) {
        getNormalizedFactor(normalizableFactors);
        double[] min = null;
        double[] max = null;
        for (EvaluationRecord record : records) {
            List<String> valuesList = record.getValues();
            if (min == null) {
                min = new double[valuesList.size()];
                max = new double[valuesList.size()];
                for (int i = 0; i < min.length; i++) {
                    min[i] = Double.POSITIVE_INFINITY;
                    max[i] = Double.NEGATIVE_INFINITY;
                }
            }
            for (int i = 0; i < valuesList.size(); i++) {
                if (normalizableFactors.contains(i)) {
                    double v = Double.parseDouble(valuesList.get(i));
                    min[i] = min[i] > v ? v : min[i];
                    max[i] = max[i] < v ? v : max[i];
                }
            }
        }
        for (EvaluationRecord record : records) {
            List<String> valuesList = record.getValues();
            for (int i = 0; i < valuesList.size(); i++) {
                if (normalizableFactors.contains(i) && max[i] != min[i]) {
                    double element = Double.parseDouble(valuesList.get(i));
                    valuesList.set(i, Double.toString(Math.round((element - min[i]) / (max[i] - min[i]) * 1000) / 1000.0));
                }
            }
        }
        for (double v : max) {
            maxs.add(v);
        }

        for (double v : min) {
            mins.add(v);
        }

    }

    private void getNormalizedFactor(List<Integer> normalizableFactors) {
        //find normalizable factors
        Iterator<EvaluationRecord> it = records.iterator();
        EvaluationRecord record = it.next();
        List<String> valuesList = record.getValues();
        for (int i = 0; i < valuesList.size(); i++) {
            String s = valuesList.get(i);
            try {
                Double.parseDouble(s);
                normalizableFactors.add(i);
            } catch (NumberFormatException e) {

            }
        }
    }

    public void unnormalize(List<Double> mins, List<Double> maxs, List<Integer> normalizationFactors) {
        for (EvaluationRecord record : records) {
            List<String> values = record.getValues();
            for (int i = 0; i < values.size(); i++) {
                if (normalizationFactors.contains(i)) {
                    values.set(i, Double.toString(
                            Math.round((Double.parseDouble(values.get(i)) * (maxs.get(i) - mins.get(i)) + mins.get(i)) * 1000) / 1000.0)
                    );
                }
            }
        }
    }
}
