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
import entities.Block;
import entities.Plane;
import general.Consts;

public class PlaneView extends JPanel {
	private static final long serialVersionUID = 1L;
	private List<BlockView> blocksList;
	private Plane<?,?> plane;
	private VisualConfig visualConfig;
	private int chipIndex;
	private int planeIndex;
    
    public PlaneView(Plane<?,?> plane, int chipIndex, int planeIndex, VisualConfig visualConfig) {
		this.plane = plane;
		this.planeIndex = planeIndex;
		this.chipIndex = chipIndex;
		this.visualConfig = visualConfig;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel titleLabel = new JLabel("Plane (" + chipIndex + "," + planeIndex + ")");
		add(titleLabel);
		blocksList = initBlocks();
		setBorder(new RoundedBorder(Consts.Colors.BORDER));
		setBackground(Consts.Colors.BG);
	}

	public void setPlane(Plane<?,?> plane) {
		if(this.plane == plane){
			return;
		}
    	this.plane = plane;
		int blockIndex = 0;
		for (Block<?> block :  plane.getBlocks()) {
			blocksList.get(blockIndex++).setBlock(block);
		}
    }

	private List<BlockView> initBlocks() {
		JPanel blocksPanel = new JPanel(new GridLayout(plane.getBlocksNum()/visualConfig.getBlocksInRow(), visualConfig.getBlocksInRow()));
		List<BlockView> blocksList = new ArrayList<BlockView>();
		int blockIndex = 0;
		for (Block<?> block : plane.getBlocks()) {
			BlockView blockView = new BlockView(block, chipIndex, planeIndex, blockIndex++, visualConfig);
			blocksList.add(blockView);
			blocksPanel.add(blockView);
		}
		add(blocksPanel);
		return blocksList;
	}
}