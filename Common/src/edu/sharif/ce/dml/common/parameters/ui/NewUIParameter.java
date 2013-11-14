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

package edu.sharif.ce.dml.common.parameters.ui;


import javax.swing.*;
import java.awt.*;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2007
 * Time: 2:37:26 PM<br/>
 * The UI class for {@link edu.sharif.ce.dml.common.parameters.logic.Parameter} objects. Each Parameter may have a
 * special concrete class of this class.
 */
public abstract class NewUIParameter extends JPanel {
    public static final String ARRAY_SEPARATOR = ",";

    /**
     * usually used to find matching parameter.
     */
    protected String name;

    /**
     * internal box of the component that each subclass should add subsequent component to this object.
     */
    protected MyBox box;

    /**
     * gridbag constraint which used as default setting to add components in the box.
     */
    private static GridBagConstraints c = new GridBagConstraints();

    static {
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1;
        c.ipadx=5;
    }

    protected NewUIParameter(String name) {
        super(new GridBagLayout());
        box = new MyBox();
        this.add(box.p, c);
        this.name = name;
    }

    /**
     * usually used to find matching parameter.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected JLabel createLabel() {
        JLabel nameLbl = new JLabel(name + ": ");
        c.weightx = 0;
        box.add(nameLbl, c);
        c.weightx = 1;
        return nameLbl;
    }

    /**
     * @return user entered value. This method has strong coupling to related {@link edu.sharif.ce.dml.common.parameters.logic.Parameter} object.
     */
    public abstract Object getValue();

    /**
     * adds an observer to for the change of current UI component. Default is to do nothing.
     * @param o
     */
    public void addObserver(Observer o) {

    }

    protected class MyBox {
        JPanel p;

        public MyBox() {
            p = new JPanel(new GridBagLayout());
        }

        /**
         * adds <tt>comp</tt> to this box using defualt settings.
         * @param comp
         */
        public void add(Component comp) {
            p.add(comp, c);
        }

        /**
         * adds <tt>comp</tt> to this box using <tt>c2</tt> settings
         * @param comp
         * @param c2
         */
        public void add(Component comp, GridBagConstraints c2) {
            p.add(comp, c2);
        }
    }
}
