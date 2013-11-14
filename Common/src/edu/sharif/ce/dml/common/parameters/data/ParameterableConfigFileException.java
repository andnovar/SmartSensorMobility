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

package edu.sharif.ce.dml.common.parameters.data;

import edu.sharif.ce.dml.common.GeneralException;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 4, 2006
 * Time: 8:47:17 PM<br/>
 * shows an exception in loading XML configuration file
 */
public class ParameterableConfigFileException extends GeneralException {
    public ParameterableConfigFileException(String message) {
        super(message);
    }
}
