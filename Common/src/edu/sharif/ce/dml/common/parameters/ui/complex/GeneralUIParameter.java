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

import edu.sharif.ce.dml.common.parameters.logic.HasInternalParameterable;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 22, 2007
 * Time: 8:51:35 PM<br>
 * A general UI component that creates ui components for a {@link edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable} object.
 */
public class GeneralUIParameter extends NewUIParameter {
    /**
     * all uiparameters for the passed parameters
     */
    private List<NewUIParameter> uiParameters = new LinkedList<NewUIParameter>();
    /**
     * gridbag constraints for vertical panel of uiparameters
     */
    private static final GridBagConstraints c = new GridBagConstraints();

    static {
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1;
        c.ipady=5;
    }

    public GeneralUIParameter(String name, Collection<Parameter> parameters, boolean showInternalParameterables) {
        super(name);
        //create panel
        JPanel v = new JPanel(new GridBagLayout());
        box.add(v);
        v.setBorder(BorderFactory.createTitledBorder(name));
        parameters = new TreeSet<Parameter>(parameters);
        for (Parameter parameter : parameters) {
            if (!showInternalParameterables && parameter instanceof HasInternalParameterable){continue;}
            if (!parameter.isVisibleUI()){continue;}
            NewUIParameter uiParameter = parameter.getUIParameter(true);
            uiParameters.add(uiParameter);
            v.add(uiParameter,c);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (NewUIParameter uiParameter : uiParameters) {
            uiParameter.setEnabled(enabled);
        }
    }

    /**
     * passes the observer <tt>o</tt> to the inline parameters.
     * @param o
     */
    public void addObserver(Observer o) {
        for (NewUIParameter uiParameter : uiParameters) {
            uiParameter.addObserver(o);
        }
    }

    /**
     * updates <tt> parameterable</tt> parameters according to uiParameter values. <br/>
     * calls each <tt>parameterable</tt> prameter <tt>setUIParameterValue</tt> method using parameter.name and
     * uiparameter.name.
     * @param parameterable
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     */
    public void updateParameter(Parameterable parameterable) throws InvalidParameterInputException {
        Map<String, Parameter> parameters = parameterable.getParameters();
        for (NewUIParameter uiParameter : uiParameters) {
            parameters.get(uiParameter.getName()).setUIParameterValue(uiParameter);
        }
        parameterable.setParameters(parameters);
    }

    public void updateUIParameter(Parameterable parameterable) throws InvalidParameterInputException {
        Map<String, Parameter> parameters = parameterable.getParameters();
        for (NewUIParameter uiParameter : uiParameters) {
            parameters.get(uiParameter.getName()).updateUIParameter(uiParameter);
        }
    }

    /**
     *
     * @return null object
     */
    public Object getValue() {
        return null;
    }
}
