/*
 * Copyright (c) 2005-2009 by Masoud Moshref Javadi <moshref@ce.sharif.edu>, http://ce.sharif.edu/~moshref
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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.levy;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.random.levy.StabRnd;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.Simulation;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.TimeTransition;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.Transition2;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased.TransitionModel;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: masoud
 * Date: Mar 20, 2009
 * Time: 1:01:29 PM
 */
public class LevyWalk extends TransitionModel {
    protected int maxSpeed, minSpeed;
    protected int maxPauseTime;

    protected double alpha, beta, flightScale, pauseTimeScale;
    protected int minFlight, maxFlight, minPauseTime;


    private StabRnd flightStabRnd;
    private StabRnd pauseStabRnd;

    boolean reset=true;


    public LevyWalk() {
        super();
        maxSpeed = 0;
        minSpeed = 0;
        maxPauseTime = 0;

        alpha = 0;
        beta = 0;
        minFlight = 0;
        maxFlight = 0;
        minPauseTime = 0;
        flightScale = 0;
        pauseTimeScale = 0;
    }

    @Override
    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        maxSpeed = (Integer) parameters.get("maxspeed").getValue();
        minSpeed = (Integer) parameters.get("minspeed").getValue();
        maxPauseTime = (Integer) parameters.get("maxpausetime").getValue();

        alpha = ((DoubleParameter) parameters.get("alpha")).getValue();
        beta = ((DoubleParameter) parameters.get("beta")).getValue();
        minFlight = ((IntegerParameter) parameters.get("minflight")).getValue();
        maxFlight = ((IntegerParameter) parameters.get("maxflight")).getValue();
        minPauseTime = ((IntegerParameter) parameters.get("minpausetime")).getValue();
        flightScale = ((DoubleParameter) parameters.get("flightscale")).getValue();
        pauseTimeScale = ((DoubleParameter) parameters.get("pausetimescale")).getValue();
        reset = true;
    }

    @Override
    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("maxspeed", new IntegerParameter("maxspeed", maxSpeed));
        parameters.put("minspeed", new IntegerParameter("minspeed", minSpeed));
        parameters.put("maxpausetime", new IntegerParameter("maxpausetime", maxPauseTime));

        parameters.put("alpha", new DoubleParameter("alpha", 2, 0, 0.1, alpha));
        parameters.put("beta", new DoubleParameter("beta", 2, 0, 0.1, beta));
        parameters.put("minflight", new IntegerParameter("minflight", minFlight));
        parameters.put("maxflight", new IntegerParameter("maxflight", maxFlight));
        parameters.put("minpausetime", new IntegerParameter("minpausetime", minPauseTime));
        parameters.put("flightscale", new DoubleParameter("flightscale", flightScale));
        parameters.put("pausetimescale", new DoubleParameter("pausetimescale", pauseTimeScale));
        return parameters;
    }

    @Override
    protected Transition2 getNewTransition(GeneratorNode node) {
        if (reset){
            reset=false;
            flightStabRnd = new StabRnd(alpha, 0, flightScale, 0, Simulation.getLongRandomNumber(),Simulation.getLongRandomNumber());
            pauseStabRnd = new StabRnd(beta, 0, pauseTimeScale, 0,Simulation.getLongRandomNumber(),Simulation.getLongRandomNumber());
        }
        node.setDirection(getRandomValue() * 2 * Math.PI);
        node.setSpeed((getRandomValue() * (maxSpeed - minSpeed)) + minSpeed);
        double A = minFlight;
        while (!(Math.abs(A) > minFlight && A < maxFlight)) {
            A = flightStabRnd.getNext();
        }
        A = Math.round(A);
        // generate pause time
        double B = maxPauseTime;
        while (!(B >= minPauseTime && B < maxPauseTime)) {
            B = pauseStabRnd.getNext();
        }
        B = Math.round(B);
        return new TimeTransition(A,B);
    }
}


