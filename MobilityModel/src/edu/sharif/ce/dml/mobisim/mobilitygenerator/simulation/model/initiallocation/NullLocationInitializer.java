package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.initiallocation;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 9, 2010
 * Time: 10:49:43 PM
 */

public class NullLocationInitializer extends LocationInitializer{
    @Override
    public Location getLocation(int width, int height) {
        return null;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
    }

    public Map<String, Parameter> getParameters() {
        return new HashMap<String, Parameter>();
    }
}
