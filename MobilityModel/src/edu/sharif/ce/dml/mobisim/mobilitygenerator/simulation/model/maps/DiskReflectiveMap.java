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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;

import static java.lang.Math.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Sep 20, 2007
 * Time: 12:40:38 PM
 */
public class DiskReflectiveMap extends DiskMap implements ReflectiveMap {

    private double accuracy = 100000;


    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param r
     * @return
     */
    public Location findHitPoint(double x1, double y1, double x2, double y2, double r) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dr = sqrt(pow(dx, 2) + pow(dy, 2));
        double dD = x1 * y2 - x2 * y1;
        double temp1 = sqrt(pow(r, 2) * pow(dr, 2) - pow(dD, 2));
        if (pow(r, 2) * pow(dr, 2) - pow(dD, 2) <= 0) {
            DevelopmentLogger.logger.debug(pow(r, 2) * pow(dr, 2) - pow(dD, 2) + " : " +
                    x1 + " : " + y1 + " : " + x2 + " : " + y2);
        }
        assert pow(r, 2) * pow(dr, 2) - pow(dD, 2) > 0 : "less than two intersection!";
        double sign = Math.signum(dy);
        sign = sign == 0 ? 1 : sign;
        double temp2 = sign * dx * temp1;
        double xHit1 = (dD * dy + temp2) / pow(dr, 2);
        double xHit2 = (dD * dy - temp2) / pow(dr, 2);
        temp2 = Math.abs(dy) * temp1;
        double yHit1 = (-dD * dx + temp2) / pow(dr, 2);
        double yHit2 = (-dD * dx - temp2) / pow(dr, 2);
        xHit1 = (int) (xHit1 * accuracy) / accuracy;
        yHit1 = (int) (yHit1 * accuracy) / accuracy;
        xHit2 = (int) (xHit2 * accuracy) / accuracy;
        yHit2 = (int) (yHit2 * accuracy) / accuracy;

        if (xHit1 >= min(x1, x2) && xHit1 <= max(x1, x2) &&
                yHit1 >= min(y1, y2) && yHit1 <= max(y1, y2)) {
            return new Location(xHit1, yHit1);
        }
        if ((xHit2 >= min(x1, x2) && xHit2 <= max(x1, x2) &&
                yHit2 >= min(y1, y2) && yHit2 <= max(y1, y2))) {
            return new Location(xHit2, yHit2);
        }
//        DevelopmentLogger.logger.debug("!!!! " + xHit1 + " " + yHit1 + " " + xHit2 + " " + yHit2 + " : "
//                + x1 + " : " + y1 + " : " + x2 + " : " + y2);
        Location hit1 = new Location(xHit1, yHit1);
        Location hit2 = new Location(xHit2, yHit2);
        if (hit1.getLength(x2, y2) > hit2.getLength(x2, y2)) {
            return hit2;
        } else {
            return hit1;
        }

//                assert xHit2 >= min(x1, x2) && xHit2 <= max(x1, x2) &&
//                        yHit2 >= min(y1, y2) && yHit2 <= max(y1, y2);


    }

    public Location isHitBorder(Location loc1, Location
            loc2, Location mirror) {
        if (loc2.getLength(center) <= radius ) {
            return null;
        }
//        DevelopmentLogger.logger.debug(loc1 + " > " + loc2);
        double x1 = loc1.getX() - center.getX();
        double y1 = loc1.getY() - center.getY();
        double x2 = loc2.getX() - center.getX();
        double y2 = loc2.getY() - center.getY();

        x1 = (int) (x1 * accuracy) / accuracy;
        y1 = (int) (y1 * accuracy) / accuracy;
        x2 = Math.ceil(x2 * accuracy) / accuracy;
        y2 = Math.ceil(y2 * accuracy) / accuracy;

        //find line-circle intersection
        double r = radius ;
        Location hit = findHitPoint(x1, y1, x2, y2, r);
        //rotatatin
        double cosAlpha = hit.getX() / r;
        double sinAlpha = -hit.getY() / r;
        double rotatedX2 = x2 * cosAlpha - y2 * sinAlpha;
        double rotatedY2 = x2 * sinAlpha + y2 * cosAlpha;
        //find mirror
        double rotatedXMirror = 2 * r - rotatedX2;
        double rotatedYMirror = rotatedY2;
        //undo rotation
        sinAlpha = -sinAlpha;
        double xM = rotatedXMirror * cosAlpha - rotatedYMirror * sinAlpha;
        double yM = rotatedXMirror * sinAlpha + rotatedYMirror * cosAlpha;

        if (new Location(xM, yM).getLength(0, 0) >= r) {
            xM = (int) (xM * accuracy) / accuracy;
            yM = (int) (yM * accuracy) / accuracy;
            if (new Location(xM, yM).getLength(0, 0) >= r) {
                xM = Math.floor(xM);
                yM = Math.floor(yM);
                while (new Location(xM, yM).getLength(0, 0) >= r) {
                    xM -= xM;
                    yM -= yM;
                }
            }
        }
        mirror.pasteCoordination(xM + center.getX(), yM + center.getY());
        hit.pasteCoordination(hit.getX() + center.getX(), hit.getY() + center.getY());
        return hit;
    }


}
