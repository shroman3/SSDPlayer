/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
 * Copyright (c) 2015, Technion � Israel Institute of Technology
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
package manager;

import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;

import entities.Device;

public class ZipfWorkloadWidget <D extends Device<?>, S extends SSDManager<?,?,?,?,D>> 
			extends UniformWorkloadWidget<D,S> {
	private static final long serialVersionUID = 1L;

	private JFormattedTextField exponentInput;
	
	public ZipfWorkloadWidget(S manager) {
		super("Zipf", manager);
		exponentInput = new JFormattedTextField(new DecimalFormat("#0.00"));
		exponentInput.setValue(0.5);
		addField(exponentInput, "Exponent");
	}

	@Override
	public WorkloadGenerator<D,S> createWorkloadGenerator() {
		return new ZipfWorkloadGenerator<D,S>(manager, getWorkloadLength(), getSeed(), getExponent());
	}

	public WorkloadGenerator<D, S> createWorkloadGenerator(Integer workloadLength, Integer seed, Double exponent) {
		return new ZipfWorkloadGenerator<D,S>(manager, workloadLength, seed, exponent);
	}

	private double getExponent() {
		return ((Number)exponentInput.getValue()).doubleValue();
	}
	
	public void validateParms() {
		super.validateParms();
		double exp = ((Number)exponentInput.getValue()).doubleValue();
		if (exp <= 0) {
			throw new IllegalArgumentException("Exponent should be a positive number");
		}
	}
}
