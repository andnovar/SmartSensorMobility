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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.markov;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.Simulation;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ModelInitializationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.ReflectiveMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 10, 2007
 * Time: 8:29:32 PM
 */
public class BiasedMarkov extends Model {
    protected int maxSpeed, minSpeed;

    private double alpha;
    /*
        private int stepTime;
        private int currentStep;
    */
    double[] vDev = new double[]{1, 1};
    double randomAmp;

    private java.util.Map<GeneratorNode, MarkovNode> nodeMarkovNode;

    public BiasedMarkov() {
        super();
        maxSpeed = 0;
        minSpeed = 0;
        alpha = 0;
        randomAmp = 0;
    }


    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);

        maxSpeed = (Integer) parameters.get("maxspeed").getValue();
        minSpeed = (Integer) parameters.get("minspeed").getValue();

        alpha = (Double) parameters.get("memoryfactor").getValue();
        randomAmp = (Double) parameters.get("randomamplitude").getValue();
//        stepTime = (Integer) uiparameters.get("steptime").getValue();
    }

    public java.util.Map<String, Parameter> getParameters() {
        java.util.Map<String, Parameter> parameters = super.getParameters();
        parameters.put("maxspeed", new IntegerParameter("maxspeed", maxSpeed));
        parameters.put("minspeed", new IntegerParameter("minspeed", minSpeed));

        parameters.put("memoryfactor", new DoubleParameter("memoryfactor", 1, 0, 0.1, alpha));
        parameters.put("randomamplitude", new DoubleParameter("randomamplitude", randomAmp));
//        uiparameters.put("steptime", new Parameter<Integer>("steptime", stepTime));
        return parameters;
    }


    public void setModelNodes(List<GeneratorNode> modelNodes) throws ModelInitializationException {
        super.setModelNodes(modelNodes);
        nodeMarkovNode = new HashMap<GeneratorNode, MarkovNode>(modelNodes.size());
        for (GeneratorNode node : modelNodes) {
            nodeMarkovNode.put(node, new MarkovNode());
        }
    }

    @Override
    public void getNextStep(double timeStep, GeneratorNode node) {
//            DevelopmentLogger.logger.debug("updating "+node.getName());

        MarkovNode markovNode = nodeMarkovNode.get(node);
        Location loc = node.getDoubleLocation();
        double[] newV = markovNode.generateNextV();
        Location nextLoc = new Location(loc.getX() + newV[0], loc.getY() + newV[1]);
        Location mirror = new Location(nextLoc);
        Location hit = ((ReflectiveMap) getMap()).isHitBorder(loc, nextLoc, mirror);
        if (hit != null) {
            double timePassed = (mirror.getLength(hit) + hit.getLength(loc)) / node.getSpeed();
            loc.pasteCoordination(mirror);
            node.setDirection(Location.calculateRadianAngle(hit, mirror));
            markovNode.setvMean(new double[]{node.getSpeed() * Math.cos(node.getDirection()),
                    node.getSpeed() * Math.sin(node.getDirection())});
            getNextStep(timeStep - timePassed, node);
        } else {
            node.setSpeed(loc.getLength(nextLoc) / node.getSpeed());
            node.setDirection(Location.calculateRadianAngle(loc, nextLoc));
            loc.pasteCoordination(nextLoc);
        }
    }

    @Override
    public void initNode(GeneratorNode node) throws ModelInitializationException {
        node.setLocation(generateInitLocation());
        node.setSpeed(getRandomValue() * (maxSpeed - minSpeed) + minSpeed);
        node.setDirection(getRandomValue() * Math.PI);
        nodeMarkovNode.get(node).setvMean(new double[]{node.getSpeed() * Math.cos(node.getDirection()),
                node.getSpeed() * Math.sin(node.getDirection())});
    }

    private class MarkovNode {
        double v_1X;
        double v_1Y;
        double[] vMean = new double[2];
        private Random rand = new Random(Simulation.getLongRandomNumber());

        public void setvMean(double[] vMean) {
            this.vMean = vMean;
            v_1X = vMean[0];
            v_1Y = vMean[1];
        }

        double[] generateNextV() {

            double vX = alpha * v_1X + (1 - alpha) * vMean[0] + vDev[0] * (Math.sqrt(1 - Math.pow(alpha, 2)) * (rand.nextGaussian() * 2 * randomAmp - randomAmp));
            double vY = alpha * v_1Y + (1 - alpha) * vMean[1] + vDev[1] * (Math.sqrt(1 - Math.pow(alpha, 2)) * (rand.nextGaussian() * 2 * randomAmp - randomAmp));
//            DevelopmentLogger.logger.debug(vX+" : "+vY);
            v_1X = vX;
            v_1Y = vY;
            return new double[]{vX, vY};
        }
    }
}
