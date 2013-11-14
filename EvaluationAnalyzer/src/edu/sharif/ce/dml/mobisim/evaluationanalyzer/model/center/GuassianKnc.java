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

package edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.center;

import edu.sharif.ce.dml.common.logic.entity.evaluation.Evaluation;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluationRecordGroup;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.EvaluatorClassifier;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.model.fuzzy.FuzzyEvaluationRecordGroup;
import edu.sharif.ce.dml.mobisim.evaluator.model.Evaluator;
import edu.sharif.ce.dml.mobisim.evaluator.model.analyze.MembershipPerClass;
import edu.sharif.ce.dml.mobisim.evaluator.model.analyze.RightMembershipAverage;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 25, 2009
 * Time: 12:02:21 PM
 */
public class GuassianKnc extends NearestCenter implements EvaluatorClassifier {
    private MembershipPerClass membershipPerClassEvaluator = new MembershipPerClass();
    private Map<String, FuzzyEvaluationRecordGroup> nameGroupMap = new HashMap<String, FuzzyEvaluationRecordGroup>();

    public void setEvaluationData(Evaluation evaluation) {
        //set membership evaluator labels
        List<String> labels = new LinkedList<String>();
        Map<EvaluationRecordGroup, Double> groupSigmaMap = new HashMap<EvaluationRecordGroup, Double>();
        Map<EvaluationRecordGroup, Double> groupMeanMap = new HashMap<EvaluationRecordGroup, Double>();
        for (EvaluationRecordGroup evaluationRecordGroup : groupClusterCenter.keySet()) {
            labels.add(evaluationRecordGroup.getName());
            double mean = 0;
            int number = 0;
            //for (EvaluationRecordGroup evaluationRecordGroup2 : groupClusterCenter.keySet()) {
                for (EvaluationRecord evaluationRecord : evaluationRecordGroup.getRecords()) {
                    double value = groupClusterCenter.get(evaluationRecordGroup).getLength(evaluationRecord.getConsideringValues());
                    mean += value;
                    number++;
                }
        //    }
            mean /=number;
            groupMeanMap.put(evaluationRecordGroup,mean);
            double sigma=0;
            number=0;
            //for (EvaluationRecordGroup evaluationRecordGroup2 : groupClusterCenter.keySet()) {
                for (EvaluationRecord evaluationRecord : evaluationRecordGroup.getRecords()) {
                    double value = groupClusterCenter.get(evaluationRecordGroup).getLength(evaluationRecord.getConsideringValues());
                    sigma+=(value-mean)*(value-mean);
                    number++;
                }
           // }
            sigma = sigma / (number-1);

            /*for (EvaluationRecordGroup recordGroup : groupClusterCenter.keySet()) {
                NearestCenter.Coordination center = groupClusterCenter.get(recordGroup);
                mean += center.getLength(groupClusterCenter.get(evaluationRecordGroup));
            }
            mean = mean / groupClusterCenter.size();
            groupMeanMap.put(evaluationRecordGroup, mean);
            //calculate sigma for each group
            double sigma = 0;
            Collection<EvaluationRecord> groupRecords = evaluationRecordGroup.getRecords();
            for (EvaluationRecord evaluationRecord : groupRecords) {
                double values = 0;
                //sigma is sigma of this group members to center of all others
                for (EvaluationRecordGroup recordGroup : groupClusterCenter.keySet()) {
                    NearestCenter.Coordination center = groupClusterCenter.get(recordGroup);
                    values += center.getLength(getClusternigFactors(evaluationRecord.getValues()));
                }
                values = values / groupClusterCenter.size();
                sigma += (values - mean) * (values - mean);
            }
            if (sigma == 0) {
                DevelopmentLogger.logger.warn(" sigma of group " + evaluationRecordGroup.getName() + " is zero " +
                        "with features " + clusteringFactors);
            }
            sigma = sigma / groupRecords.size();*/
            groupSigmaMap.put(evaluationRecordGroup, sigma);
            FuzzyEvaluationRecordGroup group = new FuzzyEvaluationRecordGroup();
            group.setName(evaluationRecordGroup.getName());
            nameGroupMap.put(group.getName(), group);
        }
        membershipPerClassEvaluator.setLabels(labels);

        double sqrt2pi = Math.sqrt(2 * Math.PI);
        for (EvaluationRecord evaluationRecord : evaluation.getRecords()) {
            double sumAlphaI = 0;
            for (EvaluationRecordGroup evaluationRecordGroup : groupClusterCenter.keySet()) {
                double sigmaI = groupSigmaMap.get(evaluationRecordGroup);

                //find mean distance to other groups
                double values = 0;
                NearestCenter.Coordination center = groupClusterCenter.get(evaluationRecordGroup);
                values += center.getLength(evaluationRecord.getConsideringValues());
                double mean = groupMeanMap.get(evaluationRecordGroup);

                double alphaI = 0;
                if (sigmaI == 0) {
                    if (mean - values == 0) {
                        alphaI = 1;
                    } else {
                        alphaI = 0;
                    }
                } else {
                    alphaI = 1 / (Math.sqrt(sigmaI) * sqrt2pi) * Math.exp(-(
                            (values - mean) * (values - mean) /
                                    (2 * sigmaI)));
                }
                sumAlphaI += alphaI;
                nameGroupMap.get(evaluationRecordGroup.getName()).updateRecord(evaluationRecord, alphaI);
            }
            if (sumAlphaI == 0) {
                DevelopmentLogger.logger.fatal("sigma of all classes is zero");
                System.exit(1);
            }
            for (FuzzyEvaluationRecordGroup fuzzyEvaluationRecordGroup : nameGroupMap.values()) {
                double sigmaI = groupSigmaMap.get(fuzzyEvaluationRecordGroup);
                if (sigmaI > 0) {
                    fuzzyEvaluationRecordGroup.updateRecord(evaluationRecord,
                            fuzzyEvaluationRecordGroup.getRecordMembership(evaluationRecord) / sumAlphaI);
                }
            }
        }
    }


    public Collection<EvaluationRecordGroup> getEvaluationGroups() {
        //return groups that contains only maximum membership models
        List<EvaluationRecordGroup> output = new LinkedList<EvaluationRecordGroup>();
        FuzzyEvaluationRecordGroup.classifyBasedonMemberShip(nameGroupMap.values());
        for (FuzzyEvaluationRecordGroup group : nameGroupMap.values()) {
            for (EvaluationRecord evaluationRecord : group.getRecords()) {
                List values = new LinkedList();
                values.add(group.getName());
                for (FuzzyEvaluationRecordGroup fuzzyEvaluationRecordGroup : nameGroupMap.values()) {
                    values.add(
                            fuzzyEvaluationRecordGroup.getRecordMembership(evaluationRecord));
                }
                evaluationRecord.addValues(values);
            }
            output.add(group);
        }
        return output;

    }

    public List<String> getLabels() {
        List<String> output = new LinkedList<String>(super.getLabels());
        for (String s : nameGroupMap.keySet()) {
            output.add(s);
        }
        return output;
    }

    public String toString() {
        return "Gaussian KNC";
    }

    public List<Evaluator<EvaluationRecordGroup>> getEvaluators() {
        List<Evaluator<EvaluationRecordGroup>> list = new LinkedList<Evaluator<EvaluationRecordGroup>>();
        list.add(new RightMembershipAverage());
        list.add(membershipPerClassEvaluator);
        return list;
    }
}
