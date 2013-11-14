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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.freeway;

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
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.freeway.maps.FreeWayWithLaneMap;

import java.util.*;


/**
 * <br/>This is an implementation of <i>FreeWay</i> Mobility Model which nodes runs toward specified nodes
 * which the map specifies, and will rotate into opposite lane at the end of the map.
 * note that rotation also takes time, this model supports multi
 * lane in freeway (but forward and backward lane number should be the same because of rotation algorithm),
 * and at each destination node in map nodes will stop and their speed will be 0,
 * then initial speed will be <code>minspeed</code>. in this model nodes can not pass each other. and
 * can not move accross lanes.
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
 * {@link Integer} maxpausetime: duration in which each node stops at destination location, not used in this model<br/>
 * {@link Integer} maxacceleration<br/>
 * {@link Integer} safedistanceratio<br/>
 * {@link Integer} fixedacceleration<br/>
 * {@link Double} positivespeedratio<br/>
 */
public class FreeWayWithLaneModel extends Model {
    protected int maxSpeed, minSpeed;
    protected int maxPauseTime;
    /**
     * a map for mapping destNode to nodes that moves toward them.
     */
    private Map<Location, List<GeneratorNode>> destNodeLocNodes = new HashMap<Location, List<GeneratorNode>>();
    /**
     * stores that lane number of each node.
     */
    private Map<GeneratorNode, Integer> nodeToLaneNum = new HashMap<GeneratorNode, Integer>();
    private double safeDistanceRatio;
    private int maxAcceleration;
    private int nodeRegisteredForUpdate;
    private boolean fixedAcceleration;
    private double positiveAccRatio = 2;
    private final DestNodeDistanceComparator destNodeDistanceComparator = new DestNodeDistanceComparator();

    public FreeWayWithLaneModel() {
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
        super.setModelNodes(modelNodes);
        nodeToLaneNum = new HashMap<GeneratorNode, Integer>();
        destNodeLocNodes = new HashMap<Location, List<GeneratorNode>>();
        for (Location location : getFreeWayMap().generateAllPosiblePointLocation()) {
            destNodeLocNodes.put(location, new ArrayList<GeneratorNode>());
        }

    }


    @Override
    protected void initNode(GeneratorNode node) throws ModelInitializationException {
        Location initialLocation = new Location(0, 0);
        Location sourceNodeLoc = new Location(0, 0);
        //generate destNode and sourceNode location
        //generate a random lane
        FreeWayWithLaneMap freeWayWithLaneMap = getFreeWayMap();
        int laneNum = (int) (freeWayWithLaneMap.getLanesNum() * getRandomValue());
        Location destNodeLoc = freeWayWithLaneMap.generateDestnodePointandLocation(
                laneNum, getRandomValue(), sourceNodeLoc, initialLocation);
        addToDestNodeLocNode(destNodeLoc, node);
        nodeToLaneNum.put(node, laneNum);
        //initial speed is set to 0
        node.setSpeed(0);
        node.setLocation(initialLocation);
        node.setDirection(Location.calculateRadianAngle(destNodeLoc, initialLocation));
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
            destNodeDistanceComparator.setDestNodeLoc(destNodeLoc);
            Collections.sort(nodes, destNodeDistanceComparator);
            GeneratorNode[] nodesArtemp = new GeneratorNode[nodes.size()];
            GeneratorNode[] nodesAr = nodes.toArray(nodesArtemp);
            //for each node in that transition
            for (int i = 0; i < nodesAr.length; i++) {
                GeneratorNode node = nodesAr[i];
                Location loc = node.getDoubleLocation();
                if (loc.getLength(destNodeLoc) > 0) {
                    //if node goes in the way and do not reach any endpoint
                    //todo verify (actual angle and presented angle differs because of discrete world)
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
//                    if (node.getTrack().getRemainedPauseTime() >= timeStep) {
//                        node.getTransition().setSpeed(0);
//                        node.getTrack().decreaseRemainedPauseTime(timeStep);
//                    } else {
                    //if next transition should be created

                    //if node reachs a cross point
                    //if node reachs an end point
//                            timeStep -= (int) (loc.getLength(hit) / trans.getSpeed() + node.getTrack().getRemainedPauseTime());
//                            long lastPauseTime = node.getTrack().getRemainedPauseTime();
                    //generate new transition
                    deleteDestNodeLocNode(destNodeLoc, node);

                    //generate new direction
                    Location newDestLoc = new Location(0, 0);
                    int newLaneNum = getFreeWayMap().getNextDestNode(
                            nodeToLaneNum.get(node), node.getDoubleLocation(), newDestLoc);
                    nodeToLaneNum.put(node, newLaneNum);
                    node.setDirection(Location.calculateRadianAngle(newDestLoc,node.getDoubleLocation()));
                    addToDestNodeLocNode(newDestLoc, node);
                    //todo update location to complete the timeStep remaining time
                    //updateLoc(timeStep,node);

                }
            }

        }
    }


    private FreeWayWithLaneMap getFreeWayMap() {
        return (FreeWayWithLaneMap) getMap();
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
            DevelopmentLogger.logger.warn("FreeWay Model: " + node + " error in finding destNode");
        } else {
            NodesList.remove(node);
        }

    }

    /**
     * a class that compares two nodes by their distance from their destnodes.
     */
    private class DestNodeDistanceComparator implements Comparator<GeneratorNode> {
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
