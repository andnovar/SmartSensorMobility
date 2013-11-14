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

package edu.sharif.ce.dml.common.parameters.logic.primitives;

import edu.sharif.ce.dml.common.parameters.data.GeneralDataParameter;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.ui.InvalidUIParameterInputException;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.primitives.DoubleUIParameter;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2007
 * Time: 4:50:30 PM<br/>
 * double parameter.<br/>
 * Assumes input dataparameters structure as:<br/>
 * <ul><li>{@link edu.sharif.ce.dml.common.parameters.data.StringDataParameter}</il></ul>
 */
public class DoubleParameter extends Parameter {
    private double value;
    private double step;
    private double minValue;
    private double maxValue;


    /**
     * @param name
     * @param maxValue maximum possible double value
     * @param minValue minimum possible double value
     * @param step     proposed double step
     * @param value    default value
     */
    public DoubleParameter(String name, double maxValue, double minValue, double step, double value) {
        super(name);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.step = step;
        this.value = value;
    }

    /**
     * used 0 as minimum, {@link Double#MAX_VALUE} as maximum, and 0.1 as proposed step.
     *
     * @param name
     * @param value default value
     */
    public DoubleParameter(String name, double value) {
        this(name, Double.MAX_VALUE, 0, 0.1, value);
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Object v) throws InvalidParameterInputException {
        try {
            this.value = (Double) v;
        } catch (Exception e) {
            throw new InvalidParameterInputException("Double input Parameter type exception: " + v.getClass(), getName(), v);
        }
        if (value < minValue || value > maxValue) {
            throw new InvalidParameterInputException("Double input Parameter out of range [" +
                    minValue + ", " + maxValue + "]  exception: " + v.getClass(), getName(), v);
        }
    }

    /**
     * parses dataparameter.getValue using {@link Double#parseDouble(String)}
     *
     * @param dataParameter
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public void setInitData(GeneralDataParameter dataParameter) throws InvalidParameterInputException {
        super.setInitData(dataParameter);
        if (dataParameter instanceof StringDataParameter) {
            setStringValue(((StringDataParameter) dataParameter).getValue());
        } else {
            throw new InvalidParameterInputException("Initialization dataparameter is not an instance of StringDataParameter but is " +
                    dataParameter.getClass(), getName(), dataParameter);
        }
    }

    public void updateUIParameter(NewUIParameter uiParameter) throws InvalidParameterInputException {
        try {
            ((DoubleUIParameter) uiParameter).setValue(getValue());
        } catch (InvalidUIParameterInputException e) {
            throw new InvalidParameterInputException("UI update exception: " + e.getMessage(), getName(), getValue(), e);
        }
    }

    public void setStringValue(String s) throws InvalidParameterInputException {
        setValue(new Double(s));
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getStep() {
        return step;
    }

    public NewUIParameter getUIParameter(boolean showInternalParameterables) {
        return new DoubleUIParameter(getName(), getMaxValue(), getMinValue(),
                getStep(), getValue());
    }

    public void setUIParameterValue(NewUIParameter uiParameter) throws InvalidParameterInputException {
        setValue(uiParameter.getValue());
    }
}
