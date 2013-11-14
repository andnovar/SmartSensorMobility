package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.tortoise;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 23, 2010
 * Time: 12:47:35 PM
 */
abstract class TortoiseAction extends ParameterableImplement implements Comparable {
    private int order = 0;


    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        order = ((IntegerParameter) parameters.get("order")).getValue();
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("order", new IntegerParameter("order", order));
        return parameters;
    }

    public int getOrder() {
        return order;
    }

    public int compareTo(Object o) {
        int sortOutput = this.getOrder() - ((TortoiseAction) o).getOrder();
        if (sortOutput==0){
            return super.compareTo(o);
        }
        return sortOutput;
    }

    public abstract TransitionTortoiseActionMemento nextStep(GeneratorNode node, TortoiseActionMemento memento, int timeStep);

    public abstract TortoiseActionMemento getInitialActionMemento(TortoiseActionMemento parentMemento);

}

abstract class TortoiseActionMemento {
    private TortoiseActionMemento parentMemento;
    protected TortoiseAction myAction;


    protected TortoiseActionMemento(TortoiseActionMemento parentMemento, TortoiseAction myAction) {
        this.parentMemento = parentMemento;
        this.myAction = myAction;
    }

    /**
     * it must end on a transition type
     * @param node
     * @param timeStep
     * @return
     */
    public TransitionTortoiseActionMemento run(GeneratorNode node, int timeStep) {
        return myAction.nextStep(node, this, timeStep);
    }

    public TortoiseActionMemento getParentMemento() {
        return parentMemento;
    }

}





