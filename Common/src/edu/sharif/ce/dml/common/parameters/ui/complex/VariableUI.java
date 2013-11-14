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

package edu.sharif.ce.dml.common.parameters.ui.complex;

import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.primitives.DoubleUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.primitives.IntegerUIParameter;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 26, 2007
 * Time: 11:48:02 PM <br/>
 * UI component for a Double variable parameter. Is a composite of some other
 * {@link edu.sharif.ce.dml.common.parameters.ui.NewUIParameter} components
 */
public class VariableUI extends NewUIParameter implements Iterable {
    private final DoubleUIParameter init, step;
    /**
     * number of changes
     */
    private final IntegerUIParameter number;

    public VariableUI(String name, DoubleUIParameter init, DoubleUIParameter step, IntegerUIParameter number) {
        super(name);
        this.init = init;
        this.step = step;
        this.number = number;
        createLabel();
        box.add(init);
        box.add(step);
        box.add(number);
    }

    public VariableUI(String name, double minValue, double stepValue, double maxValue, double defaultValue) {
        this(name, new DoubleUIParameter("init", maxValue, minValue, stepValue, defaultValue),
                new DoubleUIParameter("step", maxValue, minValue, stepValue, stepValue),
                new IntegerUIParameter("number", Integer.MAX_VALUE, 0, 1, 0));
    }

    public DoubleUIParameter getInit() {
        return init;
    }

    public DoubleUIParameter getStep() {
        return step;
    }

    public IntegerUIParameter getNumber() {
        return number;
    }

    /**
     * @return this object
     */
    public Object getValue() {
        return this;
    }

    //todo: this section should be removed and use parameter codes
    /**
     * @return iterator that starts from <tt>init</tt> value and runs for <tt>number</tt> times, and
     * adds <tt>step</tt> value each time.
     */
    public Iterator iterator() {
        return new VariableIterator();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        init.setEnabled(enabled);
        step.setEnabled(enabled);
        number.setEnabled(enabled);
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
