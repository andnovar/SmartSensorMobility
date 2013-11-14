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

package edu.sharif.ce.dml.common.parameters.logic.parameterable;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.ClassInstantiationException;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.BooleanParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.StringParameter;
import edu.sharif.ce.dml.common.parameters.ui.GraphicalStandAloneObject;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.complex.FileUIParameter;
import edu.sharif.ce.dml.common.util.FileManager;
import edu.sharif.ce.dml.common.util.InvalidRequiredInputFileException;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2007
 * Time: 4:50:49 PM<br/>
 * Manages a file parameter, and has a special UI<br/>
 * parameters are:<br/>
 * {@link edu.sharif.ce.dml.common.parameters.logic.primitives.StringParameter} fileaddress<br/>
 * {@link edu.sharif.ce.dml.common.parameters.logic.primitives.StringParameter} defaultfilter : default file filter<br/>
 * {@link edu.sharif.ce.dml.common.parameters.logic.primitives.BooleanParameter} multiple : if multiple file allowed.<br/>
 * todo: possible filefilter parameter should be added
 */
public class FileParameter extends ParameterableImplement implements GraphicalStandAloneObject {
    /**
     * internal file object
     */
    private File file;
    private boolean multiple;
    //    private FileFilter[] filters;
    private FileFilter defaultFilter;
    private String fileString;
    private boolean input = false;

    public FileParameter() {
        super();
        file = new File("");
        multiple = false;
        defaultFilter = null;
        fileString = null;
    }

    /**
     * @param name
     * @param defaultValue  default file
     * @param multiple      if it may contains multiple files.
     * @param defaultFilter default file filter
     */
    public FileParameter(String name, File defaultValue, boolean multiple,
//                         FileFilter[] filters,
                         FileFilter defaultFilter) {
        this.file = defaultValue;
        this.multiple = multiple;
//        this.filters = filters;
        this.defaultFilter = defaultFilter;
    }

    /**
     * @return internal file object
     */
    public File getValue() {
        if (file == null) {
            try {
                setValue(FileManager.getInstance().getFile(fileString, getName(), false, new FileFilter[]{defaultFilter}, input));
            } catch (InvalidRequiredInputFileException e) {
                e.printStackTrace();
            }
        }
        if (file.isDirectory())
            //creates the directory if it doesn't exist
            if (!file.exists()) {
                file.mkdirs();
            }
        return file;
    }

    /**
     * @param o a {@link java.io.File} object
     */
    public void setValue(Object o) {
        setFile((File) o);
    }

    private void setFile(File f) {
        file = f;
        fileString = file.getPath();
    }

    /*public void setInitData(GeneralDataParameter dataParameter) throws InvalidParameterInputException {
        super.setInitData(dataParameter);
        if (dataParameter instanceof ParameterableData) {
            try {
                Map<String, GeneralDataParameter> params = ((ParameterableData) dataParameter).getParameters();
                setStringValue(((StringDataParameter) params.get("fileaddress")).getValue());
                multiple = BooleanParameter.convertBoolean(((StringDataParameter) params.get("multiple")).getValue());
                defaultFilter = (FileFilter) Class.forName((((StringDataParameter) params.get("defaultfilter")).
                        getValue())).getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new InvalidParameterInputException("error in initiating parameter");
            }
        } else {
            throw new InvalidParameterInputException("invalid dataParameter type");
        }
    }*/

    public boolean isMultiple() {
        return multiple;
    }

//    public FileFilter[] getFilters() {
//        return filters;
//    }

    public FileFilter getDefaultFilter() {
        return defaultFilter;
    }

    public String toString() {
        return getName();
    }

    public void setStringValue(String s) {
        //set null to load next
        file = null;
        this.fileString = s;
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameteres = new HashMap<String, Parameter>();
        parameteres.put("fileaddress", new StringParameter("fileaddress", fileString));
        parameteres.put("isinput", new BooleanParameter("isinput", input));
        parameteres.put("multiple", new BooleanParameter("multiple", multiple));
        parameteres.put("defaultfilter", new StringParameter("defaultfilter",
                defaultFilter == null ? "" : defaultFilter.getClass().getName()));
        return parameteres;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        String filterClass = ((StringParameter) parameters.get("defaultfilter")).getValue();
        try {
            Class<?> aClass = Class.forName(filterClass);
            try {
                setStringValue(((StringParameter) parameters.get("fileaddress")).getValue());
                multiple = ((BooleanParameter) parameters.get("multiple")).getValue();
                defaultFilter = (FileFilter) aClass.getConstructor().newInstance();
                input = ((BooleanParameter) parameters.get("isinput")).getValue();
            } catch (InvocationTargetException e) {
                throw new ClassInstantiationException(e.getMessage(), "defaultfilter", aClass, e);
            } catch (NoSuchMethodException e) {
                throw new ClassInstantiationException(e.getMessage(), "defaultfilter", aClass, e);
            } catch (IllegalAccessException e) {
                throw new ClassInstantiationException(e.getMessage(), "defaultfilter", aClass, e);
            } catch (InstantiationException e) {
                throw new ClassInstantiationException(e.getMessage(), "defaultfilter", aClass, e);
            }
        } catch (ClassNotFoundException e) {
            throw new InvalidParameterInputException(e.getMessage(), "defaultfilter", filterClass, e);
        }

    }

    public NewUIParameter getUIParameter(boolean showInternalParameterables) {
        return new FileUIParameter(getName(), getValue(), multiple, new FileFilter[]{defaultFilter}, defaultFilter);
    }

    public void setUIParameterValue(NewUIParameter uiParameter) {
        setFile((File) uiParameter.getValue());
        //todo filter
    }

    public void updateUIParameter(NewUIParameter uiParameter) throws InvalidParameterInputException {
        FileUIParameter fileUIParameter = (FileUIParameter) uiParameter;
        fileUIParameter.setValue(getValue());
        fileUIParameter.setMultiple(isMultiple());
    }
}
