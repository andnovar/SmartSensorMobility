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

import edu.sharif.ce.dml.common.logic.entity.SnapShot;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 13, 2007
 * Time: 4:10:00 PM
 */
public class ConnectivityEvaluator extends NetworkEvaluator {
    private boolean connected=false;
    private int numberOfDisconnection=0;
    public void evaluate(SnapShot snapShot) {
        boolean newConnected = snapShot.isConnected();
        if (connected && !newConnected){
            numberOfDisconnection++;
        }
        connected=newConnected;
    }

    public void reset() {
        connected = false;
        numberOfDisconnection=0;
    }

    /*
       data for this specific object not internal objects
    */
    public List print() {
        return Arrays.asList(numberOfDisconnection);
    }

    public List<String> getLabels() {
        return Arrays.asList("Disconnection #");
    }
}
