package ui.breakpoints;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import breakpoints.IBreakpoint;

public class ManageBreakpointsDialog extends JDialog {
	public static final String DIALOG_HEADER = "Manage Breakpoints";
	
	private static final long serialVersionUID = 1L;
	private Window mParentWindow;
	private JPanel mBreakpointsListPanel;
	private JPanel mNoBreakpointsPanel;
	private HashMap<IBreakpoint, JPanel> mBreakpoints;
	
	public ManageBreakpointsDialog(Window parentWindow) {
		super(parentWindow, DIALOG_HEADER);
		mParentWindow = parentWindow;
		mBreakpoints = new HashMap<>(); 
		
		setDefaultLookAndFeelDecorated(true);
		setModal(true);
		setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(460, 350);
		setLocationRelativeTo(parentWindow);
		initComponents();
	}
	
	private void initComponents() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5 , 5));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel);
		
		addNoBreakpointsLabel(mainPanel);
		addBreakpointsPanel(mainPanel);
		addNewBreakpointButton(mainPanel);
	}

	private void addNewBreakpointButton(JPanel mainPanel) {
		mainPanel.add(Box.createVerticalGlue());
		JButton newBreakpointButton = new JButton("Define new breakpoint");
		newBreakpointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefineBreakpointDialog newBpDialog = new DefineBreakpointDialog(mParentWindow, null);
				newBpDialog.setVisible(true);
				IBreakpoint breakpoint = newBpDialog.getBreakpoint();
				if (breakpoint != null) {
					addBreakpoint(breakpoint);
					mBreakpointsListPanel.revalidate();
				}
			}
		});
		newBreakpointButton.setMargin(new Insets(2, 2, 2, 2));
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(newBreakpointButton);
		mainPanel.add(buttonBox);
	}

	private void addBreakpointsPanel(JPanel mainPanel) {
		mBreakpointsListPanel = new JPanel();
		mBreakpointsListPanel.setLayout(new GridLayout(0, 1));
		JScrollPane scrollPane = new JScrollPane(mBreakpointsListPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(scrollPane);
	}

	private void addNoBreakpointsLabel(JPanel mainPanel) {
		JLabel noBreakpointsLabel = new JLabel("No breakpoints defined...");
		mNoBreakpointsPanel = new JPanel();
		mNoBreakpointsPanel.setLayout(new BorderLayout(0, 0));
		mNoBreakpointsPanel.add(noBreakpointsLabel, BorderLayout.WEST);
		mainPanel.add(mNoBreakpointsPanel);
	}
	
	public List<IBreakpoint> getBreakpoints() {
		Set<IBreakpoint> breakpoints = mBreakpoints.keySet();
		List<IBreakpoint> res = new ArrayList<>();
		
		for (IBreakpoint bp : breakpoints) {
			res.add(bp);
		}
		
		return res;
	}

	public void setBreakpoints(List<IBreakpoint> breakpoints) {
		mBreakpoints.clear();
		for (IBreakpoint breakpoint : breakpoints) {
			addBreakpoint(breakpoint);
		}
		
		updateNoBreakpointsLabelVisibility();
	}

	public void addBreakpoint(IBreakpoint breakpoint) {
		JPanel breakpointPanel = new JPanel();
		buildBreakpointPanel(breakpoint, breakpointPanel);
		
		mBreakpointsListPanel.add(breakpointPanel);
		mBreakpoints.put(breakpoint, breakpointPanel);
		updateNoBreakpointsLabelVisibility();
	}

	private void buildBreakpointPanel(IBreakpoint breakpoint, JPanel breakpointPanel) {
		breakpointPanel.setLayout(new GridBagLayout());
		addBreakpointLabel(breakpoint, breakpointPanel);
		addEditButton(breakpointPanel, breakpoint);
		addRemoveButton(breakpointPanel, breakpoint);
		addSeparator(breakpointPanel);
	}
	
	public void removeBreakpoint(IBreakpoint breakpoint) {
		JPanel panel = mBreakpoints.remove(breakpoint);
		mBreakpointsListPanel.remove(panel);
		mBreakpointsListPanel.revalidate();
		updateNoBreakpointsLabelVisibility();
	}

	private void addBreakpointLabel(IBreakpoint breakpoint, JPanel breakpointPanel) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 0.8;
		constraints.insets = new Insets(1,1,1,3);
		
		JLabel breakpointLabel = new JLabel(breakpoint.getDescription());
		breakpointPanel.add(breakpointLabel, constraints);
	}

	private void addEditButton(final JPanel breakpointPanel, final IBreakpoint breakpoint) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(1,5,1,1);
		constraints.gridx = 1;
		
		JButton editButton = new JButton("Edit");
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefineBreakpointDialog editBpDialog = new DefineBreakpointDialog(mParentWindow, breakpoint);
				editBpDialog.setVisible(true);
				IBreakpoint newBreakpoint = editBpDialog.getBreakpoint();
				if (newBreakpoint != null) {
					editBreakpoint(breakpointPanel, breakpoint, newBreakpoint);
				}
			}
		});
		editButton.setPreferredSize(new Dimension(55, 25));
		breakpointPanel.add(editButton, constraints);
	}
	
	private void editBreakpoint(JPanel breakpointPanel, final IBreakpoint oldBreakpoint, 
			IBreakpoint newBreakpoint) {
		breakpointPanel.removeAll();
		mBreakpoints.remove(oldBreakpoint);
		mBreakpoints.put(newBreakpoint, breakpointPanel);
		
		buildBreakpointPanel(newBreakpoint, breakpointPanel);
		breakpointPanel.revalidate();
	}
	
	private void addRemoveButton(JPanel breakpointPanel, final IBreakpoint breakpoint) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(0,0,0,0);
		constraints.gridx = 2;
		
		JButton removeButton = new JButton("X");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeBreakpoint(breakpoint);
			}
		});
		removeButton.setPreferredSize(new Dimension(35, 25));
		breakpointPanel.add(removeButton, constraints);
	}
	
	private void addSeparator(JPanel breakpointPanel) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridwidth = 3;
		constraints.gridy = 1;
		
		JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
		separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
		breakpointPanel.add(separator, constraints);
	}

	private void updateNoBreakpointsLabelVisibility() {
		if (mBreakpoints.size() != 0) {
			mNoBreakpointsPanel.setVisible(false);
		} else {
			mNoBreakpointsPanel.setVisible(true);
		}
	}
}
