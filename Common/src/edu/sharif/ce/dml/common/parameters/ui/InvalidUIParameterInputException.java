package edu.sharif.ce.dml.common.parameters.ui;

import edu.sharif.ce.dml.common.GeneralException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 28, 2010
 * Time: 11:23:45 AM
 */
public class InvalidUIParameterInputException extends GeneralException {
    public InvalidUIParameterInputException(String message) {
        super(message);
    }

    public InvalidUIParameterInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
