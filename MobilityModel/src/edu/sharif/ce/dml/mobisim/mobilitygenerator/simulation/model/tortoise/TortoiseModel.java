package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.tortoise;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.SelectOneParameterable;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ModelInitializationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.Transition2;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.TransitionModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 23, 2010
 * Time: 12:47:21 PM
 */
public class TortoiseModel extends TransitionModel {
    private java.util.Map<GeneratorNode, TransitionTortoiseActionMemento> nodeMemento;
    private SelectOneParameterable rootAction = new SelectOneParameterable(true);

    @Override
    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        rootAction = (SelectOneParameterable) parameters.get("rootaction");
    }

    @Override
    public java.util.Map<String, Parameter> getParameters() {
        java.util.Map<String, Parameter> parameters = super.getParameters();
        parameters.put("rootaction", rootAction);
        return parameters;
    }

    @Override
    public void setModelNodes(List<GeneratorNode> modelNodes) throws ModelInitializationException {
        super.setModelNodes(modelNodes);
        nodeMemento = new HashMap<GeneratorNode, TransitionTortoiseActionMemento>();
    }

    @Override
    protected Transition2 getNewTransition(GeneratorNode node) {
        //initially put the first one
        TransitionTortoiseActionMemento currentActionMemento = nodeMemento.get(node);
        if (currentActionMemento == null) {
            TransitionTortoiseActionMemento initMemento = ((TortoiseAction) rootAction.getValue()).getInitialActionMemento(null).run(node, 0);
            nodeMemento.put(node, initMemento);
            return initMemento.generateTransition(node);
        } else {
            TransitionTortoiseActionMemento actionMemento = currentActionMemento.getParentMemento().run(node, 0);
            nodeMemento.put(node, actionMemento);
            return actionMemento.generateTransition(node);
        }
    }

}
