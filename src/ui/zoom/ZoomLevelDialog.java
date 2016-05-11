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

import general.OneObjectCallback;
import manager.SSDManager;
import manager.VisualConfig;
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
	
	private VisualConfig visualConfig;
	private OneObjectCallback<Boolean> resetDevice;
	
	public ZoomLevelDialog(Window parentWindow, SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig, OneObjectCallback<Boolean> resetDevice) {
		super(parentWindow, DIALOG_HEADER);
		
		this.resetDevice = resetDevice;
		mManager = manager;
		mZoomLevels = new HashMap<>();
		mZoomGroupPanels = new HashMap<>();
		this.visualConfig = visualConfig; 
		
		setDefaultLookAndFeelDecorated(true);
		setModal(true);
		setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(300, 300);
		setLocationRelativeTo(parentWindow);
		initComponents();
		addDialogButtons();
		this.addComponentListener(new ShownListener());
		visualConfig.restoreXmlValues();
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
		boolean anySelected = false;
		JRadioButton firstButton = null;
		
		int zoomLevelNumber = 1;
		for (IZoomLevel zoomLevel : mManager.getSupportedZoomLevels()) {
			String zoomGroupName = zoomLevel.getGroup();

			JRadioButton zoomLevelButton = new JRadioButton(zoomLevel.getName());
			if (zoomGroupName == null) {
				zoomLevelButton.setText(zoomLevelNumber + ". " + zoomLevel.getName());
				zoomLevelNumber++;
			}
			
			if (firstButton == null) {
				firstButton = zoomLevelButton;
			}
			
			if (zoomLevel.getName().equals(mSelectedZoomLevel)) {
				zoomLevelButton.setSelected(true);
				anySelected = true;
			}
			
			if (zoomGroupName != null && !mZoomGroupPanels.containsKey(zoomGroupName)) {
				JPanel groupPanel = new JPanel();
				groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
				groupPanel.setBorder(BorderFactory.createEmptyBorder(0, 22, 0, 0));
				
				JPanel groupOptionsPanel = new JPanel();
				groupOptionsPanel.setLayout(new BoxLayout(groupOptionsPanel, BoxLayout.Y_AXIS));
				
				groupOptionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
				
				groupPanel.add(new JLabel(zoomLevelNumber + ". " + zoomGroupName));
				groupPanel.add(groupOptionsPanel);
				mZoomGroupPanels.put(zoomGroupName, groupOptionsPanel);
				
				radioPanel.add(groupPanel);
				zoomLevelNumber++;
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
		
		if (!anySelected) {
			firstButton.setSelected(true);
			mSelectedZoomLevel = firstButton.getActionCommand();
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
				mZoomLevels.get(mSelectedZoomLevel).applyZoom(mManager, visualConfig);
			}
		}
		resetDevice.message(true);
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
