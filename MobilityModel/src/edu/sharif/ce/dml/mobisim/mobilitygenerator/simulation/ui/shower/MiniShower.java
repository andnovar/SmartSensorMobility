package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.shower;

import edu.sharif.ce.dml.common.GeneralException;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.ui.forms.DialogTemplate;
import edu.sharif.ce.dml.common.util.PublicConfig;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.Simulation;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 3, 2010
 * Time: 2:13:36 PM
 */
public class MiniShower<T extends SnapShotData> extends DialogTemplate {
    private final JProgressBar timeBar;
    private final JLabel timeValueLbl;
    private AbstractDrawPanel<T> selectedDrawPanel;
    private final JComboBox drawPanelCmb;
    private final JToggleButton startPauseBtn;
    private final JSlider speedSlider;
    private final JPanel drawPanelContainer;
    private final JButton restartBtn;
    private File outputFile;
    private JLabel currentFileText;

    public static final int MAX_SIZE=600;
    public static final int MIN_SIZE=400;

    public MiniShower(JFrame owner, String title, Map<String, String> parameters,
                      Long maxSimTime, List<AbstractDrawPanel<T>> drawPanels) throws HeadlessException {
        super(owner, title, true);
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);
        {//toolbar
            JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
            toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
            toolbar.setFloatable(false);
            JPanel p = new JPanel(new BorderLayout());
            p.add(toolbar, BorderLayout.WEST);
            mainPanel.add(p, BorderLayout.NORTH);
            String imagesFolder = PublicConfig.getInstance().getImagesFolderName();
            {
                startPauseBtn = new JToggleButton();
                toolbar.add(startPauseBtn);
                startPauseBtn.setIcon(new ImageIcon(imagesFolder + "/start.png"));
                startPauseBtn.setSelectedIcon(new ImageIcon(imagesFolder + "/pause.png"));
                startPauseBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (startPauseBtn.isSelected()) {
                            start();
                        } else {
                            pause();
                        }
                    }
                });
                restartBtn = new JButton(new ImageIcon(imagesFolder + "/restart.png"));
                restartBtn.setToolTipText("Restart");
                restartBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        reset();
                    }
                });
                toolbar.add(restartBtn);
            }
            toolbar.add(new JSeparator(JSeparator.VERTICAL));
            {
//zoom and pan
                JButton zoominBtn = new JButton(new ImageIcon(imagesFolder + "/zoomin.png"));
                zoominBtn.setToolTipText("Zoom In");
                toolbar.add(zoominBtn);
                JButton zoomoutBtn = new JButton(new ImageIcon(imagesFolder + "/zoomout.png"));
                zoomoutBtn.setToolTipText("Zoom Out");
                toolbar.add(zoomoutBtn);
                zoominBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        float zoomValue = selectedDrawPanel.getZoomFactor() * 1.1f;
                        selectedDrawPanel.setZoomFactor(zoomValue);
                        //pan
                    }
                });
                zoomoutBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        float zoomValue = selectedDrawPanel.getZoomFactor() * 0.9f;
                        selectedDrawPanel.setZoomFactor(zoomValue);
                        //pan
                    }
                });

                final JToggleButton panBtn = new JToggleButton(new ImageIcon(imagesFolder + "/pan.png"));
                panBtn.setToolTipText("Pan");
                toolbar.add(panBtn);
                panBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        selectedDrawPanel.setPanning(panBtn.isSelected());
                    }
                });
            }
            toolbar.add(new JSeparator(JSeparator.VERTICAL));
            {
//speed
                speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 30, 10);
