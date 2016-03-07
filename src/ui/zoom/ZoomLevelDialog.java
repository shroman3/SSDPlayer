package ui.zoom;

import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import manager.SSDManager;
import zoom.ZoomLevel;

public class ZoomLevelDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String DIALOG_HEADER = "Zoom Level";
	
	private SSDManager<?, ?, ?, ?, ?> mManager;
	private String mZoomLevel;
	private HashMap<String, ZoomLevel> mAvailableZoomLevels;
	private HashMap<String, JPanel> mZoomLevelOptionsPanels;
	
	public ZoomLevelDialog(Window parentWindow, SSDManager<?, ?, ?, ?, ?> manager) {
		super(parentWindow, DIALOG_HEADER);
		
		mManager = manager;
		mAvailableZoomLevels = new HashMap<>();
		setDefaultLookAndFeelDecorated(true);
		setModal(true);
		setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(460, 250);
		setLocationRelativeTo(parentWindow);
		initComponents();
	}

	private void initComponents() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5 , 5));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel);
		
		addManagerZoomLevels(mainPanel);
	}

	private void addManagerZoomLevels(JPanel mainPanel) {
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		
		ButtonGroup group = new ButtonGroup();
		for (ZoomLevel zoomLevel : mManager.getSupportedZoomLevels()) {
			JRadioButton zoomLevelButton = new JRadioButton(zoomLevel.getName());
			zoomLevelButton.setActionCommand(zoomLevel.getName());
			zoomLevelButton.addActionListener(this);
			
			radioPanel.add(zoomLevelButton);
			group.add(zoomLevelButton);
			mAvailableZoomLevels.put(zoomLevel.getName(), zoomLevel);
			
			JPanel subOptionsPanel = new JPanel(new GridLayout(0, 1));
			
			if (!zoomLevel.getSubOptions().isEmpty()) {
				ButtonGroup subGroup = new ButtonGroup();
				
				for (String subOption : zoomLevel.getSubOptions()) {
//					JRadioButton zoomLevelButton = new JRadioButton(zoomLevel.getName());
//					zoomLevelButton.setActionCommand(zoomLevel.getName());
//					zoomLevelButton.addActionListener(this);
				}
			}
			
		}
		
		mainPanel.add(radioPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String zoomLevelName = e.getActionCommand();
		ZoomLevel zoomLevel = mAvailableZoomLevels.get(zoomLevelName);
		
		if (!zoomLevel.getSubOptions().isEmpty()) {
			
		}
	}
	
}
