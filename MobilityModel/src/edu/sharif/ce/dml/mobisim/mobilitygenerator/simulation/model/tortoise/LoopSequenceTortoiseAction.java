package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.tortoise;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.LazyMultipleSelectParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 23, 2010
 * Time: 6:16:14 PM
 */
public class LoopSequenceTortoiseAction extends TortoiseAction {
    private LazyMultipleSelectParameter actionsParameterable = new LazyMultipleSelectParameter("actionsParameterable");
    private List<TortoiseAction> actions;
    private int loop = 0;


    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        loop = ((IntegerParameter) parameters.get("loop")).getValue();
        actionsParameterable = ((LazyMultipleSelectParameter) parameters.get("actions"));
        List<Parameterable> selectedActions = actionsParameterable.getSelected();
        Set<TortoiseAction> sortedActions = new TreeSet<TortoiseAction>();
        for (Parameterable selectedAction : selectedActions) {
            sortedActions.add((TortoiseAction) selectedAction);
        }
        actions = new ArrayList<TortoiseAction>(sortedActions);
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("loop", new IntegerParameter("loop", loop));
        parameters.put("actions", actionsParameterable);
        return parameters;
    }

    public TransitionTortoiseActionMemento nextStep(GeneratorNode node, TortoiseActionMemento memento, int timeStep) {
        LoopSequenceTortoiseActionMemento myMemento = (LoopSequenceTortoiseActionMemento) memento;
//        DevelopmentLogger.logger.debug("LoopSeq.nextStep: loop="+myMemento.getCurrentLoop()+"/"+loop+
//                ",step="+myMemento.getCurrentStep()+"/"+actions.size());
        if (myMemento.isEnded()) {//do nothing
            return TransitionTortoiseAction.generateNullTransitionMemento(myMemento);
        }
        int steps = actions.size();
        if (myMemento.getCurrentStep() >= steps) {//this loop finished
            if (myMemento.getCurrentLoop() >= loop) {//action finished
                TortoiseActionMemento parentTortoiseActionMemento = myMemento.getParentMemento();
                if (parentTortoiseActionMemento != null) {//for non root ones
//                    DevelopmentLogger.logger.debug("go up");
                    return parentTortoiseActionMemento.run(node, timeStep);
                } else {
                    myMemento.setEnded(true);
                    return TransitionTortoiseAction.generateNullTransitionMemento(myMemento);
                }
            } else { //next loop
//                DevelopmentLogger.logger.debug("next loop");
                myMemento.setCurrentStep(0);
                myMemento.setCurrentLoop(myMemento.getCurrentLoop()+1);
                return nextStep(node,myMemento,timeStep);
            }
        } else { //ordinary step
//            DevelopmentLogger.logger.debug("next step");
            TortoiseActionMemento stepActionMemento = actions.get(myMemento.getCurrentStep()).getInitialActionMemento(myMemento);
            myMemento.setCurrentStep(myMemento.getCurrentStep()+1);
            return stepActionMemento.run(node, timeStep);
        }
    }

    @Override
    public TortoiseActionMemento getInitialActionMemento(TortoiseActionMemento parentMemento) {
        return new LoopSequenceTortoiseActionMemento(parentMemento, this);
    }

}

class LoopSequenceTortoiseActionMemento extends TortoiseActionMemento {
    private int currentLoop = 0;
    private int currentStep = 0;
    private boolean ended = false;

    protected LoopSequenceTortoiseActionMemento(TortoiseActionMemento parentMemento, LoopSequenceTortoiseAction myAction) {
        super(parentMemento, myAction);
    }

    public int getCurrentLoop() {
        return currentLoop;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setCurrentLoop(int currentLoop) {
        this.currentLoop = currentLoop;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }
}