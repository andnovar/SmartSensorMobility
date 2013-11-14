package edu.sharif.ce.dml.mobisim.mediator.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 21, 2010
 * Time: 1:59:38 AM
 */
public class MenuPanel extends JPanel implements Observer {
    private java.util.List<MenuItemPanel> menuItems;
    private MenuItemPanel selected;

    private MenuItemChangedObservable itemChangedObservable;

    public MenuPanel() {
        menuItems = new ArrayList<MenuItemPanel>();
        itemChangedObservable=new MenuItemChangedObservable();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void addItem( MenuItemPanel item){
        this.add(item);
        menuItems.add(item);
        item.addObserver(this);
    }

    public void addObserver (Observer o){
        itemChangedObservable.addObserver(o);
    }

    public void setSelected(MenuItemPanel item){
        if (selected!=null){
            selected.setSelected(false);
        }
        if (selected!=item){
            itemChangedObservable.change(item);
        }
        selected= item;
    }

    public void update(Observable o, Object arg) {
        setSelected((MenuItemPanel) arg);
    }

    public MenuItemPanel getItem(int index){
        return menuItems.get(index);
    }

    public class MenuItemChangedObservable extends Observable {
        protected synchronized void setChanged() {
            super.setChanged();
        }

        public void change(Object arg) {
            setChanged();
            notifyObservers(arg);
        }
    }
}
