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
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.ReflectiveMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.DestinationTransition;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.Transition2;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.TransitionModel;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 24, 2006
 * Time: 1:41:09 PM
 * <br/> This is an implementation of <i>Random WayPoint</i> mobility model. which has reflection at border of map.
 * so it uses {@link ReflectiveMap}s
 * <br/> list of uiparameters: <br/>
 * {@link Integer} maxspeed<br/>
 * {@link Integer} minspeed<br/>
 * {@link Integer} maxpausetime: duration in which each node stops at destination location<br/>
 */
public class RandomWayPoint extends TransitionModel {
    protected int maxSpeed, minSpeed;
    protected int maxPauseTime;

    public RandomWayPoint() {
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
         edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map map = getMap();
        Location destLoc = new Location(getRandomValue() * map.getWidth() + map.getOrigin().getX(),
                getRandomValue() * map.getHeight() + map.getOrigin().getY());
        node.setDirection(Location.calculateRadianAngle(node.getDoubleLocation(),destLoc));
        node.setSpeed((getRandomValue() * (maxSpeed - minSpeed)) + minSpeed);
        return new DestinationTransition((long) (getRandomValue() * maxPauseTime),destLoc);
    }
}
