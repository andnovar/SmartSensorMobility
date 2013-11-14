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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.sequential;

import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jun 4, 2009
 * Time: 6:18:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class SequentialMapHandleGroup extends MapHandleGroup {
    private SequentialMapHandleGroup.SequentialMapHandleComparator comparator;

    public SequentialMapHandleGroup(List<MapHandle> handles, MapHandle size) {
        super(handles, size);
        comparator = new SequentialMapHandleComparator();
        Collections.sort(this.handles, comparator);
    }

    public MapHandle createMapHandle(int x, int y) {
        int seq =0;
        if (handles.size()>0){
            seq = ((SequentialMapHandle) handles.get(handles.size() - 1)).getSeq();
        }
        SequentialMapHandle mapHandle = new SequentialMapHandle(x, y, seq+1);
        handles.add(mapHandle);
        return mapHandle;
    }

    private class SequentialMapHandleComparator implements Comparator<MapHandle>{

        public int compare(MapHandle o1, MapHandle o2) {
            return ((SequentialMapHandle) o1).getSeq()- ((SequentialMapHandle) o2).getSeq();
        }
    }

    public void sortHandles(){
        Collections.sort(handles,comparator);
    }
}
