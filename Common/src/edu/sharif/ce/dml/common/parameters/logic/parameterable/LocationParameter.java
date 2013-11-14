package edu.sharif.ce.dml.common.parameters.logic.parameterable;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: 12/27/10
 * Time: 4:41 PM<br/>
 * A wrapper parameter class for a location.
 */
public class LocationParameter extends ParameterableImplement{
    private Location loc=new Location(0,0);

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        double x= ((DoubleParameter) parameters.get("x")).getValue();
        double y= ((DoubleParameter) parameters.get("y")).getValue();
        loc.pasteCoordination(x, y);
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("x", new DoubleParameter("x",loc.getX()));
        parameters.put("y", new DoubleParameter("y",loc.getY()));
        return parameters;
    }

    public Location getLocation() {
        return new Location(loc);
    }
}
