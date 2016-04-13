package ui.breakpoints;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import breakpoints.BreakpointBase;
import breakpoints.IBreakpoint;
import manager.SSDManager;

public class ManageBreakpointsDialog extends JDialog {
	public static final String DIALOG_HEADER = "Manage Breakpoints";
	
	private static final long serialVersionUID = 1L;
	private Window mParentWindow;
	private JPanel mBreakpointsListPanel;
	private JPanel mNoBreakpointsPanel;
	private HashMap<BreakpointBase, JPanel> mBreakpoints;
	private SSDManager<?, ?, ?, ?, ?> mManager;
	
	public ManageBreakpointsDialog(Window parentWindow, SSDManager<?, ?, ?, ?, ?> manager) {
		super(parentWindow, DIALOG_HEADER);
		mParentWindow = parentWindow;
		mManager = manager;
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
				DefineBreakpointDialog newBpDialog = new DefineBreakpointDialog(mParentWindow, mManager, null);
				newBpDialog.setVisible(true);
				BreakpointBase breakpoint = newBpDialog.getBreakpoint();
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
	
	public List<BreakpointBase> getBreakpoints() {
		Set<BreakpointBase> breakpoints = mBreakpoints.keySet();
		List<BreakpointBase> res = new ArrayList<>();
		
		for (BreakpointBase bp : breakpoints) {
			res.add(bp);
		}
		
		return res;
	}

	public void setBreakpoints(List<BreakpointBase> breakpoints) {
		mBreakpoints.clear();
		for (BreakpointBase breakpoint : breakpoints) {
			addBreakpoint(breakpoint);
		}
		
		updateNoBreakpointsLabelVisibility();
	}

	public void addBreakpoint(BreakpointBase newBreakpoint) {
		for (BreakpointBase bp : mBreakpoints.keySet()) {
			if (bp.isEquals(newBreakpoint)) return;
		}
		
		JPanel breakpointPanel = new JPanel();
		buildBreakpointPanel(newBreakpoint, breakpointPanel);
		
		mBreakpointsListPanel.add(breakpointPanel);
		mBreakpoints.put(newBreakpoint, breakpointPanel);
		updateNoBreakpointsLabelVisibility();
	}

	private void buildBreakpointPanel(BreakpointBase breakpoint, JPanel breakpointPanel) {
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
		breakpointLabel.setOpaque(true);
		breakpointPanel.add(breakpointLabel, constraints);
	}

	private void addEditButton(final JPanel breakpointPanel, final BreakpointBase breakpoint) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(1,5,1,1);
		constraints.gridx = 1;
		
		JButton editButton = new JButton("Edit");
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefineBreakpointDialog editBpDialog = new DefineBreakpointDialog(mParentWindow, mManager, breakpoint);
				editBpDialog.setVisible(true);
				BreakpointBase newBreakpoint = editBpDialog.getBreakpoint();
				if (newBreakpoint != null) {
					editBreakpoint(breakpointPanel, breakpoint, newBreakpoint);
				}
			}
		});
		editButton.setPreferredSize(new Dimension(55, 25));
		breakpointPanel.add(editButton, constraints);
	}
	
	private void editBreakpoint(JPanel breakpointPanel, final BreakpointBase oldBreakpoint, 
			BreakpointBase newBreakpoint) {
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

	public void updateHitBreakpoints() {
		for (IBreakpoint breakpoint : mBreakpoints.keySet()) {
			JLabel label = (JLabel) mBreakpoints.get(breakpoint).getComponent(0);
			if (breakpoint.isHit()) {
				float[] hsbValues = new float[3];
				Color.RGBtoHSB(198, 13, 0, hsbValues);
				label.setForeground(Color.getHSBColor(hsbValues[0], hsbValues[1], hsbValues[2]));
			} else {
				label.setForeground(UIManager.getColor("text"));
			}
			
			label.revalidate();
		}
	}

	public void setManager(SSDManager<?, ?, ?, ?, ?> manager) {
		mManager = manager;
	}
}
