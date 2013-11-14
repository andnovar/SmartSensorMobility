package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 19, 2010
 * Time: 1:33:55 PM<br/>
 * This shows that a model could not initialize itself mostly because of bad configs.

 */
public class ModelInitializationException extends Exception{
    public ModelInitializationException(String message) {
        super(message);
    }
}
