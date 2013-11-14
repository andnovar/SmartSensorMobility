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
import edu.sharif.ce.dml.common.parameters.ui.primitives.BooleanUIParameter;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 23, 2007
 * Time: 12:50:19 PM<br/>
 * boolean parameter.<br/> use "yes", "YES", "Yes", "Y, "y", "t","T" , "1", "True", "TRUE" or "true" as
 * <tt>true</tt> and null and others as <tt>false</tt>.
 * Assumes input dataparameters structure as:<br/>
 * <ul><li>{@link edu.sharif.ce.dml.common.parameters.data.StringDataParameter}</il></ul>
 */
public class BooleanParameter extends Parameter {
    /**
     * value object.
     */
    private boolean value;

    public BooleanParameter(String name, boolean value) {
        super(name);
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Object v) throws InvalidParameterInputException {
        try {
            value = (Boolean) v;
        } catch (Exception e) {
            throw new InvalidParameterInputException("Boolean input parameter type exception: "+v.getClass(),getName(),v);
        }
    }

    /**
     * parses the dataParameter's value as a {@link edu.sharif.ce.dml.common.parameters.data.StringDataParameter} object
     * @param dataParameter
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     */
    public void setInitData(GeneralDataParameter dataParameter) throws InvalidParameterInputException {
        super.setInitData(dataParameter);
        if (dataParameter instanceof StringDataParameter) {
            setStringValue(((StringDataParameter) dataParameter).getValue());
        } else {
            throw new InvalidParameterInputException("Initialization dataparameter is not an instance of StringDataParameter but is "+
                    dataParameter.getClass(),getName(),dataParameter);
        }
    }

    public void updateUIParameter(NewUIParameter uiParameter) throws InvalidParameterInputException {
        try {
            ((BooleanUIParameter) uiParameter).setValue(getValue());
        } catch (InvalidUIParameterInputException e) {
            throw new InvalidParameterInputException("UI update exception: "+e.getMessage(),getName(),getValue(),e);
        }
    }

    public void setStringValue(String S) throws InvalidParameterInputException {
        if (S != null) {
            setValue(convertBoolean(S));
            return;
        }
        setValue(false);
    }

    /**
     * use <tt>s.lowercase()</tt> "yes", "y", "t", "1", or "true" as
     * <tt>true</tt> and null and others as <tt>false</tt>.
     * @param s
     * @return
     */
    public static boolean convertBoolean(String s) {
        if (s==null){
            return false;
        }
        s = s.toLowerCase();
        return s.equals("t") || s.equals("yes") || s.equals("y") || s.equals("true") ||
                s.equals("1");
    }

    public NewUIParameter getUIParameter(boolean showInternalParameterables) {
        return new BooleanUIParameter(getName(), getValue());
    }

    public void setUIParameterValue(NewUIParameter uiParameter) {
        value = (Boolean) uiParameter.getValue();
    }
}
