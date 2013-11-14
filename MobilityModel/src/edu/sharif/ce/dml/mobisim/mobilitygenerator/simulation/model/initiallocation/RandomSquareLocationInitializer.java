package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.initiallocation;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.random.RandomParameterable;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.Simulation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2010
 * Time: 5:58:28 PM
 */
public class RandomSquareLocationInitializer extends LocationInitializer {
    private ParameterableParameter random1Parameter = new ParameterableParameter();
    private ParameterableParameter random2Parameter = new ParameterableParameter();
    private boolean reset = true;

    @Override
    public Location getLocation(int width, int height) {
        if (reset) {
            reset = false;
            long longRandomNumber = Simulation.getLongRandomNumber();
            ((RandomParameterable) random1Parameter.getValue()).setSeed(longRandomNumber);
            long longRandomNumber1 = Simulation.getLongRandomNumber();
            ((RandomParameterable) random2Parameter.getValue()).setSeed(longRandomNumber1);
        }
        return new Location(((RandomParameterable) random1Parameter.getValue()).initValue() * width,
                ((RandomParameterable) random1Parameter.getValue()).initValue() * height);
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        random1Parameter = (ParameterableParameter) parameters.get("random1");
        random2Parameter = (ParameterableParameter) parameters.get("random2");
        reset = true;
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("random1", random1Parameter);
        parameters.put("random2", random2Parameter);
        return parameters;
    }
}
