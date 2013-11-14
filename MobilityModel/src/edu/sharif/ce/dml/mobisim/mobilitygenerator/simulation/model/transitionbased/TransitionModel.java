package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.transitionbased;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ModelInitializationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.ReflectiveMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2010
 * Time: 7:07:21 PM
 */
public abstract class TransitionModel extends Model {
    protected Map<GeneratorNode, Transition2> nodeCurrentTransition = new HashMap<GeneratorNode, Transition2>();

    @Override
    public void setModelNodes(List<GeneratorNode> modelNodes) throws ModelInitializationException {
        super.setModelNodes(modelNodes);
        nodeCurrentTransition.clear();
    }

    @Override
    public void getNextStep(double timeStep, GeneratorNode node) {
        Transition2 transition = nodeCurrentTransition.get(node);
        Location nextLoc = null;
        try {
            nextLoc = transition.getNextLocation(node, timeStep);
            updateLocation(node, nextLoc, timeStep, transition);
        } catch (FinishedTransition finishedTransition) {
            updateLocation(node, finishedTransition.getCurrentLocation(), timeStep, transition);//why?
            nodeCurrentTransition.put(node, getNewTransition(node));
            getNextStep(finishedTransition.getRemainedTimeStep(), node);
        }
    }

    protected abstract Transition2 getNewTransition(GeneratorNode node);

    protected void updateLocation(GeneratorNode node, Location nextLoc, double timeStep, Transition2 transition) {
        edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map map = getMap();
        if (map instanceof ReflectiveMap) {
            //check hit
            //checks if it is hit the border generate new transition
            Location mirror = new Location(nextLoc.getX(), nextLoc.getY());
            //mirror will be the mirror point of the nextstep location
            Location hit = ((ReflectiveMap) map).isHitBorder(node.getDoubleLocation(), nextLoc, mirror);
            if (hit != null) {
                double timeAfterHit = timeStep - node.getDoubleLocation().getLength(hit) / node.getSpeed();
                node.setLocation(hit);
                transition.hit(node, hit, mirror);
                if (timeAfterHit > 0) {
                    updateLocation(node, mirror, timeAfterHit, transition);
                }
            } else {
                node.setLocation(nextLoc);
            }
        } else {
            node.setLocation(nextLoc);
        }
    }

    @Override
    public void initNode(GeneratorNode node) throws ModelInitializationException {
        Location initLocation = generateInitLocation();
        node.setLocation(initLocation);
        nodeCurrentTransition.put(node, getNewTransition(node));
    }


}
