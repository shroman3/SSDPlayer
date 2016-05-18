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

import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.javatuples.Triplet;

import entities.Device;
import entities.RAID.RAIDBasicBlock;
import entities.RAID.RAIDBasicChip;
import entities.RAID.RAIDBasicDevice;
import entities.RAID.RAIDBasicPage;
import entities.RAID.RAIDBasicPlane;
import manager.RAIDBasicSSDManager;

/**
 * 
 * @author Or Mauda
 *
 */
public abstract class AddressWidget <P extends RAIDBasicPage, B extends RAIDBasicBlock<P>, T extends RAIDBasicPlane<P,B>, C extends RAIDBasicChip<P,B,T>, D extends RAIDBasicDevice<P,B,T,C>, S extends RAIDBasicSSDManager<P, B, T, C, D>> 
	extends JPanel {
	private static final long serialVersionUID = 1L;
	private String name;
	protected S manager;
	protected D device;
	
	public AddressWidget(String name, S manager) {
		this.name = name; 
		this.manager = manager;
		setLayout(new GridLayout(0,2));
		setSize(300, 300);
	}
	
	/**
	 * Highlights the stripe using the physical\logical\parity page's address given in GUI.
	 *
	 * @return a triplet.	first value - the stripe number.
	 * 						second value - a list including the stripe pages (parity pages + data pages).
	 * 				  		third value - the updated device.
	 * @throws Exception 
	 */
	public abstract Triplet<Integer, List<P>, ? extends RAIDBasicDevice<P,B,T,C>> getStripeInformation() throws Exception;
	
	public String getDisplayName() {
		return name;
	}
	
	protected void addField(final Component input, String label) {
		add(new JLabel(label));
		input.addFocusListener(new java.awt.event.FocusAdapter() {
		    public void focusGained(java.awt.event.FocusEvent evt) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		            	((JTextField) input).selectAll();
		            }
		        });
		    }
		});
		add(input);
	}
	
	@SuppressWarnings("unchecked")
	public void setDevice(Device<?,?,?,?> device) {
		this.device = (D) device;
	}

	public abstract void validateParms();
}
