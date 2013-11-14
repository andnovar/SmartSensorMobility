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

import edu.sharif.ce.dml.common.data.trace.filter.TraceFilter;
import edu.sharif.ce.dml.common.logic.entity.evaluation.Evaluation;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.FileParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationAnalyzer;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationRecordGroup;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluatorClassifier;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.LearnableAnalyzer;
import edu.sharif.ce.dml.mobisim.evaluator.model.Evaluator;
import edu.sharif.ce.dml.mobisim.evaluator.model.analyze.MembershipPerClass;
import edu.sharif.ce.dml.mobisim.evaluator.model.analyze.RightMembershipAverage;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Aug 29, 2009
 * Time: 12:32:32 PM
 */
public class FuzzyClassification extends EvaluationAnalyzer implements LearnableAnalyzer, EvaluatorClassifier {
    public ParameterableParameter learnFileParameter = null;

    protected int kNeighbors = 0;
    protected int distanceWeight = 0;


    protected List<FuzzyEvaluationRecordGroup> learnClasses = new ArrayList<FuzzyEvaluationRecordGroup>();
    protected Set<EvaluationRecordWrapper> classsifiedRecordWrappers = new HashSet<EvaluationRecordWrapper>();

    protected MembershipPerClass membershipPerClassEvaluator = new MembershipPerClass();
    private Map<String, FuzzyEvaluationRecordGroup> nameTestClasses;

    public FuzzyClassification() {
        this.learnFileParameter = new ParameterableParameter();
    }

    public void reset() {
        learnClasses = new ArrayList<FuzzyEvaluationRecordGroup>();
        classsifiedRecordWrappers.clear();
    }

    public String toString() {
        return "Fuzzy KNN Classification";
    }

    public File getLearnFile() {
        return getLearnFileParameter().getValue();
    }

    private FileParameter getLearnFileParameter() {
        return (FileParameter) learnFileParameter.getValue();
    }

    public TraceFilter<EvaluationRecord> getLearnFileFilter() {
        return (TraceFilter<EvaluationRecord>) getLearnFileParameter().getDefaultFilter();
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        learnFileParameter = (ParameterableParameter) parameters.get("learnfile");
        kNeighbors = ((IntegerParameter) parameters.get("kneighbors")).getValue();
        distanceWeight = ((IntegerParameter) parameters.get("distanceweight")).getValue();

    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("learnfile", learnFileParameter);
        parameters.put("kneighbors", new IntegerParameter("kneighbors", kNeighbors));
        parameters.put("distanceweight", new IntegerParameter("distanceweight", distanceWeight));
        return parameters;
    }

    protected double getLength(EvaluationRecord er1, EvaluationRecord er2) {
        List<Double> list1 = er1.getConsideringValues();
        List<Double> list2 = er2.getConsideringValues();
        double sum = 0;
        for (int i =0; i<list1.size(); i++){
            sum += Math.pow(list1.get(i) -list2.get(i), 2);
        }

        return Math.sqrt(sum);
    }

    public List<Evaluator<EvaluationRecordGroup>> getEvaluators() {
        List<Evaluator<EvaluationRecordGroup>> list = new LinkedList<Evaluator<EvaluationRecordGroup>>();
        list.add(new RightMembershipAverage());
        list.add(membershipPerClassEvaluator);
        return list;
    }

