package ui.zoom;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import manager.SSDManager;
import zoom.IZoomLevel;

public class ZoomLevelDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final String DIALOG_HEADER = "Zoom Level";
	
	private SSDManager<?, ?, ?, ?, ?> mManager;
	private JPanel mMainPanel;
	private ButtonGroup mRadioGroup;
	private String mSelectedZoomLevel;
	private HashMap<String, IZoomLevel> mZoomLevels;
	private HashMap<String, JPanel> mZoomGroupPanels;
	
	public ZoomLevelDialog(Window parentWindow, SSDManager<?, ?, ?, ?, ?> manager) {
		super(parentWindow, DIALOG_HEADER);
		
		mManager = manager;
		mZoomLevels = new HashMap<>();
		mZoomGroupPanels = new HashMap<>();
		
		setDefaultLookAndFeelDecorated(true);
		setModal(true);
		setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(300, 300);
		setLocationRelativeTo(parentWindow);
		initComponents();
		addDialogButtons();
		this.addComponentListener(new ShownListener());
	}

	private void initComponents() {
		mMainPanel = new JPanel();
		mMainPanel.setBorder(new EmptyBorder(5, 5, 5 , 5));
		mMainPanel.setLayout(new BoxLayout(mMainPanel, BoxLayout.Y_AXIS));
		add(mMainPanel);
		
		addManagerZoomLevels();
	}

	private void addManagerZoomLevels() {
		JPanel radioFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		mMainPanel.add(radioFlowPanel);
		
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		
		mZoomLevels.clear();
		
		mRadioGroup = new ButtonGroup();
		boolean selected = false;
		JRadioButton firstButton = null;
		
		for (IZoomLevel zoomLevel : mManager.getSupportedZoomLevels()) {
			JRadioButton zoomLevelButton = new JRadioButton(zoomLevel.getName());
			
			if (firstButton == null) {
				firstButton = zoomLevelButton;
			}
			
			if (zoomLevel.getName().equals(mSelectedZoomLevel)) {
				zoomLevelButton.setSelected(true);
				selected = true;
			}
			
			String zoomGroupName = zoomLevel.getGroup();
			if (zoomGroupName != null && !mZoomGroupPanels.containsKey(zoomGroupName)) {
				JPanel groupPanel = new JPanel();
				groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
				
				JPanel groupOptionsPanel = new JPanel();
				groupOptionsPanel.setLayout(new BoxLayout(groupOptionsPanel, BoxLayout.Y_AXIS));
				
				groupOptionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
				
				groupPanel.add(new JLabel(zoomGroupName));
				groupPanel.add(groupOptionsPanel);
				mZoomGroupPanels.put(zoomGroupName, groupOptionsPanel);
				
				radioPanel.add(groupPanel);
			}
			
			if (mZoomGroupPanels.containsKey(zoomGroupName)) {
				mZoomGroupPanels.get(zoomGroupName).add(zoomLevelButton);
			} else {
				radioPanel.add(zoomLevelButton);
			}
			
			mRadioGroup.add(zoomLevelButton);
			mZoomLevels.put(zoomGroupName + " " + zoomLevel.getName(), zoomLevel);
			zoomLevelButton.setActionCommand(zoomGroupName + " " + zoomLevel.getName());
		}
		
		if (!selected) {
			firstButton.setSelected(true);
			mSelectedZoomLevel = firstButton.getText();
		}
		
		radioFlowPanel.add(radioPanel);
	}

	private void addDialogButtons() {
		Box buttonsBox = Box.createHorizontalBox();
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setDefaultCapable(true);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ZoomLevelDialog.this.setVisible(false);
				ZoomLevelDialog.this.dispose();
			}
		});
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setZoomLevel();
				setVisible(false);
				dispose();
			}
		});
		buttonsBox.add(Box.createHorizontalGlue());
		buttonsBox.add(cancelButton);
		buttonsBox.add(okButton);
		mMainPanel.add(buttonsBox);
	}

	private void setZoomLevel() {
		Enumeration<AbstractButton> buttons = mRadioGroup.getElements();
		
		while (buttons.hasMoreElements()) {
			JRadioButton jbutton = (JRadioButton) buttons.nextElement();
			if (jbutton.isSelected()) {
				mSelectedZoomLevel = jbutton.getActionCommand();
				mZoomLevels.get(mSelectedZoomLevel).applyZoom(mManager);
			}
		}
	}
	
	class ShownListener implements ComponentListener {
		@Override
		public void componentShown(ComponentEvent e) {
			Enumeration<AbstractButton> buttons = mRadioGroup.getElements();
			
			while (buttons.hasMoreElements()) {
				JRadioButton jbutton = (JRadioButton) buttons.nextElement();
				if (jbutton.getActionCommand().equals(mSelectedZoomLevel)) {
					jbutton.setSelected(true);
				}
			}
		}

		@Override
		public void componentResized(ComponentEvent e) {
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}
	}
}
