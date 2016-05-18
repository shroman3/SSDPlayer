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

import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.javatuples.Triplet;

import entities.Device;
import entities.RAID.RAIDBasicDevice;
import general.OneObjectCallback;
import manager.RAIDBasicSSDManager;
import utils.Utils;

/**
 * 
 * @author Or Mauda
 *
 */
public class ChooseStripesFrame extends JDialog {
	private static final long serialVersionUID = 1L;

	private AddressWidget<?,?,?,?,?,?> addressWidget;
	private RAIDBasicSSDManager<?, ?, ?, ?, ?> manager;
	private Device<?, ?, ?, ?> currentDevice;
	private OneObjectCallback<Device<?, ?, ?, ?>> updateDevice;
	
	/** The information.
	 * 	The format is: (stripe, stripePagesList, updatedDevice)
	 * */
	private Triplet<Integer, ?, ? extends RAIDBasicDevice<?, ?, ?, ?>> information;

	private ButtonGroup radioGroup;
	
	public ChooseStripesFrame(Window window, RAIDBasicSSDManager<?, ?, ?, ?, ?> manager, Device<?, ?, ?, ?> device, OneObjectCallback<Device<?, ?, ?, ?>> updateDevice) throws HeadlessException {
		super(window, "Choose Stripe", ModalityType.APPLICATION_MODAL);
		Utils.validateNotNull(manager, "manager");
		setDefaultLookAndFeelDecorated(true);
		this.manager = manager; 
		this.currentDevice = device;
		this.updateDevice = updateDevice;
		initSizes(window);
		initComponents();
	}
	
	public Triplet<Integer,?,? extends RAIDBasicDevice<?,?,?,?>> getStripeInformation() {
		return information;
	}
	
	private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        getContentPane().add(mainPanel);
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        mainPanel.add(inputPanel);


        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        inputPanel.add(radioPanel);

        final JPanel addressWidgetPanel = new JPanel(new FlowLayout());
        inputPanel.add(addressWidgetPanel);

        radioGroup = new ButtonGroup();
        for (final AddressWidget<?,?,?,?,?,?> addressWidget : manager.getAddressGetterWidgets()) {
        	JRadioButton radio = new JRadioButton(addressWidget.getDisplayName());
        	radio.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {         
                	if (e.getStateChange()==1) {
                		addressWidget.setDevice(currentDevice);
                		ChooseStripesFrame.this.addressWidget = addressWidget;
                		addressWidgetPanel.removeAll();
                		addressWidgetPanel.add(addressWidget);
                		addressWidgetPanel.updateUI();
                    }
                }
        	});
        	radioGroup.add(radio);
        	radioPanel.add(radio);
		}        
        
        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (addressWidget == null) {
					JOptionPane.showMessageDialog(null, "Type of address wasn't selected", "", JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						addressWidget.validateParms();
						information = addressWidget.getStripeInformation();
						updateDevice.message(information.getValue2());
						ChooseStripesFrame.this.dispose();
					}
					catch (Throwable e) {
						JOptionPane.showMessageDialog(null, e.getMessage(), "This page doesn't exist.", JOptionPane.ERROR_MESSAGE);						
					}
				}
			}

		});
		mainPanel.add(button);
	}

	private void initSizes(Window window) {
		setSize(400, 211);
        setResizable(false);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setLocationRelativeTo(window);
	}
	
}
