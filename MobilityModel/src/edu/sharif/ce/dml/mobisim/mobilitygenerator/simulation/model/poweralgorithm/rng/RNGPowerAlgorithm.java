package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.poweralgorithm.rng;

import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.poweralgorithm.PowerAlgorithm;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Aug 28, 2007
 * Time: 4:28:12 PM
 */
public class RNGPowerAlgorithm extends PowerAlgorithm {

    public String toString() {
        return "RNG";
    }

    @Override
    public void setRange(List<GeneratorNode> nodes) {
        for (GeneratorNode node : nodes) {
            node.setRange(0d);
        }
        for (int i = 0; i < nodes.size(); i++) {
            GeneratorNode aNode1 = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                GeneratorNode aNode2 = nodes.get(j);
                double distance = aNode1.getLocation().getLength(aNode2.getLocation());
                boolean found1 = false;
                for (int k = 0; k < nodes.size(); k++) {
                    if (k != i && k != j) {
                        GeneratorNode aNode3 = nodes.get(k);
                        double distance2 = Math.max(aNode1.getLocation().getLength(aNode3.getLocation()),
                                aNode2.getLocation().getLength(aNode3.getLocation()));
                        if (distance2 < distance) {
                            found1 = true;
                            break;
                        }
                    }
                }
                if (!found1) {
                    //set powers so that node1 and node2 can connect
                    aNode2.setRange(Math.max(aNode2.getRange(), distance + POWER_MARGIN));
                    aNode1.setRange(Math.max(aNode1.getRange(), distance + POWER_MARGIN));
                }
            }
        }
    }
}
