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

package edu.sharif.ce.dml.mobisim.evaluator.model.position;


import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Feb 16, 2007
 * Time: 7:38:12 PM
 */
public class SpatialCalculator extends MobilityEvaluator {
    public static final int MAX_SPATIAL_RELATION = 100;
    ///////////////////
    private double storage = 0;
    private long numOfValue = 0;
    private int maxSpatialRelation;


    public SpatialCalculator() {
        maxSpatialRelation = MAX_SPATIAL_RELATION;
    }

    public  void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        maxSpatialRelation = (Integer) parameters.get("maxspatialrelation").getValue();
    }

    public  java.util.Map<String, Parameter> getParameters(){
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("maxspatialrelation",new IntegerParameter("maxspatialrelation",maxSpatialRelation));
        return parameters;
    }



    public void reset() {
        storage=0;
        numOfValue=0;
    }

    public void evaluate(SnapShotData currentSnapShotData) {
        for (NodeShadow nodeShadow1 : currentSnapShotData.getNodeShadows()) {
            for (NodeShadow nodeShadow2 : currentSnapShotData.getNodeShadows()) {
                if (!nodeShadow1.getName().equals(nodeShadow2.getName()) &&
                        Math.abs(nodeShadow1.getLocation().getLength(nodeShadow2.getLocation())) < maxSpatialRelation) {
                    storage += getSpeedRatio(nodeShadow1, nodeShadow2) * getRelativeDir(nodeShadow1, nodeShadow2);
                    numOfValue++;
                }
            }
        }
    }


    /*
       data for this specific object not internal objects
    */
    public List print() {
        return Arrays.asList(storage / numOfValue);
    }

    public List<String> getLabels() {
        return Arrays.asList("Spatial Dependency");
    }
}
