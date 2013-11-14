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

package edu.sharif.ce.dml.common.parameters.ui.complex;

import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.ui.components.FileSelector;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2007
 * Time: 2:35:44 PM<br/>
 * A parameter to manage a file as a parameter of a {@link edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable}
 */
public class FileUIParameter extends NewUIParameter implements Observer {
    /**
     * embedded file object
     */
    private File file;
    private FileSelector fileSelector;

    public FileUIParameter(String name, File defaultValue, boolean multiple, FileFilter[] filters, FileFilter defaultFilter) {
        super(name);
        fileSelector = new FileSelector(multiple, name, filters);
        fileSelector.setSelectedFiles(new File[]{defaultValue}, defaultFilter);
        fileSelector.addObserver(this);
        file = defaultValue;
        box.add(fileSelector);
    }

    public File getValue() {
        return file;
    }

    public void setMultiple(boolean b){
        fileSelector.setMultipleFile(b);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        fileSelector.setEnabled(enabled);
    }

    /**
     *
     * @param v an {@link File} object.
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     */
    public void setValue(Object v) throws InvalidParameterInputException {
        try {
            file = (File) v;
        } catch (Exception e) {
            throw new InvalidParameterInputException("File input Type exception: "+v.getClass(),getName(),v);
        }
    }

    /**
     *
     * @return file path.
     */
    public String getStringValue() {
        return file.getPath();
    }

    /**
     *
     * @param s file path
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     */
    public void setStringValue(String s) throws InvalidParameterInputException {
        setValue(new File(s));
    }

    /**
     * used first object in <tt>arg</tt> array.
     * @param o
     * @param arg an array of {@link File} objects.
     */
    public void update(Observable o, Object arg) {
        file = ((File[]) arg)[0];
    }
}
