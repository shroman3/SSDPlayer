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

import general.XMLGetter;
import general.XMLParsingException;


public class VisualConfig {
	public static final String VISUAL_CONFIG = "visual";
	private int speed = -1;
	private boolean showCounters = true;
	
	private int pageWidth = -1;
	private int pageHeight = -1;
	private int blockSpace = -1;
	private int pagesInRow = -1;
	private int blocksInRow = -1;
	private int planesInRow = -1;
	
	public VisualConfig(XMLGetter xmlGetter) throws XMLParsingException {
		this.showCounters = xmlGetter.getBooleanField(VISUAL_CONFIG, "show_counters");
		this.speed = xmlGetter.getIntField(VISUAL_CONFIG, "speed");
		this.pageWidth =xmlGetter.getIntField(VISUAL_CONFIG, "page_width");
		this.pageHeight = xmlGetter.getIntField(VISUAL_CONFIG, "page_height");
		this.blockSpace = xmlGetter.getIntField(VISUAL_CONFIG, "block_space");
		this.pagesInRow = xmlGetter.getIntField(VISUAL_CONFIG, "pages_in_row");
		this.blocksInRow = xmlGetter.getIntField(VISUAL_CONFIG, "blocks_in_row");
		this.planesInRow = xmlGetter.getIntField(VISUAL_CONFIG, "planes_in_row");
	}

	public int getSpeed() {
		return speed;
	}

	public boolean isShowCounters() {
		return showCounters;
	}

	public int getPageWidth() {
		return pageWidth;
	}
	
	public int getPageHeight() {
		return pageHeight;
	}

	public int getBlockSpace() {
		return blockSpace;
	}

	public int getPagesInRow() {
		return pagesInRow;
	}
	
	public int getBlocksInRow() {
		return blocksInRow;
	}
	
	public int getPlanesInRow() {
		return planesInRow;
	}
}
