/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import entities.Device;
import entities.StatisticsGetter;
import general.Consts;
import utils.Utils;

public abstract class GeneralStatisticsGraph extends Component {
	private static final long serialVersionUID = 1L;

	protected static final int VER_ASIX = 30;
	protected static final int HOR_ASIX = 24;
	protected static final int HEIGHT = 85;
	protected static final int SPACING = 8;
	protected static final int TITLE_HIEGHT = 17;
	protected static final int COL_WIDTH = 8;
	
	protected int graphWidth;
	
	private Dimension dimension;
	protected StatisticsGetter statisticsGetter;
	private String statisticsTitle;
	protected int colSpace;
	protected int width;
    
    public GeneralStatisticsGraph(String statisticsTitle, StatisticsGetter statisticsGetter, int colSpace) {
    	Utils.validateNotNull(statisticsTitle, "Statistics Title");
    	Utils.validateNotNull(statisticsGetter, "Statistics Getter");
		this.statisticsTitle = statisticsTitle;
		this.statisticsGetter = statisticsGetter;
		this.colSpace = colSpace;
		initSizesAndSpacing();
	}
    
    public abstract void updateStatistics(Device<?, ?, ?, ?> device);

	private void initSizesAndSpacing() {
		graphWidth = calculateGraphWidth();
		dimension = new Dimension(width= VER_ASIX + graphWidth + SPACING, HEIGHT + HOR_ASIX + TITLE_HIEGHT);
	}

	protected abstract int calculateGraphWidth();
    
	public Dimension getPreferredSize(){
		return dimension;
    }
            
    public void paint(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
		doDrawing(g2d);
    }
    	
	private void doDrawing(Graphics2D g2d) {
		g2d.setColor(Consts.Colors.BG);
		g2d.fillRoundRect(VER_ASIX, TITLE_HIEGHT , graphWidth, HEIGHT+1, 6,6);
		g2d.setColor(Consts.Colors.BORDER);
		g2d.drawRoundRect(VER_ASIX, TITLE_HIEGHT, graphWidth, HEIGHT+1, 6,6);
//		Graphics2D g2d2 = (Graphics2D) g2d.create();
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
		g2d.setStroke(dashed);

		g2d.drawLine(VER_ASIX, TITLE_HIEGHT + HEIGHT/2, VER_ASIX + graphWidth, TITLE_HIEGHT + HEIGHT/2);
		g2d.drawLine(VER_ASIX, TITLE_HIEGHT + HEIGHT*3/4, VER_ASIX + graphWidth, TITLE_HIEGHT + HEIGHT*3/4);
		g2d.drawLine(VER_ASIX, TITLE_HIEGHT + HEIGHT/4, VER_ASIX + graphWidth, TITLE_HIEGHT + HEIGHT/4);

		dashed = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g2d.setStroke(dashed);
		//gets rid of the copy
//        g2d.dispose();

		g2d.setFont(Consts.UI.SMALL_FONT); 
		g2d.setColor(Consts.Colors.TEXT);
		g2d.drawString(statisticsTitle, (width/2) - (g2d.getFontMetrics().stringWidth(statisticsTitle)/2), TITLE_HIEGHT - SPACING/2);
		completeDrawing(g2d);
	}
	
	protected abstract void completeDrawing(Graphics2D g2d);
}