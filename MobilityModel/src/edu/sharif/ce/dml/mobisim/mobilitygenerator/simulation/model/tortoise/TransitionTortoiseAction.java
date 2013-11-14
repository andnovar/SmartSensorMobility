package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.tortoise;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.BooleanParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.TimeTransition;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.Transition2;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 23, 2010
 * Time: 6:14:51 PM
 */
public class TransitionTortoiseAction extends TortoiseAction {
    protected double angle = 0;
    protected double speed = 0;
    protected int duration = 0;
    protected int pauseTime = 0;
    protected boolean relativeAngle = true;


    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        setTransitionParameters(parameters);
        relativeAngle = ((BooleanParameter) parameters.get("relativeangle")).getValue();
    }

    protected void setTransitionParameters(Map<String, Parameter> parameters) {
        angle = ((DoubleParameter) parameters.get("angle")).getValue();
        speed = ((DoubleParameter) parameters.get("speed")).getValue();
        duration = ((IntegerParameter) parameters.get("duration")).getValue();
        pauseTime = ((IntegerParameter) parameters.get("pausetime")).getValue();
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        getTransitionParameters(parameters);
        parameters.put("relativeangle", new BooleanParameter("relativeangle", relativeAngle));
        return parameters;
    }

    protected void getTransitionParameters(Map<String, Parameter> parameters) {
        parameters.put("angle", new DoubleParameter("angle", angle));
        parameters.put("speed", new DoubleParameter("speed", speed));
        parameters.put("duration", new IntegerParameter("duration", duration));
        parameters.put("pausetime", new IntegerParameter("pausetime", pauseTime));
    }

    @Override
    public TransitionTortoiseActionMemento nextStep(GeneratorNode node, TortoiseActionMemento memento, int timeStep) {
        TransitionTortoiseActionMemento myMemento = (TransitionTortoiseActionMemento) memento;
        DevelopmentLogger.logger.debug("Transition.nextStep: Time=" + myMemento.getTime() + "/" + duration +
                ", Timestep=" + timeStep);
        if (myMemento.getTime() + timeStep > duration) {
            DevelopmentLogger.logger.debug("go up");
            return myMemento.getParentMemento().run(node, timeStep);
        }
        myMemento.setTime(myMemento.getTime() + timeStep);
        return myMemento;
    }

    @Override
    public TortoiseActionMemento getInitialActionMemento(TortoiseActionMemento parentMemento) {
        return new TransitionTortoiseActionMemento(parentMemento, this);
    }

    public Transition2 generateTransition(GeneratorNode node) {
        double angleRadians;
//        DevelopmentLogger.logger.debug("Generate transition: angle="+angle);
        if (relativeAngle) {
            //find current angle
            double currentAngle = node.getDirection();
//            DevelopmentLogger.logger.debug("oldAngle="+currentAngle);
            angleRadians = angle / 180 * Math.PI + currentAngle;
        } else {
            angleRadians = angle / 180 * Math.PI;
        }
//        DevelopmentLogger.logger.debug("Transition generated: angle=" + angleRadians * 180 / Math.PI);
        node.setSpeed(speed);
        node.setDirection(angleRadians);
        return new TimeTransition(pauseTime, duration);
    }

    static TransitionTortoiseActionMemento generateNullTransitionMemento(TortoiseActionMemento parent) {
        return new TransitionTortoiseActionMemento(parent, null) {
            public Transition2 generateTransition(GeneratorNode node) {
                node.setSpeed(0);
                node.setDirection(0);
                return new TimeTransition(1000, 0);
            }
        };
    }
}

class TransitionTortoiseActionMemento extends TortoiseActionMemento {
    private int time = 0;

    public TransitionTortoiseActionMemento(TortoiseActionMemento parentMemento, TransitionTortoiseAction myAction) {
        super(parentMemento, myAction);
    }

    public Transition2 generateTransition(GeneratorNode node) {
        return ((TransitionTortoiseAction) myAction).generateTransition(node);
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

}




