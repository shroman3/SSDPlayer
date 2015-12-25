package ui;

import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import breakpoints.IBreakpoint;

public class ManageBreakpointsDialog extends JDialog {
	public static final String DIALOG_HEADER = "Manage Breakpoints";
	
	private static final long serialVersionUID = 1L;
	private Window mParentWindow;
	private JPanel breakpointsListPanel;
	
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
		
		breakpointsListPanel = new JPanel();
		breakpointsListPanel.setLayout(new GridLayout(3, 2));
		mainPanel.add(breakpointsListPanel);
		
		JButton newBreakpointButton = new JButton("Define new breakpoint");
		mainPanel.add(newBreakpointButton);
	}

	public void addBreakpoints(List<IBreakpoint> breakpoints) {
		for (IBreakpoint breakpoint : breakpoints) {
			JPanel breakpointPanel = new JPanel();
			breakpointPanel.setLayout(new BoxLayout(breakpointPanel, BoxLayout.X_AXIS));
			breakpointPanel.add(new JLabel(breakpoint.toString()));
			breakpointPanel.add(new JButton("X"));
			breakpointsListPanel.add(breakpointPanel);
		}
	}
}
