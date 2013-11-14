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

package edu.sharif.ce.dml.common.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Dec 1, 2006
 * Time: 3:24:01 PM
 * <br/>This class provides logging services for all classes in application. 
 */
public class DevelopmentLogger {
    public static Logger logger = Logger.getLogger(DevelopmentLogger.class);
   static {
   ConsoleAppender appender = new ConsoleAppender(new SimpleLayout());
   logger.addAppender(appender);
   logger.setLevel(Level.ALL);}
   //PropertyConfigurator.configure("plainlog4jconfig.properties");
   //DOMConfigurator.configure("xmllog4jconfig.xml");
}
