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

package edu.sharif.ce.dml.mobisim.evaluator.model.position;

import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 1, 2007
 * Time: 2:25:40 PM
 */
public class AverageDistance extends MobilityEvaluator {

    private double distanceSum = 0;
    private int distanceNum = 0;

    public void evaluate(SnapShotData currentSnapShot) {
        NodeShadow[] nodes = currentSnapShot.getNodeShadows();
        distanceNum += (nodes.length) * (nodes.length - 1) / 2;
        for (int i = 0; i < nodes.length; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                distanceSum += Math.abs(nodes[i].getLocation().getLength(nodes[j].getLocation()));
            }
        }
    }


    public void reset() {
        distanceNum = 0;
        distanceSum = 0;
    }

    /*
       data for this specific object not internal objects
    */
    public List print() {
        return Arrays.asList(distanceSum / distanceNum);
    }

    public List<String> getLabels() {
        return Arrays.asList("Distance");
    }
}
