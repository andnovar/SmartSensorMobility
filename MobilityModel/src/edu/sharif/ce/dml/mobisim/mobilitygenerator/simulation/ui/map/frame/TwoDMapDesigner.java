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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.frame;

import edu.sharif.ce.dml.common.ui.forms.DialogTemplate;
import edu.sharif.ce.dml.common.util.PublicConfig;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.MapHandleSupport;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.ConfigPanel2;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.multi.MultiMapHandleGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2009
 * Time: 9:58:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class TwoDMapDesigner extends DialogTemplate {
    private MapHandleSupport mapHandleSupport;


    private MapEditorDrawPanel mapEditorDrawPanel;
    private ConfigPanel2 configPanel;
    private final JSplitPane splitPane;
    private final JSlider zoomSlider = new JSlider(JSlider.HORIZONTAL, 1, 30, 10);
    private JButton updateBtn, fillBtn;
    private JScrollPane configPanelSP;
    private JToggleButton removeHandleBtn;
    private JToggleButton addHandleBtn;
    private JPanel handleConfigPanel;
    private final CloseObservable closeObservable = new CloseObservable();
    private JPanel addedToolbarPanel;
    private JToggleButton moveMapBtn;

    public TwoDMapDesigner(JFrame owner, String title, boolean modal, MapHandleSupport mapHandleSupport) throws HeadlessException {
        super(owner, title, modal);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        addedToolbarPanel = new JPanel(new GridLayout());

        setMapHandleSupport(mapHandleSupport);
        createLayout();
        updateButtons();
    }

    private void updateButtons() {
        boolean b = mapHandleSupport.getHandles().isAddPoint();
        addHandleBtn.setEnabled(b);
        removeHandleBtn.setEnabled(b);
    }


    //    public void setParameters(java.util.Map<String, Parameter> parameters) {
    public void setMapHandleSupport(MapHandleSupport mapHandleSupport) {
        this.mapHandleSupport = mapHandleSupport;
        handleConfigPanel = new JPanel();
        configPanel = new ConfigPanel2(mapHandleSupport, false, true);
        addedToolbarPanel.removeAll();
        MapHandleGroup mapHandleGroup = mapHandleSupport.getHandles();
        if (mapHandleGroup instanceof MultiMapHandleGroup) {
            addedToolbarPanel.add(((MultiMapHandleGroup) mapHandleGroup).getConfigToolbar());
        }
        mapEditorDrawPanel = new MapEditorDrawPanel(mapHandleSupport, MapEditorDrawPanel.HandleManamgementState.MOVE, handleConfigPanel, mapHandleGroup);
        mapEditorDrawPanel.setPreferredSize(new Dimension(600, 600));
        mapEditorDrawPanel.revalidate();
        splitPane.setLeftComponent(mapEditorDrawPanel);
        splitPane.setDividerLocation(-1);
    }

//    public java.util.Map<String, Parameter> getParameters() {
//        java.util.Map<String, Parameter> parameters = new HashMap<String, Parameter>();
//        parameters.put("mapHandleSupport", mapHandleSupport);
//        return parameters;
//    }


    public String toString() {
        return getName();
    }

    public void addObserver(Observer o) {
        closeObservable.addObserver(o);
    }

    private void createLayout() {

        {
            JPanel p = new JPanel(new BorderLayout());
            setContentPane(p);
            p.add(splitPane, BorderLayout.CENTER);
            {
                //toolbar
                JPanel toolbarPanel = new JPanel();
                toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.X_AXIS));
                JToolBar toolbar = new JToolBar();
                toolbar.setFloatable(false);
                toolbar.setRollover(true);
                toolbar.setFocusable(false);
                toolbarPanel.add(toolbar);
                toolbarPanel.add(addedToolbarPanel);
                toolbarPanel.add(Box.createHorizontalGlue());
                p.add(toolbarPanel, BorderLayout.PAGE_START);

                String imagesFolder = PublicConfig.getInstance().getImagesFolderName();

                JToggleButton moveBtn = new JToggleButton(new ImageIcon(imagesFolder + "/move.png"));
                moveBtn.setToolTipText("Move");
                toolbar.add(moveBtn);
                moveBtn.setSelected(true);

                addHandleBtn = new JToggleButton(new ImageIcon(imagesFolder + "/add.png"));
                addHandleBtn.setDisabledIcon(new ImageIcon(imagesFolder + "/adddisabled.png"));
                addHandleBtn.setToolTipText("Add");
                toolbar.add(addHandleBtn);

                removeHandleBtn = new JToggleButton(new ImageIcon(imagesFolder + "/remove.png"));
                removeHandleBtn.setDisabledIcon(new ImageIcon(imagesFolder + "/removedisabled.png"));
                removeHandleBtn.setToolTipText("Remove");
                toolbar.add(removeHandleBtn);

                moveMapBtn = new JToggleButton(new ImageIcon(imagesFolder + "/hand.png"));
                moveMapBtn.setToolTipText("Move Panel");
                toolbar.add(moveMapBtn, BorderLayout.SOUTH);

                ButtonGroup toolbarButtonGroup = new ButtonGroup();
                toolbarButtonGroup.add(addHandleBtn);
                toolbarButtonGroup.add(removeHandleBtn);
                toolbarButtonGroup.add(moveMapBtn);
                toolbarButtonGroup.add(moveBtn);

                moveBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (((JToggleButton) e.getSource()).isSelected()) {
                            setDrawPanelState(0);
                            mapEditorDrawPanel.setMapDragging(false);
                        }
                    }
                });

                addHandleBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (((JToggleButton) e.getSource()).isSelected()) {
                            setDrawPanelState(1);
                            mapEditorDrawPanel.setMapDragging(false);
                        }
                    }
                });
                removeHandleBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (((JToggleButton) e.getSource()).isSelected()) {
                            setDrawPanelState(2);
                            mapEditorDrawPanel.setMapDragging(false);
                        }
                    }
                });

                moveMapBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        mapEditorDrawPanel.setMapDragging(true);
                    }
                });

            }
        }
        JSplitPane configSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel rightPanel = new JPanel(new GridBagLayout());
        splitPane.setRightComponent(configSplitPane);
        {
            //right section
            GridBagConstraints constraints = new GridBagConstraints();
            {
                constraints.weightx = 1;
                constraints.fill = GridBagConstraints.BOTH;
                constraints.gridx = 0;
                constraints.ipady = 5;
            }
            {
                rightPanel.setBorder(BorderFactory.createEtchedBorder());
                splitPane.setOneTouchExpandable(true);
                //splitPane.setRightComponent(rightPanel);
                configPanelSP = new JScrollPane(configPanel);
                constraints.weighty = 1;
                rightPanel.add(configPanelSP, constraints);
                constraints.weighty = 0;
                configSplitPane.setTopComponent(rightPanel);

            }
            {
                String imagesFolder = PublicConfig.getInstance().getImagesFolderName();
                Box h1 = Box.createHorizontalBox();
                updateBtn = new JButton(new ImageIcon(imagesFolder + "/update.png"));
                updateBtn.setToolTipText("Update");
                fillBtn = new JButton(new ImageIcon(imagesFolder + "/fill.png"));
                fillBtn.setToolTipText("Fill");
                h1.add(updateBtn);
                h1.add(Box.createHorizontalStrut(10));
                h1.add(fillBtn);
                h1.add(Box.createHorizontalGlue());
                rightPanel.add(h1, constraints);
            }

            /*{
                Box h2 = Box.createHorizontalBox();
                h2.add(new JLabel("Zoom: "));
                h2.add(zoomSlider);
                rightPanel.add(h2, constraints);
                zoomSlider.setMinorTickSpacing(1);
                java.util.Hashtable<Integer, JLabel> zoomLables = new Hashtable<Integer, JLabel>();
                zoomLables.put(1, new JLabel("1"));
                zoomLables.put(10, new JLabel("10"));
                zoomLables.put(20, new JLabel("20"));
                zoomLables.put(30, new JLabel("Max"));
                zoomSlider.setLabelTable(zoomLables);
                zoomSlider.setSnapToTicks(true);
                zoomSlider.setPaintTicks(true);
                zoomSlider.setPaintLabels(true);
                zoomSlider.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        mapEditorDrawPanel.setZoomFactor((float) (((JSlider) e.getSource()).getValue() / 10.0));
                    }
                });
            }*/

            configSplitPane.setBottomComponent(handleConfigPanel);
            //
        }
        fillBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapEditorDrawPanel.fillMap();
                configPanel.updateUIFromParameterable();
                mapEditorDrawPanel.repaint();
            }
        });
        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configPanel.updateValues();
                mapEditorDrawPanel.updateFromMap();
                //check if some buttons should be enabled
                updateButtons();
            }
        });

        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                closeObservable.change();
            }
        });
        this.pack();
        if (rightPanel.getHeight()<100){
            configSplitPane.setDividerLocation(400);
        }


    }

    /**
     * @param v 0 for no button on, 1 for add, 2 for delete
     */
    private void setDrawPanelState(int v) {
        if (v == 1) {
            removeHandleBtn.setSelected(false);
            mapEditorDrawPanel.setState(MapEditorDrawPanel.HandleManamgementState.ADD);
        } else if (v == 2) {
            addHandleBtn.setSelected(false);
            mapEditorDrawPanel.setState(MapEditorDrawPanel.HandleManamgementState.DELETE);
        } else {
            mapEditorDrawPanel.setState(MapEditorDrawPanel.HandleManamgementState.MOVE);
        }

    }

    private class CloseObservable extends Observable {
        protected synchronized void setChanged() {
            super.setChanged();
        }

        public void change() {
            setChanged();
            notifyObservers(mapHandleSupport);
        }
    }


}
