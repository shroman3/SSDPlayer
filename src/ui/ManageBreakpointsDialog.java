package ui;

import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ManageBreakpointsDialog extends JDialog {
	public static final String DIALOG_HEADER = "Manage Breakpoints";
	
	private static final long serialVersionUID = 1L;
	private Window mParentWindow;
	
	public ManageBreakpointsDialog(Window parentWindow) {
		super(parentWindow, DIALOG_HEADER);
		mParentWindow = parentWindow;
		
		setDefaultLookAndFeelDecorated(true);
		setModal(true);
		initSizes();
		initComponents();
	}
	
	private void initSizes() {
		setSize(400, 300);
        setResizable(false);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setLocationRelativeTo(mParentWindow);
	}
	
	private void initComponents() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel);
		
		JLabel noBreakpointsLabel = new JLabel("No breakpoints defined...");
		mainPanel.add(noBreakpointsLabel);
		
		JPanel breakpointsListPanel = new JPanel();
		breakpointsListPanel.setLayout(new GridLayout(3, 2));
		mainPanel.add(breakpointsListPanel);
		
		JPanel breakpoint1Panel = new JPanel();
		breakpoint1Panel.setLayout(new BoxLayout(breakpoint1Panel, BoxLayout.X_AXIS));
		breakpoint1Panel.add(new JLabel("Breakpoint 1"));
		breakpoint1Panel.add(new JButton("X"));
		breakpointsListPanel.add(breakpoint1Panel);
		
		JPanel breakpoint2Panel = new JPanel();
		breakpoint2Panel.setLayout(new BoxLayout(breakpoint2Panel, BoxLayout.X_AXIS));
		breakpoint2Panel.add(new JLabel("Breakpoint 2"));
		breakpoint2Panel.add(new JButton("X"));
		breakpointsListPanel.add(breakpoint2Panel);
		
		JPanel breakpoint3Panel = new JPanel();
		breakpoint3Panel.setLayout(new BoxLayout(breakpoint3Panel, BoxLayout.X_AXIS));
		breakpoint3Panel.add(new JLabel("Breakpoint 3"));
		breakpoint3Panel.add(new JButton("X"));
		breakpointsListPanel.add(breakpoint3Panel);
		
		JButton newBreakpointButton = new JButton("Define new breakpoint");
		mainPanel.add(newBreakpointButton);
	}

}
