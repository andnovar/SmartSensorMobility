package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.shower;

import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.MyGraphics2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 7, 2010
 * Time: 6:58:13 PM
 */
public abstract class AbstractDrawPanel<T extends SnapShotData> extends JPanel {
    protected int height = 0;
    private JPanel oldPanel = null;
    private float zoomFactor = 0.9f;
    //pan
    private int xPan = 10;
    private int yPan = -20;
    private boolean isPanning = false;
    private Point dragStart;
    protected Model model;
    protected T snapShot;
    protected boolean started = false;
    protected Map<GeneratorNode, Model.NodePainter> nodeNodePainter;

    protected AbstractDrawPanel(int height, Model model) {
        setLayout(null);
        this.height = height;
        this.model = model;
        MouseAdapter ma = new PanMouseAdapter(this);

        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);
        MyGraphics2D.getInstance().setHOffset(height);
        this.setBackground(Color.white);
    }

    public void setSnapShot(T snapShot) {
        this.snapShot = snapShot;
        started = true;
    }

    public void reset() {
        this.snapShot = null;
        this.started = false;
        nodeNodePainter=null;
    }

    protected void setNodeNodePainter(Map<GeneratorNode, Model.NodePainter> nodeNodePainter) {
        this.nodeNodePainter = nodeNodePainter;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(this.getBackground());
        g2.clearRect(0, 0, getWidth(), getHeight());
        if (started) {
            g2.transform(new AffineTransform(zoomFactor, 0, 0, -zoomFactor, xPan, height + yPan));
            if (model != null) {
                if (nodeNodePainter == null) {
                    setNodeNodePainter(model.getNodeNodePainter());
                }
                model.paintBackground(g2);
                paintSnapShot(g2);

                //model.paint(g2, this, new Point(0, 0));
            } else{
                throw new RuntimeException("Null model in a draw panel");
            }
        }
        g2.dispose();
    }


    public abstract void paintSnapShot(Graphics2D g2);

    public float getZoomFactor() {
        return zoomFactor;
    }

    public int getXPan() {
        return xPan;
    }

    public int getYPan() {
        return yPan;
    }

    public void setZoomFactor(float zoomFactor) {
        this.zoomFactor = zoomFactor;
        repaint();
    }

    public void setPan(int xPan, int yPan) {
        this.xPan = xPan;
        this.yPan = yPan;
        repaint();
    }


    protected abstract JPanel getSpecialConfigPanel();

    public JPanel getPanel() {
        if (oldPanel == null) {
            oldPanel = getSpecialConfigPanel();
        }
        return oldPanel;
    }

    public void setPanning(boolean panning) {
        isPanning = panning;
    }

    private class PanMouseAdapter extends MouseAdapter {
        private int oldXPan, oldYPan;
        private JPanel thisPanel;

        private PanMouseAdapter(JPanel thisPanel) {
            this.thisPanel = thisPanel;
        }

        public void mousePressed(MouseEvent e2) {
            super.mousePressed(e2);
            if (isPanning) {
                dragStart = e2.getPoint();
                oldXPan = xPan;
                oldYPan = yPan;
            }
        }

        public void mouseDragged(MouseEvent e2) {
            super.mousePressed(e2);
            if (isPanning) {
                xPan = (int) (e2.getX() - dragStart.getX() + oldXPan);
                yPan = (int) (e2.getY() - dragStart.getY() + oldYPan);
                thisPanel.repaint();
            }

        }
    }
}
