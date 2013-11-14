package edu.sharif.ce.dml.common.parameters.logic.exception;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 1, 2010
 * Time: 1:10:08 PM
 */
public class ClassInstantiationException extends InvalidParameterInputException {
    public ClassInstantiationException(String message, String paramName, Class<?> c) {
        super("Exception in parameter having name=" + paramName + " when initializing from class=" + c.toString() + " with message: " + message);
    }

    protected ClassInstantiationException(String message) {
        super(message);
    }

    public ClassInstantiationException(String message, String paramName, Class<?> c, Throwable cause) {
        super("Exception in parameter having name=" + paramName + " when initializing from class=" + c.toString() + " with message: " + message, cause);
    }
}
