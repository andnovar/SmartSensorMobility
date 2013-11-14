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

import edu.sharif.ce.dml.common.logic.worker.ProcessInstance;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 1, 2007
 * Time: 9:28:02 PM<br/>
 * 
 */
public class ProgressForm extends JDialog {
    private JProgressBar formProgressBar;
    private JTextPane textPane;

    private double realProgress;
    private double step;


    public ProgressForm(int taskSizes) {
        super((Frame) null, "Working . . .");
        step = 100d / taskSizes;

        formProgressBar = new JProgressBar(0, 100);
        JLabel frameLbl = new JLabel("Percent completed: ");
        formProgressBar.setStringPainted(true);
        JPanel mainPanel = new JPanel();
        getContentPane().add(mainPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder());
        Box v = Box.createVerticalBox();
        mainPanel.add(v);
        v.add(frameLbl);
        v.add(Box.createVerticalGlue());
        v.add(formProgressBar);

        textPane = new JTextPane();
        textPane.setEditable(false);
        StyledDocument doc = textPane.getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
        StyleConstants.setForeground(doc.addStyle("failure", regular), Color.red);
        StyleConstants.setForeground(doc.addStyle("success", regular), Color.blue);

        JScrollPane paneScrollPane = new JScrollPane(textPane);
        paneScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        paneScrollPane.setPreferredSize(new Dimension(450, 155));
        paneScrollPane.setMinimumSize(new Dimension(10, 10));

        v.add(paneScrollPane);

        pack();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        new Thread() {
            public void run() {
                setVisible(true);
            }
        }.start();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);


    }

    public void progress() {
        progress("", true);
    }

     public void progress(String message, boolean successful) {
         progress(new ProcessInstance(message,successful? ProcessInstance.ProcessResult.success: ProcessInstance.ProcessResult.failure,0));
     }

    public void progress(ProcessInstance result) {
        //update Bar
        realProgress += step;
        formProgressBar.setValue((int) Math.round(realProgress));
        if (formProgressBar.getValue() == 100) {
            formProgressBar.setValue(100);
        }

        StyledDocument doc = textPane.getStyledDocument();
        try {
            if (result.getResult().equals(ProcessInstance.ProcessResult.success)) {
                doc.insertString(doc.getLength(), result.getMessage()+ " ("+result.getDurationMilSec()+"ms) .... OK\n\r",
                        doc.getStyle("success"));
            } else {
                doc.insertString(doc.getLength(), result.getMessage() +" ("+result.getDurationMilSec()+"ms) .... Error !!\n\r",
                        doc.getStyle("failure"));
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

}

