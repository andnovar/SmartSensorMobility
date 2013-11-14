package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased;

import edu.sharif.ce.dml.common.logic.entity.Location;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2010
 * Time: 9:31:13 PM
 * To change this template use File | Settings | File Templates.
 */
class FinishedTransition extends RuntimeException {
    private double remainedTimeStep;
    private Location currentLocation;

    public FinishedTransition(double remainedTimeStep, Location currentLocation) {
        this.remainedTimeStep = remainedTimeStep;
        this.currentLocation = currentLocation;
    }

    public double getRemainedTimeStep() {
        return remainedTimeStep;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}
