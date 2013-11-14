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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.randomwalk;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.TimeTransition;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.Transition2;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.TransitionModel;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 28, 2006
 * Time: 3:25:51 PM
 * <br/>This is an implementation of <i>Random Walk</i> model.
 * in this model nodes will move toward a random destination. but destination location
 * generated and node's speed will be generated so that <tt>distance = walkingtime * speed</tt>.
 * <br/> list of uiparameters: <br/>
 * {@link Integer} maxspeed<br/>
 * {@link Integer} minspeed<br/>
 * {@link Integer} maxpausetime: duration in which each node stops at destination location<br/>
 * {@link Integer} walkingtime: movement time of the node in moving from source to destination.<br/>
 */
public class RandomWalkModel extends TransitionModel {
    protected int maxSpeed, minSpeed;
    protected int maxPauseTime;
    private int walkingTime;


    public RandomWalkModel() {
        super();
        maxSpeed = 0;
        minSpeed = 0;
        maxPauseTime = 0;
        walkingTime = 0;
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("walkingtime", new IntegerParameter("walkingtime", walkingTime));
        parameters.put("maxspeed", new IntegerParameter("maxspeed", maxSpeed));
        parameters.put("minspeed", new IntegerParameter("minspeed", minSpeed));
        parameters.put("maxpausetime", new IntegerParameter("maxpausetime", maxPauseTime));
        return parameters;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        this.walkingTime = (Integer) (parameters.get("walkingtime").getValue());
        maxSpeed = (Integer) parameters.get("maxspeed").getValue();
        minSpeed = (Integer) parameters.get("minspeed").getValue();
        maxPauseTime = (Integer) parameters.get("maxpausetime").getValue();
    }

    @Override
    protected Transition2 getNewTransition(GeneratorNode node) {
        node.setDirection(getRandomValue() * 2 * Math.PI);
        node.setSpeed((getRandomValue() * (maxSpeed - minSpeed)) + minSpeed);
        return new TimeTransition(0,walkingTime);
    }
}
