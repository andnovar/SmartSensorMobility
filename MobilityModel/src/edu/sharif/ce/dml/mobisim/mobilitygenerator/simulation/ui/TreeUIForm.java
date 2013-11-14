package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui;

import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.TreeParameterableConfigDialog;
import edu.sharif.ce.dml.common.ui.forms.FrameTemplate;
import edu.sharif.ce.dml.common.util.FileManager;
import edu.sharif.ce.dml.common.util.PublicConfig;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.ScenarioConfigFileFilter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.Simulation;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.shower.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 1, 2010
 * Time: 12:17:39 PM
 */
public class TreeUIForm extends FrameTemplate {
    private TreeParameterableConfigDialog treeConfig;

    public TreeUIForm(String title, final Parameterable parameterable, boolean enableSimulation) throws HeadlessException {
        super(title);
        treeConfig = new TreeParameterableConfigDialog(parameterable, true);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(treeConfig.getContentPane(), BorderLayout.CENTER);
        this.setContentPane(mainPanel);
        {//toolbar
            JToolBar toolBar = new JToolBar();
            mainPanel.add(toolBar, BorderLayout.NORTH);

            String imagesFolder = PublicConfig.getInstance().getImagesFolderName();
            {//save, load root
                JButton saveRootBtn = new JButton(new ImageIcon(imagesFolder + "/saveroot.png"));
                JButton loadRootBtn = new JButton(new ImageIcon(imagesFolder + "/openroot.png"));
                saveRootBtn.setToolTipText("Save root node");
                loadRootBtn.setToolTipText("Load root node");
                toolBar.add(saveRootBtn);
                toolBar.add(loadRootBtn);
                saveRootBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        File[] saveOutputFile = FileManager.getInstance().showFileDialog("output", false, new FileFilter[]{new ScenarioConfigFileFilter()});
                        if (saveOutputFile != null && saveOutputFile.length > 0) {
                            treeConfig.saveRoot(saveOutputFile[0]);
                        }
                    }
                });

                loadRootBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        File[] loadInputFile = FileManager.getInstance().showFileDialog("input", false, new FileFilter[]{new ScenarioConfigFileFilter()});
                        if (loadInputFile != null && loadInputFile.length > 0) {
                            treeConfig.loadRoot(loadInputFile[0]);
                        }
                    }
                });
            }
            /*{//save load current
                JButton saveBtn = new JButton(new ImageIcon(imagesFolder + "/save.png"));
                JButton loadBtn = new JButton(new ImageIcon(imagesFolder + "/open.png"));
                saveBtn.setToolTipText("Save current node");
                loadBtn.setToolTipText("Load current node");
                toolBar.add(saveBtn);
                toolBar.add(loadBtn);
                saveBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        File[] saveOutputFile = FileManager.getInstance().showFileDialog("Select output File", false, new FileFilter[]{new ScenarioConfigFileFilter()});
                        if (saveOutputFile != null && saveOutputFile.length > 0) {
                            treeConfig.saveCurrent(saveOutputFile[0]);
                        }
                    }
                });

                loadBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        File[] loadInputFile = FileManager.getInstance().showFileDialog("Select input File", false, new FileFilter[]{new ScenarioConfigFileFilter()});
                        if (loadInputFile != null && loadInputFile.length > 0) {
                            treeConfig.loadToCurrent(loadInputFile[0]);
                        }
                    }
                });

            }*/

            if (enableSimulation) {
                JButton startSimulationBtn = new JButton(new ImageIcon(imagesFolder + "/run.png"));
                startSimulationBtn.setToolTipText("Run Simulation");
                toolBar.add(startSimulationBtn);
                startSimulationBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Simulation simulation = (Simulation) treeConfig.getRootElement();
                        java.util.List<AbstractDrawPanel<SnapShotData>> drawPanels = new ArrayList<AbstractDrawPanel<SnapShotData>>();
                        Model model = simulation.getCurrentModel();
                        int height = model.getMap().getHeight();
                        drawPanels.add(new InstanceDrawPanel(height, model));
                        drawPanels.add(new FadeInkyDrawPanel(height, model));
                        drawPanels.add(new InkyDrawPanel(height, model));
                        drawPanels.add(new LinkDrawPanel(height,model));
                        MiniShower.Init(simulation, thisFrame, drawPanels);
                    }
                });
            }
        }
        setPreferredSize(new Dimension(600, 600));
        pack();
    }


}
