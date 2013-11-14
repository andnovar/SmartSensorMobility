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

import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Aug 28, 2009
 * Time: 9:41:03 PM
 */
public class LocationDistributionVariance extends MobilityEvaluator {
    private List<List <Integer>> distribution= new ArrayList<List<Integer>>();
    private int sliceSize=1;

    public Map<String, Parameter> getParameters() {
        Map<String,Parameter> parameters =  super.getParameters();
        parameters.put("slicesize",new IntegerParameter("slicesize",sliceSize));
        return parameters;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        sliceSize = ((IntegerParameter) parameters.get("slicesize")).getValue();
    }

    protected void evaluate(SnapShotData snapShot) {
        for (NodeShadow nodeShadow : snapShot.getNodeShadows()) {
            int x = nodeShadow.getLocation().getX()/sliceSize;
            int y = nodeShadow.getLocation().getY()/sliceSize;
            if (distribution.size()<=y){
                int bound = y - (distribution.size()) + 1;
                for (int i =0; i< bound; i++){
                    distribution.add(new ArrayList<Integer>());
                }
            }

            List<Integer> list = distribution.get(y);
            if (list.size()<=x){
                int bound = x - list.size() + 1;
                for (int i =0; i< bound; i++){
                    list.add(0);
                }
            }
            list.set(x,list.get(x)+1);
        }
    }

    public void reset() {
        distribution.clear();
    }

    /*
       data for this specific object not internal objects
    */
    public List print() {
        double mean=0;
        int count =0;
        for (List<Integer> integers : distribution) {
            for (Integer integer : integers) {
                mean+=integer;
            }
            count+=integers.size();
        }
        mean=mean/count;
        double variance=0;
        for (List<Integer> integers : distribution) {
            for (Integer integer : integers) {
                variance+=(integer-mean)*(integer-mean);
            }
        }
        return Arrays.asList(variance/count);
    }

    public List<String> getLabels() {
        return Arrays.asList("Location Distribution Variance");
    }
}
