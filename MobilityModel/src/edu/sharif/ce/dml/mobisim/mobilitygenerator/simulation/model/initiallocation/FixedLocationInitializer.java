package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.initiallocation;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.MultipleSelectParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.LocationParameter;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: 12/27/10
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class FixedLocationInitializer extends LocationInitializer {
    private int currentIndex = 0;
    private MultipleSelectParameter locations = new MultipleSelectParameter("locations");

    @Override
    public Location getLocation(int width, int height) {
        List<Parameterable> selectedParameterables = locations.getSelected();
        LocationParameter locParameter = (LocationParameter) selectedParameterables.get(currentIndex);
        currentIndex = (currentIndex + 1) % selectedParameterables.size();
        return locParameter.getLocation();
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        locations = (MultipleSelectParameter) parameters.get("locations");
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("locations", locations);
        return parameters;
    }
}
