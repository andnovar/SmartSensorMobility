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

package edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.center;

import edu.sharif.ce.dml.common.data.trace.filter.TraceFilter;
import edu.sharif.ce.dml.common.logic.entity.evaluation.Evaluation;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.FileParameter;
import edu.sharif.ce.dml.common.util.io.output.BufferOutputWriter;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationAnalyzer;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationRecordGroup;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.LearnableAnalyzer;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 2, 2007
 * Time: 5:42:46 PM<br>
 * A class that uses a clustering algorithm like Kmeans with two phase (learn an cluster). So it needs
 * a {@link edu.sharif.ce.dml.common.parameters.logic.parameterable.FileParameter} with name "learnfile" to work. it should be
 * passed using an intermediate parameter {@link edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter}.
 */
public class NearestCenter extends EvaluationAnalyzer implements LearnableAnalyzer {
    /**
     * keeps the center of each group.
     */
    protected  Map<EvaluationRecordGroup, Coordination> groupClusterCenter = new HashMap<EvaluationRecordGroup, Coordination>();

    /**
     * learn file FileParameter
     */
    private ParameterableParameter learnFileParameter = null;
    protected int coordinateSize;


    public NearestCenter() {
        learnFileParameter = new ParameterableParameter();
    }

    public void learn(Collection<EvaluationRecordGroup> inputData) {
        for (EvaluationRecordGroup evaluationRecordGroup : inputData) {
            EvaluationRecordGroup group = new EvaluationRecordGroup(evaluationRecordGroup);
            groupClusterCenter.put(group, calculateCenter(group.getRecords()));
        }
    }

    public File getLearnFile() {
        return getLearnFileParameter().getValue();
    }

    private FileParameter getLearnFileParameter() {
        return (FileParameter)learnFileParameter.getValue();
    }

    public TraceFilter<EvaluationRecord> getLearnFileFilter() {
        return (TraceFilter<EvaluationRecord>) getLearnFileParameter().getDefaultFilter();
    }

    /**
     * calculates center of <tt>records</tt> using averaging algorithm.
     * @param records
     * @return
     */
    private Coordination calculateCenter(Collection<EvaluationRecord> records) {
        if (records.size() > 0) {
            double[] centerCoord = new double[coordinateSize];
            for (EvaluationRecord record : records) {
                List<Double> cFactors = record.getConsideringValues();
                for (int i = 0; i < centerCoord.length; i++) {
                    centerCoord[i] += cFactors.get(i);
                }
            }
            for (int i = 0; i < centerCoord.length; i++) {
                centerCoord[i] /= records.size();
            }
            return new Coordination(centerCoord);
        }
        double[] returnVal = new double[coordinateSize];
        Arrays.fill(returnVal, 0d);
        return new Coordination(returnVal);
    }



    public void setEvaluationData(Evaluation evaluation) {
        //after each record it updates groups center!!
        for (EvaluationRecord evaluationRecord : evaluation.getRecords()) {
            EvaluationRecordGroup group = getNearestGroup(evaluationRecord);
            group.getRecords().add(evaluationRecord);
            //recalculate center
          //  groupClusterCenter.put(group, calculateCenter(group.getRecords()));
        }
    }

    public String toString() {
        return "KNC Classification";
    }

    public Collection<EvaluationRecordGroup> getEvaluationGroups() {
        for (EvaluationRecordGroup evaluationRecordGroup : groupClusterCenter.keySet()) {
            for (EvaluationRecord evaluationRecord : evaluationRecordGroup.getRecords()) {
                evaluationRecord.addValue(evaluationRecordGroup.getName());
            }
        }
        return groupClusterCenter.keySet();
    }

    @Override
    public void setConsideringFactorsSize(int size) {
        this.coordinateSize=size;
    }

    /**
     * @param record
     * @return the group which has nearest center to the <tt>record</tt>
     */
    private EvaluationRecordGroup getNearestGroup(EvaluationRecord record) {
        EvaluationRecordGroup minGroup = null;
        double minGroupDistance = 0;
        for (EvaluationRecordGroup group : groupClusterCenter.keySet()) {
            double testDistance = groupClusterCenter.get(group).getLength(record.getConsideringValues());
            if (minGroup == null || testDistance < minGroupDistance) {
                minGroup = group;
                minGroupDistance = testDistance;
            }
        }
        return minGroup;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        learnFileParameter = (ParameterableParameter) parameters.get("learnfile");
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("learnfile", learnFileParameter);
        return parameters;
    }

    public void reset() {
        groupClusterCenter= new HashMap<EvaluationRecordGroup, Coordination>();
    }

    /**
     * a N dimentional coordination to track center of each group.
     */
    public class Coordination {
        private double[] coordinations;

        public Coordination(double[] coordinations) {
            this.coordinations = coordinations;
        }


        /**
         * creates a new location from loc Coordination coordinations
         *
         * @param loc
         */
        public Coordination(Coordination loc) {
            this(loc.getCoordinations());
        }

        public double getCoordination(int i) {
            return coordinations[i];
        }

        public void setCoordination(int i, double value) {
            coordinations[i] = value;
        }

/*
*/
        /**
         *
         * @return a unique int for each location in space. note that two location with same coordination
         *  will have same hashcode.
         */
/*
    public int hashCode() {
        return Integer.parseInt(new StringBuilder().append((int)x).append((int)y).append((int)getLength(0, 0)).toString());
    }
*/

        /**
         * @return distance from this location to location with x and y coordinations
         */
        public double getLength(double[] coordinations) {
            double storage = Math.pow(coordinations[0] - this.coordinations[0], 2);
            for (int i = 1; i < coordinations.length; i++) {
                storage += Math.pow(coordinations[i] - this.coordinations[i], 2);
            }
            return Math.sqrt(storage);
        }

        public double getLength(List<Double> coordinations) {
            double storage = Math.pow(coordinations.get(0) - this.coordinations[0], 2);
            for (int i = 1; i < coordinations.size(); i++) {
                storage += Math.pow(coordinations.get(i) - this.coordinations[i], 2);
            }
            return Math.sqrt(storage);
        }

        public double[] getCoordinations() {
            return coordinations;
        }

        public double getLength(Coordination dest) {
            return getLength(dest.getCoordinations());
        }

        public String print() {
            StringBuffer sb = new StringBuffer();
            for (double coordination : coordinations) {
                sb.append(BufferOutputWriter.separator).append(coordination);
            }
            return sb.toString();
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < coordinations.length; i++) {
                sb.append(i).append(": ").append(coordinations[i]).append(", ");
            }

            return sb.toString();
        }

        /**
         * @param o
         * @return this.getLength(o)
         */
        public boolean equals(Object o) {
            return this.getLength((Coordination) o) == 0;
        }
    }

}
