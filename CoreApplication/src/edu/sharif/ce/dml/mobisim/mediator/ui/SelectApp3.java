package edu.sharif.ce.dml.mobisim.mediator.ui;

import edu.sharif.ce.dml.common.GeneralException;
import edu.sharif.ce.dml.common.ui.AboutForm;
import edu.sharif.ce.dml.common.ui.forms.FrameTemplate;
import edu.sharif.ce.dml.common.util.InvalidRequiredInputFileException;
import edu.sharif.ce.dml.common.util.PublicConfig;
import edu.sharif.ce.dml.common.util.ui.PropertiesEditorFrame;
import edu.sharif.ce.dml.mobisim.diagram.ui.EvaluationDiagramUI;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.ui.EvaluationAnalyzerFrame;
import edu.sharif.ce.dml.mobisim.evaluationanalyzer.ui.FeatureRankerFrame;
import edu.sharif.ce.dml.mobisim.evaluator.ui.MobilityEvaluationFrame;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.wizard.SelectApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 20, 2010
 * Time: 11:58:03 PM
 */
public class SelectApp3 extends FrameTemplate implements Observer {
    private JPanel mainPanel;
    private JPanel cardsPanel;
    //private Map<MenuItemPanel, JPanel> menuItemJPanel = new HashMap<MenuItemPanel, JPanel>();

    public SelectApp3(String title) throws HeadlessException {
        super(title);
        createGUI();
    }

    public void createGUI() {
        mainPanel = new JPanel(new BorderLayout());
        this.setContentPane(mainPanel);
        final MenuPanel menuPanel = new MenuPanel();
        mainPanel.add(menuPanel, BorderLayout.PAGE_START);
        cardsPanel = new JPanel(new CardLayout());
        mainPanel.add(cardsPanel, BorderLayout.CENTER);
        {

            //menuPanel.setPreferredSize(new Dimension(600, 200));
            menuPanel.addObserver(this);
            {
                MenuItemPanel newItem = new MenuItemPanel("Mobility Generator");
                menuPanel.addItem(newItem);
                newItem.setAction(new MenuItemPanelInitAciton() {
                    public void init(MenuItemPanel item) {
                        FrameTemplate app = new SelectApp();
                        cardsPanel.add(app.getContentPane(), item.getText());
                    }
                });
            }

            {
                MenuItemPanel newItem = new MenuItemPanel("Evaluation");
                menuPanel.addItem(newItem);
                newItem.setAction(new MenuItemPanelInitAciton() {
                    public void init(MenuItemPanel item) {
                        TabbedPanel evaluatorPanel = new TabbedPanel(item.getText());
                        evaluatorPanel.addTab(new MobilityEvaluationFrame());
                        evaluatorPanel.addTab(new EvaluationDiagramUI());
                        cardsPanel.add(evaluatorPanel, item.getText());
                    }
                });
            }

            {

                MenuItemPanel newItem = new MenuItemPanel("Classifier");
                menuPanel.addItem(newItem);
                newItem.setAction(new MenuItemPanelInitAciton() {
                    public void init(MenuItemPanel item) {
                        TabbedPanel evaluatorPanel = new TabbedPanel(item.getText());
                        evaluatorPanel.addTab(new EvaluationAnalyzerFrame());
                        evaluatorPanel.addTab(new FeatureRankerFrame());
                        cardsPanel.add(evaluatorPanel, item.getText());
                    }
                });
            }

//             {
//                MenuItemPanel newItem = new MenuItemPanel("Other");
//                menuPanel.addItem(newItem);
//                newItem.setAction(new MenuItemPanelInitAciton() {
//                    public void init(MenuItemPanel item) {
//                        TabbedPanel OtherPanel = new TabbedPanel(item.getText());
//                        OtherPanel.addTab(new PowerGeneratorForm());
//                        OtherPanel.addTab(new LocationPredictorForm());
//                        cardsPanel.add(OtherPanel, item.getText());
//                    }
//                });
//            }


            MenuItemPanel firstItem = menuPanel.getItem(0);
            menuPanel.setSelected(firstItem);
            firstItem.setSelected(true);

        }

        {
            JMenuBar menuBar = new JMenuBar();
            this.setJMenuBar(menuBar);
            JMenu fileMenu = new JMenu("File");
            menuBar.add(fileMenu);
            fileMenu.setMnemonic('F');
            JMenuItem preferencesMenuItem = new JMenuItem("Preferences",'P');
            fileMenu.add(preferencesMenuItem);
            fileMenu.add(new JSeparator());
            JMenuItem exitMenuItem = new JMenuItem("Exit",'X');
            fileMenu.add(exitMenuItem);

            JMenu helpMenu = new JMenu("Help");
            menuBar.add(helpMenu);
            helpMenu.setMnemonic('H');

            JMenuItem userGuideMenuItem = new JMenuItem("User Guide",'G');
            helpMenu.add(userGuideMenuItem);
            userGuideMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));//0 works!!

            JMenuItem aboutMenuItem = new JMenuItem("About",'A');
            helpMenu.add(aboutMenuItem);



            {
                preferencesMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            PropertiesEditorFrame preferencesEditor = new PropertiesEditorFrame(thisFrame, "Preferences", PublicConfig.getInstance().getConfigFilePropertyManager());
                            preferencesEditor.setVisible(true);
                            if (preferencesEditor.isStoreNeeded()){
                                PublicConfig.getInstance().saveConfigFile();
                            }
                        } catch (InvalidRequiredInputFileException e1) {
                            new GeneralException("Cannot open properties file",e1);
                        }
                    }
                });

                exitMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });

                userGuideMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (!Desktop.isDesktopSupported()){
                            new GeneralException("Cannot open UserGuide.pdf file. The desktop in not supported in your platform!");
                        }
                        try {
                            Desktop.getDesktop().open(new File("UserGuide.pdf"));
                        } catch (IOException e1) {
                            new GeneralException("Cannot open UserGuide.pdf file",e1).showMessage(thisFrame);
                        }
                    }
                });

                aboutMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        AboutForm.createGUI(thisFrame);
                    }
                });

            }
        }

        this.pack();
    }


    public void update(Observable o, Object arg) {
        CardLayout c1 = (CardLayout) cardsPanel.getLayout();
        MenuItemPanel itemPanel = (MenuItemPanel) arg;
        itemPanel.init();
        c1.show(cardsPanel, itemPanel.getText());
        pack();
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        JFrame f = new SelectApp3("MobiSim3");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }


}
