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

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import manager.VisualConfig;
import entities.Chip;
import entities.Plane;
import general.Consts;

public class ChipView extends JPanel {
	private static final long serialVersionUID = 1L;
	private List<PlaneView> planeList;
	private Chip<?,?,?> chip;
	private int chipIndex;
	private VisualConfig visualConfig;
    
    public ChipView(Chip<?,?,?> chip, int chipIndex, VisualConfig visualConfig) {
		this.chip = chip;
		this.visualConfig = visualConfig;
		this.chipIndex = chipIndex;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel titleLabel = new JLabel("Chip " + chipIndex);
		add(titleLabel);
		planeList = initPlanes();
		setBorder(new RoundedBorder(Consts.Colors.BORDER));
		setBackground(Consts.Colors.CONTROL_LIGHTER);
	}

	public void setChip(Chip<?,?,?> chip) {
		if(this.chip == chip){
			return;
		}
    	this.chip = chip;
		int planeIndex = 0;
		for (Plane<?,?> plane :  chip.getPlanes()) {
			planeList.get(planeIndex++).setPlane(plane);
		}
    }

	private List<PlaneView> initPlanes() {
		JPanel planesPanel = new JPanel(new GridLayout(visualConfig.getPlanesInRow(), 1));
		planesPanel.setLayout(new GridLayout(0, visualConfig.getPlanesInRow()));
		List<PlaneView> planesList = new ArrayList<PlaneView>();
		int planeIndex = 0;
		for (Plane<?,?> plane : chip.getPlanes()) {
			PlaneView planeView = new PlaneView(plane, chipIndex, planeIndex++, visualConfig);
			planesList.add(planeView);
			planesPanel.add(planeView);
		}
		add(planesPanel);
		return planesList;
	}
}