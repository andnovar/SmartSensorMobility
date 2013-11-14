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

package edu.sharif.ce.dml.common.parameters.logic.parameterable;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 28, 2007
 * Time: 8:10:41 AM<br>
 * used as a Parameter to indicate a Double variable. It is iterable so can used via an {@link java.util.Iterator}.
 * Has a special UI.<br/>
 * parameters are:<br/>
 * {@link edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter} init: initial value.<br/>
 * {@link edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter} step: variable step.<br/>
 * {@link edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter} number: number of change of vaule by adding step.
 */
public class VariableParameter extends ParameterableImplement implements Iterable {

    private DoubleParameter init, step;
    /**
     * number of change of value by adding step.
     */
    private IntegerParameter number;

    public VariableParameter() {
        init = new DoubleParameter("init", 0);
        step = new DoubleParameter("step", 0);
        number = new IntegerParameter("number", 0);
    }

    /**
     * uses value of p as initial vaule, step of p as step, 0 as number of changes.
     * @param p
     */
    public VariableParameter(DoubleParameter p) {
        this(p.getValue(), p.getStep(), 0, p.getName());
    }

    /**
     * uses value of p as initial vaule, step of p as step, 0 as number of changes.
     * @param p
     */
    public VariableParameter(IntegerParameter p) {
        this(p.getValue(), p.getStep(), 0, p.getName());
    }

    /**
     * @return number parameter + 1, means number of running for over this iterable object.
     */
    public int getNumberOfRun() {
        return number.getValue() + 1;
    }

    public boolean equals(Object obj) {
        return getName().equals(((VariableParameter) obj).getName());
    }

    /**
     *
     * @param init initial value
     * @param step change step
     * @param number number of changes
     * @param name
     */
    public VariableParameter(double init, double step, int number, String name) {
        this.init = new DoubleParameter("init", init);
        this.step = new DoubleParameter("step", step);
        this.number = new IntegerParameter("number", number);
        setName(name);
    }

    /**
     * @return iterator that starts from <tt>init</tt> value and runs for <tt>number</tt> times, and
     * adds <tt>step</tt> value each time.
     */
    public Iterator iterator() {
        return new VariableIterator();
    }

    /**
     * @return this object.
     */
    public Object getValue() {
        return this;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        init = (DoubleParameter) parameters.get("init");
        step = (DoubleParameter) parameters.get("step");
        number = (IntegerParameter) parameters.get("number");
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("init", init);
        parameters.put("step", step);
        parameters.put("number", number);
        return parameters;
    }

    private class VariableIterator implements Iterator {
        private int currentStep = 0;
        private double currentVal;

        private VariableIterator() {
            currentVal = init.getValue() - step.getValue();
        }

        public boolean hasNext() {
            return currentStep <= number.getValue();
        }

        public Object next() {
            currentStep++;
            currentVal += step.getValue();
            return currentVal;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
