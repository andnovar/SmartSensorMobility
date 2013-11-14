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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.ui.GraphicalStandAloneObject;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.primitives.ButtonUIParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.frame.TwoDMapDesigner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jun 10, 2009
 * Time: 8:16:32 PM
 * <br/> The adapter class for the frame of map editor as a parameterable
 */
public class MapEditor extends ParameterableImplement implements GraphicalStandAloneObject {

    private MapHandleSupport mapHandleSupport;
    private Model model;

    public void setMapHandleSupport(MapHandleSupport mapHandleSupport) {
        this.mapHandleSupport = mapHandleSupport;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {

    }

    public Map<String, Parameter> getParameters() {
        return new HashMap<String, Parameter>();
    }

    public NewUIParameter getUIParameter(boolean showInternalParameterables) {
        final ButtonUIParameter buttonUIParameter= new ButtonUIParameter("mapeditor");
        buttonUIParameter.setActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setMapHandleSupport(model.getMapHandleSupport());//To make sure it has the most recent MapHandleSupport object
                    TwoDMapDesigner tdmd = new TwoDMapDesigner(null, "Map Editor", true, mapHandleSupport);
                    tdmd.addObserver(buttonUIParameter);
                    tdmd.setVisible(true);
                }
            });
        return buttonUIParameter;
    }

    public void setUIParameterValue(NewUIParameter uiParameter) throws InvalidParameterInputException {

    }

    public void updateUIParameter(NewUIParameter uiParameter) throws InvalidParameterInputException {

    }

}
