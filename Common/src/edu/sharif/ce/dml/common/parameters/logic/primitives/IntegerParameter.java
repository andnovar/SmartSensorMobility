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
import edu.sharif.ce.dml.common.parameters.ui.primitives.IntegerUIParameter;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2007
 * Time: 4:50:18 PM<br/>
 * integer parameter.<br/>
 * Assumes input dataparameters structure as:<br/>
 * <ul><li>{@link edu.sharif.ce.dml.common.parameters.data.StringDataParameter}</il></ul>
 */
public class IntegerParameter extends Parameter {
    private int value;
    private int step;
    private int minValue;
    private int maxValue;

    /**
     * @param name
     * @param maxValue maximum possible integer value
     * @param minValue minimum possible integer value
     * @param step     proposed integer step
     * @param value    default value
     */
    public IntegerParameter(String name, int maxValue, int minValue, int step, int value) {
        super(name);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.step = step;
        this.value = value;
    }

    /**
     * assumes <code>maximum</code>={@link Integer#MAX_VALUE}, <code>minimum</code>=0, and <code>step</code>=0
     *
     * @param name
     * @param value
     */
    public IntegerParameter(String name, int value) {
        this(name, Integer.MAX_VALUE, 0, 1, value);
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Object v) throws InvalidParameterInputException {
        try {
            this.value = (Integer) v;
        } catch (Exception e) {
            throw new InvalidParameterInputException("Integer input Parameter type exception: " + v.getClass(), getName(), v);
        }
        if (value < minValue || value > maxValue){
            throw new InvalidParameterInputException("Integer input Parameter out of range [" +
                    minValue + ", " + maxValue + "]  exception: " + v.getClass(), getName(), v);
        }
    }

    public void setInitData(GeneralDataParameter dataParameter) throws InvalidParameterInputException {
        super.setInitData(dataParameter);
        if (dataParameter instanceof StringDataParameter) {
            setStringValue(((StringDataParameter) dataParameter).getValue());
        } else {
            throw new InvalidParameterInputException("Initialization dataparamter is not an instance of StringDataParameter but is " +
                    dataParameter.getClass(), getName(), dataParameter);
        }
    }

    public void updateUIParameter(NewUIParameter uiParameter) throws InvalidParameterInputException {
        try {
            ((IntegerUIParameter) uiParameter).setValue(getValue());
        } catch (InvalidUIParameterInputException e) {
            throw new InvalidParameterInputException("UI update exception: " + e.getMessage(), getName(), getValue(), e);
        }
    }


    public void setStringValue(String s) throws InvalidParameterInputException {
        setValue(new Integer(s));
    }


    public int getMaxValue() {
        return maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getStep() {
        return step;
    }

    public NewUIParameter getUIParameter(boolean showInternalParameterables) {
        return new IntegerUIParameter(getName(), getMaxValue(), getMinValue(),
                getStep(), getValue());
    }

    public void setUIParameterValue(NewUIParameter uiParameter) throws InvalidParameterInputException {
        setValue(uiParameter.getValue());
    }
}
