package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic;

import edu.sharif.ce.dml.common.logic.entity.Location;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2010
 * Time: 7:12:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class HitException extends RuntimeException{
    private Location hitLocation;
    private Location targetLocation;
    private Location mirrorLocation;

    public HitException(Location hitLocation, Location targetLocation, Location mirrorLocation) {
        this.hitLocation = hitLocation;
        this.targetLocation = targetLocation;
        this.mirrorLocation = mirrorLocation;
    }

    public Location getHitLocation() {
        return hitLocation;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public Location getMirrorLocation() {
        return mirrorLocation;
    }
}
