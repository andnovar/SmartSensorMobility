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

package edu.sharif.ce.dml.mobisim.evaluator.model.position;

import edu.sharif.ce.dml.common.data.entity.DataLocation;
import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Aug 22, 2009
 * Time: 1:09:27 AM
 */
public class RepetitiveBehavior extends MobilityEvaluator {
    private int range = 0;
    private int initalPointSelectionSampleTime = 0;
    private Map<NodeShadow, NodeHistory> totalRepetition=new HashMap<NodeShadow, NodeHistory>();

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("range", new IntegerParameter("range", range));
        parameters.put("initialpointselectionsampletime", new IntegerParameter("initialpointselectionsampletime",
                initalPointSelectionSampleTime));
        return parameters;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        range = ((IntegerParameter) parameters.get("range")).getValue();
        initalPointSelectionSampleTime = ((IntegerParameter) parameters.get("initialpointselectionsampletime")).getValue();
    }

    protected void evaluate(SnapShotData snapShot) {
        for (NodeShadow nodeShadow : snapShot.getNodeShadows()) {
            NodeHistory nodeHistory = totalRepetition.get(nodeShadow);
            if (nodeHistory == null || nodeHistory.referenceLoc == null) {
                break;
            }
            if (nodeHistory.referenceLoc.getLength(nodeShadow.getLocation()) <= range) {
                nodeHistory.value += snapShot.getTimeStep();
            }
        }
        if (totalRepetition.size()==0){
            for (NodeShadow nodeShadow : snapShot.getNodeShadows()) {
                totalRepetition.put(nodeShadow,new NodeHistory());
            }
        }
        if ((initalPointSelectionSampleTime > 0 &&
                snapShot.getTime() % initalPointSelectionSampleTime == 1) ||
                (initalPointSelectionSampleTime==0 && snapShot.getTime()==1)) {
            for (NodeShadow nodeShadow : snapShot.getNodeShadows()) {
                NodeHistory nodeHistory = totalRepetition.get(nodeShadow);
                nodeHistory.referenceLoc=nodeShadow.getLocation();
            }
        }
    }


    public void reset() {
        totalRepetition.clear();
    }/*
       data for this specific object not internal objects
    */

    public List print() {
        double average =0;
        for (NodeHistory nodeHistory : totalRepetition.values()) {
            average+=nodeHistory.value;
        }
        return Arrays.asList(average / totalRepetition.size());
    }

    public List<String> getLabels() {
        return Arrays.asList("Repetitive Behavior");
    }

    private class NodeHistory {
        DataLocation referenceLoc;
        int value = 0;
    }
}
