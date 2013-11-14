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

package edu.sharif.ce.dml.common.parameters.logic;


import edu.sharif.ce.dml.common.parameters.data.GeneralDataParameter;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.ui.GraphicalStandAloneObject;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 4, 2006
 * Time: 6:21:15 PM
 * <br/>represents a raw property that is extracted from configuration files
 * for every item
 */
public abstract class Parameter implements Comparable, GraphicalStandAloneObject {
    public static final String ARRAY_SEPARATOR = ",";
    private boolean visibleUI = true;
    /**
     * unique name of the parameter.
     */
    private String name;

    public Parameter(String name) {
        this.name = name;
    }

    /**
     * @param obj
     * @return if two parameter's name is equal.
     */
    public boolean equals(Object obj) {
        return name.equals(((Parameter) obj).getName());
    }

    public boolean isVisibleUI() {
        return visibleUI;
    }

    public void setVisibleUI(boolean visibleUI) {
        this.visibleUI = visibleUI;
    }

    /**
     * @return unique name of the parameter
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * compares using object name. {@link edu.sharif.ce.dml.common.parameters.logic.HasInternalParameterable}s
     * have higher periority.
     *
     * @param o
     * @return
     */
    public int compareTo(Object o) {
        if (o instanceof HasInternalParameterable) {
            if (this instanceof HasInternalParameterable) {
                return this.name.compareTo(((Parameter) o).getName());
            } else {
                return -1;
            }
        } else {
            if (this instanceof HasInternalParameterable) {
                return 1;
            } else {
                return this.name.compareTo(((Parameter) o).getName());
            }
        }
    }

    /**
     * @return parameter's value toString
     */
    public String toString() {
        return getValue().toString();
    }

    /**
     * updatets <tt>params</tt>'s parameters using <tt>dParams</tt> data parameters using
     * {@link #setInitData(edu.sharif.ce.dml.common.parameters.data.GeneralDataParameter)} method.<br/>
     * it doesn't add any parameter to <tt>params</tt> from <tt>dParams</tt>
     *
     * @param dParams
     * @param params
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public static void updateParameters(Map<String, ? extends GeneralDataParameter> dParams, Map<String, Parameter> params)
            throws InvalidParameterInputException {
        for (String name : params.keySet()) {
            GeneralDataParameter dParam = dParams.get(name);
            if (dParam != null) {
                try {
                    params.get(name).setInitData(dParam);
                } catch (InvalidParameterInputException e) {
                    e.addToParameterPath(name);
                    throw e;
                }
            } else {
                //  DevelopmentLogger.logger.info("parameter with name " + name + " not found.");
            }
        }
    }

    /**
     * updatets <tt>params</tt>'s parameters using <tt>dParams</tt> data parameters using
     * {@link #setInitData(edu.sharif.ce.dml.common.parameters.data.GeneralDataParameter)} method.<br/>
     *
     * @param dParams
     * @param params
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public static void updateParameters(List<? extends GeneralDataParameter> dParams, Map<String, Parameter> params)
            throws InvalidParameterInputException {
        for (GeneralDataParameter dParam : dParams) {
            Parameter param = params.get(dParam.getName());
            if (param != null) {
                try {
                    param.setInitData(dParam);
                } catch (InvalidParameterInputException e) {
                    e.addToParameterPath(dParam.getName());
                    throw e;
                }
            }
        }
    }

    /**
     * @param params
     * @return all dataParameters in <tt>params</tt> map.
     */
    public static List<StringDataParameter> getFlatDataParameters(Map<String, Parameter> params) {
        List<GeneralDataParameter> dataParameters = new LinkedList<GeneralDataParameter>();
        for (Parameter parameter : params.values()) {
            dataParameters.add(parameter.getForFlatDataParameters());
        }
        List<StringDataParameter> flatDataParameters = new LinkedList<StringDataParameter>();
        for (GeneralDataParameter generalDataParameter : dataParameters) {
            flatDataParameters.addAll(generalDataParameter.flat());
        }
        return flatDataParameters;
    }

    public GeneralDataParameter getForFlatDataParameters() {
        return getDataParameters(false);
    }

    /**
     * @return {@link #getName()}'s hashcod
     */
    public int hashCode() {
        return name.hashCode();
    }

    public abstract Object getValue();

    /**
     * initializes this parameter using <tt>dataParameter</tt>.<br/>
     * loading parameter's name by default.
     *
     * @param dataParameter
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public void setInitData(GeneralDataParameter dataParameter) throws InvalidParameterInputException {
        setName(dataParameter.getName());
    }

    /**
     * @param verbose
     * @return data view of the parameter. returns a {@link edu.sharif.ce.dml.common.parameters.data.StringDataParameter}
     *         from {@link #getValue()} method by default. it should not be used to create xml in verbose mode
     */
    public GeneralDataParameter getDataParameters(boolean verbose) {
        return new StringDataParameter(getName(), toString());
    }

}