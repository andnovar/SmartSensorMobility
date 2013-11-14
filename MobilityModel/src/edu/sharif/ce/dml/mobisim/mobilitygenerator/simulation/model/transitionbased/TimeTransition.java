package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2010
 * Time: 9:32:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class TimeTransition extends Transition2 {
    private double duration;
    private double passedTime = 0;

    public TimeTransition(double pauseTime, double duration) {
        super(pauseTime);
        this.duration = duration;
    }

    @Override
    public Location getNextLocation(final GeneratorNode node, double timeStep) throws FinishedTransition {
        Location loc = node.getDoubleLocation();
        if (passedTime >= duration) {
            return pause(timeStep, loc);
        } else {
            Location nextStepLoc = new Location(
                    Math.cos(node.getDirection()) * node.getSpeed() * timeStep + loc.getX(),
                    Math.sin(node.getDirection()) * node.getSpeed() * timeStep + loc.getY());
            if (passedTime + timeStep > duration) {
                nextStepLoc = new Location(
                        Math.cos(node.getDirection()) * node.getSpeed() * duration - passedTime + loc.getX(),
                        Math.sin(node.getDirection()) * node.getSpeed() * duration - passedTime + loc.getY());
                timeStep = timeStep - (duration - passedTime);
                passedTime = duration;
                return pause(timeStep, nextStepLoc);
            } else {
                passedTime += timeStep;
                return nextStepLoc;
            }
        }
    }

    @Override
    public void hit(GeneratorNode node, Location hit, Location mirror) {
        if (hit.getLength(mirror)>0){
            node.setDirection(Location.calculateRadianAngle(hit,mirror));
        }
    }
}
