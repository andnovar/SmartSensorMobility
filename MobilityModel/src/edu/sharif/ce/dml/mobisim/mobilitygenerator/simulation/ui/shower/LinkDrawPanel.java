package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.shower;

import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.logic.entity.Node;
import edu.sharif.ce.dml.common.logic.entity.SnapShot;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: 1/10/11
 * Time: 10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class LinkDrawPanel extends AbstractDrawPanel<SnapShotData> {
    protected Map<GeneratorNode, Node> generatorNodeNodeMap;
    protected SnapShot snapShot2;
    private boolean oneWayLink = false;
    private final Polygon ARROW_HEAD = new Polygon();
    private AffineTransform tx;
    private boolean showRanges;

    public LinkDrawPanel(int height, Model model) {
        super(height, model);
        tx = new AffineTransform();
        ARROW_HEAD.addPoint(0, 5);
        ARROW_HEAD.addPoint(-5, -5);
        ARROW_HEAD.addPoint(5, -5);
        tx.setToIdentity();

    }

    @Override
    public void paintSnapShot(Graphics2D g2) {
        for (GeneratorNode node : nodeNodePainter.keySet()) {
            Model.NodePainter nodePainter = nodeNodePainter.get(node);
            g2.translate(nodePainter.getOffset().getX(), nodePainter.getOffset().getY());
            nodePainter.paint(g2, node);
            if (showRanges) {
                int range = (int) node.getRange();
                Color color = g2.getColor();
                g2.setColor(nodePainter.getColor(node));
                g2.drawOval(node.getLocation().getX() - range, node.getLocation().getY() - range, 2 * range, 2 * range);
                g2.setColor(color);
            }
            g2.translate(-nodePainter.getOffset().getX(), -nodePainter.getOffset().getY());
        }

        try {
            for (GeneratorNode generatorNode1 : generatorNodeNodeMap.keySet()) {
                Node node1 = generatorNodeNodeMap.get(generatorNode1);
                Model.NodePainter nodePainter1 = nodeNodePainter.get(generatorNode1);
                for (GeneratorNode generatorNode2 : generatorNodeNodeMap.keySet()) {
                    Node node2 = generatorNodeNodeMap.get(generatorNode2);
                    if (!node1.equals(node2) && node1.hasLinkto(node2) && (oneWayLink || node2.hasLinkto(node1))) {
                        Model.NodePainter nodePainter2 = nodeNodePainter.get(generatorNode2);
                        double x1 = nodePainter1.getOffset().getX() + node1.getLocation().getX();
                        double y1 = nodePainter1.getOffset().getY() + node1.getLocation().getY();
                        double x2 = nodePainter2.getOffset().getX() + node2.getLocation().getX();
                        double y2 = nodePainter2.getOffset().getY() + node2.getLocation().getY();

                        g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

                        if (oneWayLink) {
                            double angle = Math.atan2(y2 - y1, x2 - x1);
                            tx.translate(x2, y2);
                            tx.rotate((angle - Math.PI / 2d));

                            g2.transform(tx);
                            g2.fill(ARROW_HEAD);
                            g2.transform(tx.createInverse());
                            tx.setToIdentity();
                        }
                    }
                }
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setNodeNodePainter(Map<GeneratorNode, Model.NodePainter> nodeNodePainter) {
        super.setNodeNodePainter(nodeNodePainter);
        generatorNodeNodeMap = new HashMap<GeneratorNode, Node>();

    }

    @Override
    public void setSnapShot(SnapShotData snapShot) {
        super.setSnapShot(snapShot);
        if (snapShot2 == null) {
            if (nodeNodePainter == null) {
                setNodeNodePainter(model.getNodeNodePainter());
            }
            snapShot2 = new SnapShot(nodeNodePainter.size());
        }
        snapShot2.setNodesData(snapShot.getNodeShadows());
        if (generatorNodeNodeMap.size() == 0) {
            for (GeneratorNode node : nodeNodePainter.keySet()) {
                generatorNodeNodeMap.put(node, (Node) snapShot2.getNodeShadow(node.getName()));
            }
        }
    }

    @Override
    protected JPanel getSpecialConfigPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        final JCheckBox showRangesChBox = new JCheckBox("Show Ranges");
        p.add(showRangesChBox);
        p.add(Box.createHorizontalStrut(10));
        final JCheckBox oneWayChBox = new JCheckBox("One Way Link");
        p.add(oneWayChBox);

        showRangesChBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                showRanges = showRangesChBox.isSelected();
            }
        });

        oneWayChBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                oneWayLink = oneWayChBox.isSelected();
            }
        });
        return p;
    }

    @Override
    public String toString() {
        return "Link";
    }
}
