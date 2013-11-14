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
 * Date: Sep 22, 2007
 * Time: 12:05:08 AM
 */
public class RowModel extends AbstractGroupModel {
    public RowModel() {
        super();
    }

    protected NodeInGroup createNodeInGroup(GeneratorNode node, String groupMemeber, Group group) {
        return new RowNode(node, groupMemeber, group);
    }

    protected class RowNode extends NodeInGroup {
        double refrencePointDev;

        public RowNode(GeneratorNode node, String type, Group group) {
            super(node, type, group);
            generateRefrencePointDev();
        }

        public void generateRefrencePointDev() {
            refrencePointDev = 2 * getRandomValue() * maxInitialDistance - maxInitialDistance;
        }
    }

    public void initNode(GeneratorNode node) throws ModelInitializationException {
        NodeInGroup nodeInGroup = nodeNodeInGroup.get(node);
        GeneratorNode leaderNode = nodeInGroup.getGroup().getLeader().getNode();
        double speed = generateSpeed(leaderNode);
        node.setSpeed(speed);
        stackDepth = 0;
        Location loc = generateInitialNodeLocation(nodeInGroup.getGroup().getLeaderLoc(),
                leaderNode.getDirection(), (RowNode) nodeNodeInGroup.get(node));
        node.setLocation(loc);
        node.setDirection(leaderNode.getDirection());
    }

    protected Location generateInitialNodeLocation(Location leaderLocation, double leaderDirection, RowNode rowNode) throws ModelInitializationException {
        double sinAlpha = -Math.sin(leaderDirection);
        double cosAlpha = Math.cos(leaderDirection);
        double rotatedX = leaderLocation.getX() * cosAlpha - leaderLocation.getY() * sinAlpha;
        double rotatedY = leaderLocation.getX() * sinAlpha + leaderLocation.getY() * cosAlpha + rowNode.refrencePointDev;
        sinAlpha = -sinAlpha;
        Location tempLocation = new Location(rotatedX * cosAlpha - rotatedY * sinAlpha,
                rotatedX * sinAlpha + rotatedY * cosAlpha);
        stackDepth++;
        try {
            if (stackDepth > MAX_STACK_DEPTH) {
                throw new ModelInitializationException("Could not initiate nodes location. Make sure there is" +
                        " enough room for member nodes to be around leaders");
            }
            ((PassiveMap) getMap()).validateNode(tempLocation);
        } catch (InvalidLocationException e) {
            rowNode.generateRefrencePointDev();
//            DevelopmentLogger.logger.debug(rowNode.getNode().getName()+": "+rowNode.refrencePointDev);
            return generateInitialNodeLocation(leaderLocation, leaderDirection, rowNode);
        }

        return tempLocation;
    }

    @Override
    protected void updateGroupNodeProperties(GeneratorNode node, GeneratorNode leaderNode) {
        node.setDirection(leaderNode.getDirection());
        node.setSpeed(generateSpeed(leaderNode));
    }
}