    public void learn(Collection<EvaluationRecordGroup> inputData) {
        List<EvaluationRecordWrapper> allRecordWrappers = new LinkedList<EvaluationRecordWrapper>();
        //fill data structures for learning records
        for (EvaluationRecordGroup evaluationRecordGroup : inputData) {
            //create fuzzyGroup
            FuzzyEvaluationRecordGroup fuzzyGroup = new FuzzyEvaluationRecordGroup();
            fuzzyGroup.setName(evaluationRecordGroup.getName());
            List<EvaluationRecord> learnGroupRecords = new LinkedList<EvaluationRecord>();
            for (EvaluationRecord evaluationRecord : evaluationRecordGroup.getRecords()) {
                //create fuzzy recordWrapper whith its actual fuzzyGroup
                EvaluationRecordWrapper recordWrapper = new EvaluationRecordWrapper(evaluationRecord, fuzzyGroup);
                allRecordWrappers.add(recordWrapper);
                learnGroupRecords.add(evaluationRecord);
            }
            //set to the group that learn records is these
            fuzzyGroup.setRecords(learnGroupRecords);
            learnClasses.add(fuzzyGroup);
        }
        List<EvaluationRecordWrapper> sortedNeighborList = new ArrayList<EvaluationRecordWrapper>(allRecordWrappers.size());
        for (EvaluationRecordWrapper recordWrapper : allRecordWrappers) {
            //find distances of other records to this recordWrapper

            for (EvaluationRecordWrapper evaluationRecordWrapper : allRecordWrappers) {
                if (!recordWrapper.equals(evaluationRecordWrapper)) {
                    evaluationRecordWrapper.setDistance(getLength(recordWrapper.getEvaluationRecord(), evaluationRecordWrapper.getEvaluationRecord()));
                    sortedNeighborList.add(evaluationRecordWrapper);
                }
            }
            //set my class
            //creates test classes that is different than training classses
            FuzzyEvaluationRecordGroup myClass = recordWrapper.getGroup();
            myClass.updateRecord(recordWrapper.getEvaluationRecord(), 0.51);
            //for each k nearest
            int upperBound = Math.min(sortedNeighborList.size(), kNeighbors);
            Collections.sort(sortedNeighborList);

            //find out how many of my nearest neighbors are in each class
            Map<FuzzyEvaluationRecordGroup, Integer> neighborInGroups = new HashMap<FuzzyEvaluationRecordGroup, Integer>();
            Iterator<EvaluationRecordWrapper> itr = sortedNeighborList.iterator();
            for (int i = 0; i < upperBound; i++) {
                EvaluationRecordWrapper neighbor = itr.next();
                FuzzyEvaluationRecordGroup learnClass = neighbor.getGroup();
                Integer numberInClass = neighborInGroups.get(learnClass);
                if (numberInClass == null) {
                    numberInClass = 1;
                } else {
                    numberInClass++;
                }
                neighborInGroups.put(learnClass, numberInClass);
            }
            for (FuzzyEvaluationRecordGroup neighborClass : neighborInGroups.keySet()) {
                //add recordWrapper to the neighbor group
                if (recordWrapper.getGroup().equals(neighborClass)) {
                    //calculate neighborClass.getRecordsSize()
                    neighborClass.updateRecord(recordWrapper.getEvaluationRecord(), 0.51 + 0.49 * neighborInGroups.get(neighborClass) / upperBound);
                } else {
                    neighborClass.updateRecord(recordWrapper.getEvaluationRecord(), 0.49 * neighborInGroups.get(neighborClass) / upperBound);
                }
            }
            classsifiedRecordWrappers.add(recordWrapper);

            sortedNeighborList.clear();
        }

        List<String> groupLabels = new LinkedList<String>();
        for (FuzzyEvaluationRecordGroup aClass : learnClasses) {
            groupLabels.add(aClass.getName());
        }
        membershipPerClassEvaluator.setLabels(groupLabels);
    }


