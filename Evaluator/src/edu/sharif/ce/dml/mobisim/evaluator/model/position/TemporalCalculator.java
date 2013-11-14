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


import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Feb 16, 2007
 * Time: 5:39:04 PM
 */
public class TemporalCalculator extends MobilityEvaluator {
    public static final int MAX_TIME_RELATION = 30;
    ///////////////////
    private double storage = 0;
    private long numOfValue = 0;
    private int maxTimeRelation;
    private List<SnapShotData> datas =  new LinkedList<SnapShotData>();

    public TemporalCalculator() {
        maxTimeRelation = MAX_TIME_RELATION;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        maxTimeRelation = (Integer) parameters.get("maxtimerelation").getValue();
    }

    public  java.util.Map<String, Parameter> getParameters(){
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("maxtimerelation",new IntegerParameter("maxtimerelation",maxTimeRelation));
        return parameters;
    }

    public void reset() {
        storage=0;
        numOfValue=0;
        datas = new LinkedList<SnapShotData>();
    }

    public void evaluate(SnapShotData data) {
        datas.add(data);
    }

    /*
       data for this specific object not internal objects
    */
    public List print() {
        for (ListIterator it = datas.listIterator(); it.hasNext();) {
            SnapShotData shotA = (SnapShotData) it.next();
            for (ListIterator it1 = datas.listIterator(Math.max(0, it.previousIndex() - maxTimeRelation));
                 it1.hasNext() && it1.nextIndex() < it.nextIndex() + maxTimeRelation;) {
                SnapShotData shotB = (SnapShotData) it1.next();
                if (it.previousIndex() != it1.previousIndex()) {
                    for (int i = 0; i < shotB.getNodeShadows().length; i++) {
                        storage += getSpeedRatio(shotA.getNodeShadows()[i], shotB.getNodeShadows()[i]) *
                                getRelativeDir(shotA.getNodeShadows()[i], shotB.getNodeShadows()[i]);
                        numOfValue++;
                    }
                }
            }
        }
        return Arrays.asList(storage / numOfValue);
    }

    public List<String> getLabels() {
        return Arrays.asList("Temporal Dependency");
    }
}
