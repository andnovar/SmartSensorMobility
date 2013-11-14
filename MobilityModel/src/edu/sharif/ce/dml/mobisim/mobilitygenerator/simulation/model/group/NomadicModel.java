package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.group;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ModelInitializationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.PassiveMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.exception.InvalidLocationException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 9, 2010
 * Time: 1:30:47 AM
 */
public class NomadicModel extends AbstractGroupModel{

    public NomadicModel() {
    }

    public void initNode(GeneratorNode node) throws ModelInitializationException {
        NodeInGroup nodeInGroup = nodeNodeInGroup.get(node);
        AbstractGroupModel.NodeInGroup leaderInGroup = nodeInGroup.getGroup().getLeader();
        GeneratorNode leaderNode = leaderInGroup.getNode();
        node.setSpeed(generateSpeed(leaderNode));
        stackDepth = 0;
        Location loc = generateInitialNodeLocation(leaderInGroup.getMovedLocation());
        node.setLocation(loc);
        double angleDev = ((getRandomValue() * ADR * 2) - ADR) * maxAngle;
        node.setDirection(leaderNode.getDirection() + angleDev);
    }

     protected Location generateInitialNodeLocation(Location leaderLocation) throws ModelInitializationException {
        double xDis = getRandomValue() * maxInitialDistance * 2 - maxInitialDistance;
        double yDis = getRandomValue() * maxInitialDistance * 2 - maxInitialDistance;
        Location tempLocation = new Location(leaderLocation.getX() + xDis, leaderLocation.getY() + yDis);
        stackDepth++;
        try {
            ((PassiveMap) getMap()).validateNode(tempLocation);
        } catch (InvalidLocationException e) {
            if (stackDepth > MAX_STACK_DEPTH) {

                throw new ModelInitializationException("Could not initiate nodes location. Make sure there is" +
                        " enough room for member nodes to be around leaders");
            }
            return generateInitialNodeLocation(leaderLocation);
        }
        return tempLocation;
    }

    @Override
    protected void updateGroupNodeProperties(GeneratorNode node, GeneratorNode leaderNode) {
        double newSpeed = generateSpeed(leaderNode);
        node.setSpeed(newSpeed);
        double newLeaderDir = leaderNode.getDirection();
        double angleDev = ((getRandomValue() * ADR * 2) - ADR) * maxAngle;
        double angle = newLeaderDir + angleDev;
        node.setDirection(angle);
    }
}
