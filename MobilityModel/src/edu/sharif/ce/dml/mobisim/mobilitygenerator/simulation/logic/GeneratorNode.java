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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic;


import edu.sharif.ce.dml.common.data.entity.DataLocation;
import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.logic.entity.Location;

import java.util.Arrays;
import java.util.LinkedList;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 22, 2006
 * Time: 7:07:19 PM <br/>
 * This class represents a node in a {@link edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model}
 */
public class GeneratorNode extends NodeShadow implements Comparable<GeneratorNode> {
    private Location doubleLocation;

    public Location getDoubleLocation() {
        return doubleLocation;
    }

    public void setLocation(Location location) {
        this.doubleLocation = location;        
    }

    public DataLocation getLocation() {
        if (location==null){
            return new DataLocation((int)doubleLocation.getX(),(int)doubleLocation.getY());
        }
        location.setX((int)doubleLocation.getX());
        location.setY((int)doubleLocation.getY());
        return location;
    }

    @Override
    public void setLocation(DataLocation location) {
        super.setLocation(location);
        this.doubleLocation.pasteCoordination(location);
    }

    public int compareTo(GeneratorNode o) {
        return getIntName()-o.getIntName();
    }

     /**
     * @return a brief about this node using <code> name</code> and its {@link Track}
     */
    public java.util.List print(Location offset) {
        java.util.List toWrite = new LinkedList();
        toWrite.add(name);
        toWrite.addAll(doubleLocation.print(offset));
        toWrite.addAll(Arrays.asList(new Object[]{speed, direction, range}));
        return toWrite;
    }
}
