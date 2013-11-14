package edu.sharif.ce.dml.common.parameters.ui.dialogs;

import edu.sharif.ce.dml.common.parameters.data.*;
import edu.sharif.ce.dml.common.parameters.logic.HasInternalParameterable;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.ui.GraphicalStandAloneObject;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.complex.GeneralUIParameter;
import edu.sharif.ce.dml.common.ui.forms.DialogTemplate;
import edu.sharif.ce.dml.common.util.FileManager;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2010
 * Time: 11:13:03 AM
 */
public class TreeParameterableConfigDialog extends DialogTemplate {
    private JTree tree;
    private DefaultTreeModel treeModel;
    private ConfigPanel configPanel;
    private JPopupMenu popup;

    public enum popupAction {
        REFRESH, SAVE, LOAD
    }

    public TreeParameterableConfigDialog(final Parameterable parameterable, final boolean asPanel) throws HeadlessException {
        super(null, parameterable.toString(), true);
        DefaultMutableTreeNode rootNode = createTreeNodes(null, parameterable);
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        configPanel = new ConfigPanel();
        configPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = getCurrentSelectedNode();

                if (node == null) return;
                if (node.getChildCount() > 0) {
                    tree.scrollPathToVisible(getPath(node.getChildAt(0)));
                }

                Object nodeInfo = node.getUserObject();
                NewUIParameter uiParameter;
                if (nodeInfo instanceof GraphicalStandAloneObject) {
                    uiParameter = ((GraphicalStandAloneObject) nodeInfo).getUIParameter(false);
                } else {
                    uiParameter = new GeneralUIParameter(nodeInfo.toString(), ((Parameterable) nodeInfo).getParameters().values(), false);
                }
                uiParameter.addObserver(new Observer() {
                    public void update(Observable o, Object arg) {
                        //update parent tree node
                        DefaultMutableTreeNode selectedNode = getCurrentSelectedNode();
                        if (selectedNode==null){
                            return;
                        }
                        updateParamterableAndTree(selectedNode);
                    }
                });
                configPanel.setMainContent(uiParameter);

                if (!asPanel) {
                    thisDialog.pack();
                }
            }
        });


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                tree,
                configPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setLastDividerLocation(200);
        splitPane.setResizeWeight(0);
        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);
        {
            PopupActionListener popupActionListener = new PopupActionListener();
            popup = new JPopupMenu();
            final JMenuItem savePopupMenuItem = new JMenuItem("Save");
            savePopupMenuItem.addActionListener(popupActionListener);
            savePopupMenuItem.setActionCommand(popupAction.SAVE.toString());
            popup.add(savePopupMenuItem);
            final JMenuItem loadPopupMenuItem = new JMenuItem("Load");
            loadPopupMenuItem.addActionListener(popupActionListener);
            loadPopupMenuItem.setActionCommand(popupAction.LOAD.toString());
            popup.add(loadPopupMenuItem);
            popup.add(new JSeparator());
            final JMenuItem refreshPopupMenuItem = new JMenuItem("Refresh");
            refreshPopupMenuItem.addActionListener(popupActionListener);
            refreshPopupMenuItem.setActionCommand(popupAction.REFRESH.toString());
            popup.add(refreshPopupMenuItem);
            popup.setOpaque(true);
            popup.setLightWeightPopupEnabled(true);
            tree.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popup.show((JComponent) e.getSource(), e.getX(), e.getY());
                    }
                }
            });
            tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = getCurrentSelectedNode();

                    if (node == null) return;
                    boolean isParameterableOrSelectOne = node.getUserObject() instanceof Parameterable ||
                            (node.getUserObject() instanceof HasInternalParameterable &&
                                    !((HasInternalParameterable) node.getUserObject()).hasMultipleInternalParamterable());
                    savePopupMenuItem.setEnabled(isParameterableOrSelectOne);
                    loadPopupMenuItem.setEnabled(isParameterableOrSelectOne);
                }
            });
        }
        tree.setSelectionPath(getPath((DefaultMutableTreeNode) treeModel.getRoot()));
        tree.setPreferredSize(new Dimension(200, 300));

        if (!asPanel) {
            this.pack();
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }

    }

    public Parameterable getRootElement() {
        return (Parameterable) ((DefaultMutableTreeNode) treeModel.getRoot()).getUserObject();
    }

    public void loadRoot(File file) {
        Parameterable parameterable = getRootElement();
        DefaultMutableTreeNode newRootNode = loadParameterable(file, parameterable);
        if (newRootNode != null) {
            treeModel.setRoot(newRootNode);
            tree.setSelectionPath(getPath((DefaultMutableTreeNode) treeModel.getRoot()));
        }
    }

    public void loadToCurrent(File file) {
        DefaultMutableTreeNode currentSelectedNode = getCurrentSelectedNode();
        if (currentSelectedNode != null) {
            Object userObject = currentSelectedNode.getUserObject();
            if (userObject instanceof Parameterable) {
                DefaultMutableTreeNode newTreeNode = loadParameterable(file, (Parameterable) userObject);
                updateTree(getCurrentSelectedNode());
            } else if (userObject instanceof HasInternalParameterable &&
                    !((HasInternalParameterable) userObject).hasMultipleInternalParamterable()) {
                Parameterable currentParameterable = ((HasInternalParameterable) userObject).getInternalParamterable().get(0);
                DefaultMutableTreeNode newTreeNode = loadParameterable(file, currentParameterable);
                updateTree(getCurrentSelectedNode());

            }
        }
    }

    private DefaultMutableTreeNode loadParameterable(File file, Parameterable parameterable) {
        try {
            ParameterableConfigLoader pcl2 = ParameterableConfigLoader.load(file);
            Map<String, Parameter> parameters = parameterable.getParameters();
            ParameterableData parameterableData = pcl2.getParameterableDatas().get(0);
            Parameter.updateParameters(parameterableData.getParameters(), parameters);
            parameterable.setParameters(parameters);
            parameterable.setName(parameterableData.getName());
            return createTreeNodes(null, parameterable);

        } catch (ParameterableConfigFileException e1) {
            e1.showMessage(null);
        } catch (InvalidParameterInputException e1) {
            e1.showMessage(null);
        }
        return null;
    }

    public void saveRoot(File selectedFile) {
        Parameterable rootParameterable = getRootElement();
        saveParameterable(selectedFile, rootParameterable);
    }

    public void saveCurrent(File selectedFile) {
        DefaultMutableTreeNode currentSelectedNode = getCurrentSelectedNode();
        if (currentSelectedNode != null) {
            Object userObject = currentSelectedNode.getUserObject();
            if (userObject instanceof Parameterable) {
                Parameterable currentParameterable = (Parameterable) userObject;
                saveParameterable(selectedFile, currentParameterable);
            } else if (userObject instanceof HasInternalParameterable &&
                    !((HasInternalParameterable) userObject).hasMultipleInternalParamterable()) {
                Parameterable currentParameterable = ((HasInternalParameterable) userObject).getInternalParamterable().get(0);
                saveParameterable(selectedFile, currentParameterable);
            }
        }
    }

    private void saveParameterable(File selectedFile, Parameterable toBeSaved) {
        Element rootElement = CompositeDataParameter.createRootElement();
        GeneralDataParameter parameter = ParameterableParameter.getDataParameters(toBeSaved, false);
        parameter.toXML(rootElement);

        //writing to file
        if (selectedFile.exists()) {
            selectedFile.delete();
        }

        Document outputDocument = new Document(rootElement);
        XMLOutputter outputer = new XMLOutputter(Format.getCompactFormat());
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(selectedFile));
            outputer.output(outputDocument, outputWriter);
            outputWriter.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private DefaultMutableTreeNode createTreeNodes(DefaultMutableTreeNode parentNode, Parameterable parameterable) {
        DefaultMutableTreeNode parameterableChild = new DefaultMutableTreeNode(parameterable);
        if (parentNode != null) {
            parentNode.add(parameterableChild);
        }
        createChildNodes(parameterableChild, parameterable);
        return parameterableChild;
    }

    private void createChildNodes(DefaultMutableTreeNode parameterableChild, Parameterable parameterable) {
        Map<String, Parameter> parameters = parameterable.getParameters();
        for (String name : parameters.keySet()) {
            Parameter parameter = parameters.get(name);
            if (parameter instanceof HasInternalParameterable) {
                createTreeNodesHasInternal(parameterableChild, (HasInternalParameterable) parameter);
            }
        }
    }

    private DefaultMutableTreeNode createTreeNodesHasInternal(DefaultMutableTreeNode parentNode, HasInternalParameterable parameter) {
        DefaultMutableTreeNode hasInternalNode = new DefaultMutableTreeNode(parameter);
        parentNode.add(hasInternalNode);
        if (parameter.hasMultipleInternalParamterable()) {
            for (Parameterable p : parameter.getInternalParamterable()) {
                createTreeNodes(hasInternalNode, p);
            }
        } else {
            createChildNodes(hasInternalNode, parameter.getInternalParamterable().get(0));
        }
        return hasInternalNode;
    }

    private void updateTree(DefaultMutableTreeNode node) {
        Object o = node.getUserObject();
        if (node.getParent() == null) {
            //it is root
            DefaultMutableTreeNode treeNode;
            if (o instanceof Parameterable) {
                treeNode = createTreeNodes(null, (Parameterable) o);
            } else {
                treeNode = createTreeNodesHasInternal(null, (HasInternalParameterable) o);
            }
            treeModel.setRoot(treeNode);
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(treeNode);
            //reselect the new
            Object lastSelectedPathComponent = getCurrentSelectedNode();
            if (lastSelectedPathComponent != null) {
                tree.setSelectionPath(getPath((DefaultMutableTreeNode) lastSelectedPathComponent));
            } else {
                TreePath treePath = getPath(treeNode);
                tree.setSelectionPath(treePath);
            }
        } else {//tree update
            //remove from parent
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            int index = parentNode.getIndex(node);
            treeModel.removeNodeFromParent(node);
            //add again to parent
            DefaultMutableTreeNode treeNode;
            if (o instanceof Parameterable) {
                treeNode = createTreeNodes(parentNode, (Parameterable) o);
            } else {
                treeNode = createTreeNodesHasInternal(parentNode, (HasInternalParameterable) o);
            }
            treeModel.insertNodeInto(treeNode, parentNode, index);
            //reselect the new
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
            TreePath treePath = getPath(treeNode);
            tree.setSelectionPath(treePath);
        }
    }

    private void updateParamterableAndTree(DefaultMutableTreeNode node) {
        Object o = node.getUserObject();
        {//parameterable update

            try {
                if (o instanceof GraphicalStandAloneObject) {
                    ((GraphicalStandAloneObject) o).setUIParameterValue(configPanel.getMainContent());
                } else {
                    ((GeneralUIParameter) configPanel.getMainContent()).updateParameter((Parameterable) o);
                }
            } catch (InvalidParameterInputException e1) {
                e1.showMessage(null);
            }
        }
        updateTree(node);
        //TODO the internal objects might change the outer one! (mapeditor example). but by updating whole tree we lose the current path
    }

    private DefaultMutableTreeNode getCurrentSelectedNode() {
        Object lastSelectedPathComponent = tree.getLastSelectedPathComponent();
        if (lastSelectedPathComponent != null) {
            return (DefaultMutableTreeNode) lastSelectedPathComponent;
        }
        return null;
    }

    // Returns a TreePath containing the specified node.

    private TreePath getPath(TreeNode node) {
        java.util.List<TreeNode> list = new ArrayList<TreeNode>();

        // Add all nodes to list
        while (node != null) {
            list.add(node);
            node = node.getParent();
        }
        Collections.reverse(list);

        // Convert array of nodes to TreePath
        return new TreePath(list.toArray());
    }

    private class ConfigPanel extends JPanel {
        private NewUIParameter mainContent;

        private ConfigPanel() {
            setLayout(new BorderLayout());
            {//buttons
                Box h = Box.createHorizontalBox();
                h.add(Box.createHorizontalGlue());
                JButton updateBtn = new JButton("Update");
                h.add(updateBtn);
                add(h, BorderLayout.SOUTH);
                updateBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        DefaultMutableTreeNode currentSelectedNode = getCurrentSelectedNode();
                        if (currentSelectedNode==null){
                            return;
                        }
                        updateParamterableAndTree(currentSelectedNode);
                    }
                });
            }
        }

        public NewUIParameter getMainContent() {
            return mainContent;
        }

        public void setMainContent(NewUIParameter p) {
            if (mainContent != null) {
                this.remove(mainContent);
            }
            mainContent = p;
            this.add(mainContent, BorderLayout.NORTH);
            revalidate();
            repaint();
        }
    }

    private class PopupActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode dmtn;

            TreePath path = tree.getSelectionPath();
            dmtn = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (e.getActionCommand().equals(popupAction.LOAD.toString())) {

                File[] loadInputFile = FileManager.getInstance().showFileDialog("input", false, new FileFilter[]{new ParameterableConfigFileFilter()});
                if (loadInputFile != null && loadInputFile.length > 0) {
                    loadToCurrent(loadInputFile[0]);
                }
            } else if (e.getActionCommand().equals(popupAction.SAVE.toString())) {
                File[] saveOutputFile = FileManager.getInstance().showFileDialog("output", false, new FileFilter[]{new ParameterableConfigFileFilter()});
                if (saveOutputFile != null && saveOutputFile.length > 0) {
                    saveCurrent(saveOutputFile[0]);
                }
            } else if (e.getActionCommand().equals(popupAction.REFRESH.toString())) {
                updateTree(dmtn);
            }
        }
    }
}
