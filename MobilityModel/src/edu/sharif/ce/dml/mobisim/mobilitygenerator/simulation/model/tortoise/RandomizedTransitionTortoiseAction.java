package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.tortoise;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.random.RandomParameterable;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.Simulation;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.Transition2;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 27, 2010
 * Time: 12:31:41 PM
 */
public class RandomizedTransitionTortoiseAction extends TransitionTortoiseAction {
    protected ParameterableParameter angleParameter = new ParameterableParameter();
    protected ParameterableParameter speedParameter = new ParameterableParameter();
    protected ParameterableParameter durationParameter = new ParameterableParameter();
    protected ParameterableParameter pauseTimeParameter = new ParameterableParameter();
    private boolean resetSeed=true;

    public void setTransitionParameters(Map<String, Parameter> parameters) {
        angleParameter = (ParameterableParameter) parameters.get("angle");
        speedParameter = (ParameterableParameter) parameters.get("speed");
        durationParameter = (ParameterableParameter) parameters.get("duration");
        pauseTimeParameter = (ParameterableParameter) parameters.get("pausetime");
        resetSeed=true;
    }

    private RandomParameterable getRandomValue(Parameter p) {
        return (RandomParameterable) p.getValue();
    }

    public void getTransitionParameters(Map<String, Parameter> parameters) {
        parameters.put("angle", angleParameter);
        parameters.put("speed", speedParameter);
        parameters.put("duration", durationParameter);
        parameters.put("pausetime", pauseTimeParameter);
    }

    @Override
    public Transition2 generateTransition(GeneratorNode node) {
        //put here because in setparameters the simulation random generator is not ready
        if (resetSeed){
            resetSeed=false;
            ((RandomParameterable) angleParameter.getValue()).setSeed(Simulation.getLongRandomNumber());
            ((RandomParameterable) speedParameter.getValue()).setSeed(Simulation.getLongRandomNumber());
            ((RandomParameterable) durationParameter.getValue()).setSeed(Simulation.getLongRandomNumber());
            ((RandomParameterable) pauseTimeParameter.getValue()).setSeed(Simulation.getLongRandomNumber());
        }
        angle = getRandomValue(angleParameter).initValue();
        speed = getRandomValue(speedParameter).initValue();
        duration = (int) getRandomValue(durationParameter).initValue();
        pauseTime = (int) getRandomValue(pauseTimeParameter).initValue();
        if (speed<0 || duration<0 || pauseTime<0){
            DevelopmentLogger.logger.fatal("Negative value for speed="+speed+
                    " or duration="+duration+" or pausetime="+pauseTime);
            System.exit(1);
        }
        DevelopmentLogger.logger.info("angle="+angle+", speed="+speed+", duration="+duration+", pauseTime="+pauseTime);
        return super.generateTransition(node);
    }
}
