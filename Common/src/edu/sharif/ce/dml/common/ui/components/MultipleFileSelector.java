package edu.sharif.ce.dml.common.ui.components;

import edu.sharif.ce.dml.common.util.FileManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 21, 2010
 * Time: 6:37:40 PM
 */
public class MultipleFileSelector extends JPanel {

    private JLabel nameLbl;
    private JButton addBtn;
    private JButton removeBtn;
    private List<File> selectedFiles = new LinkedList<File>();
    private JList filesList;

    public MultipleFileSelector(final String name, final FileFilter[] filters2) {
        super();
        this.setLayout(new BorderLayout());
        nameLbl= new JLabel(name);
        this.add(nameLbl,BorderLayout.NORTH);
        filesList= new JList();
        filesList.setVisibleRowCount(5);
        this.add(filesList,BorderLayout.CENTER);
        Box v = Box.createVerticalBox();
        this.add(v,BorderLayout.EAST);
        addBtn=new JButton("Add");
        v.add(addBtn);
        v.add(Box.createVerticalStrut(10));
        removeBtn=new JButton("Remove");
        v.add(removeBtn);

        /*addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File[] selectedFilesTemp = FileManager.getInstance().showFileDialog(name, true, filters2);
                if (selectedFiles.length > 0) {
                    fileFilter = FileManager.getInstance().getlastFileFilter();
                    fileObservable.setChanged();
                    fileObservable.notifyObservers(selectedFiles);
                    if (selectedFiles.length == 1) {
                        stateLbl.setText(selectedFiles[0].getName() + ONE_SELECTED);
                    } else {
                        stateLbl.setText(MULTI_SELECTED);
                    }
                    ((Window) getTopLevelAncestor()).pack();
                }
            }
        });*/

    }
}
