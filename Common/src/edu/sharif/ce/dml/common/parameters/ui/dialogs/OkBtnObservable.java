/*
 * Copyright (c) 2005-2009 by Masoud Moshref Javadi <moshref@ce.sharif.edu>, http://ce.sharif.edu/~moshref
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

package edu.sharif.ce.dml.common.parameters.ui.dialogs;

import java.util.Observable;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2009
 * Time: 10:21:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class OkBtnObservable extends Observable {

    public OkBtnObservable() {
    }

    protected synchronized void setChanged() {
        super.setChanged();
    }

    public void change(Object arg) {
        setChanged();
        notifyObservers(arg);
    }

}
