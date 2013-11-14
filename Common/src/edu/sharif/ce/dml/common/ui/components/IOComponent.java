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

package edu.sharif.ce.dml.common.ui.components;

import edu.sharif.ce.dml.common.data.trace.filter.FileFilters;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 1, 2007
 * Time: 8:21:17 AM<br>
 * a UI component that has two {@link edu.sharif.ce.dml.common.ui.components.FileSelector} used when Trace input and
 * single output are necessary. usually has many input files and one output directory or file
 */
public class IOComponent extends JPanel {
    private final FileSelector traceSelector;
    private final FileSelector outputSelector;

    /**
     * @param outFileFilters output file selector filter
     */
    public IOComponent(FileFilter[] outFileFilters) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        traceSelector = new FileSelector(true, "Trace", FileFilters.getAllTraceFilters());
        add(traceSelector);
        add(Box.createVerticalStrut(10));
        outputSelector = new FileSelector(false, "Output", outFileFilters);
        add(outputSelector);
    }

    /**
     * @return input files
     */
    public File[] getInputFiles() {
        if (traceSelector.isFileSelected()) {
            return traceSelector.getSelectedFiles();
        } else {
            JOptionPane.showMessageDialog(traceSelector, "Please select input trace files");
            return null;
        }
    }

    public FileFilter getSelectedFilter(){
        return traceSelector.getSelectedFileFilter();
    }

    /**
     * @return selected output directory or file
     */
    public File getOutputFile() {
        if (outputSelector.isFileSelected()) {
            return outputSelector.getSelectedFiles()[0];
        } else {
            //FIXME: returns null!
            if (JOptionPane.showConfirmDialog(null, "You did not selected output folder. Do you want to use input folder as output too?",
                    "No output folder", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                File firstFile = getInputFiles()[0];
                return new File(firstFile.getPath().substring(0, firstFile.getPath().indexOf(firstFile.getName())));
            } else {
                return null;
            }
        }

    }
}
