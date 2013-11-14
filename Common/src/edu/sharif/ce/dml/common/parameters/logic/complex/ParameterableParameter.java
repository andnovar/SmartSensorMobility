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

package edu.sharif.ce.dml.common.parameters.logic.complex;

import edu.sharif.ce.dml.common.parameters.data.CompositeDataParameter;
import edu.sharif.ce.dml.common.parameters.data.GeneralDataParameter;
import edu.sharif.ce.dml.common.parameters.data.ParameterableData;
import edu.sharif.ce.dml.common.parameters.logic.HasInternalParameterable;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.ui.GraphicalStandAloneObject;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.complex.GeneralUIParameter;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 28, 2007
 * Time: 1:37:25 PM<br>
 * An intermediate class that has only one {@link edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable} inside.<br/>
 * it assumes init data has this structure:<br/>
 * <ul><li>{@link edu.sharif.ce.dml.common.parameters.data.ParameterableData}</li><ul>
 * <li>{@link edu.sharif.ce.dml.common.parameters.data.CompositeDataParameter}</li></ul></ul>
 */
public class ParameterableParameter extends Parameter implements HasInternalParameterable {
    private Parameterable parameterable;

    public ParameterableParameter() {
        super("");
    }

    /**
     * push a parameterable into the object and sets the name of the parameter as <tt>name</tt>
     * @param name this parameter name.
     * @param parameterable
     */
    public ParameterableParameter(String name, Parameterable parameterable) {
        super(name);
        this.parameterable = parameterable;
    }



    /**
     * if the internal parameterable can handle its UI ({@link edu.sharif.ce.dml.common.parameters.ui.GraphicalStandAloneObject} implementation)
     * its UI will be passed, otherwise it uses {@link edu.sharif.ce.dml.common.parameters.ui.complex.GeneralUIParameter}
     * to construct the internal parameterable UI.
     * @return
     * @param showInternalParameterables
     */
    public NewUIParameter getUIParameter(boolean showInternalParameterables) {
        if (parameterable instanceof GraphicalStandAloneObject) {
            return ((GraphicalStandAloneObject) parameterable).getUIParameter(true);
        } else {
            return new GeneralUIParameter(getName(), parameterable.getParameters().values(),showInternalParameterables);
        }
    }

    public void setUIParameterValue(NewUIParameter uiParameter) throws InvalidParameterInputException {
        if (parameterable instanceof GraphicalStandAloneObject) {
            ((GraphicalStandAloneObject) parameterable).setUIParameterValue(uiParameter);
        } else {
            ((GeneralUIParameter) uiParameter).updateParameter(parameterable);
        }
    }

    /**
     * Extracts internal parameterable's data parameters using {@link #getDataParameters(edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable,boolean)}
     * @return
     * @param verbose
     */
    public GeneralDataParameter getDataParameters(boolean verbose) {
        ParameterableData parameterableData = new ParameterableData();
        parameterableData.setParameterableClass(getClass());
        parameterableData.setName(getName());
        CompositeDataParameter choiceParameterableData = new CompositeDataParameter();
        {
            choiceParameterableData.setName("choice");
            Map<String, GeneralDataParameter> choiceDataParameterMap = new HashMap<String, GeneralDataParameter>();
            choiceDataParameterMap.put(parameterable.toString(), getDataParameters(parameterable, verbose));
            choiceParameterableData.setParameters(choiceDataParameterMap);
        }
        Map<String, GeneralDataParameter> parametersMap = new HashMap<String, GeneralDataParameter>();
        parametersMap.put("choice", choiceParameterableData);

        parameterableData.setParameters(parametersMap);
        return parameterableData;
    }

    public void updateUIParameter(NewUIParameter uiParameter) throws InvalidParameterInputException {
         if (parameterable instanceof GraphicalStandAloneObject) {
            ((GraphicalStandAloneObject) parameterable).updateUIParameter(uiParameter);
        } else {
            ((GeneralUIParameter) uiParameter).updateUIParameter(parameterable);
        }
    }

    /**
     * exracts dataparameters from parameters of a <tt>parameterable</tt>
     * @param parameterable
     * @param verbose
     * @return
     */
    public static GeneralDataParameter getDataParameters(Parameterable parameterable, boolean verbose) {
        ParameterableData parameterableData = new ParameterableData();
        parameterableData.setName(parameterable.toString());
        parameterableData.setParameterableClass(parameterable.getClass());
        Map<String, GeneralDataParameter> gParameters = new HashMap<String, GeneralDataParameter>();
        Map<String, Parameter> parameters = parameterable.getParameters();
        for (String s : parameters.keySet()) {
            gParameters.put(s, parameters.get(s).getDataParameters(verbose));
        }
        parameterableData.setParameters(gParameters);
        return parameterableData;
    }

    public static GeneralDataParameter getFlatDataParameters(Parameterable parameterable) {
        ParameterableData parameterableData = new ParameterableData();
        parameterableData.setName(parameterable.toString());
        parameterableData.setParameterableClass(parameterable.getClass());
        Map<String, GeneralDataParameter> gParameters = new HashMap<String, GeneralDataParameter>();
        Map<String, Parameter> parameters = parameterable.getParameters();
        for (String s : parameters.keySet()) {
            gParameters.put(s, parameters.get(s).getForFlatDataParameters());
        }
        parameterableData.setParameters(gParameters);
        return parameterableData;
    }

    public GeneralDataParameter getForFlatDataParameters() {
       return getFlatDataParameters(parameterable);
    }

    /**
     * Initialzes this parameter using <tt>dataParameter</tt>
     * @param dataParameter
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     */
    public void setInitData(GeneralDataParameter dataParameter) throws InvalidParameterInputException {
        super.setInitData(dataParameter);
        Map<String, GeneralDataParameter> params = ((ParameterableData) dataParameter).getParameters();
        for (GeneralDataParameter generalDataParameter : ((CompositeDataParameter) params.get("choice")).getParameters().values()) {
            parameterable = ParameterableImplement.instantiate((ParameterableData) generalDataParameter,parameterable);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean hasMultipleInternalParamterable() {
        return false;
    }

    /**
     *
     * @return internal parameterable
     */
    public Parameterable getValue() {
        return parameterable;
    }

    public List<Parameterable> getInternalParamterable() {
        List<Parameterable> output = new ArrayList<Parameterable>();
        output.add(getValue());
        return output;
    }
}

