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

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jun 13, 2009
 * Time: 11:46:29 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class MyGraphics2D {
    private static MyGraphics2D ourInstance = new MyGraphics2D();
    int h=0;

    public static MyGraphics2D getInstance() {
        return ourInstance;
    }

    private MyGraphics2D() {
    }

    public void setHOffset(int h){
        this.h=h;
    }

    public void drawString (String s, int x, int y, Graphics2D g){
        g.transform( new AffineTransform(1,0,0,-1,0,h));
        g.drawString(s,x,-y+h);
        g.transform( new AffineTransform(1,0,0,-1,0,h));
    }
}
