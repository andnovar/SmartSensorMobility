/*
 * Copyright (c) 2005-2008 by Masoud Moshref Javadi <moshref@ce.sharif.edu>, http://ce.sharif.edu/~moshref
 * The license.txt file describes the conditions under which this software may be distributed.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.randomdirection;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.ReflectiveMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.DestinationTransition;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.Transition2;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.TransitionModel;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 24, 2006
 * Time: 7:35:53 PM
 * <br/> this is an implementation of <i>Random Direction</i> model.
 * In this model nodes will move toward maps border. waits there and then moves toward map border
 * again with a random angle.
 * <br/> list of uiparameters: <br/>
 * {@link Integer} maxspeed<br/>
 * {@link Integer} minspeed<br/>
 * {@link Integer} maxpausetime: duration in which each node stops at border location<br/>
 */
public class RandomDirectionModel extends TransitionModel {
    protected int maxSpeed, minSpeed;
    protected int maxPauseTime;

    public RandomDirectionModel() {
        super();
        maxSpeed = 0;
        minSpeed = 0;
        maxPauseTime = 0;
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("maxspeed", new IntegerParameter("maxspeed", maxSpeed));
        parameters.put("minspeed", new IntegerParameter("minspeed", minSpeed));
        parameters.put("maxpausetime", new IntegerParameter("maxpausetime", maxPauseTime));
        return parameters;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        maxSpeed = (Integer) parameters.get("maxspeed").getValue();
        minSpeed = (Integer) parameters.get("minspeed").getValue();
        maxPauseTime = (Integer) parameters.get("maxpausetime").getValue();
    }

    @Override
    protected Transition2 getNewTransition(GeneratorNode node) {
        Location destLoc = generateDestNodeLocation(0,2*Math.PI,
                //node.getDirection() + 2 * Math.PI / 3, node.getDirection() + 4 * Math.PI / 3,
                node.getDoubleLocation());
        node.setSpeed((getRandomValue() * (maxSpeed - minSpeed)) + minSpeed);
        node.setDirection(Location.calculateRadianAngle(node.getDoubleLocation(),destLoc));
        return new DestinationTransition((long) (getRandomValue() * maxPauseTime), destLoc);
    }

    @Override
    protected void updateLocation(GeneratorNode node, Location nextLoc, double timeStep, Transition2 transition) {
         edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map map = getMap();
        if (map instanceof ReflectiveMap) {
            //check hit
            //checks if it is hit the border generate new transition
            Location mirror = new Location(nextLoc.getX(), nextLoc.getY());
            //mirror will be the mirror point of the nextstep location
            Location hit = ((ReflectiveMap) map).isHitBorder(node.getDoubleLocation(), nextLoc, mirror);
            if (hit != null) {
                node.setLocation(hit);
                transition.hit(node, hit, mirror);
                ((DestinationTransition) transition).setDestination(hit);
                double timeAfterHit = timeStep - node.getDoubleLocation().getLength(hit) / node.getSpeed();
                getNextStep(timeAfterHit,node);
            } else {
                node.setLocation(nextLoc);
            }
        } else {
            node.setLocation(nextLoc);
        }
    }

    protected Location generateDestNodeLocation(double angle1, double angle2, Location loc) {
        double angle = angle1 + getRandomValue() * (angle2 - angle1);
        double length =Math.sqrt(Math.pow(getMap().getWidth(),2)+Math.pow(getMap().getHeight(),2));
                //((ReflectiveMap) getMap()).howFarFromBorder(angle, loc);
        return new Location(loc.getX() + length * Math.cos(angle),
                loc.getY() + length * Math.sin(angle));
    }
}
