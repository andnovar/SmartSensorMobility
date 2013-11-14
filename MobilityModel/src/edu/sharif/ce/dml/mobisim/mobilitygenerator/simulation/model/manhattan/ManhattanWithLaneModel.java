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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.manhattan;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.BooleanParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ModelInitializationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.manhattan.maps.ManhattanWithLaneMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.exception.NodeOutOfMap;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 30, 2006
 * Time: 8:39:30 PM
 * <br/>This is an implementation of Manhattan mobility model with two lane streets.
 * nodes will reflect at end of map on appropiate lane. in the crosspoints
 * nodes can not rotate 180 degree. and rotating to left in crosspoints do not take
 * a time.nodes can not overpass each other. also in the crosspoints proiority of running is not implemented.
 * <p>if a node is in <tt>safedistanceratio * speed</tt> distance from next node, its maximum speed
 * will be equal to next node speed. if <code>fixedacceleration</code> is <tt>false</tt>, nodes acceleration
 * will be a random number between -<code>maxacceleration</code> and <code>maxacceleration</code>, with
 * <code>positivespeedratio</code> probability for positive accelerations, else it  will be fixed to
 * <code>maxacceleration</code>. and also note that in this implementation node's speed is between 0 and
 * <code>maxspeed</code>, and started with speed=0 in initial time.
 * </p>
 * some malfunctions at destination nodes!
 * <br/> list of uiparameters: <br/>
 * {@link Integer} maxspeed<br/>
 * {@link Integer} minspeed<br/>
 * {@link Integer} maxpausetime: duration in which each node stops at destination location<br/>
 * {@link Integer} maxacceleration<br/>
 * {@link Integer} safedistanceratio<br/>
 * {@link Integer} fixedacceleration<br/>
 * {@link Double} positivespeedratio<br/>
 */
public class ManhattanWithLaneModel extends Model {
    protected int maxSpeed, minSpeed;
    protected int maxPauseTime;
    private Map<Location, List<GeneratorNode>> destNodeLocNodes = new HashMap<Location, List<GeneratorNode>>();
    private double safeDistanceRatio;
    private int maxAcceleration;
    private int nodeRegisteredForUpdate = 0;
    private boolean fixedAcceleration;
    private double positiveAccRatio = 2;
    private Map<GeneratorNode, Integer> nodeRemainedPauseTime;

    private DestNodeDistance destNodeDistance = new DestNodeDistance();

    public ManhattanWithLaneModel() {
        super();
        maxSpeed = 0;
        minSpeed = 0;
        maxPauseTime = 0;
        maxAcceleration = 0;
        safeDistanceRatio = 0;
        fixedAcceleration = false;
        positiveAccRatio = 0;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        maxSpeed = (Integer) parameters.get("maxspeed").getValue();
        minSpeed = (Integer) parameters.get("minspeed").getValue();
        maxPauseTime = (Integer) parameters.get("maxpausetime").getValue();

        maxAcceleration = (Integer) parameters.get("maxacceleration").getValue();
        safeDistanceRatio = (Double) parameters.get("safedistanceratio").getValue();
        fixedAcceleration = (Boolean) parameters.get("fixedacceleration").getValue();
        positiveAccRatio = (Double) parameters.get("positiveaccratio").getValue();
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("maxspeed", new IntegerParameter("maxspeed", maxSpeed));
        parameters.put("minspeed", new IntegerParameter("minspeed", minSpeed));
        parameters.put("maxpausetime", new IntegerParameter("maxpausetime", maxPauseTime));

        parameters.put("maxacceleration", new IntegerParameter("maxacceleration", maxAcceleration));
        parameters.put("safedistanceratio", new DoubleParameter("safedistanceratio", safeDistanceRatio));
        parameters.put("fixedacceleration", new BooleanParameter("fixedacceleration", fixedAcceleration));
        parameters.put("positiveaccratio", new DoubleParameter("positiveaccratio", positiveAccRatio));
        return parameters;
    }

