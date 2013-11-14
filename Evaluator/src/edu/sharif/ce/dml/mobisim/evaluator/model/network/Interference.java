/*
 * Copyright (c) 2005-2009 by Masoud Moshref Javadi <moshref@ce.sharif.edu>, http://ce.sharif.edu/~moshref
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 4, 2009
 * Time: 6:23:42 PM
 */
public class Interference extends NetworkEvaluator{
    private double totalValue=0;
    private int totalCount=0;
    protected void evaluate(SnapShot snapShot) {
        int maxNode=0;
        Set<Node> uniqueNeighborsSet = new HashSet<Node>();
        for (Node node : snapShot.getNodeShadows()) {
            for (Node node1 : node.getNeighbors()) {
                uniqueNeighborsSet.addAll(node.getNeighbors());
                uniqueNeighborsSet.addAll(node1.getNeighbors());
            }
            int currentInterference = uniqueNeighborsSet.size() - 2;
            maxNode=maxNode< currentInterference ?currentInterference:maxNode;
            uniqueNeighborsSet.clear();
        }
        totalCount++;
        totalValue+=maxNode;
    }

    public void reset() {
        totalCount=0;
        totalValue=0;
    }
    /*
       data for this specific object not internal objects
    */
    public List print() {
        return Arrays.asList(totalValue/totalCount);
    }

    public List<String> getLabels() {
        return Arrays.asList("Interference");
    }
}
