package edu.sharif.ce.dml.mobisim.mediator.ui;

import edu.sharif.ce.dml.common.util.PublicConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 21, 2010
 * Time: 12:57:29 AM
 */
public class MenuItemPanel extends JPanel {
    private String text;
    private JLabel label;
    private boolean selected;
    private SelectObservable selectObservable;
    private static final Image UNSELECTED_IMAGE = new ImageIcon(PublicConfig.getInstance().getImagesFolderName()
            + "/unselected.png").getImage();
    private static final Image SELECTED_IMAGE = new ImageIcon(PublicConfig.getInstance().getImagesFolderName()
            + "/selected.png").getImage();
    private static final Dimension UNSELECTED_DIMENSION = new Dimension(140, 70);
    private static final Dimension SELECTED_DIMENSION = new Dimension(140, 70);
    private Font originalFont;
    private boolean initialized=false;
    private MenuItemPanelInitAciton action;

    public MenuItemPanel(String text) {
        this.text = text;
        this.selectObservable = new SelectObservable();
        this.setLayout(new BorderLayout());
        label = new JLabel(text);
        label.setForeground(Color.white);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        originalFont = label.getFont();
        label.setFont(new Font(originalFont.getFontName(), originalFont.getStyle(), 12));
        originalFont = label.getFont();
        /*Box h = Box.createHorizontalBox();
        h.add(Box.createHorizontalStrut(5));
        h.add(label);
        h.add(Box.createHorizontalStrut(5));*/
        this.add(Box.createHorizontalStrut(10),BorderLayout.EAST);
        this.add(Box.createHorizontalStrut(10),BorderLayout.WEST);
        this.add(label, BorderLayout.CENTER);

        setSelected(false);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!selected) {
                    setSelected(!selected);
                }
            }
        });
    }

    public void setSelected(boolean b) {
        if (selected != b && b) {
            selectObservable.change(this);
        }
        selected = b;
        if (selected) {
            this.setPreferredSize(SELECTED_DIMENSION);
            label.setFont(new Font(originalFont.getFontName(), Font.BOLD, originalFont.getSize()));
        } else {
            this.setPreferredSize(UNSELECTED_DIMENSION);
            label.setFont(new Font(originalFont.getFontName(), Font.PLAIN, originalFont.getSize()));
        }
        repaint();
    }

    public void setAction(MenuItemPanelInitAciton action) {
        this.action = action;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getText() {
        return text;
    }

    public void addObserver(Observer o) {
        selectObservable.addObserver(o);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (selected) {
            g2.drawImage(SELECTED_IMAGE, 0, 0, SELECTED_DIMENSION.width, SELECTED_DIMENSION.height, this);
        } else {
            g2.drawImage(UNSELECTED_IMAGE, 0, 0, UNSELECTED_DIMENSION.width, UNSELECTED_DIMENSION.height, this);
        }
    }

    public class SelectObservable extends Observable {

        protected synchronized void setChanged() {
            super.setChanged();
        }

        public void change(Object arg) {
            setChanged();
            notifyObservers(arg);
        }
    }

    public void init(){
        if (!initialized){
            initialized=true;
            action.init(this);
        }
    }
}

abstract class MenuItemPanelInitAciton {
    abstract void init(MenuItemPanel item);
}
