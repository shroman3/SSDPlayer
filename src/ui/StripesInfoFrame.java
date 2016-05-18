/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Or Mauda, Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
 * Copyright (c) 2015, Technion – Israel Institute of Technology
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *******************************************************************************/
package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;

import org.javatuples.Triplet;

import entities.Device;
import entities.Page;
import entities.RAID.RAIDBasicDevice;
import general.OneObjectCallback;
import manager.RAIDBasicSSDManager;
import manager.RAIDSSDManager;
import manager.RAIDVisualizationSSDManager;
import manager.SettableTraceParser;
import manager.TraceParser;
import utils.Utils;

/**
 * 
 * @author Or Mauda
 *
 */
public class StripesInfoFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private RAIDBasicSSDManager<?, ?, ?, ?, ?> manager;
	
	private SettableTraceParser<?,?> parser;
	
	private OneObjectCallback<Device<?, ?, ?, ?>> updateDevice;
	
	private Window parentWindow;
	
	private ChooseStripesFrame stripesChooserFrame;
	
	private static JPanel panel = null;
	
	/** The current informations.
	 *  objects' format is: (stripe, stripePagesList, updatedDevice)
	 *  */
	private static List<Triplet<Integer, ?, ?>> currentInformations;
	
	private static List<JTextField> textFields;
	private static List<JTextPane> textPanes;
	private static List<JButton> buttons;
	
	private static int stripesNumber = 0; // the number of stripes currently showed in Stripes Info Frame
	
	private final int stripesToShow = 10; // the number of possible stripes in StripesInfoFrame 
	
	@SuppressWarnings("rawtypes")
	public StripesInfoFrame(Window window, RAIDBasicSSDManager<?, ?, ?, ?, ?> manager, TraceParser<?,?> parser, OneObjectCallback<Device<?, ?, ?, ?>> updateDevice) throws HeadlessException {
		super(window, "Stripes Info", ModalityType.APPLICATION_MODAL);
		if (!(parser instanceof SettableTraceParser)) {
			throw new IllegalArgumentException("Cannot create StripesInfoFrame with regular trace parser (SettableTraceParser needed)");
		}
		setResizable(false);
		Utils.validateNotNull(manager, "manager");
		setDefaultLookAndFeelDecorated(true);
		this.manager = manager;
		this.parser = (SettableTraceParser)parser;
		this.parentWindow = window;
		this.updateDevice = updateDevice;
		initSizes(window);
		initComponents();
	}
	
	public static void reset(StripesInfoFrame frame) {
		panel = null;
		textFields = null;
		textPanes = null;
		buttons = null;
		stripesNumber = 0;
		currentInformations = new ArrayList<Triplet<Integer,?,?>>();
	}
	
	private void initComponents() {	
		if (currentInformations == null) {
			currentInformations = new ArrayList<Triplet<Integer,?,?>>();
		}
		
        JPanel bottomPanel = new JPanel();
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        
        if (panel == null) {
        	initInformationPanel();        	
        } else {
        	updateInformationsAfterRun();
        	updateInformationPanel();
        	getContentPane().add(panel, BorderLayout.CENTER);
        	panel.setVisible(true);
        }
        
        JButton showStripeButton = new JButton("Show another stripe");
        GroupLayout gl_bottomPanel = new GroupLayout(bottomPanel);
        gl_bottomPanel.setHorizontalGroup(
        	gl_bottomPanel.createParallelGroup(Alignment.TRAILING)
        		.addGroup(Alignment.LEADING, gl_bottomPanel.createSequentialGroup()
        			.addGap(265)
        			.addComponent(showStripeButton, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
        			.addGap(266))
        );
        gl_bottomPanel.setVerticalGroup(
        	gl_bottomPanel.createParallelGroup(Alignment.TRAILING)
        		.addGroup(gl_bottomPanel.createSequentialGroup()
        			.addContainerGap(18, Short.MAX_VALUE)
        			.addComponent(showStripeButton)
        			.addContainerGap())
        );
        gl_bottomPanel.setAutoCreateContainerGaps(true);
        gl_bottomPanel.setAutoCreateGaps(true);
        bottomPanel.setLayout(gl_bottomPanel);
        showStripeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addStripe();
			}

		});		
	}
	
	private boolean isPresented(int stripe) {
		for(int i = 0; i < stripesNumber; i++) {
			if(currentInformations.get(i).getValue0() == stripe) {
				return true;
			}
		}
		return false;
	}
	
	private void initInformationPanel() {
        panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        
        panel.setLayout(null);
        
        textFields = new ArrayList<JTextField>(stripesToShow);
        textPanes = new ArrayList<JTextPane>(stripesToShow);
        buttons = new ArrayList<JButton>(stripesToShow);
        
        for(int i = 1; i <= stripesToShow; i++) {
        	JTextPane currentTextPane = new JTextPane();
        	currentTextPane.setEnabled(true);
        	currentTextPane.setEditable(false);
        	currentTextPane.setBounds(20, 3 + 30*i, 100, 23);
        	Font font = new Font("Courier", Font.BOLD,12);
        	currentTextPane.setFont(font);
        	panel.add(currentTextPane);
        	textPanes.add(currentTextPane);
        	
        	JTextField currentTextField = new JTextField();
        	currentTextField.setEditable(false);
        	currentTextField.setBounds(140, 30*i, 450, 27);
        	panel.add(currentTextField);
        	currentTextField.setColumns(10);
        	textFields.add(currentTextField);
        	
    		JButton currentButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/removeStripe.png")));
    		currentButton.setEnabled(false);
    		currentButton.setBounds(596, 3 + 30*i, 22, 22);
    		
    		final int position = i;
    		currentButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					cancelStripe(position);
				}
			});
    		
    		panel.add(currentButton);
    		buttons.add(currentButton);
        }
	}
	
	/**
	 * Cancels a stripe. Called when a remove button is pressed
	 *
	 * @param position the position
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void cancelStripe(int position) {
		Integer stripe = getDigits(textPanes.get(position-1).getText());
		
		// turn-off the highlighted stripe
		Triplet<Integer, ?, ? extends RAIDBasicDevice<?, ?, ?, ?>> information = ((RAIDBasicDevice) parser.getCurrentDevice()).setHighlightByParityP(false, -1, stripe, false);
		if (information != null) {
			RAIDBasicSSDManager.setDevice(manager, parser, information.getValue2());

			updateDevice.message((Device<?, ?, ?, ?>) information.getValue2());
		}
		shiftDown(position);
		stripesNumber--;
	}
	
	/**
	 * Adds the stripe. Called when 'showStripeButton' is pressed.
	 */
	private void addStripe() {
		if (stripesNumber == stripesToShow) {
			JOptionPane.showMessageDialog(null, "You can only highlight "+ stripesToShow +" stripes.", "", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {			
			stripesChooserFrame = new ChooseStripesFrame(parentWindow, manager, parser.getCurrentDevice(), updateDevice);
		} catch (Exception e) {
			stripesChooserFrame = null;
		}
		utils.Utils.validateNotNull(stripesChooserFrame, "stripesChooserFrame");
		stripesChooserFrame.setVisible(true);
		Triplet<Integer,?,? extends RAIDBasicDevice<?,?,?,?>> information = stripesChooserFrame.getStripeInformation();
		if (information != null) {
			if (isPresented(information.getValue0())) {
				JOptionPane.showMessageDialog(null, "This stripe (" + information.getValue0() + ") is already presented.", "", JOptionPane.ERROR_MESSAGE);
				return;
			}
			currentInformations.add(stripesNumber, information);
			RAIDBasicSSDManager.setDevice(manager, parser, information.getValue2());
			
			addStripeInfo(information);
		}
	}
	
	/**
	 * Extracts only the digits from text and returns an integer.
	 *
	 * @param text the text
	 * @return the digits
	 */
	private Integer getDigits(String text) {
		if (text == null) {
			throw new NullPointerException();
		}
		return Integer.parseInt(text.replaceAll("\\D+",""));
	}
	
	/**
	 * Update the information list, and parser device, after a run.
	 */
	private void updateInformationsAfterRun() {
		RAIDBasicDevice<?, ?, ?, ?> deviceAfterRun = (RAIDBasicDevice<?, ?, ?, ?>) parser.getCurrentDevice();
		int stripe;
		Triplet<Integer, ?, ?> information = null;
		
		for(int i = 0; i < stripesNumber; i++) {
			stripe = currentInformations.get(i).getValue0();
			information = deviceAfterRun.setHighlightByParityP(true, -1, stripe, false);
			deviceAfterRun = (RAIDBasicDevice<?, ?, ?, ?>) information.getValue2();
			currentInformations.set(i, information);
		}
		RAIDBasicSSDManager.setDevice(manager, parser, deviceAfterRun);

		updateDevice.message((Device<?, ?, ?, ?>) deviceAfterRun);
	}
	
	/**
	 * Update information panel after a run.
	 */
	private void updateInformationPanel() {
		for(int i = 0; i < stripesNumber; i++) {
			textFields.get(i).setText(getInfoDataFormat(currentInformations.get(i)));
		}
	}
	
	/**
	 * Add a stripe info to be shown in the text fields and text panes, and the remove buttons.
	 *
	 * @param information the information
	 */
	private void addStripeInfo(Triplet<Integer, ?, ?> information) {
		stripesNumber++;
		
		// Get the stripe's color
		Color stripeColor;
		if (manager.getManagerName().toLowerCase().contains("raid simulation") == true) { // means we're in visualization
			stripeColor = ((RAIDVisualizationSSDManager) manager).getStripeFrameColor(information.getValue0()); 
		} else { // means we're not in visualization
			stripeColor = ((RAIDSSDManager) manager).getStripeFrameColor(information.getValue0());
		}
		
		// The color of the border (equals the color of the matched stripe)
		LineBorder coloredBorder = new LineBorder(stripeColor, 3);
		
		JTextPane textPane = textPanes.get(stripesNumber - 1);
		textPane.setText(getInfoStripeTitleFormat(information));
		textPane.setBorder(coloredBorder);
		textPanes.set(stripesNumber - 1, textPane);
		
		JTextField textField = textFields.get(stripesNumber - 1);
		textField.setText(getInfoDataFormat(information));
		textField.setBorder(coloredBorder);
		textFields.set(stripesNumber - 1, textField);
		
		JButton button = buttons.get(stripesNumber - 1);
		button.setEnabled(true);
		buttons.set(stripesNumber - 1, button);
	}
	
	private String getInfoDataFormat(Triplet<Integer, ?, ?> information) {
		@SuppressWarnings("unchecked")
		List<Integer> logicalPages = getLogicalPagesList((List<Page>) information.getValue1());
		return " Logical Pages: " + logicalPages.toString();
	}
	
	private String getInfoStripeTitleFormat(Triplet<Integer, ?, ?> information) {
		return "Stripe " + Integer.toString(information.getValue0()) + ":";
	}
	
	private List<Integer> getLogicalPagesList(List<Page> pagesList) {
		List<Integer> logicalPages = new ArrayList<>();
		for (Page page : pagesList) {
			if (page.getLp() != -1) { // means it's a data page and not a parity page
				logicalPages.add(page.getLp());
			}
		}
		return logicalPages;
	}
	
	
	/**
	 * Shifts down the next stripe's information after removing one stripe information below (numeric) them. (appears as 'up' in the GUI)
	 *
	 * @param position the position of removed stripe (between 1 to 10)
	 */
	private void shiftDown(int position) {
		for(int i = position; i <= stripesNumber; i++) { // the stripes number is counted as before the removal of 'position'
			if (i == stripesNumber) {
				textPanes.get(i-1).setText("");
				textFields.get(i-1).setText("");
				
				textPanes.get(i-1).setBorder(null);
				textFields.get(i-1).setBorder(null);
				
				buttons.get(i-1).setEnabled(false);
				currentInformations.set(i-1, null);
			} else {
				textPanes.get(i-1).setText(textPanes.get(i).getText());
				textFields.get(i-1).setText(textFields.get(i).getText());
				
				textPanes.get(i-1).setBorder(textPanes.get(i).getBorder());
				textFields.get(i-1).setBorder(textFields.get(i).getBorder());
				
				currentInformations.set(i-1, currentInformations.get(i));
			}
		}
	}

	private void initSizes(Window window) {
		setSize(640, 435);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setLocationRelativeTo(window);
	}
}
