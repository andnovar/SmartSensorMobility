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
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.HasInternalParameterable;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.logic.primitives.BooleanParameter;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.complex.MultipleSelectUIParameter;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 20, 2007
 * Time: 8:21:29 PM<br>
 * An intermediate parameter that has multiple choices, And user may select none, one or many of them. <br/>
 * assumes input dataParameter structure as:<br/>
 * <ul><li>{@link edu.sharif.ce.dml.common.parameters.data.ParameterableData}</li><ul>
 * <li>{@link edu.sharif.ce.dml.common.parameters.data.CompositeDataParameter}: name="choices"</li>
 * <li>{@link edu.sharif.ce.dml.common.parameters.data.CompositeDataParameter}: name="selected"
 * (if varialenumber is true it should have parameterableData objects otherwise it may have StringDataParameter objects.)</li>
 * <li>{@link edu.sharif.ce.dml.common.parameters.data.StringDataParameter}: name="variablenumber"</li></ul></ul>
 */
public class MultipleSelectParameter extends Parameter implements HasInternalParameterable {
    /**
     * all value choices.
     */
    private Map<String, Parameterable> choices = new TreeMap<String, Parameterable>();
    /**
     * selected choices.
     */
    private List<Parameterable> selected = new LinkedList<Parameterable>();
    /**
     * if user selection has upper bound.
     */
    private BooleanParameter variableNumberParameter;
    /**
     * ui component, used for intecsection in setting selected objects
     */
    private MultipleSelectUIParameter multipleSelectUIParameter;

    public MultipleSelectParameter(String name) {
        this(name,false);
    }

    public MultipleSelectParameter(String name,boolean variableNumber) {
        super(name);
        variableNumberParameter = new BooleanParameter("variablenumber", variableNumber);
    }