    public void setModelNodes(List<GeneratorNode> modelNodes) throws ModelInitializationException {
        super.setModelNodes(modelNodes);    //To change body of overridden methods use File | Settings | File Templates.
        destNodeLocNodes = new HashMap<Location, List<GeneratorNode>>();
        nodeRemainedPauseTime = new HashMap<GeneratorNode, Integer>();
        for (Location location : getManhattanMap().generateAllPosiblePointLocation()) {
            destNodeLocNodes.put(location, new ArrayList<GeneratorNode>());
        }
    }

    @Override
    protected void initNode(GeneratorNode node) throws ModelInitializationException {
        Location initialLocation = new Location(0, 0);
        //get if node goes in vertical lines or horizontal lines
        boolean source = getRandomValue() >= 0.5;
        //location of dest or source point
        Location point = getManhattanMap().getPoint(getRandomValue(), getRandomValue());
        //generate destNode and sourceNode location
        Location destNodeLoc = getManhattanMap().generateDestnodePointandLocation(
                point, source, getRandomValue(), initialLocation);
        addToDestNodeLocNode(destNodeLoc, node);
        node.setSpeed(0);
        node.setLocation(initialLocation);
        node.setDirection(Location.calculateRadianAngle(initialLocation, destNodeLoc));
    }

    @Override
    protected void getNextStep(double timeStep, GeneratorNode node) {
        //first it calculates each node speed as only this node is in the lane toward that destination
        if (fixedAcceleration) {
            node.setSpeed(Math.min(Math.max(node.getSpeed() +
                    maxAcceleration, 0), maxSpeed));
        } else {
            int coef = 1;
            if (getRandomValue() * (positiveAccRatio + 1) > 1) {
                //acceleration should be positive

            } else {
                //acceleration sould be negative
                coef = -1;
            }
            node.setSpeed(Math.min(Math.max(node.getSpeed() +
                    coef * getRandomValue() * maxAcceleration, 0), maxSpeed));

//            node.getTransition().setSpeed(Math.min(Math.max(node.getTransition().getSpeed() +
//                    Math.random() * maxAcceleration * 2 - maxAcceleration, 0),maxSpeed));
        }

        nodeRegisteredForUpdate++;
        if (nodeRegisteredForUpdate >= modelNodes.size()) {
            /*if all node's in this model has been updated know check their speed according to preceding node,
              and update their location.*/
            updateAllNodes(timeStep);
            nodeRegisteredForUpdate = 0;
        }
    }

