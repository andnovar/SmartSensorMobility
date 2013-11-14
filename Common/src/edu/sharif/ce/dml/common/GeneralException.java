package edu.sharif.ce.dml.common;

import edu.sharif.ce.dml.common.ui.forms.DialogTemplate;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 28, 2010
 * Time: 11:37:43 AM
 */
public  class GeneralException extends Exception {

    public GeneralException(String message) {
        super(message);
    }

    public GeneralException(String message, Throwable cause) {
        super(message, cause);
    }

    public void showMessage(Component parent) {
        DevelopmentLogger.logger.error(getMessage(), this);

        new ExceptionDialog(getMessage()).setVisible(true);

        //JOptionPane.showMessageDialog(parent, getMessage());
    }

    private class ExceptionDialog extends DialogTemplate {
        private JScrollPane detailScrollPane;

        public ExceptionDialog(String message) throws HeadlessException {
            super(null, "Exception", true);
            final JPanel mainPanel = new JPanel(new BorderLayout());
            this.setContentPane(mainPanel);
            JPanel contentPanel = new JPanel();
            mainPanel.add(contentPanel, BorderLayout.NORTH);
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            Box h = Box.createHorizontalBox();
            {
                JLabel exceptionTopic = new JLabel("The following exception occurred:");
                contentPanel.add(h);
                exceptionTopic.setHorizontalAlignment(JLabel.LEFT);
                h.add(exceptionTopic);
                h.add(Box.createHorizontalGlue());
            }
            contentPanel.add(Box.createVerticalStrut(10));
            {
                JTextArea exceptionMessage = new JTextArea();
                contentPanel.add(exceptionMessage);
                Font f = exceptionMessage.getFont();
                exceptionMessage.setFont(new Font(f.getFontName(), f.getStyle(), 12));
                exceptionMessage.setText(message);

                exceptionMessage.setEditable(false);
                exceptionMessage.setLineWrap(true);
                exceptionMessage.setWrapStyleWord(true);
            }
            contentPanel.add(Box.createVerticalStrut(10));
            {
                JLabel helpLbl = new JLabel("<html>The cause is in the input config file or UI input data.<br/>" +
                        "See the log file or click on the \"Show Details\" button for more information</html>");
                h = Box.createHorizontalBox();
                h.add(helpLbl);
                h.add(Box.createHorizontalGlue());
                contentPanel.add(h);
            }
            contentPanel.add(Box.createVerticalStrut(10));
            {
                //buttons part
                h = Box.createHorizontalBox();
                mainPanel.add(h, BorderLayout.SOUTH);
                h.add(Box.createHorizontalGlue());
                JButton detailsBtn = new JButton("Show Details");
                h.add(detailsBtn);

                JButton closeBtn = new JButton("Close");
                h.add(closeBtn);

                detailsBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JTextArea detailArea = new JTextArea();
                        detailArea.setEditable(false);
                        detailScrollPane = new JScrollPane(detailArea);
                        detailScrollPane.setVerticalScrollBarPolicy(
                                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                        mainPanel.add(detailScrollPane, BorderLayout.CENTER);
                        StringWriter sw = new StringWriter();
                        printStackTrace(new PrintWriter(sw));
                        detailArea.setText(sw.toString());
                        detailArea.setFont(new Font(detailArea.getFont().getFontName(), Font.PLAIN, 12));
                        ((JButton) e.getSource()).setEnabled(false);
                        Dimension preferredDimension = getPreferredSize();
                        setPreferredSize(new Dimension(preferredDimension.width, preferredDimension.height + 200));
                        pack();
                    }
                });
                closeBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        thisDialog.dispose();
                    }
                });
            }
            setPreferredSize(new Dimension(500, 200));
            pack();
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }
    }
}
