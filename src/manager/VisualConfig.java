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

import java.awt.Color;
import java.util.ArrayList;

import general.XMLGetter;
import general.XMLParsingException;

public class VisualConfig {	
	public static final String VISUAL_CONFIG = "visual";

	public enum BlockColorMeaning {
		PARITY_COUNT, VALID_COUNT, ERASE_COUNT, AVERAGE_TEMPERATURE, AVERAGE_WRITE_LEVEL, NONE
	}
	
	private int xmlSpeed = -1;
	private boolean xmlShowCounters = true;
	private int xmlPageWidth = -1;
	private int xmlPageHeight = -1;
	private int xmlBlockSpace = -1;
	private int xmlPagesInRow = -1;
	private int xmlBlocksInRow = -1;
	private int xmlPlanesInRow = -1;
	private boolean xmlThinCross = false;
	private boolean xmlMovedPattern = true;
	private boolean xmlShowPages = true;
	private BlockColorMeaning xmlBlocksColorMeaning = BlockColorMeaning.NONE;
	private boolean xmlDrawFrame = true;

	private int speed = -1;
	private boolean showCounters = true;
	private int pageWidth = -1;
	private int pageHeight = -1;
	private int blockSpace = -1;
	private int pagesInRow = -1;
	private int blocksInRow = -1;
	private int planesInRow = -1;
	private boolean thinCross = false;
	private boolean movedPattern = true;
	private boolean showPages = true;
	private BlockColorMeaning blocksColorMeaning = BlockColorMeaning.NONE;

	private ArrayList<Color> mBlocksColorRange;
	private Integer mRangeHighValue;
	private Integer mRangeLowValue;
	private boolean mDrawFrame = true;

	
	public VisualConfig(XMLGetter xmlGetter) throws XMLParsingException {
		this.xmlShowCounters = xmlGetter.getBooleanField(VISUAL_CONFIG, "show_counters");
		this.xmlSpeed = xmlGetter.getIntField(VISUAL_CONFIG, "speed");
		this.xmlPageWidth =xmlGetter.getIntField(VISUAL_CONFIG, "page_width");
		this.xmlPageHeight = xmlGetter.getIntField(VISUAL_CONFIG, "page_height");
		this.xmlBlockSpace = xmlGetter.getIntField(VISUAL_CONFIG, "block_space");
		this.xmlPagesInRow = xmlGetter.getIntField(VISUAL_CONFIG, "pages_in_row");
		this.xmlBlocksInRow = xmlGetter.getIntField(VISUAL_CONFIG, "blocks_in_row");
		this.xmlPlanesInRow = xmlGetter.getIntField(VISUAL_CONFIG, "planes_in_row");
		
		restoreXmlValues();
	}
	
	public void restoreXmlValues(){
		setSpeed(xmlSpeed);
		setShowCounters(xmlShowCounters);
		setPageWidth(xmlPageWidth);
		setPageHeight(xmlPageHeight);
		setBlockSpace(xmlBlockSpace);
		setPagesInRow(xmlPagesInRow);
		setBlocksInRow(xmlBlocksInRow);
		setPlanesInRow(xmlPlanesInRow);
		setThinCross(xmlThinCross);
		setMovedPattern(xmlMovedPattern);
		setShowPages(xmlShowPages);
		setBlocksColorMeaning(xmlBlocksColorMeaning);
		setDrawFrame(xmlDrawFrame);
		
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isShowCounters() {
		return showCounters;
	}

	public void setShowCounters(boolean showCounters) {
		this.showCounters = showCounters;
	}

	public int getPageWidth() {
		return pageWidth;
	}

	public void setPageWidth(int pageWidth) {
		this.pageWidth = pageWidth;
	}

	public int getPageHeight() {
		return pageHeight;
	}

	public void setPageHeight(int pageHeight) {
		this.pageHeight = pageHeight;
	}

	public int getBlockSpace() {
		return blockSpace;
	}

	public void setBlockSpace(int blockSpace) {
		this.blockSpace = blockSpace;
	}

	public int getPagesInRow() {
		return pagesInRow;
	}

	public void setPagesInRow(int pagesInRow) {
		this.pagesInRow = pagesInRow;
	}

	public int getBlocksInRow() {
		return blocksInRow;
	}

	public void setBlocksInRow(int blocksInRow) {
		this.blocksInRow = blocksInRow;
	}

	public int getPlanesInRow() {
		return planesInRow;
	}

	public void setPlanesInRow(int planesInRow) {
		this.planesInRow = planesInRow;
	}

	public boolean isThinCross() {
		return thinCross;
	}

	public void setThinCross(boolean thinCross) {
		this.thinCross = thinCross;
	}

	public boolean isMovedPattern() {
		return movedPattern;
	}

	public void setMovedPattern(boolean movedPattern) {
		this.movedPattern = movedPattern;
	}

	public boolean isShowPages() {
		return showPages;
	}

	public void setShowPages(boolean showPages) {
		this.showPages = showPages;
	}

	public BlockColorMeaning getBlocksColorMeaning() {
		return blocksColorMeaning;
	}

	public void setBlocksColorMeaning(BlockColorMeaning blocksColorMeaning) {
		this.blocksColorMeaning = blocksColorMeaning;
	}
	
	public void smallerPages(){
		setPageHeight((int)Math.floor(getPageHeight()/ Math.sqrt(2)));
		setPageWidth((int)Math.floor(getPageWidth()/ Math.sqrt(2)));
		setBlockSpace(getBlockSpace()/2);
		mDrawFrame = false;
	}
	
	public void extraSmallerPages(){
		setPageHeight((int)Math.floor(getPageHeight()/ 2));
		setPageWidth((int)Math.floor(getPageWidth()/ 2));
		setBlockSpace(getBlockSpace()/2);
		mDrawFrame = false;
	}

	public void setBlocksColorRange(ArrayList<Color> colorRange) {
		mBlocksColorRange  = colorRange;
	}
	
	public ArrayList<Color> getBlocksColorRange(){
		return mBlocksColorRange;
	}

	public Integer getRangeHighValue() {
		return mRangeHighValue;
	}

	public void setRangeHighValue(Integer mRangeHighValue) {
		this.mRangeHighValue = mRangeHighValue;
	}

	public Integer getRangeLowValue() {
		return mRangeLowValue;
	}

	public void setRangeLowValue(Integer mRangeLowValue) {
		this.mRangeLowValue = mRangeLowValue;
	}

	public boolean isDrawFrame() {
		return mDrawFrame;
	}

	public void setDrawFrame(boolean mDrawFrame) {
		this.mDrawFrame = mDrawFrame;
	}
	
}