    private void updateAllNodes(double timeStep) {
        //sort each destnode nodes list
        for (Location destNodeLoc : destNodeLocNodes.keySet()) {
            List<GeneratorNode> nodes = destNodeLocNodes.get(destNodeLoc);
            destNodeDistance.setDestNodeLoc(destNodeLoc);
            Collections.sort(nodes, destNodeDistance);
            GeneratorNode[] nodesArtemp = new GeneratorNode[nodes.size()];
            GeneratorNode[] nodesAr = nodes.toArray(nodesArtemp);
            //for each node in that transition
            for (int i = 0; i < nodesAr.length; i++) {
                GeneratorNode node = nodesAr[i];
                Location loc = node.getDoubleLocation();
                if (loc.getLength(destNodeLoc) > 0) {
                    //if node goes in the way and do not reach any endpoint
                    //actual angle and presented angle differs because of discrete world
                    double radianAngle = Location.calculateRadianAngle(loc, destNodeLoc);

                    double newSpeed = node.getSpeed();
                    if (i > 0) {
                        //check the speed of next node in this line and generate a speed
                        double lengthToForwardNode = nodesAr[i - 1].getDoubleLocation().getLength(node.getDoubleLocation());
                        if (lengthToForwardNode <= safeDistanceRatio * newSpeed) {
                            newSpeed = Math.min(nodesAr[i - 1].getSpeed(), newSpeed);
                        }
                    }
                    node.setSpeed(newSpeed);

                    Location nextStepLoc = new Location(
                            Math.cos(radianAngle) * node.getSpeed() * timeStep + loc.getX(),
                            Math.sin(radianAngle) * node.getSpeed() * timeStep + loc.getY());
                    //check if destination reached or overpassed
                    if (loc.getLength(nextStepLoc) >= loc.getLength(destNodeLoc)) {
                        loc.pasteCoordination(destNodeLoc);
                        //updateLoc(timeStep-(int)(loc.getLength(trans.getDestNode().getLoc())/trans.getSpeed()),node);
                    } else {
                        loc.pasteCoordination(nextStepLoc);
                    }
                } else {
                    //if node reaches a cross point or end point
                    //should pause at destNode
                    if (!nodeRemainedPauseTime.containsKey(node)) {
                        nodeRemainedPauseTime.put(node, (int) (getRandomValue() * maxPauseTime));
                    }
                    int remainedPauseTime = nodeRemainedPauseTime.get(node);
                    if (remainedPauseTime >= timeStep) {
                        node.setSpeed(0);
                        remainedPauseTime -= timeStep;
                        nodeRemainedPauseTime.put(node, remainedPauseTime);
                    } else {
                        //if next transition should be created

                        //if node reaches a cross point
                        //if node reaches a end point
//                            timeStep -= (int) (loc.getLength(hit) / trans.getSpeed() + node.getTrack().getRemainedPauseTime());
                        //generate new transition
                        deleteDestNodeLocNode(destNodeLoc, node);

                        int destTransitionNum = (int) (getRandomValue() * 3); // 3 is three possible way in each crossPoint
                        try {
                            Location newDestNodeLoc = getManhattanMap().getNextDestNode(destTransitionNum, node.getDoubleLocation());
                            node.setDirection(Location.calculateRadianAngle(node.getDoubleLocation(), newDestNodeLoc));
                            addToDestNodeLocNode(newDestNodeLoc, node);
                        } catch (NodeOutOfMap nodeOutOfMap) {
                            nodeOutOfMap.printStackTrace();
                            DevelopmentLogger.logger.error("ManhattanModel: " + node + " Out of map");
                        }
                        //update location to complete the timeStep remaining time
                        //updateLoc(timeStep-(int)lastPauseTime,node);
                    }
                }
            }

        }
    }

    private ManhattanWithLaneMap getManhattanMap() {
        return (ManhattanWithLaneMap) getMap();
    }

    /**
     * adds a node to the list of nodes that moves toward a destNode
     *
     * @param destNodeLoc
     * @param node
     */
    private void addToDestNodeLocNode(Location destNodeLoc, GeneratorNode node) {
        List<GeneratorNode> NodesList = destNodeLocNodes.get(destNodeLoc);
        if (NodesList != null) {
            NodesList.add(node);
        }

    }

    /**
     * removes a node from a list of nodes that moves toward a destNode
     *
     * @param destNodeLoc
     * @param node
     */
    private void deleteDestNodeLocNode(Location destNodeLoc, GeneratorNode node) {
        List<GeneratorNode> NodesList = destNodeLocNodes.get(destNodeLoc);
        if (NodesList == null) {
            DevelopmentLogger.logger.warn("Manhattan Model: " + node + " error in finding destNode");
        } else {
            NodesList.remove(node);
        }

    }

    /**
     * a class that compares two nodes by their distance from their destnodes.
     */
    private class DestNodeDistance implements Comparator<GeneratorNode> {
        private Location destNodeLoc;

        public void setDestNodeLoc(Location destNodeLoc) {
            this.destNodeLoc = destNodeLoc;
        }

        public int compare(GeneratorNode node1, GeneratorNode node2) {
            return (int) (destNodeLoc.getLength(node1.getDoubleLocation()) -
                    destNodeLoc.getLength(node2.getDoubleLocation()));
        }
    }
}
