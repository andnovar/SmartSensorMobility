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

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.MyGraphics2D;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;

import java.awt.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2009
 * Time: 11:06:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class SequentialMapHandle extends MapHandle {
    private int seq;

    public SequentialMapHandle(int x, int y, int seq) {
        super(x, y);
        this.seq = seq;
    }

    public int getSeq() {
        return seq;
    }

    public Map<String, Parameter> getParameters() {
        Map<String,Parameter> parameters = super.getParameters();
        parameters.put("sequence", new IntegerParameter("sequence", seq));
        return parameters;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        seq = ((IntegerParameter) parameters.get("sequence")).getValue();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        MyGraphics2D.getInstance().drawString(Integer.toString(seq), getX()-SIZE/2,getY()-SIZE/2,(Graphics2D)g);
    }
}
