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
import java.util.List;

import javax.swing.JFormattedTextField;

import org.javatuples.Triplet;

import entities.RAID.RAIDBasicBlock;
import entities.RAID.RAIDBasicChip;
import entities.RAID.RAIDBasicDevice;
import entities.RAID.RAIDBasicPage;
import entities.RAID.RAIDBasicPlane;
import ui.AddressWidget;
import utils.Utils;;

/**
 * 
 * @author Or Mauda
 *
 */
public class LogicalAddressWidget <P extends RAIDBasicPage, B extends RAIDBasicBlock<P>, T extends RAIDBasicPlane<P,B>, C extends RAIDBasicChip<P,B,T>, D extends RAIDBasicDevice<P,B,T,C>, S extends RAIDBasicSSDManager<P, B, T, C, D>>
	extends AddressWidget<P,B,T,C,D,S> {
	private static final long serialVersionUID = 1L;

	private JFormattedTextField logicalPageInput;

	public LogicalAddressWidget(S manager) {
		this("Logical Page", manager);
	}
	
	protected LogicalAddressWidget(String name, S manager) {
		super(name, manager);
		logicalPageInput = new JFormattedTextField(new DecimalFormat());
		logicalPageInput.setValue(0);
		addField(logicalPageInput, "logical page");
	}

	@Override
	public Triplet<Integer, List<P>, RAIDBasicDevice<P, B, T, C>> getStripeInformation() throws Exception {
		Triplet<Integer, List<P>, RAIDBasicDevice<P, B, T, C>> information = device.setHighlightByLogicalP(true, getLogicalPage());
		if (information.getValue0() == -1) { // means we didn't find this page
			throw new Exception("This page doesn't exist.");
		}
		return information;
	}
	
	
	protected int getLogicalPage() {
		int number = ((Number)logicalPageInput.getValue()).intValue();
		if (number < 0) {
			throw new IndexOutOfBoundsException("logical page must be a positive number");
		}
		return number;
	}
	
	@Override
	public void validateParms() {
		double logicalPage = ((Number)logicalPageInput.getValue()).doubleValue();
		Utils.validateNotNegative(logicalPage, "logical page");
		Utils.validateInteger(logicalPage, "logical page");
	}
}
