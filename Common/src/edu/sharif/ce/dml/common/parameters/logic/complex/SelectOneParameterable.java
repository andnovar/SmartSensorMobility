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
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.complex.SelectOneUIParameter;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 11, 2007
 * Time: 5:30:29 PM<br>
 * An intermediate parameter which has some {@link edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable} choices and
 * at each time only one of them may be selected.<br/>
 * Assumes input dataparameters structure as:<br/>
 * <ul><li>{@link edu.sharif.ce.dml.common.parameters.data.ParameterableData}</il>
 * <ul><li>{@link edu.sharif.ce.dml.common.parameters.data.CompositeDataParameter}: name="choices"</li>
 * <li>{@link edu.sharif.ce.dml.common.parameters.data.StringDataParameter}: name="selected"</li></ul></ul>
 */
public class SelectOneParameterable extends Parameter implements HasInternalParameterable {
    /**
     * a sortedMap of choices that may be selected in this parameter.
     */
    protected Map<String, Parameterable> choices = new TreeMap<String, Parameterable>();

    /**
     * selected parameterable from choices.
     */
    protected Parameterable selected;

    /**
     * if selected parameterable's parameters should be presented on UI under this parameter configs
     */
    protected boolean showConfigs = false;


    public SelectOneParameterable() {
        super("");
    }

    /**
     * @param showConfigs if selected parameterable's parameters should be presented on UI under this parameter configs
     */
    public SelectOneParameterable(boolean showConfigs) {
        this();
        this.showConfigs = showConfigs;
    }

    public Parameterable getValue() {
        return selected;
    }

    @Override
    public String toString() {
        return getName() + " (" + super.toString() + ")";
    }

    /**
     * set selected object
     *
     * @param v should be instance of {@link edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable} and be amonge choices.
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public void setValue(Object v) throws InvalidParameterInputException {
        if (!(v instanceof Parameterable && choices.containsValue(v))) {
            throw new InvalidParameterInputException("The selected choice is not in available choices", getName(), v);
        }
        selected = (Parameterable) v;
    }

    public void setSelected(String name) throws InvalidParameterInputException {
        List<Parameterable> list = getChoices();
        for (Parameterable parameterable : list) {
            if (parameterable.toString().equals(name)) {
                selected = parameterable;
                return;
            }
        }
        throw new InvalidParameterInputException("To be selected choice not found", getName(), name);
    }


    public List<Parameterable> getChoices() {
        return new LinkedList<Parameterable>(choices.values());
    }

    /**
     * first intstanciates all choices and then selects the selected object
     *
     * @param dataParameter
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public void setInitData(GeneralDataParameter dataParameter) throws InvalidParameterInputException {
        super.setInitData(dataParameter);
        Map<String, GeneralDataParameter> params = ((ParameterableData) dataParameter).getParameters();
        for (GeneralDataParameter generalDataParameter : ((CompositeDataParameter) params.get("choices")).getParameters().values()) {
            Parameterable parameterable = ParameterableImplement.instantiate((ParameterableData) generalDataParameter,
                    choices.get(generalDataParameter.getName()));
            choices.put(generalDataParameter.getName(), parameterable);
        }
        String selectedName = ((StringDataParameter) params.get("selected")).getValue();
        selected = choices.get(selectedName);
    }

    public NewUIParameter getUIParameter(boolean showInternalParameterables) {
        return new SelectOneUIParameter(getName(), getChoices(), selected, showConfigs, showInternalParameterables);
    }

    public void setUIParameterValue(NewUIParameter uiParameter) throws InvalidParameterInputException {
        try {
            selected = (Parameterable) uiParameter.getValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidParameterInputException(e.getMessage(), getName(), uiParameter.getValue());
        }
    }

    public void updateUIParameter(NewUIParameter uiParameter) throws InvalidParameterInputException {
        //change the selected
        SelectOneUIParameter selectOneUIParameter = (SelectOneUIParameter) uiParameter;
        selectOneUIParameter.setValue(selected);
        //update all the parameterables
        selectOneUIParameter.updateParameterablePanels();
    }

    /**
     * only converts selected object not all choices.
     *
     * @param verbose put only selected and selected in choices or not
     * @return
     */
    public GeneralDataParameter getDataParameters(boolean verbose) {

        //create parameterable data for selected
        ParameterableData parameterableData = new ParameterableData();
        parameterableData.setParameterableClass(getClass());
        parameterableData.setName(getName());
        CompositeDataParameter choicesParameterableData = new CompositeDataParameter();
        {
            choicesParameterableData.setName("choices");
            Map<String, GeneralDataParameter> choicesDataParameterMap = new HashMap<String, GeneralDataParameter>();
            if (verbose) {
                for (Parameterable parameterable : choices.values()) {
                    choicesDataParameterMap.put(parameterable.toString(), ParameterableParameter.getDataParameters(parameterable, verbose));
                }
            } else {
                choicesDataParameterMap.put(selected.toString(), ParameterableParameter.getDataParameters(selected, verbose));
            }
            choicesParameterableData.setParameters(choicesDataParameterMap);
        }
        Map<String, GeneralDataParameter> parametersMap = new HashMap<String, GeneralDataParameter>();
        parametersMap.put("choices", choicesParameterableData);
        parametersMap.put("selected", new StringDataParameter("selected", selected.toString()));

        parameterableData.setParameters(parametersMap);
        return parameterableData;
    }


    public GeneralDataParameter getForFlatDataParameters() {
        //put only selected and selected in choices;
        //create parameterable data for selected
        ParameterableData parameterableData = new ParameterableData();
        parameterableData.setParameterableClass(getClass());
        parameterableData.setName(getName());
        CompositeDataParameter choicesParameterableData = new CompositeDataParameter();
        {
            choicesParameterableData.setName("choices");
            Map<String, GeneralDataParameter> choicesDataParameterMap = new HashMap<String, GeneralDataParameter>();
            choicesDataParameterMap.put(selected.toString(), ParameterableParameter.getFlatDataParameters(selected));
            choicesParameterableData.setParameters(choicesDataParameterMap);
        }
        Map<String, GeneralDataParameter> parametersMap = new HashMap<String, GeneralDataParameter>();
        parametersMap.put("choices", choicesParameterableData);
        parametersMap.put(getName(), new StringDataParameter(getName(), selected.toString()));

        parameterableData.setParameters(parametersMap);
        return parameterableData;
    }

    public List<Parameterable> getInternalParamterable() {
        List<Parameterable> output = new ArrayList<Parameterable>();
        output.add(getValue());
        return output;
    }

    public boolean hasMultipleInternalParamterable() {
        return false;
    }
}
