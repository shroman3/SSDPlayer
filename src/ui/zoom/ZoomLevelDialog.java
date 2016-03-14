package ui.zoom;

import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
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
	
	public ZoomLevelDialog(Window parentWindow, SSDManager<?, ?, ?, ?, ?> manager) {
		super(parentWindow, DIALOG_HEADER);
		
		mManager = manager;
		mZoomLevels = new HashMap<>();
		
		setDefaultLookAndFeelDecorated(true);
		setModal(true);
		setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(350, 250);
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
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
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
			
			radioPanel.add(zoomLevelButton);
			mRadioGroup.add(zoomLevelButton);
			
			mZoomLevels.put(zoomLevel.getName(), zoomLevel);
		}
		
		if (!selected) {
			firstButton.setSelected(true);
			mSelectedZoomLevel = firstButton.getText();
		}
		
		mMainPanel.add(radioPanel);
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
				mSelectedZoomLevel = jbutton.getText();
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
				if (jbutton.getText().equals(mSelectedZoomLevel)) {
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
