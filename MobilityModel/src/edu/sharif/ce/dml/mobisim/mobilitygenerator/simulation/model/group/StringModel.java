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
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 21, 2007
 * Time: 8:36:40 PM
 */
public class StringModel extends AbstractGroupModel {
    public StringModel() {
        super();
    }

    public void initNode(GeneratorNode node) throws ModelInitializationException {
        NodeInGroup nodeInGroup1 = nodeNodeInGroup.get(node);
        NodeInGroup prevNode = null;
        {
            for (NodeInGroup nodeInGroup : nodeInGroup1.getGroup().getNodes()) {
                if (nodeInGroup.getNode().equals(node)) {
                    break;
                }
                prevNode = nodeInGroup;
            }
            assert prevNode != null;
        }
        GeneratorNode prevNode1 = prevNode.getNode();
        double speed = generateSpeed(prevNode1);
        node.setSpeed(speed);
        stackDepth = 0;
        Location prevNodeLoc = prevNode.isLeader() ? prevNode.getMovedLocation() : prevNode1.getDoubleLocation();
        Location loc = generateInitialNodeLocation(prevNodeLoc, prevNode1.getDirection());
        node.setLocation(loc);
        node.setDirection(Location.calculateRadianAngle(loc, prevNodeLoc));
    }

    protected Location generateInitialNodeLocation(Location prevNodeLoc, double prevNodeDir) throws ModelInitializationException {
        double xDis1 = getRandomValue() * maxInitialDistance;
        double yDis1 = getRandomValue() * maxInitialDistance * 2 - maxInitialDistance;
        double xDis = -xDis1 * Math.cos(prevNodeDir) + yDis1 * Math.sin(prevNodeDir);
        double yDis = -xDis1 * Math.sin(prevNodeDir) - yDis1 * Math.cos(prevNodeDir);
        Location tempLocation = new Location(prevNodeLoc.getX() + xDis, prevNodeLoc.getY() + yDis);
        stackDepth++;
        try {
            if (stackDepth > MAX_STACK_DEPTH) {
                throw new ModelInitializationException("Could not initiate nodes location. Make sure there is" +
                        " enough room for member nodes to be around leaders");
            }
            ((PassiveMap) getMap()).validateNode(tempLocation);
        } catch (InvalidLocationException e) {
            return generateInitialNodeLocation(prevNodeLoc, prevNodeDir);
        }
        return tempLocation;
    }

    @Override
    protected void updateGroupNodeProperties(GeneratorNode node, GeneratorNode leaderNode) {
        NodeInGroup leaderNodeInGroup = nodeNodeInGroup.get(leaderNode);
        Group group = leaderGroup.get(leaderNodeInGroup);
        NodeInGroup prevNode = null;
        {
            for (NodeInGroup nodeInGroup : group.getNodes()) {
                if (nodeInGroup.getNode().equals(node)) {
                    break;
                }
                prevNode = nodeInGroup;
            }
            assert prevNode != null;
        }
        GeneratorNode prevNode1 = prevNode.getNode();
        Location prevLoc = prevNode.isLeader() ? prevNode.getMovedLocation() : prevNode1.getDoubleLocation();
        Location loc = node.getDoubleLocation();
        double angle = Location.calculateRadianAngle(loc, prevLoc);
        node.setDirection(angle);
        node.setSpeed(prevLoc.getLength(loc));
    }
}
