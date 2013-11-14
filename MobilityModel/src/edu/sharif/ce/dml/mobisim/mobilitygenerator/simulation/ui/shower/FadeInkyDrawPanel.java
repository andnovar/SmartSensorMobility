package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.shower;

import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 7, 2010
 * Time: 6:59:57 PM
 */

/**
 * its for drawing nodes shadow on the dialog, for repainting it should have a
 * {@link edu.sharif.ce.dml.common.logic.entity.SnapShot} for drawing.
 */
public class FadeInkyDrawPanel extends AbstractDrawPanel<SnapShotData> {
    private BufferedImage bufferedImage;
    private Graphics2D biGraphics2D;
    private SnapShotData lastSnapShot;

    private RescaleOp alphaScale;
    private int filterTurn = 0;
    private float scaleParam = 0.9f;

    public FadeInkyDrawPanel(int height, Model model) {
        super(height, model);
        float[] scales = {1f, 1f, 1f, scaleParam};
        float[] offsets = new float[4];
        alphaScale = new RescaleOp(scales, offsets, null);
    }

    @Override
    public void paintSnapShot(Graphics2D g2) {
        g2.drawImage(bufferedImage, 0, 0, this);
    }

    @Override
    public void reset() {
        super.reset();
        lastSnapShot = null;
        bufferedImage = null;
        filterTurn = 0;
    }

    @Override
    public void setSnapShot(SnapShotData snapShot) {
        if (this.snapShot != null) {
            lastSnapShot = this.snapShot;
            this.snapShot = snapShot.deepClone(null);
            if (bufferedImage == null) {
                //create new bufferedImage
                bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                biGraphics2D = bufferedImage.createGraphics();
            }
            if (filterTurn++ > 10) {
                filterTurn = 0;
                bufferedImage = alphaScale.filter(bufferedImage, bufferedImage);
            }
            if (nodeNodePainter != null) {
                for (GeneratorNode node : nodeNodePainter.keySet()) {
                    Model.NodePainter nodePainter = nodeNodePainter.get(node);
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
    public JPanel getSpecialConfigPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        JLabel scaleLbl = new JLabel("Scale:");
        p.add(scaleLbl);
        final SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(0.9, 0, 1, 0.05);
        JSpinner scaleSpn = new JSpinner(spinnerNumberModel);
        scaleSpn.setPreferredSize(new Dimension(50, 10));
        p.add(scaleSpn);
        scaleLbl.setLabelFor(scaleSpn);
        JButton okBtn = new JButton("OK");
        p.add(okBtn);
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaleParam = spinnerNumberModel.getNumber().floatValue();
                float[] scales = {1f, 1f, 1f, scaleParam};
                float[] offsets = new float[4];
                alphaScale = new RescaleOp(scales, offsets, null);
            }
        });

        return p;
    }

    @Override
    public String toString() {
        return "Fade FootPrint";
    }
}
