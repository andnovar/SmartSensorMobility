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
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Aug 28, 2009
 * Time: 6:10:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinkEvaluator2 extends NetworkEvaluator {
    private SnapShot lastSnapShot;

    protected void evaluate(SnapShot snapShot) {
        lastSnapShot = snapShot;

    }

    public void reset() {
    }

    /*
       data for this specific object not internal objects
    */
    public List print() {
        double totalDuration = 0;
        int totalCount = 0;
        double totalDisconnection = 0;
        double totalIntermeetingTime = 0;
        int totalIntermeetingTimeCount = 0;
        for (Node node : lastSnapShot.getNodeShadows()) {
            double[] values = node.computeAverageLinkDuration(lastSnapShot.getTime());
            totalDuration += values[0];
            totalCount += values[1];

            totalDisconnection += node.computeLinkDisconnection(lastSnapShot.getTime())[0];
            values = node.computeLinkIntermeetingTime(lastSnapShot.getTime());
            totalIntermeetingTime += values[0];
            totalIntermeetingTimeCount += values[1];
        }

        if (totalCount!=0){
            totalDuration/=totalCount;
            totalDisconnection/=totalCount;
        }

        if (totalIntermeetingTimeCount!=0){
            totalIntermeetingTime/=totalIntermeetingTimeCount;
        }

        return Arrays.asList(totalDuration, totalDisconnection,
                totalIntermeetingTime );
    }

    public List<String> getLabels() {
        return Arrays.asList("Link Duration", "Neighborhood Instability", "Intermeeting Time");
    }

    public String getName() {
        return "Link Evaluator";
    }

    @Override
    public String toString() {
        return getName();
    }
}