    public void setEvaluationData(Evaluation evaluation) {
        nameTestClasses = new HashMap<String, FuzzyEvaluationRecordGroup>();
        for (FuzzyEvaluationRecordGroup learnClass : learnClasses) {
            FuzzyEvaluationRecordGroup testClass = new FuzzyEvaluationRecordGroup();
            testClass.setName(learnClass.getName());
            nameTestClasses.put(learnClass.getName(),testClass);
        }
        List<EvaluationRecordWrapper> sortedNeighborList = new ArrayList<EvaluationRecordWrapper>(classsifiedRecordWrappers.size());
        for (EvaluationRecord evaluationRecord : evaluation.getRecords()) {
            //find k nearest neighbors
            for (EvaluationRecordWrapper classsifiedRecordWrapper : classsifiedRecordWrappers) {
                classsifiedRecordWrapper.setDistance(getLength(evaluationRecord, classsifiedRecordWrapper.getEvaluationRecord()));
                sortedNeighborList.add(classsifiedRecordWrapper);
            }
            int upperBound = Math.min(sortedNeighborList.size(), kNeighbors);
            Collections.sort(sortedNeighborList);

            //extract k neighbors
            List<EvaluationRecordWrapper> kNeighborsRecordWrapper = new ArrayList<EvaluationRecordWrapper>(upperBound);
            Iterator<EvaluationRecordWrapper> itr = sortedNeighborList.iterator();
            for (int i = 0; i < upperBound; i++) {
                kNeighborsRecordWrapper.add(itr.next());
            }
            //extract neighbors that are exactly where current record is
            List<EvaluationRecordWrapper> kZeroNeighborsRecordWrapper = new ArrayList<EvaluationRecordWrapper>(upperBound);
            for (EvaluationRecordWrapper evaluationRecordWrapper : kNeighborsRecordWrapper) {
                if (evaluationRecordWrapper.getDistance() == 0) {
                    kZeroNeighborsRecordWrapper.add(evaluationRecordWrapper);
                }
            }

            // find my membership in each class
            for (FuzzyEvaluationRecordGroup aClass : learnClasses) {
                FuzzyEvaluationRecordGroup testClass = nameTestClasses.get(aClass.getName());
                if (kZeroNeighborsRecordWrapper.size() > 0) {
                    double membershipSum = 0;
                    for (EvaluationRecordWrapper evaluationRecordWrapper : kZeroNeighborsRecordWrapper) {
                        membershipSum += aClass.getRecordMembership(evaluationRecordWrapper.getEvaluationRecord());
                    }
                    testClass.updateRecord(evaluationRecord, membershipSum / kZeroNeighborsRecordWrapper.size());
                } else {
                    double membershipSum = 0;
                    double distanceSum = 0;
                    for (EvaluationRecordWrapper neighborRecordWrapper : kNeighborsRecordWrapper) {
                        double distance = 1 / Math.pow(neighborRecordWrapper.getDistance(), 2 / (distanceWeight - 1));
                        membershipSum += aClass.getRecordMembership(neighborRecordWrapper.getEvaluationRecord()) * distance;
                        distanceSum += distance;
                    }
                    double value = membershipSum / distanceSum;
                    // check nan values
                    if (distanceSum > 0) {
                        testClass.updateRecord(evaluationRecord, value);
                    } else {
                        testClass.updateRecord(evaluationRecord, 0);
                    }
                }
            }

            //classsifiedRecordWrappers.add(recordWrapper);
            sortedNeighborList.clear();
        }
    }

    public Collection<EvaluationRecordGroup> getEvaluationGroups() {
        //return groups that contains only maximum membership models
        List<EvaluationRecordGroup> output = new LinkedList<EvaluationRecordGroup>();
        FuzzyEvaluationRecordGroup.classifyBasedonMemberShip(nameTestClasses.values());
        for (FuzzyEvaluationRecordGroup group : nameTestClasses.values()) {
            for (EvaluationRecord evaluationRecord : group.getRecords()) {
                List addedValues = new LinkedList();
                addedValues.add(group.getName());
                for (FuzzyEvaluationRecordGroup fuzzyEvaluationRecordGroup : nameTestClasses.values()) {
                    addedValues.add(Double.toString(
                            fuzzyEvaluationRecordGroup.getRecordMembership(evaluationRecord)));
                }
                evaluationRecord.addValues(addedValues);
            }
            output.add(group);
        }
        return output;
    }

    @Override
    public void setConsideringFactorsSize(int size) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * it should be run after learning phase
     * @return
     */
    public List<String> getLabels() {
        List<String> output = new LinkedList<String>(super.getLabels());
        for (String s : nameTestClasses.keySet()) {
            output.add(s);
        }
        return output;
    }
}
