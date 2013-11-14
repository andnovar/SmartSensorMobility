package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.poweralgorithm.mstprime;

import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.poweralgorithm.PowerAlgorithm;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Aug 27, 2007
 * Time: 9:22:16 PM
 */
public class MSTPrimePowerAlgorithm extends PowerAlgorithm {


    private GeneratorNode extractMin(Set<GeneratorNode> remainedNodes, Map<GeneratorNode, Double> nodeKeyMap) {
        assert remainedNodes.size() > 0;
        GeneratorNode minKeyNode = remainedNodes.iterator().next();
        for (GeneratorNode remainedNode : remainedNodes) {
            minKeyNode = nodeKeyMap.get(minKeyNode) > nodeKeyMap.get(remainedNode) ? remainedNode : minKeyNode;
        }
        remainedNodes.remove(minKeyNode);
        return minKeyNode;
    }

    public String toString() {
        return "MST Prime";
    }

    @Override
    public void setRange(List<GeneratorNode> nodes) {
        Map<GeneratorNode, Double> nodeKeyMap = new HashMap<GeneratorNode, Double>();
        Map<GeneratorNode, GeneratorNode> nodeParentMap = new HashMap<GeneratorNode, GeneratorNode>();
        for (GeneratorNode tempAlgorithmNode : nodes) {
            nodeKeyMap.put(tempAlgorithmNode, Double.MAX_VALUE);
        }
        if (nodes.size() > 0) {
            GeneratorNode node = nodes.get(0);
            nodeKeyMap.put(node, 0d);
            Set<GeneratorNode> remainedNodes = new HashSet<GeneratorNode>();
            remainedNodes.addAll(nodes);
            while (remainedNodes.size() > 0) {
                GeneratorNode selectedNode = extractMin(remainedNodes, nodeKeyMap);
                selectedNode.setRange(nodeKeyMap.get(selectedNode) + POWER_MARGIN);
                //for two way range
                GeneratorNode parent = nodeParentMap.get(selectedNode);
                if (parent != null) {
                    parent.setRange(Math.max(parent.getRange(), selectedNode.getRange()));
                }
                for (GeneratorNode tempNode : remainedNodes) {
                    double distance;
                    if (isIn1WayRange(selectedNode, tempNode, nodeKeyMap.get(tempNode))) {
                        distance = selectedNode.getLocation().getLength(tempNode.getLocation());
                        nodeKeyMap.put(tempNode, distance);
                        nodeParentMap.put(tempNode, selectedNode);
                    }
                }
            }
        }
    }
}
