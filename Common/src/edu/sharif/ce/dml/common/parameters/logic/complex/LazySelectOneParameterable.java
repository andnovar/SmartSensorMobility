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
 * Date: Dec 3, 2007
 * Time: 11:40:52 PM<br>
 */
public class LazySelectOneParameterable extends Parameter implements HasInternalParameterable {
    //todo
    protected Map<String, Parameterable> choices = new TreeMap<String, Parameterable>();
    protected List<String> unLazyChoices = new LinkedList<String>();
    protected Parameterable selected;
    protected ParameterableData parameterableData;
    private String selectedName;
    private boolean initialized = false;

    public LazySelectOneParameterable() {
        super("");
    }

    public Parameterable getValue() {
        return selected;
    }

    public void setValue(Object v) throws InvalidParameterInputException {
//        if (!(v instanceof Parameter && choices.containsValue(v))) {
//            throw new InvalidParameterInputException("invalid choice");
//        }
        selected = (Parameterable) v;
    }

    @Override
    public String toString() {
        return getName() + " (" + super.toString() + ")";
    }

    public List<Parameterable> loadChoices() {
        if (!initialized) {
            initialized = true;
            //initiate choices
            try {
                Map<String, GeneralDataParameter> params = parameterableData.getParameters();
                CompositeDataParameter lazyChoices = ((CompositeDataParameter) params.get("choices")).getLazyComposite();
                if (lazyChoices != null) {
                    for (GeneralDataParameter generalDataParameter : lazyChoices.getParameters().values()) {
                        if (!generalDataParameter.getName().equals(selectedName)) {
                            Parameterable parameterable = ParameterableImplement.instantiate((ParameterableData) generalDataParameter, choices.get(generalDataParameter.getName()));
                            choices.put(generalDataParameter.getName(), parameterable);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                //todo handle this
            }
        }
        return new LinkedList<Parameterable>(choices.values());
    }

    public void setInitData(GeneralDataParameter dataParameter) throws InvalidParameterInputException {
        super.setInitData(dataParameter);
        this.parameterableData = (ParameterableData) dataParameter;
        //only create selected

        Map<String, GeneralDataParameter> params = ((ParameterableData) dataParameter).getParameters();
        CompositeDataParameter lazyreferencecomposite = ((CompositeDataParameter) params.get("choices")).getLazyComposite();
        selectedName = ((StringDataParameter) params.get("selected")).getValue();
        //if lazy composite has been set load lazy
        if (lazyreferencecomposite != null) {
            //get parameters from passed dataParameter
            //retrieve selected parameterable from referenced lazycomposite
            ParameterableData selectedParameterable = (ParameterableData) lazyreferencecomposite.getParameters().get(selectedName);
            {
                Parameterable parameterable = ParameterableImplement.instantiate(selectedParameterable, choices.get(selectedParameterable.getName()));
                choices.put(selectedParameterable.getName(), parameterable);
            }
        }
        // use inner tags
        for (GeneralDataParameter generalDataParameter : ((CompositeDataParameter) params.get("choices")).getParameters().values()) {
            Parameterable parameterable = ParameterableImplement.instantiate((ParameterableData) generalDataParameter,
                    choices.get(generalDataParameter.getName()));
            choices.put(generalDataParameter.getName(), parameterable);
            unLazyChoices.add(generalDataParameter.getName());
        }
        selected = choices.get(selectedName);

    }

    public NewUIParameter getUIParameter(boolean showInternalParameterables) {
        return new SelectOneUIParameter(getName(), loadChoices(), selected, true, showInternalParameterables);
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
     * @param verbose
     * @return in verbose mode it sets the lazy composite for its choices
     */
    public GeneralDataParameter getDataParameters(boolean verbose) {
        //put only selected and selected in choices;
        //create parameterable data for selected
        ParameterableData outputParameterableData = new ParameterableData();
        outputParameterableData.setParameterableClass(getClass());
        outputParameterableData.setName(getName());
        CompositeDataParameter choicesParameterableData = new CompositeDataParameter();
        {
            choicesParameterableData.setName("choices");
            Map<String, GeneralDataParameter> choicesDataParameterMap = new HashMap<String, GeneralDataParameter>();
            if (verbose) {
                choicesParameterableData.setLazyComposite(((CompositeDataParameter) (this.parameterableData.getParameters().get("choices"))).getLazyComposite());
                loadChoices();
                for (String unLazyChoice : unLazyChoices) {
                    choicesDataParameterMap.put(unLazyChoice, ParameterableParameter.getDataParameters(choices.get(unLazyChoice), verbose));
                }
            }
            choicesDataParameterMap.put(selected.toString(), ParameterableParameter.getDataParameters(selected, verbose));
            choicesParameterableData.setParameters(choicesDataParameterMap);
        }
        Map<String, GeneralDataParameter> parametersMap = new HashMap<String, GeneralDataParameter>();
        parametersMap.put("choices", choicesParameterableData);
        parametersMap.put("selected", new StringDataParameter("selected", selected.toString()));

        outputParameterableData.setParameters(parametersMap);
        return outputParameterableData;
    }

    public List<Parameterable> getInternalParamterable() {
        ArrayList<Parameterable> output = new ArrayList<Parameterable>();
        output.add(getValue());
        return output;
    }

    public boolean hasMultipleInternalParamterable() {
        return false;
    }
}
