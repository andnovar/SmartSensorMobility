package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2010
 * Time: 9:29:08 PM
 */
public abstract class Transition2 {
    protected double pauseTime;
    protected double passedPauseTime = 0;

    protected Transition2(double pauseTime) {
        this.pauseTime = pauseTime;
    }

    public abstract Location getNextLocation(final GeneratorNode node, double timeStep) throws FinishedTransition;

    public abstract void hit(GeneratorNode node, Location hit, Location mirror);

    protected Location pause(double timeStep, Location loc) {
        if (passedPauseTime >= pauseTime) {
            throw new FinishedTransition(timeStep, loc);
        } else {
            passedPauseTime += timeStep;
            return loc;
        }
    }
}


