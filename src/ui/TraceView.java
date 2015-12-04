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

import general.Consts;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class TraceView extends Component {
	private static final int COMMANDS_NUM = 13;

	private static final long serialVersionUID = 1L;

	private static final int ROW_WIDTH = 98;
	private static final int ROW_HIEGHT = 23;
	private static final int SPACING = 10;

	private Dimension dimension;

	private List<String> commandsList = new ArrayList<String>();

	private int height;
	
    public TraceView() {
		initSizesAndSpacing();
	}
    
	public Dimension getPreferredSize(){
		return dimension;
    }
            
    public void paint(Graphics g) {
		doDrawing(g);
    }

    public void addCommand(String command) {
    	commandsList.add(command);
    	if (commandsList.size() > COMMANDS_NUM) {
    		commandsList.remove(0);
    	}
    }
    
	private void doDrawing(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(Consts.UI.FONT); 

		int x = SPACING;
		int y = SPACING;
		g2d.setColor(Consts.Colors.BORDER);
		g2d.drawRoundRect(x, y, ROW_WIDTH, height, 6, 6);
		String title = "Trace View";
		drawTitle(g2d, title, x, y);
		y+=ROW_HIEGHT;
		g2d.setFont(Consts.UI.SMALL_FONT); 
		drawTrace(g2d, x, y);
	}
	
	private void initSizesAndSpacing() {
		height = ROW_HIEGHT*(COMMANDS_NUM+1);
		dimension = new Dimension(2*SPACING + ROW_WIDTH, 2*SPACING + height);
	}

	private void drawTrace(Graphics2D g2d, int x, int y) {
		if (commandsList.isEmpty()) {
			for(int i = 0; i < COMMANDS_NUM; ++i) {
				drawLine(g2d,"", x, y);
				y+=ROW_HIEGHT;
			}
		} else if (commandsList.size() < COMMANDS_NUM) {
			for(int i = 0; i < COMMANDS_NUM - commandsList.size(); ++i) {
				drawLine(g2d,"", x, y);
				y+=ROW_HIEGHT;
			}
			
			for(int i = 0; i < commandsList.size()-1; ++i) {
				drawLine(g2d,commandsList.get(i), x, y);
				y+=ROW_HIEGHT;
			}
			drawSelectedLine(g2d, commandsList.get(commandsList.size()-1), x, y);
		} else {
			for(int i = commandsList.size() - COMMANDS_NUM; i < commandsList.size()-1; ++i) {
				drawLine(g2d,commandsList.get(i), x, y);
				y+=ROW_HIEGHT;
			}
			drawSelectedLine(g2d, commandsList.get(commandsList.size()-1), x, y);
		}
	}

	private void drawLine(Graphics2D g2d, String title, int x, int y) {
		g2d.setColor(Consts.Colors.BORDER);
		g2d.drawLine(x, y, x + ROW_WIDTH, y);
		drawTitle(g2d, title, x, y);
	}

	private void drawTitle(Graphics2D g2d, String title, int x, int y) {
		g2d.setColor(Consts.Colors.TEXT);
		if(g2d.getFontMetrics().stringWidth(title) > ROW_WIDTH) {
			title = title.substring(0,title.length()/2) + "...";
		}
		g2d.drawString(title, x + 4, y + 16);
	}
	
	private void drawSelectedLine(Graphics2D g2d, String title, int x, int y) {
		g2d.setColor(Consts.Colors.BORDER);
		g2d.fillRect(x, y, ROW_WIDTH, ROW_HIEGHT);
		g2d.setColor(Consts.Colors.CLEAN);
		drawLine(g2d, title, x, y);
	}
}