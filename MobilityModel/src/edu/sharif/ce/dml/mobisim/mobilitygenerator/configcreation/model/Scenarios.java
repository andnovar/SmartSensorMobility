package edu.sharif.ce.dml.mobisim.mobilitygenerator.configcreation.model;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.MultipleSelectParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: 12/24/10
 * Time: 4:20 PM<br/>
 * A wrapper around multiple scenarios
 */
public class Scenarios extends ParameterableImplement {
   private MultipleSelectParameter scenarios = new MultipleSelectParameter("scenarios",true);

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        scenarios= (MultipleSelectParameter) parameters.get("scenarios");
    }

    public Map<String, Parameter> getParameters() {
        Map<String,Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("scenarios",scenarios);
        return parameters;
    }

    public List<Scenario> getScenarios(){
        List<Scenario> output = new LinkedList<Scenario>();
        for (Parameterable parameterable : scenarios.getSelected()) {
            output.add((Scenario) parameterable);
        }
        return output;
    }

    public Scenarios(Scenario scenario) {
        setName("Root");
        scenarios.addChoice(scenario);
        List<Parameterable> parameterables = new LinkedList<Parameterable>();
        parameterables.add(scenario);
        try {
            scenarios.setValue(parameterables);
        } catch (InvalidParameterInputException e) {
            e.printStackTrace();
        }
    }

    public Scenarios() {
    }
}
