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
package manager;

import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;

import entities.Block;
import entities.Chip;
import entities.Device;
import entities.Page;
import entities.Plane;
import ui.WorkloadWidget;

public class UniformWorkloadWidget <P extends Page, B extends Block<P>, T extends Plane<P,B>, C extends Chip<P,B,T>, D extends Device<P,B,T,C>, S extends SSDManager<P, B, T, C, D>>
	extends WorkloadWidget<P,B,T,C,D,S> {
	private static final long serialVersionUID = 1L;

	private JFormattedTextField seedInput;

	public UniformWorkloadWidget(S manager) {
		this("Uniform", manager);
	}
	
	protected UniformWorkloadWidget(String name, S manager) {
		super(name, manager);
		seedInput = new JFormattedTextField(new DecimalFormat());
		seedInput.setValue(0);
		addField(seedInput, "Random Seed");
	}

	@Override
	public WorkloadGenerator<P,B,T,C,D,S> createWorkloadGenerator() {
		return new UniformWorkloadGenerator<P,B,T,C,D,S>(manager, getWorkloadLength(), getSeed(), getMaxWriteSize(), isWriteSizeUniform());
	}
	
	protected int getSeed() {
		return ((Number)seedInput.getValue()).intValue();
	}
	
	public void validateParms() {
		super.validateParms();
		int seed = ((Number)seedInput.getValue()).intValue();
		if (seed < 0) {
			throw new IllegalArgumentException("Seed should be non negative");
		}
	}
}
