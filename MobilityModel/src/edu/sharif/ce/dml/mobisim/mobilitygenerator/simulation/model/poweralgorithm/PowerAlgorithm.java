package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.poweralgorithm;

import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 23, 2007
 * Time: 6:22:36 PM
 */
public abstract class PowerAlgorithm extends ParameterableImplement {
    public static final int POWER_MARGIN = 2;

    public abstract void setRange(List<GeneratorNode> nodes);

    public Map<String, Parameter> getParameters() {
        return new HashMap<String, Parameter>();
    }

    @Override
    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {

    }

    protected boolean isIn1WayRange(NodeShadow node1, NodeShadow node2, double range) {
        return (range >= node1.getLocation().getLength(node2.getLocation()));
    }
}

