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

import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.logic.entity.SnapShot;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import edu.sharif.ce.dml.mobisim.evaluator.model.position.MobilityEvaluator;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 1, 2007
 * Time: 11:10:05 AM<br>
 */
public abstract class NetworkEvaluator extends MobilityEvaluator {
    protected void evaluate(SnapShotData snapShot) {
        try {
            evaluate((SnapShot)snapShot);
        } catch (Exception e) {
            e.printStackTrace();
            DevelopmentLogger.logger.warn("Power Evaluator type mismatch!");
        }
    }

    protected abstract void evaluate(SnapShot snapShot) ;


}
