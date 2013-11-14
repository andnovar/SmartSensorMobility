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
 * Date: Aug 6, 2007
 * Time: 7:58:31 PM
 */
public class AverageNodeDegree extends NetworkEvaluator {
    private long total1NodeDegrees=0;
    private long total2NodeDegrees=0;
    private long totalCount=0;

    protected void evaluate(SnapShot snapShot) {
        Node[] nodes =  snapShot.getNodeShadows();
        totalCount+= nodes.length;

        for (Node node : nodes) {
            total1NodeDegrees+=node.get1WayDegree();
            total2NodeDegrees+=node.get2WayDegree();
        }
    }


    public void reset() {
        totalCount=0;
        total1NodeDegrees=0;
        total2NodeDegrees=0;
    }

    /*
       data for this specific object not internal objects
    */
    public List print() {
        return Arrays.asList(1.0*total1NodeDegrees/totalCount,1.0*total2NodeDegrees/totalCount);
    }

    @Override
    public String toString() {
        return "Node Degree";
    }

    public List<String> getLabels() {
        return Arrays.asList("1Way Node Degree","2Way Node Degree");
    }
}
