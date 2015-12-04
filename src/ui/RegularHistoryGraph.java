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

import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import entities.Device;
import entities.StatisticsColumn;
import entities.StatisticsGetter;
import general.Consts;

public class RegularHistoryGraph extends GeneralStatisticsGraph {
	private static final double STEP = 0.5;

	private static final double EPSILON = 0;

	private final static int POINTS_NUM = 1000;

	private static final long serialVersionUID = 1L;
	
	private double minValue = 0;
	private double maxValue = 0;

	protected List<List<StatisticsColumn>> columnsList = new ArrayList<List<StatisticsColumn>>(250);

	private DecimalFormat numFormat;

    public RegularHistoryGraph(String statisticsTitle, StatisticsGetter statisticsGetter, double maxValue, double minValue) {
    	super(statisticsTitle, statisticsGetter, 0);
    	this.minValue = minValue;
    	this.maxValue = maxValue;
		numFormat = new DecimalFormat("###.##");


	}

    @Override
    public void updateStatistics(Device<?, ?, ?, ?> device) {
		List<StatisticsColumn> statistics = statisticsGetter.getStatistics(device);
		for (StatisticsColumn column : statistics) {
			double value = column.getValue();
			if (value > maxValue-EPSILON) {
				maxValue = value + STEP;
			} else if (value < minValue+EPSILON) {
				minValue = value - STEP;
			}
		}
		columnsList.add(statistics);
		if (columnsList.size() > POINTS_NUM) {
			columnsList.remove(0);
		}
    }

	@Override
	protected int calculateGraphWidth() {
		return POINTS_NUM/5 + SPACING*2;
	}
     
	@Override
	protected void completeDrawing(Graphics2D g2d) {
		g2d.setFont(Consts.UI.SMALLER_FONT); 
		
		String format = numFormat.format(maxValue);
		g2d.drawString(numFormat.format(maxValue), VER_ASIX - g2d.getFontMetrics().stringWidth(format)-3, TITLE_HIEGHT + SPACING);
		format = numFormat.format((maxValue-minValue)*3/4 + minValue);
		g2d.drawString(format, VER_ASIX - g2d.getFontMetrics().stringWidth(format)-3, TITLE_HIEGHT + HEIGHT/4 + SPACING/2);
		format = numFormat.format((minValue + maxValue)/2);
		g2d.drawString(format, VER_ASIX - g2d.getFontMetrics().stringWidth(format)-3, TITLE_HIEGHT + HEIGHT/2 + SPACING/2);
		format = numFormat.format((maxValue-minValue)/4 + minValue);
		g2d.drawString(format, VER_ASIX - g2d.getFontMetrics().stringWidth(format)-3, TITLE_HIEGHT + HEIGHT*3/4 + SPACING/2);
		format = numFormat.format(minValue);
		g2d.drawString(numFormat.format(minValue), VER_ASIX - g2d.getFontMetrics().stringWidth(format)-3, TITLE_HIEGHT + HEIGHT);
		
//        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
//        g2d.setStroke(dashed);
//		g2d.setColor(Consts.Colors.BORDER);
//        g2d.drawLine(VER_ASIX, TITLE_HIEGHT +  HEIGHT/2, VER_ASIX+100, TITLE_HIEGHT +  HEIGHT/2);

		if(columnsList.isEmpty()) {
			return;
		}
		
		if(columnsList.size() > 0) {
			List<StatisticsColumn> prevColumn = columnsList.get(0);
			for (int i = 0; i < columnsList.size(); i+=5) {
				List<StatisticsColumn> column = columnsList.get(i);
				for (int j = 0; j < column.size(); ++j) {
					StatisticsColumn currStatisticsColumn  = column.get(j);
					StatisticsColumn prevStatisticsColumn  = prevColumn.get(j);
					drawColumn(g2d, currStatisticsColumn, prevStatisticsColumn, i);
				}
				prevColumn = column;
			}
		}
		
		g2d.setFont(Consts.UI.SMALL_FONT); 
		int i = 0;
		for (StatisticsColumn statisticsColumn : columnsList.get(0)) {
			drawLegend(g2d, statisticsColumn, i);
			i++;
		}
	}
	
	private void drawColumn(Graphics2D g2d, StatisticsColumn currStatisticsColumn, StatisticsColumn prevStatisticsColumn, int colIndex) {
		int x = VER_ASIX + SPACING + colIndex/5;
		int currColumnHeight = (int) (((currStatisticsColumn.getValue()-minValue) * HEIGHT)/(maxValue - minValue));
		int prevColumnHeight = (int) (((prevStatisticsColumn.getValue()-minValue) * HEIGHT)/(maxValue - minValue));
		int currY = TITLE_HIEGHT +  HEIGHT - currColumnHeight;
		int prevY = TITLE_HIEGHT +  HEIGHT - prevColumnHeight;
		
		g2d.setColor(currStatisticsColumn.getColor());
		g2d.drawLine(x-1, prevY, x, currY);
	}
	
	private void drawLegend(Graphics2D g2d, StatisticsColumn column, int colIndex) {
		if (column.isColTitleDrawn()) {
			int x = VER_ASIX + colIndex*((graphWidth + SPACING)/columnsList.get(0).size());
			int y = TITLE_HIEGHT +  HEIGHT + HOR_ASIX - 10;
			g2d.setColor(column.getColor());
			g2d.fillRect(x, y-5, 5, 3);
			g2d.setColor(Consts.Colors.TEXT);
			g2d.drawString(column.getColumnName(), x+6, y);
		}
	}
}