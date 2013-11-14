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
 * Time: 10:19:42 AM   <br/>
 * Note that it ignores conditions that no nodes are connected the others
 */
public class NetworkDiameterEvaluator extends NetworkEvaluator {
    private int numberOfData=0;
    private double totalDistance =0;
    protected void evaluate(SnapShot snapShot) {
        //create d0 matrix
        Node[] nodes = snapShot.getNodeShadows();
        double [][] weightMatrix = new double[nodes.length][nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            Node node1 = nodes[i];
            weightMatrix[i][i]=0;
            for (int i1 = i+1; i1 < nodes.length; i1++) {
                Node node2 = nodes[i1];
                if (node1.hasLinkto(node2) && node2.hasLinkto(node1)){
                    weightMatrix[i][i1] = 1;
                }else{
                    weightMatrix[i][i1] = Double.MAX_VALUE;
                }
                weightMatrix[i1][i]=weightMatrix[i][i1];
            }
        }
        //calculate floyd matrix
        double[][] distanceWeights = FloydWarshal(weightMatrix);
        double max=-1;
        for (int i = 0; i < distanceWeights.length; i++) {
            for (int j = i+1; j < distanceWeights.length; j++) {
                max = Math.max(max,distanceWeights[i][j]<Double.MAX_VALUE?distanceWeights[i][j]:-1);
            }
        }
        if (max!=-1){
            numberOfData++;
            totalDistance+=max;
        }
    }

    private double[][] FloydWarshal(double[][] weights) {
        double [][] dk_1= new double[weights.length][weights[0].length];
        double [][] dk = new double[weights.length][weights[0].length];
        System.arraycopy(weights,0,dk_1,0,weights.length);
        for (int k = 0; k < dk_1.length; k++) {
            for (int i = 0; i < dk_1.length; i++) {
                for (int j = 0; j < dk_1.length; j++) {
                    dk[i][j]= Math.min(dk_1[i][j],dk_1[i][k]+dk_1[k][j]);
                }
            }
            dk_1=dk;
            dk= new double[weights.length][weights[0].length];
        }
        return dk_1;
    }


    public void reset() {
        numberOfData=0;
        totalDistance =0;
    }

    /*
       data for this specific object not internal objects
    */
    public List print() {
        return Arrays.asList(totalDistance/numberOfData);
    }

    public List<String> getLabels() {
        return Arrays.asList("Network Diameter");
    }
}