    public Object getValue() {
        return selected;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean hasMultipleInternalParamterable() {
        return true;
    }

    /**
     * @param v list of {@link edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable} objects
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public void setValue(Object v) throws InvalidParameterInputException {
        try {
            //todo validate
            selected = (List<Parameterable>) v;

        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidParameterInputException(e.getMessage(),getName(),v);
        }
    }

    public void addChoice(Parameterable parameterable) {
        choices.put(parameterable.toString(), parameterable);
    }

    public void setInitData(GeneralDataParameter dataParameter) throws InvalidParameterInputException {
        super.setInitData(dataParameter);
        selected = new LinkedList<Parameterable>();
        Map<String, GeneralDataParameter> params = ((ParameterableData) dataParameter).getParameters();
        variableNumberParameter.setInitData(params.get("variablenumber"));

        for (GeneralDataParameter generalDataParameter : ((CompositeDataParameter) params.get("choices")).getParameters().values()) {
            Parameterable parameterable = ParameterableImplement.instantiate((ParameterableData) generalDataParameter, choices.get(generalDataParameter.getName()));
            choices.put(generalDataParameter.getName(), parameterable);
        }
        for (GeneralDataParameter selectedDataParameter : ((CompositeDataParameter) params.get("selected")).getParameters().values()) {
            Parameterable parameterableInstance;
            if (isVariable()) {
                parameterableInstance = ParameterableImplement.instantiate((ParameterableData) selectedDataParameter, null);
            } else {
                parameterableInstance = choices.get(((StringDataParameter) selectedDataParameter).getValue());
            }
            selected.add(parameterableInstance);
        }
    }

    /**
     * manages to maintain last selected objects that is also in new choices using equals() method
     *
     * @param choices
     */
    public void setChoices(Map<String, Parameterable> choices) {
        this.choices = choices;
        if (multipleSelectUIParameter != null) {
            // selected = multipleSelectUIParameter.getValue();
            setUIParameterValue(multipleSelectUIParameter);
        }
        selected.retainAll(choices.values());
//update uiparameter if any
        if (multipleSelectUIParameter != null) {
            multipleSelectUIParameter.updateValues(choices, selected);
        }
    }

    public Map<String, Parameterable> getChoices() {
        return choices;
    }

    public List<Parameterable> getSelected() {
        return selected;
    }

    public NewUIParameter getUIParameter(boolean showInternalParameterables) {
        multipleSelectUIParameter = new MultipleSelectUIParameter(getName(), choices, selected, isVariable(),showInternalParameterables);
        return multipleSelectUIParameter;
    }

    public void setUIParameterValue(NewUIParameter uiParameter) {
        selected = ((MultipleSelectUIParameter) uiParameter).getValue();
        if (!isVariable()) {
            //update choices
            for (String s : choices.keySet()) {
                for (Parameterable parameterable : selected) {
                    if (s.equals(parameterable.toString()) && choices.containsKey(parameterable.toString())) {
                        choices.put(parameterable.toString(), parameterable);
                        break;
                    }
                }
            }
        }
    }

    public void updateUIParameter(NewUIParameter uiParameter) throws InvalidParameterInputException {
        ((MultipleSelectUIParameter) uiParameter).updateValues(choices, selected);
    }

    /**
     * @param verbose
     * @return
     */
    public GeneralDataParameter getDataParameters(boolean verbose) {
        //put only selected and selected in choices;
        //create parameterable data for selected

        ParameterableData parameterableData = new ParameterableData();
        parameterableData.setParameterableClass(getClass());
        parameterableData.setName(getName());
        CompositeDataParameter choicesCompositeData = new CompositeDataParameter();
        CompositeDataParameter selectedCompositeData = new CompositeDataParameter();
        {
            choicesCompositeData.setName("choices");
            selectedCompositeData.setName("selected");
            Map<String, GeneralDataParameter> choicesDataParameterMap = new HashMap<String, GeneralDataParameter>();
            Map<String, GeneralDataParameter> selectedDataParameterMap = new HashMap<String, GeneralDataParameter>();
            if (verbose) {
                if (isVariable()) {
                    for (Parameterable parameterable : selected) {
                        selectedDataParameterMap.put(parameterable.toString(), ParameterableParameter.getDataParameters(parameterable, verbose));
                    }
                    for (Parameterable parameterable : choices.values()) {
                        choicesDataParameterMap.put(parameterable.toString(), ParameterableParameter.getDataParameters(parameterable, verbose));
                    }
                } else {
                    int i = 0;
                    for (Parameterable parameterable : selected) {
                        //choicesDataParameterMap.put(parameterable.toString(), ParameterableParameter.getDataParameters(parameterable, verbose));
                        selectedDataParameterMap.put(i++ + "", new StringDataParameter(i++ + "", parameterable.toString()));
                    }
                    //load all choices
                    for (Parameterable parameterable : choices.values()) {
                        choicesDataParameterMap.put(parameterable.toString(), ParameterableParameter.getDataParameters(parameterable, verbose));
                    }
                }
            } else {//load only objects that are in the selected
                if (isVariable()) {
                    int i = 0;
                    for (Parameterable parameterable : selected) {
                        selectedDataParameterMap.put(parameterable.toString(), ParameterableParameter.getDataParameters(parameterable, verbose));
                    }
                } else {
                    //load only selected
                    int i = 0;
                    for (Parameterable parameterable : selected) {
                        choicesDataParameterMap.put(parameterable.toString(), ParameterableParameter.getDataParameters(parameterable, verbose));
                        selectedDataParameterMap.put(i++ + "", new StringDataParameter(i++ + "", parameterable.toString()));
                    }
                }
            }
            selectedCompositeData.setParameters(selectedDataParameterMap);
            choicesCompositeData.setParameters(choicesDataParameterMap);

        }

        Map<String, GeneralDataParameter> parametersMap = new HashMap<String, GeneralDataParameter>();
        parametersMap.put("choices", choicesCompositeData);
        parametersMap.put("selected", selectedCompositeData);
//note above comment
        parametersMap.put("variablenumber", new StringDataParameter(variableNumberParameter.getName(), isVariable().toString()));
//variableNumberParameter.getDataParameters());

        parameterableData.setParameters(parametersMap);
        return parameterableData;
    }

    private Boolean isVariable() {
        return variableNumberParameter.getValue();
    }

    public List<Parameterable> getInternalParamterable() {
        return (List<Parameterable>) getValue();
    }
}
