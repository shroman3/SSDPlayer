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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;

import manager.VisualConfig;
import utils.UIUtils;
import entities.Block;
import entities.Page;
import general.Consts;

/**
 * 
 *  November 2015: revised by Or Mauda for additional RAID functionality.
 *
 */
public class BlockView extends Component {
	private static final int VER_SPACING_WITH_COUNTERS = 18;
	
	private static final long serialVersionUID = 1L;
	private Block<?> block;
	private int chipIndex;
	private int planeIndex;
	private int blockIndex;
	
	private Dimension dimension;
	private int blockWidth;
	private int blockHeight;
	private int pageWidth;
	private int pageHeight;
	private int spacing;
	private int pagesInRow;
	
	private int verSpacing;
	private VisualConfig visualConfig;

    
    public BlockView(Block<?> block, int chipIndex, int planeIndex, int blockIndex, VisualConfig visualConfig) {
		this.block = block;
		this.blockIndex = blockIndex;
		this.planeIndex = planeIndex;
		this.chipIndex = chipIndex;
		this.visualConfig = visualConfig;
		
		initSizesAndSpacing(block, visualConfig);
	}

    public void setBlock(Block<?> block) {
    	this.block = block;
    }
    
	private void initSizesAndSpacing(Block<?> block, VisualConfig visualConfig) {
		pageWidth = visualConfig.getPageWidth();
		pageHeight = visualConfig.getPageHeight();
		pagesInRow = visualConfig.getPagesInRow();
		spacing = visualConfig.getBlockSpace();
		if (visualConfig.isShowCounters()) {
			verSpacing = VER_SPACING_WITH_COUNTERS;
		} else {
			verSpacing = spacing;
		}
		
		blockWidth = pageWidth * pagesInRow;
		blockHeight = pageHeight * ((block.getPagesNum() + pagesInRow-1)/pagesInRow);
		
		dimension = new Dimension(blockWidth+ 2*spacing, blockHeight+spacing+verSpacing);
	}
    
	public Dimension getPreferredSize(){
		return dimension;
    }
            
    public void paint(Graphics g) {
		doDrawing(g);
    }
    	
	private void doDrawing(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;	
		g2d.setFont(Consts.UI.FONT); 

		drawBG(g2d);
		drawCounters(g2d);
		drawFrame(g2d, block);		


		int x = spacing;
		int y = spacing;
		if (visualConfig.isShowCounters()) {
			g2d.setFont(Consts.UI.TINY_FONT);
		} else {
			g2d.setFont(Consts.UI.INVISIBLE_FONT);
		}
		int pageIndex = 0;
		for (Page page :  block.getPages()) {
			drawPage(g2d, x, y, pageIndex++, page);
		}

	}

	private void drawBG(Graphics2D g2d) {
		Color bgColor = block.getBGColor();
		if(bgColor != null) {			
			g2d.setColor(bgColor);
			g2d.fillRect(0, 0, dimension.width, dimension.height);
		}
	}

	private void drawFrame(Graphics2D g2d, Block<?> block) {
		Color frameColor = block.getFrameColor();
		if (frameColor != null) {
			BasicStroke bs3 = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			g2d.setStroke(bs3);
			g2d.setColor(frameColor);
			g2d.drawRect(spacing, spacing, blockWidth, blockHeight);
			bs3 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			g2d.setStroke(bs3);
		}
	}
	
	private void drawCounters(Graphics2D g2d) {
		if (visualConfig.isShowCounters()) {						
			g2d.setFont(Consts.UI.SMALL_FONT);			
			String index = "(" + chipIndex + "," + planeIndex + "," + blockIndex + ")";
			String blockCounters = "v=" + block.getValidCounter() + ",e=" + block.getEraseCounter();
			String counters = index + " " + blockCounters + " " + block.getStatusName();
			g2d.setColor(block.getStatusColor());
			g2d.drawString(counters , spacing , spacing+blockHeight + 13);
		}
	}
	
	private void drawPage(Graphics2D g2d, int x, int y, int pageIndex, Page page) {
		x += (pageIndex%pagesInRow) * pageWidth;
		y += (pageIndex/pagesInRow) * pageHeight;
		
		Color color = page.getBGColor();
		String title = page.getTitle();
		TexturePaint tp = page.getPageTexture(color);
		g2d.setColor(color);
		g2d.setPaint(tp);
		g2d.fillRect(x, y, pageWidth, pageHeight);
		
		if (!page.isValid() && !page.isClean()) {
			UIUtils.drawInvalidPage(g2d, x, y, pageWidth, pageHeight);
		}
		
		g2d.setColor(Consts.Colors.PAGE_TEXT);
		if (page.isHighlighted()) {
			g2d.setColor(page.getStripeFrameColor());
			BasicStroke bs3 = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			g2d.setStroke(bs3);
		}
		g2d.drawRect(x, y, pageWidth, pageHeight);
		BasicStroke bs3 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g2d.setStroke(bs3);
		g2d.setColor(Consts.Colors.PAGE_TEXT);
		
		g2d.drawString(title, x+1, y+10);
	}
}