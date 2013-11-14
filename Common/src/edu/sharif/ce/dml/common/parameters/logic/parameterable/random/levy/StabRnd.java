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

package edu.sharif.ce.dml.common.parameters.logic.parameterable.random.levy;

import java.util.Random;

import static java.lang.Math.*;

/**
 * Created by IntelliJ IDEA.
 * User: masoud
 * Date: Mar 20, 2009
 * Time: 7:00:50 PM
 */
public class StabRnd {
    private double alpha, beta, c, delta;
    private Random randw, randPhi;

    public StabRnd(double alpha, double beta, double c, double delta, long seed1, long seed2) {
        this.alpha = alpha;
        this.beta = beta;
        this.c = c;
        this.delta = delta;
        if (alpha < 0.1 || alpha > 2) {
            throw new IllegalArgumentException("Alpha must be in [.1,2] for function STABRND");
        }
        if (abs(beta) > 1) {
            throw new IllegalArgumentException("Beta must be in [-1,1] for function STABRND.");
        }
        randw = new Random(seed1);
        randPhi = new Random(seed2);
    }

    public double getNext() {
        double w = -log(randw.nextDouble());
        double phi = (randPhi.nextDouble() - 0.5) * PI;
        double x;
        if (alpha == 2) {
            x = 2 * sqrt(w) * sin(phi);
            return delta + c * x;
        }
        if (beta == 0) {
            // Symmetrical cases:
            if (alpha == 1) {
                x = tan(phi);
            } else {
                x = pow((cos(1 - alpha) * phi / w), (1 / alpha - 1)) * pow(sin(alpha * phi) / cos(phi), 1 / alpha);
            }
        } else {
            // General cases:
            double cosPhi = cos(phi);
            if (abs(alpha - 1) > 1.e-8) {
                double zeta = beta * tan(PI * alpha / 2);
                double aphi = alpha * phi;
                double alphi = (1 - alpha) * phi;
                x = ((sin(alphi) + zeta * cos(aphi)) / cosPhi) * pow(((cos(alphi) + zeta * sin(alphi)) / (w * cosPhi)),
                        (1 - alpha) / alpha);
            } else {
                double bphi = (PI / 2) + beta * phi;
                x = (2 / PI) * (bphi * tan(phi) - beta * log(PI / 2 * w * cosPhi / bphi));
                if (alpha != 1) {
                    x = x + beta * tan(PI * alpha / 2);
                }
            }
        }
        x = delta + c * x;
        return x;
    }

    /*public static void main(String[] args) {
        StabRnd stabRnd = new StabRnd(1, 0, 10, 0);
        double[] data = new double[10000];
        for (int i = 0; i < 10000; i++) {
            data [i]=stabRnd.getNext();
            //System.out.println(data[i]);
        }

        double mean=0;
        double min=1000000;
        double max=-1000000;
        for (int i = 0; i < data.length; i++) {
            double v = data[i];
            min = min>v? v:min;
            max = max<v? v:max;
            mean+=v;
        }
        System.out.println("min="+min+", max="+max+", mean="+mean/10000);

    }*/

}