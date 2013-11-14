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

package edu.sharif.ce.dml.common.util.io.output;

import org.apache.log4j.*;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Aug 7, 2006
 * Time: 9:31:25 PM
 * <br/>This class provides output services for all application classes
 */
public class OutputLogger {
    public static Logger logger = Logger.getLogger(OutputLogger.class);
     static String outputFileName;
    private static FileAppender fAppender;
    private static Layout txtoutput = new PatternLayout("%m%n");

    static {
        logger.addAppender(fAppender);
        logger.setLevel(Level.INFO);
    }

    /**
     * sets the output file name, creates and opens it, and close last file if any
     *
     * @param outputFileName new output file
     */
    public static void setOutputFileName(String outputFileName) {
        //todo should be checked for bug
        if (outputFileName == null) {
            OutputLogger.outputFileName = generateOutputFileName(OutputLogger.outputFileName);
        }else{
            OutputLogger.outputFileName = outputFileName;
        }
        File f = new File(OutputLogger.outputFileName);
        if (f.exists()){
            f.delete();
        }
        if (fAppender != null) {
            fAppender.close();
            logger.removeAppender(fAppender);
        }
        try {
            fAppender = new FileAppender(txtoutput, OutputLogger.outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.addAppender(fAppender);

    }

    public static String generateOutputFileName(String outputFileName) {
        if (outputFileName.matches("^.*\\d+\\.\\w+$")) {
            int lastDotIndex = outputFileName.lastIndexOf(".");
            String s = outputFileName.substring(0, lastDotIndex);
            Pattern p = Pattern.compile("\\d+$");
            Matcher m = p.matcher(s);
            m.find();
            String sNumber = m.group();
            return outputFileName.substring(0, m.start()) +
                    +(Integer.parseInt(sNumber) + 1) + outputFileName.substring(lastDotIndex);
        }
        if (outputFileName.matches("^.*\\d+$")) {
            Pattern p = Pattern.compile("\\d+$");
            Matcher m = p.matcher(outputFileName);
            m.find();
            String sNumber = m.group();
            return outputFileName.substring(0, m.start()) +(Integer.parseInt(sNumber) + 1) ;
        }
        if (outputFileName.lastIndexOf(".")>-1){
            return outputFileName.substring(0,outputFileName.lastIndexOf("."))+"1"+
                    outputFileName.substring(outputFileName.lastIndexOf("."));
        }
        return outputFileName + "1";
    }
    //PropertyConfigurator.configure("plainlog4jconfig.properties");
    //DOMConfigurator.configure("xmllog4jconfig.xml");
}
