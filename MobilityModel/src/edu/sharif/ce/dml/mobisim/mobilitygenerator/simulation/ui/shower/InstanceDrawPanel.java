package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.shower;

import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2010
 * Time: 10:10:04 AM
 */
public class InstanceDrawPanel extends AbstractDrawPanel<SnapShotData> {


    protected boolean showTransition = false;

    public InstanceDrawPanel(int height, Model model) {
        super(height, model);
    }

    @Override
    public void paintSnapShot(Graphics2D g2) {
        for (GeneratorNode node : nodeNodePainter.keySet()) {
            Model.NodePainter nodePainter = nodeNodePainter.get(node);
            g2.translate(nodePainter.getOffset().getX(), nodePainter.getOffset().getY());
            nodePainter.paint(g2,node);
            if (showTransition) {
                node.paintTransition(g2);
            }
            g2.translate(-nodePainter.getOffset().getX(), -nodePainter.getOffset().getY());
        }
    }

    @Override
    public JPanel getSpecialConfigPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        final JCheckBox showTransitionChBox = new JCheckBox("Show Transition");
        p.add(showTransitionChBox);

        showTransitionChBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                showTransition = showTransitionChBox.isSelected();
            }
        });

        return p;
    }

    @Override
    public String toString() {
        return "Instance";
    }
}
