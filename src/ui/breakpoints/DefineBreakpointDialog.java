package ui.breakpoints;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import breakpoints.BreakpointFactory;
import breakpoints.IBreakpoint;

public class DefineBreakpointDialog  extends JDialog {
	private static final long serialVersionUID = 1L;
	public static final String DIALOG_HEADER = "Define breakpoint";
	
	private IBreakpoint mBreakpoint;
	private JComboBox<Class<? extends IBreakpoint>> mBreakpointsCBox;
	private JPanel mBpPropertiesPanel;
	private BreakpointViewBase mBreakpointView;
	List<Class<? extends IBreakpoint>> mBreakpointClasses;
	private boolean mIsCanceled;
	
	public DefineBreakpointDialog(Window parentWindow, IBreakpoint breakpoint) {
		super(parentWindow, DIALOG_HEADER);
		setDefaultLookAndFeelDecorated(true);

		addWindowListener(new WindowAdapter() {
			 @Override
			    public void windowClosing(WindowEvent e) {
			        mIsCanceled = true;
			    }
		});
		
		mBreakpointClasses = BreakpointFactory.getBreakpointClasses();

		mBreakpointView = new BreakpointViewBase();
		instantiateInitialBreakpoint(breakpoint);
		
		setModal(true);
		setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(parentWindow);
		initComponents();
		displayBreakpoint();
	}

	public void cancel() {
		mIsCanceled = true;
	}
	
	private void instantiateInitialBreakpoint(IBreakpoint breakpoint) {
		try {
			if (breakpoint == null) {
					mBreakpoint = BreakpointFactory.getBreakpointClasses().get(0).newInstance();
			} else {
				mBreakpoint = breakpoint;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			setVisible(false);
			dispose();
		}
	}
	
	private void initComponents() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5 , 5));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel);
		initComboBox(mainPanel);
		
		mBpPropertiesPanel = new JPanel();
		mBpPropertiesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		mBpPropertiesPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		
		mainPanel.add(mBpPropertiesPanel);
		mainPanel.add(Box.createVerticalGlue());
		addDialogButtons(mainPanel);
	}

	private void initComboBox(JPanel mainPanel) {
		DefaultComboBoxModel<Class<? extends IBreakpoint>> cBoxModel = new DefaultComboBoxModel<Class<? extends IBreakpoint>>();
		for (Class<? extends IBreakpoint> bpClass : mBreakpointClasses) {
			cBoxModel.addElement(bpClass);
		}
		
		mBreakpointsCBox = new JComboBox<>(cBoxModel);
		mBreakpointsCBox.setRenderer(new DisplayNameRenderer());
		mBreakpointsCBox.setPreferredSize(new Dimension(mBreakpointsCBox.getPreferredSize().width, 30));
		mBreakpointsCBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		mBreakpointsCBox.setSelectedItem(mBreakpoint.getClass());
		mBreakpointsCBox.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mBreakpoint = ((Class<? extends IBreakpoint>) mBreakpointsCBox.getSelectedItem()).newInstance();
					displayBreakpoint();
				} catch (InstantiationException | IllegalAccessException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		mainPanel.add(mBreakpointsCBox);
	}
	
	private void displayBreakpoint() {
		if (mBreakpoint == null) return;
		
		mBreakpointView.setBreakpoint(mBreakpoint);
		mBpPropertiesPanel.removeAll();
		mBpPropertiesPanel.add(mBreakpointView);
		mBpPropertiesPanel.revalidate();
	}
	
	public IBreakpoint getBreakpoint() {
		if (mIsCanceled) return null;
		
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
			@SuppressWarnings("unchecked")
			Class<? extends IBreakpoint> bpClass = (Class<? extends IBreakpoint>) value;
			String displayName = "";
			try {
				displayName = (String) bpClass.getMethod("getDisplayName").invoke(bpClass.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return super.getListCellRendererComponent(list, displayName, index, !isSelected, cellHasFocus);
		}
	}
}
