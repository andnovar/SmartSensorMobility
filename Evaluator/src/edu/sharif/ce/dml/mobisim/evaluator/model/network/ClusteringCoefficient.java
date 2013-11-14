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

package edu.sharif.ce.dml.mobisim.evaluator.model.network;

import edu.sharif.ce.dml.common.logic.entity.Node;
import edu.sharif.ce.dml.common.logic.entity.SnapShot;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Aug 31, 2007
 * Time: 12:35:44 PM
 */
public class ClusteringCoefficient extends NetworkEvaluator {
    private int totalCount = 0;
    private double totalValue = 0;

    protected void evaluate(SnapShot snapShot) {
        for (Node node : snapShot.getNodeShadows()) {
            double sum = 0;
            List<Node> neighbors = node.getNeighbors();
            if (neighbors.size() > 0) {
                for (Node neighbor : neighbors) {
                    for (Node neighbor2 : neighbor.getNeighbors()) {
                        if (neighbors.contains(neighbor2)) {
                            sum++;
                        }
                    }
                }
                totalCount++;
                totalValue+=sum/neighbors.size();
            }
        }
    }

    public String getName() {
        return "Clustering Coefficient";
    }

    public Object[] getResult() {
        return new Object[0];
    }

    public void reset() {
        totalCount = 0;
        totalValue = 0;
    }

    /*
       data for this specific object not internal objects
    */
    public List print() {
        return Arrays.asList(totalValue/totalCount);
    }

    public List<String> getLabels() {
        return Arrays.asList("Clustering Coefficient");
    }
}
