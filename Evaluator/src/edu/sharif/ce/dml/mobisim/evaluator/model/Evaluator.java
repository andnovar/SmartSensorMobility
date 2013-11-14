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

package edu.sharif.ce.dml.mobisim.evaluator.model;

import edu.sharif.ce.dml.common.data.trace.Tracable;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 13, 2007
 * Time: 4:01:56 PM
 * <br/> An abstract class for evaluators of network.
 */
public abstract class Evaluator<E> extends ParameterableImplement implements Tracable {
    protected int sampleTime;
    private int sampleTurn = -1;
    private int sortPriority =0;

    public static SortEvaluatorsComparator SORT_EVALUATOR_COMPARATOR = new SortEvaluatorsComparator();

    /**
     * runs evalutor with the edu.sharif.ce.dml.common.data in <tt>snapShot</tt> object at evaluation sampleTime.
     *
     * @param snapShot
     */
    public void runEvaluation(E snapShot) {
        if (sampleTurn == -1) {
            sampleTurn = sampleTime;
        }
        if (sampleTurn >= sampleTime) {
            evaluate(snapShot);
            sampleTurn = 1;

        } else {
            sampleTurn++;
        }
    }

    /**
     * runs evalutor with the edu.sharif.ce.dml.common.data in <tt>snapShot</tt> object.
     *
     * @param snapShot
     */
    protected abstract void evaluate(E snapShot);

    /**
     * resets all evaluation variables. as no edu.sharif.ce.dml.common.data has been come
     */
    public abstract void reset();

    /**
     * override this if the evaluator returns not a number value
     * @return
     */
    public List<Boolean> isDoubleSortable(){
        int size = getLabels().size();
        List<Boolean> output = new ArrayList<Boolean>(size);
        for (int i =0; i< size; i++){
            output.add(true);
        }
        return output;
    }


    public String toString() {
        Collection<String> names = getLabels();
        if (names.size() > 1) {
            StringBuffer sb = new StringBuffer();
            for (String name : names) {
                sb.append(name).append("|");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        if (names.size() > 0) {
            return names.iterator().next();
        }
        return "";
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("sampletime", new IntegerParameter("sampletime", sampleTime));
        parameters.put("sortpriority", new IntegerParameter("sortpriority", 100,-100,1, sortPriority));
        return parameters;
    }

    public int getSortPriority() {
        return sortPriority;
    }

    public boolean isSorting(){
        return sortPriority!=0;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        sampleTime = (Integer) parameters.get("sampletime").getValue();
        sortPriority = (Integer) parameters.get("sortpriority").getValue();
    }

    public static class SortEvaluatorsComparator implements Comparator<Evaluator>{

        public int compare(Evaluator o1, Evaluator o2) {
            return o1.sortPriority-o2.sortPriority;
        }
    }

    public int compareTo(Object o) {
        int sortOutput = sortPriority - ((Evaluator) o).sortPriority;
        if (sortOutput==0){
            return super.compareTo(o);
        }
        return sortOutput;
    }
}
