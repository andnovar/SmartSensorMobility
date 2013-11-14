package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.initiallocation;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2010
 * Time: 5:58:44 PM
 */
public abstract class LocationInitializer extends ParameterableImplement {
   public abstract Location getLocation(int width, int height);
}
