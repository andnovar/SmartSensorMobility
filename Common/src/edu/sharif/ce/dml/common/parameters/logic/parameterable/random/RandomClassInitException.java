package edu.sharif.ce.dml.common.parameters.logic.parameterable.random;

import edu.sharif.ce.dml.common.parameters.logic.exception.ClassInstantiationException;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 27, 2010
 * Time: 12:26:28 PM<br/>
 * Exception in loading or initializing external random generator class
 */
public class RandomClassInitException extends ClassInstantiationException {
    private List<Double> params;

    public RandomClassInitException(String message, String parameterName, Class c, List<Double> params) {
        super(message, parameterName, c);
        this.params = params;
    }

    public RandomClassInitException(String message, String parameterName, Class c, List<Double> params, Throwable cause) {
        super(message, parameterName, c, cause);
        this.params = params;
    }

    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        for (Double param : params) {
            sb.append(param).append(", ");
        }
        return super.getMessage() + " params=" + sb.toString();
    }
}
