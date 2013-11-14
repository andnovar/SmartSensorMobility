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

package edu.sharif.ce.dml.common.parameters.ui.primitives;

import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.ui.InvalidUIParameterInputException;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;

import javax.swing.*;
import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2007
 * Time: 2:35:18 PM<br/>
 * a UI component that uses a {@link javax.swing.JSpinner} object which validates the value using <tt>min</tt> and <tt>max</tt>.
 */
public class DoubleUIParameter extends NewUIParameter {
    private final JSpinner valueSpn;

    public DoubleUIParameter(String name, double max, double min, double step, double defaultValue) {
        super(name);
        JLabel l = createLabel();
        valueSpn = new JSpinner();
        box.add(valueSpn);
        l.setLabelFor(valueSpn);
        valueSpn.setModel(new SpinnerNumberModel(defaultValue, min, max, step));
        valueSpn.setPreferredSize(new Dimension(50, 18));
        if (step == 0) {
            valueSpn.setEnabled(false);
        }
    }

    public DoubleUIParameter(String name) {
        this(name, Double.MAX_VALUE, -Double.MAX_VALUE, 0.1, 0d);
    }

    public Double getValue() {
        return (Double) valueSpn.getValue();
    }

    /**
     * @param v a Double object
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public void setValue(Object v) throws InvalidUIParameterInputException {
        try {
            valueSpn.setValue(v);
        } catch (Exception e) {
            throw new InvalidUIParameterInputException("Invalid double value " + v + " in UIParameter with name=" + getName(),e);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        valueSpn.setEnabled(enabled);
    }

    public String toString() {
        return getValue().toString();
    }

    public void setStringValue(String s) throws InvalidUIParameterInputException {
        setValue(new Double(s));
    }

}
