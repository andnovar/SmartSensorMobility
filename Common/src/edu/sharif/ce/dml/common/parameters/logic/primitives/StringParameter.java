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
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.primitives.StringUIParameter;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 11, 2007
 * Time: 5:45:32 PM<br>
 * string parameter.<br/>
 * Assumes input dataparameters structure as:<br/>
 * <ul><li>{@link edu.sharif.ce.dml.common.parameters.data.StringDataParameter}</il></ul>
 */
public class StringParameter extends Parameter {
    private String value;

    public StringParameter(String name, String value) {
        super(name);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(Object v) throws InvalidParameterInputException {
        value = (String) v;
    }

    public void setInitData(GeneralDataParameter dataParameter) throws InvalidParameterInputException {
        super.setInitData(dataParameter);
        value = ((StringDataParameter) dataParameter).getValue();
    }

    public void updateUIParameter(NewUIParameter uiParameter) throws InvalidParameterInputException {
        ((StringUIParameter) uiParameter).setValue(getValue());
    }

    public NewUIParameter getUIParameter(boolean showInternalParameterables) {
        return new StringUIParameter(getName(), value);
    }

    public void setUIParameterValue(NewUIParameter uiParameter) throws InvalidParameterInputException {
        setValue(uiParameter.toString());
    }
}
