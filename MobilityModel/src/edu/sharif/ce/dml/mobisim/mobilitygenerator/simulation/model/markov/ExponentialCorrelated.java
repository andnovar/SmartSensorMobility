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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.markov;

import static java.lang.Math.exp;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 20, 2007
 * Time: 5:16:17 PM
 */
public class ExponentialCorrelated extends Markov2Model {
    public ExponentialCorrelated() {
        super();
    }

    protected MarkovNode newNode() {
        return new exponentialNode();
    }

    protected class exponentialNode extends MarkovNode {
        final double e = exp(-1 / alpha);

        double[] generateNextV() {

            lastSpeed = lastSpeed * e + (randomAmp * Math.sqrt(1 - Math.pow(e, 2)) * rand1.nextGaussian());
            lastDirection = lastDirection * e + (randomAmp * Math.sqrt(1 - Math.pow(e, 2)) * rand2.nextGaussian());

            return new double[]{lastSpeed * Math.cos(lastDirection), lastSpeed * Math.sin(lastDirection)};
        }


        public void setMean(double transSpeed, double radianAngle) {
            lastSpeed = transSpeed;
            lastDirection = radianAngle;
        }
    }


}
