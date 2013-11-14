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
import edu.sharif.ce.dml.common.parameters.ui.primitives.IntegerArrayUIParameter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2007
 * Time: 4:50:41 PM<br/>
 * Integer Array parameter.<br/>
 * Assumes input dataparameters structure as:<br/>
 * <ul><li>{@link edu.sharif.ce.dml.common.parameters.data.StringDataParameter}</il></ul>
 */
public class IntegerArrayParameter extends Parameter {
    /**
     * array buffer.
     */
    private int[] data;

    public IntegerArrayParameter(String name, int[] value) {
        super(name);
        data = value;
    }

    public IntegerArrayParameter(String name, Integer[] value) {
        super(name);
        data = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            data[i] = value[i];
        }
    }

    /**
     * @return an <code>int[]</code> object.
     */
    public Object getValue() {
        return data;
    }

    /**
     * manages to use <tt>v</tt> as <code>int[]</code> or <code>Integer[]</code>
     *
     * @param v
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public void setValue(Object v) throws InvalidParameterInputException {
        try {
            data = (int[]) v;
        } catch (Exception e) {
            try {
                Integer[] tempData = (Integer[]) v;
                data = new int[tempData.length];
                for (int i = 0; i < tempData.length; i++) {
                    data[i] = tempData[i];

                }
            } catch (Exception e1) {
                throw new InvalidParameterInputException("Integer Array Parameter input type exception: " + v.getClass(),
                        getName(), v);
            }
        }
    }

    /**
     * parses the string value of <tt>dataParameter</tt> using {@link #setStringValue(String)} method.
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
            throw new InvalidParameterInputException("Initialization dataparamter is not an instance of StringDataParameter but is " +
                    dataParameter.getClass(), getName(), dataParameter);
        }
    }

    public void updateUIParameter(NewUIParameter uiParameter) throws InvalidParameterInputException {
        try {
            ((IntegerArrayUIParameter) uiParameter).setValue(getValue());
        } catch (InvalidUIParameterInputException e) {
            throw new InvalidParameterInputException("UI update exception: "+e.getMessage(),getName(),getValue(),e);
        }
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Integer aInteger : (int[]) getValue()) {
            sb.append(aInteger).append(ARRAY_SEPARATOR);
        }
        return sb.toString();

    }

    /**
     * parses string <tt>s</tt> by extracting only digits, {@link #ARRAY_SEPARATOR} and "-".
     * Default is 0.
     *
     * @param s
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public void setStringValue(String s) throws InvalidParameterInputException {
        //remove not allowed characters
        s = s.replaceAll("[^\\d" + ARRAY_SEPARATOR + "-]", "");
        List<String> parsedText = new LinkedList<String>(Arrays.asList(s.split(ARRAY_SEPARATOR)));
        for (java.util.Iterator it = parsedText.iterator(); it.hasNext();) {
            String p = (String) it.next();
            if (p.length() == 0) {
                it.remove();
            }
        }
        if (parsedText.size() == 0) {
            setValue(new Integer[]{0});
        }
        Integer[] valueObject = new Integer[parsedText.size()];
        for (int i = 0; i < parsedText.size(); i++) {
            valueObject[i] = new Integer(parsedText.get(i));
        }
        setValue(valueObject);
    }

    public NewUIParameter getUIParameter(boolean showInternalParameterables) {
        return new IntegerArrayUIParameter(getName(), getValue());
    }

    public void setUIParameterValue(NewUIParameter uiParameter) throws InvalidParameterInputException {
        setValue(uiParameter.getValue());
    }
}
