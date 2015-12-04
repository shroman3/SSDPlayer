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
package utils;

import general.Consts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

public class UIUtils {
	private static final BasicStroke BOLD_STROKE = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

	public static TexturePaint getGCTexture(Color color) {
		BufferedImage bi = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
	    Graphics2D big = bi.createGraphics();
	    
	    big.setColor(Consts.Colors.CLEAN);
	    big.fillRect(0, 0, 2, 2);
	    big.fillRect(2, 2, 2, 2);	 
	    big.setColor(color);
	    big.fillRect(2, 0, 2, 2);
	    big.fillRect(0, 2, 2, 2);

		Rectangle r = new Rectangle(0, 0, 10, 10);
	    return new TexturePaint(bi, r);
	}
	
	public static TexturePaint getPartialGCTexture(Color color) {
		BufferedImage bi = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
	    Graphics2D big = bi.createGraphics();
	    
	    big.setColor(Consts.Colors.CLEAN);
	    big.fillRect(0, 0, 4, 2);
	    big.setColor(color);
	    big.fillRect(0, 2, 4, 4);

		Rectangle r = new Rectangle(0, 0, 8, 8);
	    return new TexturePaint(bi, r);
	}
	
	public static void drawInvalidPage(Graphics2D g2d, int x, int y, int width, int height) {
		g2d.setColor(Consts.Colors.BLACK);
		Stroke oldStroke = g2d.getStroke(); 
		g2d.setStroke(BOLD_STROKE);
		g2d.drawLine(x, y, x + width, y + height);
		g2d.drawLine(x + width, y, x, y + height);
		g2d.setStroke(oldStroke);
	}
}
