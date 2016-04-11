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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import entities.Device;
import entities.StatisticsColumn;
import entities.StatisticsGetter;
import general.Consts;

public class StatisticsGraph extends GeneralStatisticsGraph {
	private static final long serialVersionUID = 1L;
	protected static final int COL_SPACE = 2;

	protected List<StatisticsColumn> columnsList = new ArrayList<StatisticsColumn>();

	public StatisticsGraph(String statisticsTitle, StatisticsGetter statisticsGetter) {
	    this(statisticsTitle, statisticsGetter, COL_SPACE);
	}
	 
    protected StatisticsGraph(String statisticsTitle, StatisticsGetter statisticsGetter, int colSpace) {
    	super(statisticsTitle, statisticsGetter, colSpace);
	}

    @Override
    public void updateStatistics(Device<?, ?, ?, ?> device) {
		columnsList = statisticsGetter.getStatistics(device);
    }

	@Override
	protected int calculateGraphWidth() {
		return (COL_WIDTH + colSpace)*statisticsGetter.getNumberOfColumns() + SPACING;
	}
     
	@Override
	protected void completeDrawing(Graphics2D g2d) {
		g2d.setFont(Consts.UI.SMALLER_FONT); 
		g2d.drawString("100%", VER_ASIX - g2d.getFontMetrics().stringWidth("100%")-3, TITLE_HIEGHT + SPACING);
		g2d.drawString("75%", VER_ASIX - g2d.getFontMetrics().stringWidth("75%")-3, TITLE_HIEGHT + HEIGHT/4 + SPACING/2);
		g2d.drawString("50%", VER_ASIX - g2d.getFontMetrics().stringWidth("50%")-3, TITLE_HIEGHT + HEIGHT/2 + SPACING/2);
		g2d.drawString("25%", VER_ASIX - g2d.getFontMetrics().stringWidth("25%")-3, TITLE_HIEGHT + HEIGHT*3/4 + SPACING/2);
		g2d.drawString("0%", VER_ASIX - g2d.getFontMetrics().stringWidth("0%")-3, TITLE_HIEGHT + HEIGHT);
		
		int i = 0;
		for (StatisticsColumn column : columnsList) {
			drawColumn(g2d, column, i++);
		}
	}
	
	private void drawColumn(Graphics2D g2d, StatisticsColumn column, int colIndex) {
		int x = VER_ASIX + SPACING + colIndex*(COL_WIDTH + colSpace);
		int columnHeight = (int) ((column.getValue() * HEIGHT)/100);
		int y = TITLE_HIEGHT +  HEIGHT - columnHeight;

		g2d.fillRect(x, y, COL_WIDTH, columnHeight);
		if (column.isColTitleDrawn()) {
			if(column.getColumnName().length() > 2) {
				AffineTransform orig = g2d.getTransform();
				g2d.rotate(Math.PI/2);
				g2d.drawString(column.getColumnName(), (TITLE_HIEGHT+ HEIGHT + SPACING)-4, -x);
				g2d.setTransform(orig);
			} else {
				g2d.drawString(column.getColumnName(), x, TITLE_HIEGHT+ HEIGHT + 13);
			}
		}
	}

	protected boolean isColTitleDrawn(int colIndex) {
		return true;
	}
}