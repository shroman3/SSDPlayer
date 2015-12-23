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
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import entities.Device;
import manager.SSDManager;
import manager.WorkloadGenerator;

public abstract class WorkloadWidget <D extends Device<?,?,?,?>, S extends SSDManager<?,?,?,?,D>> 
	extends JPanel {
	private static final long serialVersionUID = 1L;
	private String name;
	private JFormattedTextField lengthInput;
	protected S manager;

	
	
	public WorkloadWidget(String name, S manager) {
		this.name = name; 
		this.manager = manager;
		setLayout(new GridLayout(0,2));
		setSize(300, 300);

		lengthInput = new JFormattedTextField(new DecimalFormat("##,###,###"));
		lengthInput.setValue(10000);
		
		addField(lengthInput, "Workload Length");
	}
	
	public abstract WorkloadGenerator<D,S> createWorkloadGenerator();
	
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
	
	protected int getWorkloadLength() {
		return ((Number)lengthInput.getValue()).intValue();
	}

	public void validateParms() {
		int length = ((Number)lengthInput.getValue()).intValue();
		if (length < 1000) {
			throw new IllegalArgumentException("Generated trace length should be at least 1000 operations long");
		}
		if (length > 1000000) {
			throw new IllegalArgumentException("Generated trace cannot be longer than 1000000 operations");
		}
	}
}
