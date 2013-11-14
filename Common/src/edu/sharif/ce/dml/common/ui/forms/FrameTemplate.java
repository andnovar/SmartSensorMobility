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

package edu.sharif.ce.dml.common.ui.forms;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 8, 2007
 * Time: 6:14:21 PM<br>
 * Default template for Frames
 */
public class FrameTemplate extends JFrame {
    /**
     * references to this object. usually used to create child windows in anonymouse classes
     */
    protected final JFrame thisFrame;
    private boolean firstPack = false;

    public static JFrame parent = new JFrame();

    public FrameTemplate(String title) throws HeadlessException {
        super(title);
        this.thisFrame = this;
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(panel);
    }

    public static JFrame getParentFrame(){
        return parent;
    }

    /**
     * overrided to set frame position at center of window
     */
    public void pack() {
        super.pack();
        if (!firstPack) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
            firstPack = true;
        }
    }


}
