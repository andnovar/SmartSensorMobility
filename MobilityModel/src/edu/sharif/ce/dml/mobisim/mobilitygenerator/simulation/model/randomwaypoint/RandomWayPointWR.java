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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.randomwaypoint;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.PassiveMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.exception.InvalidLocationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.DestinationTransition;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.Transition2;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.TransitionModel;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 22, 2006
 * Time: 10:40:02 PM
 * <br/> This is an implementation of <i>Random WayPoint</i> mobility model without reflection.
 * this model is one of simplest models and base for other models so
 * if you want to reimplement any model, can start from here.
 * <br/> list of uiparameters: <br/>
 * {@link Integer} maxspeed<br/>
 * {@link Integer} minspeed<br/>
 * {@link Integer} maxpausetime: duration in which each node stops at destination location<br/>
 */
public class RandomWayPointWR extends TransitionModel {
    protected int maxSpeed, minSpeed;
    protected int maxPauseTime;

    public RandomWayPointWR() {
        super();
        maxSpeed = 0;
        minSpeed = 0;
        maxPauseTime = 0;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        maxSpeed = (Integer) parameters.get("maxspeed").getValue();
        minSpeed = (Integer) parameters.get("minspeed").getValue();
        maxPauseTime = (Integer) parameters.get("maxpausetime").getValue();
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("maxspeed", new IntegerParameter("maxspeed", maxSpeed));
        parameters.put("minspeed", new IntegerParameter("minspeed", minSpeed));
        parameters.put("maxpausetime", new IntegerParameter("maxpausetime", maxPauseTime));
        return parameters;
    }

    protected Location generateDestNodeLocation() {
         edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map map = getMap();
        Location loc = new Location(getRandomValue() * map.getWidth(),
                getRandomValue() * map.getHeight());
        try {
            ((PassiveMap) map).validateDestNode(loc);
        } catch (InvalidLocationException e) {
            loc = generateDestNodeLocation();
        }
        return loc;
    }


    @Override
    protected Transition2 getNewTransition(GeneratorNode node) {
        Location destLoc = generateDestNodeLocation();
        node.setDirection(Location.calculateRadianAngle(node.getDoubleLocation(),destLoc));
        node.setSpeed((getRandomValue() * (maxSpeed - minSpeed)) + minSpeed);
        return new DestinationTransition((long) (getRandomValue() * maxPauseTime), destLoc);
    }

}
