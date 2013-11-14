package edu.sharif.ce.dml.mobisim.mediator.ui;

import edu.sharif.ce.dml.common.ui.forms.FrameTemplate;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 21, 2010
 * Time: 10:45:08 AM
 */
public class TabbedPanel extends JPanel {
    private String title;
    private JTabbedPane tabbedPane;

    public TabbedPanel(String title) {
        this.title = title;
        tabbedPane=new JTabbedPane();
        this.add(tabbedPane);
        this.setLayout(new GridLayout());
    }

    public String getTitle() {
        return title;
    }

    public void addTab(FrameTemplate app){
        tabbedPane.add(app.getTitle(),app.getContentPane());
    }
}
