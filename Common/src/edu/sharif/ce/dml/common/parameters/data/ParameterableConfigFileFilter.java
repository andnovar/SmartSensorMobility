package edu.sharif.ce.dml.common.parameters.data;

import edu.sharif.ce.dml.common.data.trace.filter.FileFilters;

import javax.swing.filechooser.FileFilter;
import java.io.File;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: 1/1/11
 * Time: 12:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class ParameterableConfigFileFilter extends FileFilter {
       public boolean accept(File f) {
        return f.isDirectory() || f.getName().matches(".*\\.xml$");
    }

    public String getDescription() {
        return "Configuration file";
    }
}
