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

package edu.sharif.ce.dml.common.ui;

import edu.sharif.ce.dml.common.ui.forms.DialogTemplate;
import edu.sharif.ce.dml.common.util.PublicConfig;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 22, 2007
 * Time: 10:32:46 PM<br>
 */
public class AboutForm extends DialogTemplate {

    private static final String LICENSE = "<p align='center'>GNU General Public License</p>" +
            "<p align='justify'><font size='10p'>" +
            "This program is free software: you can redistribute it and/or modify" +
            "it under the terms of the GNU General Public License as published by" +
            "the Free Software Foundation, either version 3 of the License, or" +
            "(at your option) any later version.<br/>" +
            "This program is distributed in the hope that it will be useful," +
            "but WITHOUT ANY WARRANTY; without even the implied warranty of" +
            "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the" +
            "GNU General Public License for more details.</p><p align='justify'>" +
            "You should have received a copy of the GNU General Public License" +
            "along with this program.  If not, see <font color='blue'>http://www.gnu.org/licenses/</font></p><p align='justify'>"+
            "Note that you must refer <blockquote ><b>" +
            "\"Mousavi, S. M., Rabiee, H. R., Moshref, M., and Dabirmoghaddam, A. 2007. MobiSim: A Framework for Simulation of Mobility Models in Mobile Ad-Hoc Networks. In Proceedings of the Third IEEE international Conference on Wireless and Mobile Computing, Networking and Communications (October 08 - 10, 2007). WIMOB. IEEE Computer Society, Washington, DC, 82. \" " +
            "</b></blockquote > in any paper or document which used this program. <br/>" +
            "Please contact <font color='blue'>masood.moshref.j@gmail.com</font> or find me on <font color='blue'>http://masoudmoshref.com</font>" +
            " to tell any comment." +
            "</font></p><p>This product includes software developed by\n" +
            "The Apache Software Foundation (http://www.apache.org/).</p>";

    public AboutForm(JFrame owner) throws HeadlessException {
        super(owner, "About", true);
        JPanel mainPanel;
        mainPanel = new JPanel();
        this.getContentPane().add(mainPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        {
            Box h = Box.createHorizontalBox();
            mainPanel.add(h);
            JLabel mobisimLbl = new JLabel("MobiSim v3");
            h.add(mobisimLbl);
            mobisimLbl.setForeground(Color.blue);
            mobisimLbl.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 36));
            h.add(Box.createHorizontalGlue());
        }

        {
            JTextArea contributorsTextArea = new JTextArea();
            contributorsTextArea.setEditable(false);
            mainPanel.add(contributorsTextArea);
            mainPanel.add(Box.createVerticalStrut(10));
            contributorsTextArea.setText("Project website: http://sourceforge.net/projects/mobisim \n" +
                    "Developed by:\n" +
                    "Masoud Moshref Javadi (masood.moshref.j@gmail.com) from 2007\n" +
                    "credits:\n" +
                    "    Hamid Reza Rabiee\n" +
                    "    Seyed Morteza Mousavi\n" +
                    "    Ali Dabirmoghaddam\n"+
                    "for previous versions contributions");
        }
        {
            JTextPane licenseTextPane = new JTextPane();

            licenseTextPane.setContentType("text/html");
            licenseTextPane.setText(LICENSE);

            licenseTextPane.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(licenseTextPane);
            scrollPane.setPreferredSize(new Dimension(400, 200));
            mainPanel.add(scrollPane);

        }
        mainPanel.add(Box.createVerticalGlue());

        /*{
            Box h = Box.createHorizontalBox();
            mainPanel.add(h);
            h.add(Box.createHorizontalGlue());
            JLabel iconLbl = new JLabel();
            h.add(iconLbl);

            iconLbl.setIcon(new ImageIcon(PublicConfig.getInstance().getImagesFolderName()+"/dml.png"));
            iconLbl.setText("");
        }*/
    }

    private String loadFromFile() {
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new FileReader("license.txt"));
            while (br.ready()) {
                sb.append(br.readLine()).append("");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "The license file not found in the root of project",
                    "License file not found", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    static JDialog frame;

    public static void createGUI(JFrame owner) {
        frame = new AboutForm(owner);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        createGUI(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}

