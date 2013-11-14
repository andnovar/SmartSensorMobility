package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.shower;

import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2010
 * Time: 9:21:51 AM
 */
public class InkyDrawPanel extends AbstractDrawPanel<SnapShotData> {
    private LinkedList<BufferedImage> bufferedImages;
    private SnapShotData lastSnapShot;
    private int historyParam = 20;

    public InkyDrawPanel(int height, Model model) {
        super(height, model);
        bufferedImages = new LinkedList<BufferedImage>();
    }

    @Override
    public void paintSnapShot(Graphics2D g2) {

        for (BufferedImage image : bufferedImages) {
            g2.drawImage(image, 0, 0, this);
        }
    }

    @Override
    public void reset() {
        super.reset();
        bufferedImages.clear();
        lastSnapShot = null;
    }

    @Override
    public JPanel getSpecialConfigPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        JLabel historyLbl = new JLabel("History:");
        p.add(historyLbl);
        final SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(historyParam, 0, 100, 1);
        JSpinner historySpn = new JSpinner(spinnerNumberModel);
        p.add(historySpn);
        historyLbl.setLabelFor(historySpn);
        JButton okBtn = new JButton("OK");
        p.add(okBtn);
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                historyParam = spinnerNumberModel.getNumber().intValue();
                if (bufferedImages.size() > historyParam && historyParam > 0) {
                    for (int i = 0; i < bufferedImages.size() - historyParam; i++) {
                        bufferedImages.removeFirst();
                    }
                }
            }
        });

        return p;
    }

    public void setSnapShot(SnapShotData snapShot) {
        if (this.snapShot != null) {
            lastSnapShot = this.snapShot;
            this.snapShot = snapShot.deepClone(null);
            BufferedImage bufferedImage;
            Graphics2D biGraphics2D;
            if (historyParam > 0 && bufferedImages.size() >= historyParam) {
                bufferedImage = bufferedImages.removeFirst();
                bufferedImages.addLast(bufferedImage);
                biGraphics2D = (Graphics2D) bufferedImage.getGraphics();
                biGraphics2D.setComposite(
                        AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
                Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
                biGraphics2D.fill(rect);
            } else {
                bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                biGraphics2D = bufferedImage.createGraphics();
                biGraphics2D.setBackground(this.getBackground());
                bufferedImages.addLast(bufferedImage);
            }
            if (nodeNodePainter != null) {
                for (GeneratorNode node : nodeNodePainter.keySet()) {
                    Model.NodePainter nodePainter = nodeNodePainter.get(node);
                    biGraphics2D = (Graphics2D) bufferedImages.getLast().getGraphics();
                    biGraphics2D.translate(nodePainter.getOffset().getX(), nodePainter.getOffset().getY());
                    node.paintFootPrint(biGraphics2D, lastSnapShot.getNodeShadow(node.getName()).getLocation());
                    biGraphics2D.translate(-nodePainter.getOffset().getX(), -nodePainter.getOffset().getY());
                }
            }
            this.started = true;
        } else {
            this.snapShot = snapShot.deepClone(null);
        }
    }

    @Override
    public String toString() {
        return "FootPrint";
    }
}