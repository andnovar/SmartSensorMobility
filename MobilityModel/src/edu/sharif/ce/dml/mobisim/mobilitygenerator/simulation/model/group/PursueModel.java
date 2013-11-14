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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.group;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ModelInitializationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.PassiveMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.exception.InvalidLocationException;


/**
 * mobility model that in each group, members persue the leader.
 * nodes' initial location will be behind the line perpendicular to the leader direction line.<br/>
 * Parameters is as the parent class.
 * <br/>todo: members should not stop if the leader stops or at least it should be selectable behaviour
 */
public class PursueModel extends AbstractGroupModel {
    public PursueModel() {
        super();
    }

    /**
     * manages to create transition that it's direction is toward the leader.
     *
     * @param node
     * @return
     */
    public void initNode(GeneratorNode node) throws ModelInitializationException {

        NodeInGroup nodeInGroup = nodeNodeInGroup.get(node);
        GeneratorNode leaderNode = nodeInGroup.getGroup().getLeader().getNode();
        Location leaderLoc = leaderNode.getDoubleLocation();
        double speed = generateSpeed(leaderNode);
        node.setSpeed(speed);
        stackDepth = 0;
        Location loc = generateInitialNodeLocation(nodeInGroup.getGroup().getLeaderLoc(),
                leaderNode.getDirection());
        node.setLocation(loc);
        double angleDev = ((getRandomValue() * ADR * 2) - ADR) * maxAngle;
        double angle = Location.calculateRadianAngle(loc, leaderLoc) + angleDev;
        node.setDirection(angle);
    }

    /**
     * sets the positon of nodes behind the line perpendicular to the leader direction line.
     *
     * @param leaderLocation
     * @param leaderDirection
     * @return
     */
    protected Location generateInitialNodeLocation(Location leaderLocation, double leaderDirection) throws ModelInitializationException {
        double xDis1 = getRandomValue() * maxInitialDistance;
        double yDis1 = getRandomValue() * maxInitialDistance * 2 - maxInitialDistance;
        double xDis = -xDis1 * Math.cos(leaderDirection) + yDis1 * Math.sin(leaderDirection);
        double yDis = -xDis1 * Math.sin(leaderDirection) - yDis1 * Math.cos(leaderDirection);
        Location tempLocation = new Location(leaderLocation.getX() + xDis, leaderLocation.getY() + yDis);
        stackDepth++;
        try {
            if (stackDepth > MAX_STACK_DEPTH) {
                throw new ModelInitializationException("Could not initiate nodes location. Make sure there is" +
                        " enough room for member nodes to be around leaders");
            }
            ((PassiveMap) getMap()).validateNode(tempLocation);
        } catch (InvalidLocationException e) {
            return generateInitialNodeLocation(leaderLocation, leaderDirection);
        }
        return tempLocation;
    }

    @Override
    protected void updateGroupNodeProperties(GeneratorNode node, GeneratorNode leaderNode) {
        double newSpeed = generateSpeed(leaderNode);
        node.setSpeed(newSpeed);
        double newLeaderDir = leaderNode.getDirection();
        NodeInGroup nodeInGroup = nodeNodeInGroup.get(node);
        Location leaderLoc = nodeInGroup.getGroup().getLeaderLoc();
        double angleDev = ((getRandomValue() * ADR * 2) - ADR) * maxAngle;
        double angle = Location.calculateRadianAngle(node.getDoubleLocation(),leaderLoc) + angleDev;
        node.setDirection(angle);
    }
}
