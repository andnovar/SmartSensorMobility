package edu.sharif.ce.dml.common.parameters.ui.primitives;

import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterableConfigDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: 12/29/10
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ButtonUIParameter extends NewUIParameter implements Observer {
        private JButton showBtn;
        private internalWindowObservable observable = new internalWindowObservable();

        public ButtonUIParameter(String name) {
            super(name);
            showBtn = new JButton("Show");
            JLabel l = createLabel();
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 0;
            c.anchor = GridBagConstraints.FIRST_LINE_END;
            c.fill = GridBagConstraints.NONE;
            c.weightx = 1;
            box.add(Box.createHorizontalStrut(1), c);
            c.weighty = 0;
            box.add(showBtn, c);

            l.setLabelFor(showBtn);


        }

        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            showBtn.setEnabled(enabled);
        }

        public String getValue() {
            return "";
        }

        public void setValue(Object v) throws InvalidParameterInputException {

        }

        public String toString() {
            return "Map Editor";
        }

        public void addObserver(Observer o) {
            observable.addObserver(o);
        }

        public void update(Observable o, Object arg) {
            observable.change();
        }

        public void setActionListener(ActionListener actionListener) {
             showBtn.addActionListener(actionListener);
        }

        private class internalWindowObservable extends Observable {
            protected synchronized void setChanged() {
                super.setChanged();
            }

            public void change() {
                setChanged();
                notifyObservers(ParameterableConfigDialog.UPDATE_UIPARAMETERS);
            }
        }
    }
