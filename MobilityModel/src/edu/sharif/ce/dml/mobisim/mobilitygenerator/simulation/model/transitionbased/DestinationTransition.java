package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2010
 * Time: 9:30:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DestinationTransition extends Transition2 {
    private Location destination;

    public DestinationTransition(double pauseTime, Location destination) {
        super(pauseTime);
        this.destination = destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    @Override
    public Location getNextLocation(GeneratorNode node, double timeStep) throws FinishedTransition {
        Location loc = node.getDoubleLocation();
        if (loc.getLength(destination) == 0) {
            if (passedPauseTime >= pauseTime) {
                throw new FinishedTransition(timeStep, loc);
            } else {
                passedPauseTime += timeStep;
                return destination;
            }
        } else {
            Location nextStepLoc = new Location(
                    Math.cos(node.getDirection()) * node.getSpeed() * timeStep + loc.getX(),
                    Math.sin(node.getDirection()) * node.getSpeed() * timeStep + loc.getY());
            if (loc.getLength(nextStepLoc) >= loc.getLength(destination)) {
                double movementTime = timeStep - loc.getLength(destination) / node.getSpeed();
                passedPauseTime += movementTime;
                return destination;
            } else {
                return nextStepLoc;
            }
        }
    }

    @Override
    public void hit(GeneratorNode node, Location hit, Location mirror) {
        if (hit.getLength(mirror) > 0) {//it is only possible when destination equals hit
            double angle = Location.calculateRadianAngle(hit, mirror);
            node.setDirection(angle);
            double remainedLength = destination.getLength(hit);
            destination.pasteCoordination(remainedLength * Math.cos(angle) + hit.getX(),
                    remainedLength * Math.sin(angle) + hit.getY());
        }
    }
}
