package ui.breakpoints;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import breakpoints.BreakpointBase;
import manager.SSDManager;

public class DefineBreakpointDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String DIALOG_HEADER = "Define breakpoint";

	private BreakpointBase mBreakpoint;
	private JComboBox<IBreakpointCBoxEntry> mBreakpointsCBox;
	private IBreakpointCBoxEntry mSelectedEntry;
	private JPanel mBpPropertiesPanel;
	private BreakpointViewBase mBreakpointView;
	private boolean mIsCanceled;
	private boolean mIsEdit;
	private JPanel mRadioButtonsPanel;
	private SSDManager<?, ?, ?, ?, ?> mManager;

	public DefineBreakpointDialog(Window parentWindow, SSDManager<?, ?, ?, ?, ?> manager, BreakpointBase breakpoint) {
		super(parentWindow, DIALOG_HEADER);

		mManager = manager;
		setDefaultLookAndFeelDecorated(true);
		BreakpointsUIFactory.init();
		if (breakpoint != null)
			mIsEdit = true;

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mIsCanceled = true;
			}
		});

		mBreakpointView = new BreakpointViewBase();

		setModal(true);
		setResizable(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(400, 200);
		setLocationRelativeTo(parentWindow);
		initComponents();
		instantiateInitialBreakpoint(breakpoint);
	}

	public void cancel() {
		mIsCanceled = true;
	}

	private void instantiateInitialBreakpoint(BreakpointBase breakpoint) {
		try {
			if (breakpoint == null) {
				mBreakpointsCBox.setSelectedIndex(0);
				IBreakpointCBoxEntry entry = (IBreakpointCBoxEntry) mBreakpointsCBox.getItemAt(0);
				if (entry instanceof BreakpointsGroup) {
					BreakpointsGroup groupEntry = (BreakpointsGroup) entry;
					String firstKey = groupEntry.getGroup().keySet().iterator().next();
					mBreakpoint = groupEntry.getGroup().getOrDefault(firstKey, null).newInstance();
				} else {
					SingleBreakpoint singleBp = (SingleBreakpoint) entry;
					mBreakpoint = singleBp.getBPClass().newInstance();
				}
			} else {
				mBreakpoint = breakpoint;

				for (SingleBreakpoint entry : BreakpointsUIFactory.getSingleBreakpoints()) {
					if (entry.getBPClass().getName().equals(mBreakpoint.getClass().getName())) {
						mBreakpointsCBox.setSelectedItem(entry);
						return;
					}
				}

				for (BreakpointsGroup group : BreakpointsUIFactory.getBreakpointGroups()) {
					if (group.contains(mBreakpoint.getClass())) {
						group.setSelectedItem(group.getFirstKeyWithValueOrDefault(mBreakpoint.getClass(), null));
						mBreakpointsCBox.setSelectedItem(group);
						return;
					}
				}

			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			setVisible(false);
			dispose();
		}
	}

	private void initComponents() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel);
		initComboBox(mainPanel);

		mRadioButtonsPanel = new JPanel();
		mRadioButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		mRadioButtonsPanel.setMaximumSize(new Dimension(300, 10));
		mRadioButtonsPanel.setBorder(BorderFactory.createEmptyBorder(3, -5, 5, 0));
		mainPanel.add(mRadioButtonsPanel);

		mBpPropertiesPanel = new JPanel();
		mBpPropertiesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		mBpPropertiesPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		mainPanel.add(mBpPropertiesPanel);

		mainPanel.add(Box.createVerticalGlue());
		addDialogButtons(mainPanel);
	}

	private void initComboBox(JPanel mainPanel) {
		DefaultComboBoxModel<IBreakpointCBoxEntry> cBoxModel = new DefaultComboBoxModel<IBreakpointCBoxEntry>();
		List<SingleBreakpoint> singleBreakpoints = BreakpointsUIFactory.getSingleBreakpoints();

		for (int i = 0; i < singleBreakpoints.size(); i++) {
			SingleBreakpoint bpClass = singleBreakpoints.get(i);
			try {
				if (bpClass.getBPClass().newInstance().isManagerSupported(mManager)) {
					cBoxModel.addElement(bpClass);
				}
			} catch (Exception e) {
			}
		}

		List<BreakpointsGroup> breakpointGroups = BreakpointsUIFactory.getBreakpointGroups();
		for (int i = 0; i < breakpointGroups.size(); i++) {
			BreakpointsGroup bpGroup = breakpointGroups.get(i);
			try {
				boolean validGroup = true;

				for (Class<? extends BreakpointBase> groupItem : bpGroup.getGroup().values()) {
					if (!groupItem.newInstance().isManagerSupported(mManager)) {
						validGroup = false;
						break;
					}
				}

				if (validGroup) {
					cBoxModel.addElement(bpGroup);
				}
			} catch (Exception e) {
			}
		}

		mBreakpointsCBox = new JComboBox<>(cBoxModel);
		mBreakpointsCBox.setRenderer(new DisplayNameRenderer());
		mBreakpointsCBox.setPreferredSize(new Dimension(mBreakpointsCBox.getPreferredSize().width, 30));
		mBreakpointsCBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		mBreakpointsCBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mSelectedEntry = ((IBreakpointCBoxEntry) mBreakpointsCBox.getSelectedItem());
					if (mSelectedEntry instanceof BreakpointsGroup) {
						displayGroupRadioButtons();
					} else {
						if (!mIsEdit) {
							mBreakpoint = ((SingleBreakpoint) mSelectedEntry).getBPClass().newInstance();
						}
						mRadioButtonsPanel.removeAll();
						mRadioButtonsPanel.revalidate();

						displayBreakpoint();
					}
				} catch (InstantiationException | IllegalAccessException e1) {
					e1.printStackTrace();
				}
			}
		});

		mainPanel.add(mBreakpointsCBox);
	}

	private void displayGroupRadioButtons() {
		mRadioButtonsPanel.removeAll();
		BreakpointsGroup bpGroup = (BreakpointsGroup) mSelectedEntry;

		ButtonGroup buttonGroup = new ButtonGroup();

		Set<String> keySet = bpGroup.getGroup().keySet();
		Iterator<String> iterator = keySet.iterator();
		for (int i = 0; i < keySet.size(); i++) {
			String groupEntry = iterator.next();
			JRadioButton radioButton = new JRadioButton(groupEntry);
			if (bpGroup.getSelectedItem() != null && bpGroup.getSelectedItem().equals(groupEntry)) {
				radioButton.setSelected(true);
			}
			radioButton.setActionCommand(groupEntry);
			radioButton.addActionListener(this);
			buttonGroup.add(radioButton);
			mRadioButtonsPanel.add(radioButton);
		}

		// Set first radio button to be selected
		actionPerformed(new ActionEvent(this, 0, bpGroup.getSelectedItem()));
		mRadioButtonsPanel.revalidate();
	}

	private void displayBreakpoint() {
		if (mBreakpoint == null)
			return;

		mBreakpointView.setBreakpoint(mBreakpoint);
		mBpPropertiesPanel.removeAll();
		mBpPropertiesPanel.add(mBreakpointView);
		mBpPropertiesPanel.revalidate();
		mIsEdit = false;
	}

	public BreakpointBase getBreakpoint() {
		if (mIsCanceled)
			return null;

		return mBreakpointView.createBreakpoint();
	}

	private void addDialogButtons(JPanel mainPanel) {
		Box buttonsBox = Box.createHorizontalBox();
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setDefaultCapable(true);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefineBreakpointDialog.this.cancel();
				DefineBreakpointDialog.this.setVisible(false);
				DefineBreakpointDialog.this.dispose();
			}
		});

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		buttonsBox.add(Box.createHorizontalGlue());
		buttonsBox.add(cancelButton);
		buttonsBox.add(okButton);
		mainPanel.add(buttonsBox);
	}

	private class DisplayNameRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			IBreakpointCBoxEntry bpEntry = (IBreakpointCBoxEntry) value;
			String displayName = "";
			displayName = (String) bpEntry.getDisplayName();

			return super.getListCellRendererComponent(list, displayName, index, !isSelected, cellHasFocus);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			BreakpointsGroup bpGroup = (BreakpointsGroup) mSelectedEntry;
			if (mIsEdit == false) {
				mBreakpoint = bpGroup.getGroup().get(e.getActionCommand()).newInstance();
			}
			displayBreakpoint();
		} catch (InstantiationException | IllegalAccessException exception) {
			exception.printStackTrace();
		}
	}
}
