package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.poweralgorithm.fixed;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.poweralgorithm.PowerAlgorithm;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jul 19, 2007
 * Time: 10:57:01 AM
 */
public class FixedPowerAlgorithm extends PowerAlgorithm {

    private Double value=0d;

    public String toString() {
        return "Fixed Algorithm";
    }

    @Override
    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("value", new DoubleParameter("value", value));
        return parameters;
    }

    @Override
    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        this.value = ((DoubleParameter) parameters.get("value")).getValue();
    }

    @Override
    public void setRange(List<GeneratorNode> nodes) {
        for (GeneratorNode node : nodes) {
            node.setRange(value);
        }
    }

}
