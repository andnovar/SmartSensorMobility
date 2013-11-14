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
 * Date: Dec 27, 2006
 * Time: 9:25:42 PM
 */
public class Markov2Model extends Model {
    protected int maxSpeed, minSpeed;
    protected double alpha;
    protected double randomAmp;

    private java.util.Map<GeneratorNode, MarkovNode> nodeMarkovNode;

    public Markov2Model() {
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
    }

    public java.util.Map<String, Parameter> getParameters() {
        java.util.Map<String, Parameter> parameters = super.getParameters();
        parameters.put("maxspeed", new IntegerParameter("maxspeed", maxSpeed));
        parameters.put("minspeed", new IntegerParameter("minspeed", minSpeed));

        parameters.put("memoryfactor", new DoubleParameter("memoryfactor", 1, 0, 0.1, alpha));
        parameters.put("randomamplitude", new DoubleParameter("randomamplitude", randomAmp));
        return parameters;
    }

    public void setModelNodes(List<GeneratorNode> modelNodes) throws ModelInitializationException {
        super.setModelNodes(modelNodes);
        nodeMarkovNode = new HashMap<GeneratorNode, MarkovNode>(modelNodes.size());
        for (GeneratorNode node : modelNodes) {
            nodeMarkovNode.put(node, newNode());
        }
    }

    @Override
    public void getNextStep(double timeStep, GeneratorNode node) {
        //get markov node
        MarkovNode markovNode = nodeMarkovNode.get(node);
        //loc = current loc
        Location loc = node.getDoubleLocation();
        //newV = new speed vector
        double[] newV = markovNode.generateNextV();
        //
        Location nextLoc = new Location(loc.getX() + newV[0], loc.getY() + newV[1]);
        Location mirror = new Location(nextLoc);
        Location hit = ((ReflectiveMap) getMap()).isHitBorder(loc, nextLoc, mirror);
        if (hit != null) {
            double timePassed = (mirror.getLength(hit) + hit.getLength(loc)) / node.getSpeed();
            loc.pasteCoordination(mirror);
            node.setDirection(Location.calculateRadianAngle(hit, mirror));
            markovNode.setMean(node.getSpeed(), node.getDirection());
            getNextStep(timeStep - timePassed, node);
        } else {
            node.setSpeed(loc.getLength(nextLoc) / node.getSpeed());
            node.setDirection(Location.calculateRadianAngle(loc, nextLoc));
            loc.pasteCoordination(nextLoc);
        }
    }

    protected MarkovNode newNode() {
        return new MarkovNode();
    }

    @Override
    public void initNode(GeneratorNode node) throws ModelInitializationException {
        node.setLocation(generateInitLocation());
        node.setSpeed(getRandomValue() * (maxSpeed - minSpeed) + minSpeed);
        node.setDirection(getRandomValue() * Math.PI);
        nodeMarkovNode.get(node).init(node.getSpeed(), node.getDirection());
    }

    protected class MarkovNode {
        double mSpeed, mDirection;
        double lastSpeed, lastDirection;
        int number = 0;

        protected Random rand1 = new Random(Simulation.getLongRandomNumber());
        protected Random rand2 = new Random(Simulation.getLongRandomNumber());

        public void init(double speed, double direction) {
            lastSpeed = mSpeed = speed;
            lastDirection = mDirection = direction;
            number = 1;
        }


        public double getLastDirection() {
            return lastDirection;
        }

        public double getLastSpeed() {
            return lastSpeed;
        }

        double[] generateNextV() {
            assert number > 0;
            lastSpeed = alpha * lastSpeed + (1 - alpha) * mSpeed + Math.sqrt(1 - Math.pow(alpha, 2)) * rand1.nextGaussian() * randomAmp;
            lastDirection = alpha * lastDirection + (1 - alpha) * mDirection + Math.sqrt(1 - Math.pow(alpha, 2)) * rand2.nextGaussian() * randomAmp;

            mSpeed = (mSpeed * number + lastSpeed) / (number + 1);
            mDirection = (mDirection * number + lastDirection) / ++number;
            return new double[]{lastSpeed * Math.cos(lastDirection), lastSpeed * Math.sin(lastDirection)};
        }

        public void setMean(double transSpeed, double radianAngle) {
            mSpeed = transSpeed;
            mDirection = radianAngle;
        }
    }
}