//        speedSlider.setMajorTickSpacing(5);
                speedSlider.setMinorTickSpacing(1);
                Hashtable<Integer, JLabel> speedLabels = new Hashtable<Integer, JLabel>();
                speedLabels.put(1, new JLabel("1"));
                speedLabels.put(10, new JLabel("10"));
                speedLabels.put(20, new JLabel("20"));
                speedLabels.put(30, new JLabel("Max"));
                speedSlider.setLabelTable(speedLabels);
                speedSlider.setSnapToTicks(true);
                speedSlider.setPaintTicks(true);
                speedSlider.setPaintLabels(true);
                JLabel speedLbl = new JLabel("Speed ");
                speedLbl.setLabelFor(speedSlider);
                toolbar.add(speedLbl);
                toolbar.add(speedSlider);
            }
            toolbar.add(new JSeparator(JSeparator.VERTICAL));
            {
                JPanel comboBoxPanel = new JPanel();
                comboBoxPanel.setLayout(new BoxLayout(comboBoxPanel, BoxLayout.Y_AXIS));
                comboBoxPanel.add(Box.createVerticalGlue());
                drawPanelCmb = new JComboBox();
                drawPanelCmb.setModel(new DefaultComboBoxModel(drawPanels.toArray()));
                drawPanelCmb.setEditable(false);
                comboBoxPanel.add(drawPanelCmb);
                comboBoxPanel.add(Box.createVerticalGlue());
                toolbar.add(comboBoxPanel);
                //add presenter specific options
                final JPanel toolbarDrawPanelContainer = new JPanel();
                toolbarDrawPanelContainer.setLayout(new BoxLayout(toolbarDrawPanelContainer, BoxLayout.Y_AXIS));
                toolbar.add(toolbarDrawPanelContainer);
                toolbarDrawPanelContainer.setBorder(BorderFactory.createEtchedBorder());
                drawPanelCmb.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        selectedDrawPanel = (AbstractDrawPanel<T>) drawPanelCmb.getSelectedItem();
                        selectedDrawPanel.reset();
                        drawPanelContainer.removeAll();
                        drawPanelContainer.add(selectedDrawPanel, BorderLayout.CENTER);
                        drawPanelContainer.revalidate();
                        toolbarDrawPanelContainer.removeAll();
                        toolbarDrawPanelContainer.add(Box.createVerticalGlue());
                        toolbarDrawPanelContainer.add(selectedDrawPanel.getPanel());
                        toolbarDrawPanelContainer.add(Box.createVerticalGlue());
                        toolbarDrawPanelContainer.revalidate();
                    }
                });
            }
            toolbar.add(Box.createHorizontalGlue());
        }

        {//infoPanel
            JPanel infoPanel = new JPanel(new BorderLayout());
            mainPanel.add(infoPanel, BorderLayout.WEST);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JPanel topPanel = new JPanel();
            {//progressbar
                topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
                infoPanel.add(topPanel, BorderLayout.NORTH);
                Box h = Box.createHorizontalBox();
                JLabel timeBarLbl = new JLabel("Time:");
                h.add(timeBarLbl);
                timeValueLbl = new JLabel("");
                h.add(timeValueLbl);
                h.add(Box.createHorizontalGlue());
                topPanel.add(h);
                timeBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, maxSimTime.intValue());
                topPanel.add(timeBar);
            }
            {//current file
                JPanel filePanel = new JPanel();
                topPanel.add(filePanel);
                filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));
                JLabel currentFileLbl = new JLabel("Current File:");
                filePanel.add(currentFileLbl);
                currentFileText = new JLabel();
                currentFileLbl.setLabelFor(currentFileText);
                filePanel.add(currentFileText);
                if (Desktop.isDesktopSupported()) {
                    JButton openOutputFileBtn = new JButton("Open");
                    filePanel.add(Box.createHorizontalGlue());
                    filePanel.add(openOutputFileBtn);
                    openOutputFileBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            try {
                                Desktop.getDesktop().open(outputFile);
                            } catch (IOException e1) {
                                new GeneralException("An Exception in opening the output file occurred with message=" +
                                        e1.getMessage(), e1).showMessage(thisDialog);
                            }
                        }
                    });
                }
            }
            {//config panel
                JTextArea configArea = new JTextArea(15, 20);
                configArea.setEditable(false);
                StringBuffer sb = new StringBuffer("");
                for (String s : parameters.keySet()) {
                    sb.append(s).append("=").append(parameters.get(s)).append("\n");
                }
                configArea.setText(sb.toString());
                configArea.setFont(new Font(configArea.getFont().getFontName(), Font.PLAIN, 12));
                JScrollPane configPane = new JScrollPane(configArea);
                configPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                infoPanel.add(configPane, BorderLayout.CENTER);
            }
        }

        drawPanelContainer = new JPanel(new BorderLayout());
        drawPanelContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createBevelBorder(BevelBorder.RAISED)));
        mainPanel.add(drawPanelContainer, BorderLayout.CENTER);

        //set default values
        drawPanelCmb.setSelectedIndex(0);
        pause();

        //packing
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(MIN_SIZE+200,MIN_SIZE));
        setMaximumSize(new Dimension(MAX_SIZE+200,MAX_SIZE));
        pack();
    }

    public void setOutputFile(File file) {
        outputFile = file;
        currentFileText.setText(file.getName());
    }

    public void addRestartActionListener(ActionListener a) {
        restartBtn.addActionListener(a);
    }

    public void reset() {
        selectedDrawPanel.reset();
        pauseByForce();
    }

    public static void Init(Simulation simulation, JFrame owner, java.util.List<AbstractDrawPanel<SnapShotData>> drawPanels) {
        java.util.List<StringDataParameter> dataParameterList = Parameter.getFlatDataParameters(simulation.getParameters());
        Map<String, String> parameters = new TreeMap<String, String>();
        for (StringDataParameter stringDataParameter : dataParameterList) {
            parameters.put(stringDataParameter.getName(), stringDataParameter.getValue());
        }

        MiniShower<SnapShotData> shower = new MiniShower<SnapShotData>(owner, "Simulation Visualization",
                parameters, simulation.getMaxSimulationTime(), drawPanels);
        simulation.setShower(shower);
        simulation.reset();
        shower.setVisible(true);
    }

    public void setRestartAbility(boolean b){
        restartBtn.setVisible(b);
    }

    public void setSnapShot(T snapShot) {
        selectedDrawPanel.setSnapShot(snapShot);
    }

    private void start() {
        startPauseBtn.setToolTipText("Pause");
        speedSlider.setEnabled(false);
    }

    private void pause() {
        startPauseBtn.setToolTipText("Start");
        speedSlider.setEnabled(true);
    }

    public void pauseByForce() {
        startPauseBtn.setSelected(false);
        pause();
    }

    public void addStartPauseAction(ActionListener l) {
        startPauseBtn.addActionListener(l);
    }


    public void setCurrentTime(long time) {
        timeValueLbl.setText(time + "");
        timeBar.setValue((int) time);
    }

    public int getSpeedRatio() {
        return speedSlider.getValue();
    }

    public void setSize2(int width, int height) {
        setSize(width+300,height+100);
    }
}
