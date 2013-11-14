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
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.exception.InvalidLocationException;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 24, 2006
 * Time: 1:47:31 PM
 */
public class SquareReflectMap extends SquareMap implements ReflectiveMap {

    public int getRightBorderPosition() {
        return getLeftBorderPosition() + width;
    }

    public int getTopBorderPosition() {
        return (int) (getOrigin().getY() );
    }

    public int getDownBorderPosition() {
        return getTopBorderPosition() + height;
    }

    public int getLeftBorderPosition() {
        return (int) (getOrigin().getX() );
    }

    public void validateNode(Location loc) throws InvalidLocationException {
        if (loc.getX() < getLeftBorderPosition() || loc.getX() > getRightBorderPosition() ||
                loc.getY() < getTopBorderPosition() || loc.getY() > getDownBorderPosition()) {
            throw new InvalidLocationException(loc.toString());
        }
    }

    public Location isHitBorder(Location loc1, Location loc2, Location mirror) {
        double x1, x2, y1, y2;
        x1 = loc1.getX();
        x2 = loc2.getX();
        y1 = loc1.getY();
        y2 = loc2.getY();
        Location hitX = null;
        Location hitY = null;
        Location mirrorX = new Location(mirror);
        Location mirrorY = new Location(mirror);
        if (x1 >= getLeftBorderPosition() && x2 < getLeftBorderPosition()) {
            mirrorX.setX(getLeftBorderPosition() + (getLeftBorderPosition() - x2));
            //max and min is for checking the corners
            double y = Math.min(y1 + (getLeftBorderPosition() - x1) * (y2 - y1) / (x2 - x1), getDownBorderPosition());
            y = Math.max(y, getLeftBorderPosition());
            hitX = new Location(getLeftBorderPosition(), y);
        } else if (x1 <= getRightBorderPosition() && x2 > getRightBorderPosition()) {
            mirrorX.setX(getRightBorderPosition() - (x2 - (getRightBorderPosition())));
            //max and min is for checking the corners
            double y = Math.min(y1 + (getRightBorderPosition() - x1) * (y2 - y1) / (x2 - x1), getDownBorderPosition());
            y = Math.max(y, getLeftBorderPosition());
            hitX = new Location(getRightBorderPosition(), y);
        }
        if (y1 >= getTopBorderPosition() && y2 < getTopBorderPosition()) {
            mirrorY.setY(getTopBorderPosition() + (getTopBorderPosition() - y2));
            hitY = new Location(x1 + (getTopBorderPosition() - y1) * (x2 - x1) / (y2 - y1), getTopBorderPosition());
        } else if (y1 <= getDownBorderPosition() && y2 > getDownBorderPosition()) {
            mirrorY.setY(getDownBorderPosition() - (y2 - (getDownBorderPosition())));
            hitY = new Location(x1 + (getDownBorderPosition() - y1) * (x2 - x1) / (y2 - y1), getDownBorderPosition());
        }

        Location hit = hitX;
        mirror.pasteCoordination(mirrorX);
        if (hitX == null ) {
            hit = hitY;
            mirror.pasteCoordination(mirrorY);
        }else if (hitY != null ){
            if (loc1.getLength(hitX) > loc1.getLength(hitY)){
                hit = hitY;
            }
            mirror.pasteCoordination(mirrorX.getX(), mirrorY.getY());
        }
        //if hit is in corner mirror is on first location.
        if (hit != null && (hit.getLength(getLeftBorderPosition(), getTopBorderPosition()) == 0 ||
                hit.getLength(getLeftBorderPosition(), getDownBorderPosition()) == 0 ||
                hit.getLength(getRightBorderPosition(), getTopBorderPosition()) == 0 ||
                hit.getLength(getRightBorderPosition(), getDownBorderPosition()) == 0)) {
            mirror.pasteCoordination(loc1);
        }
//        if (hit!=null) DevelopmentLogger.logger.debug("hit: " +hit+" mirror: " + mirror+
//                " loc1: "+loc1+" loc2: "+loc2);
        return hit;
    }
}
