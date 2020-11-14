package ui.sampling;

import manager.SSDManager;
import manager.VisualConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;

public class SamplingRateDialog extends JDialog {
    public static final String DIALOG_HEADER = "Manage Sampling Rate";

    private static final long serialVersionUID = 1L;
    private Window mParentWindow;
    private JPanel mBreakpointsListPanel;
    private JPanel mNoBreakpointsPanel;
    private SSDManager<?, ?, ?, ?, ?> mManager;
    private VisualConfig visualConfig;
    private JFormattedTextField rateInput;
    private int lastValue;

    public SamplingRateDialog(Window parentWindow, SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
        super(parentWindow, DIALOG_HEADER);
        mParentWindow = parentWindow;
        mManager = manager;
        this.visualConfig = visualConfig;

        setDefaultLookAndFeelDecorated(true);
        setModal(true);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(200, 110);
        setLocationRelativeTo(parentWindow);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        Box buttonsBox = Box.createHorizontalBox();

        rateInput = new JFormattedTextField(new DecimalFormat("##,###,###"));
        rateInput.setPreferredSize(new Dimension(80,30));
        rateInput.setValue(visualConfig.getViewSample());
        lastValue = visualConfig.getViewSample();

        JButton okButton = new JButton();
        okButton.setText("ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setSamplingRate();
                setVisible(false);
                dispose();
            }
        });

        JButton cancelButton = new JButton();
        cancelButton.setText("cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                resetSmaplingRate();
                setVisible(false);
                dispose();
            }
        });

        mainPanel.add(new JLabel("Sampling Rate"));
        mainPanel.add(rateInput);
        buttonsBox.add(cancelButton);
        buttonsBox.add(okButton);

        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        buttonsBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonsBox.setAlignmentY(Component.BOTTOM_ALIGNMENT);


        mainPanel.add(buttonsBox);
    }


    public void setSamplingRate(){
        int input = ((Number)rateInput.getValue()).intValue();
        if(input < 1){
            throw new IllegalArgumentException("Sampling Rate must be positive");
        }
        visualConfig.setViewSample(((Number)rateInput.getValue()).intValue());
        lastValue = visualConfig.getViewSample();
    }

    public void resetSmaplingRate(){
        rateInput.setValue(lastValue);
    }

}
