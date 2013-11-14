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
 * Date: Mar 13, 2007
 * Time: 4:16:57 PM
 */
public class AvarageRange extends NetworkEvaluator {
    double totalRange = 0;
    int numberOfData = 0;

    public void evaluate(SnapShot snapShot) {
        for (Node node :  snapShot.getNodeShadows()) {
            if (node.isActive()){
                totalRange += node.getRange();
                numberOfData ++;
            }
        }
    }

    public void reset() {
        totalRange = 0;
        numberOfData = 0;
    }

    /*
       data for this specific object not internal objects
    */
    public List print() {
        return Arrays.asList(totalRange / numberOfData);
    }

    public List<String> getLabels() {
        return Arrays.asList("Range");
    }
}
