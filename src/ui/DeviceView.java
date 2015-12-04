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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import manager.VisualConfig;
import entities.Device;
import entities.Chip;

public class DeviceView extends JPanel {
	private static final long serialVersionUID = 1L;
	private List<ChipView> chipList;
	private Device<?,?,?,?> device;
	private VisualConfig visualConfig;

	public DeviceView(VisualConfig visualConfig, Device<?,?,?,?> device) {
		this.device = device;
		this.visualConfig = visualConfig;
		setLayout(new FlowLayout());
		chipList = initChips();
	}

	public void setDevice(Device<?,?,?,?> device) {
		this.device = device;
		int chipIndex = 0;
		for (Chip<?,?,?> chip : device.getChips()) {
			chipList.get(chipIndex++).setChip(chip);
		}
	}

	private List<ChipView> initChips() {
		List<ChipView> chipsList = new ArrayList<ChipView>();
		int chipIndex = 0;
		for (Chip<?,?,?> chip : device.getChips()) {
			ChipView chipView = new ChipView(chip, chipIndex++, visualConfig);
			chipsList.add(chipView);
			add(chipView);
		}
		return chipsList;
	}
}