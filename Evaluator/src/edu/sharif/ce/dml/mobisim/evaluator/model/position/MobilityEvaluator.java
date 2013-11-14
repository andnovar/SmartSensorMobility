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
import edu.sharif.ce.dml.common.logic.entity.SnapShot;
import edu.sharif.ce.dml.mobisim.evaluator.model.Evaluator;
import edu.sharif.ce.dml.mobisim.evaluator.model.network.NetworkEvaluator;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Feb 16, 2007
 * Time: 7:38:32 PM
 */
public abstract class MobilityEvaluator extends Evaluator<SnapShotData> {

    protected double getSpeedRatio(NodeShadow a, NodeShadow b) {
        if (a.getSpeed() == 0 && b.getSpeed() == 0) {
            return 0;
        }
        return Math.min(a.getSpeed(), b.getSpeed()) / Math.max(a.getSpeed(), b.getSpeed());
    }

    protected double getRelativeDir(NodeShadow a, NodeShadow b) {
        return Math.cos(b.getDirection() - a.getDirection());
    }
}
