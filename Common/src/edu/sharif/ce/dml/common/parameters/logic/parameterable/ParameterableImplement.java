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

package edu.sharif.ce.dml.common.parameters.logic.parameterable;

import edu.sharif.ce.dml.common.parameters.data.GeneralDataParameter;
import edu.sharif.ce.dml.common.parameters.data.ParameterableData;
import edu.sharif.ce.dml.common.parameters.logic.HasInternalParameterable;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.ClassInstantiationException;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Dec 15, 2007
 * Time: 7:54:04 PM<br>
 * An abstract subclass of Paramterable. Has some facilities to manage Parameterable name
 * and some algorithms related to the class.
 */
public abstract class ParameterableImplement implements Parameterable {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    /**
     * instantiates a parameterable object according to <tt>parameterableData</tt> object.
     * if <tt>lastParameterable</tt> was null it will create it, otherwise it only updates <tt>lastParameterable</tt> parameters
     *
     * @param parameterableData
     * @param lastParameterable it can be null
     * @return
     */
    public static Parameterable instantiate(ParameterableData parameterableData, Parameterable lastParameterable) throws InvalidParameterInputException, ClassInstantiationException {
        Parameterable parameterable = lastParameterable;
        try {
            if (parameterable == null) {
                parameterable =
                        (Parameterable)
                                parameterableData.getParameterableClass().
                                        getConstructor().newInstance();
            }
            Map<String, Parameter> parameters = parameterable.getParameters();
            Parameter.updateParameters(parameterableData.getParameters(), parameters);
            parameterable.setParameters(parameters);
            parameterable.setName(parameterableData.getName());
        } catch (InstantiationException e) {
            throw new ClassInstantiationException(e.getMessage(), parameterableData.getName(), parameterableData.getParameterableClass(), e);
        } catch (IllegalAccessException e) {
            throw new ClassInstantiationException(e.getMessage(), parameterableData.getName(), parameterableData.getParameterableClass(), e);
        } catch (InvocationTargetException e) {
            throw new ClassInstantiationException(e.getMessage(), parameterableData.getName(), parameterableData.getParameterableClass(), e);
        } catch (NoSuchMethodException e) {
            throw new ClassInstantiationException("Default constructor not found with message: " + e.getMessage()
                    , parameterableData.getName(), parameterableData.getParameterableClass(), e);
        }
        return parameterable;
    }

    public static Parameterable cloneParameterable(Parameterable parameterableTemplate, String name) throws InvalidParameterInputException, ClassInstantiationException {
        Parameterable parameterableInstance = null;
        try {
            parameterableInstance = parameterableTemplate.getClass().
                    getConstructor().newInstance();

            {
                // should be deep clone
                GeneralDataParameter generalDataParameter = ParameterableParameter.getDataParameters(parameterableTemplate, true);
                ParameterableImplement.instantiate((ParameterableData) generalDataParameter, parameterableInstance);
                parameterableInstance.setName(name);
            }
        } catch (InstantiationException e) {
            throw new ClassInstantiationException(e.getMessage(), name, parameterableTemplate.getClass(), e);
        } catch (IllegalAccessException e) {
            throw new ClassInstantiationException(e.getMessage(), name, parameterableTemplate.getClass(), e);
        } catch (InvocationTargetException e) {
            throw new ClassInstantiationException(e.getMessage(), name, parameterableTemplate.getClass(), e);
        } catch (NoSuchMethodException e) {
            throw new ClassInstantiationException("Default constructor not found with message: " + e.getMessage()
                    , name, parameterableTemplate.getClass(), e);
        }
        return parameterableInstance;
    }

    public static void resetParameters(Parameterable parameterable) {
        Map<String, Parameter> parameters = parameterable.getParameters();
        for (Parameter parameter : parameters.values()) {
            if (parameter instanceof HasInternalParameterable){
                for (Parameterable parameterable2: ((HasInternalParameterable) parameter).getInternalParamterable()) {
                    resetParameters(parameterable2);
                }
            }
        }
        try {
            parameterable.setParameters(parameters);
        } catch (InvalidParameterInputException e) {
            e.printStackTrace();  //it should not happen
        }
    }

    public int compareTo(Object o) {
        return this.toString().compareTo(o.toString());
    }

}
