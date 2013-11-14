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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.probabilisticrandomwalk;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ModelInitializationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.ReflectiveMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Feb 8, 2007
 * Time: 8:57:12 AM
 * <br/> it doesn't have pause time.
 */
public class ProbabilisticRandomWalk extends Model {
    protected int maxSpeed, minSpeed;
    private double[][] probabilities;
    private java.util.Map<GeneratorNode, ProbabilisticNode> nodePNode;

    public ProbabilisticRandomWalk() {
        super();
        maxSpeed = 0;
        minSpeed = 0;
        probabilities = new double[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);

        maxSpeed = (Integer) parameters.get("maxspeed").getValue();
        minSpeed = (Integer) parameters.get("minspeed").getValue();

        //loads probabilities
        probabilities = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                probabilities[i][j] = (Double) parameters.get("p" + i + "," + j).getValue();
            }
        }
    }

    public java.util.Map<String, Parameter> getParameters() {
        java.util.Map<String, Parameter> parameters = super.getParameters();
        parameters.put("maxspeed", new IntegerParameter("maxspeed", maxSpeed));
        parameters.put("minspeed", new IntegerParameter("minspeed", minSpeed));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                parameters.put("p" + i + "," + j, new DoubleParameter("p" + i + "," + j,
                        Double.MAX_VALUE, 0, 0.1, probabilities[i][j]));
            }
        }
        return parameters;
    }


    public void setModelNodes(List<GeneratorNode> modelNodes) throws ModelInitializationException {
        super.setModelNodes(modelNodes);
        nodePNode = new HashMap<GeneratorNode, ProbabilisticNode>(modelNodes.size());
        for (GeneratorNode modelNode : modelNodes) {
            nodePNode.put(modelNode, new ProbabilisticNode(modelNode));
        }
    }

    @Override
    public void getNextStep(double timeStep, GeneratorNode node) {
        Location loc = node.getDoubleLocation();
        Location nextLoc = nodePNode.get(node).generateNextLoc(timeStep, (ReflectiveMap) getMap());
        node.setSpeed(loc.getLength(nextLoc) / node.getSpeed());
        node.setDirection(Location.calculateRadianAngle(loc, nextLoc));
        node.setLocation(nextLoc);
    }

    @Override
    public void initNode(GeneratorNode node) throws ModelInitializationException {
        //puts two location
        Location currentLoc = generateInitLocation();
        ProbabilisticNode pNode = nodePNode.get(node);
        pNode.putLocation(currentLoc);
        double speed = (getRandomValue() * (maxSpeed - minSpeed)) + minSpeed;
        double radianAngle = getRandomValue() * Math.PI;
        node.setSpeed(speed);
        pNode.setSpeed(speed);
        int step2TimeStep = 1;
        Location nextLoc = new Location(
                Math.cos(radianAngle) * speed * step2TimeStep + currentLoc.getX(),
                Math.sin(radianAngle) * speed * step2TimeStep + currentLoc.getY());
        Location mirrorLoc = new Location(0, 0);
        Location hitPoint = ((ReflectiveMap) getMap()).isHitBorder(currentLoc, nextLoc, mirrorLoc);
        if (hitPoint != null) {
            node.setDirection(Math.atan((mirrorLoc.getY()-hitPoint.getY())/(mirrorLoc.getX()-hitPoint.getX())));
            pNode.putLocation(mirrorLoc);
            node.setLocation(mirrorLoc);
        }else{
            node.setDirection(radianAngle);
            pNode.putLocation(nextLoc);
            node.setLocation(nextLoc);
        }
    }
}