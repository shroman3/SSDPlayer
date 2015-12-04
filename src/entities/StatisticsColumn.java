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
package entities;

import general.Consts;

import java.awt.Color;

import utils.Utils;
 
/**
 * @author Roman
 * 
 * The entity that represents statistics data for some entity statistics graph
 */
public class StatisticsColumn {
	private String columnName;
	private double value;
	private Color color;
	private boolean isColTitleShown;
	
	public StatisticsColumn(String columnName, double percentage) {
		this(columnName, percentage, true, Consts.Colors.TEXT);
	}
	
	public StatisticsColumn(String columnName, double percentage, boolean isColTitleShown) {
		this(columnName, percentage, isColTitleShown, Consts.Colors.TEXT);
	}
	
	public StatisticsColumn(String columnName, double percentage, boolean isColTitleShown, Color color) {
		Utils.validateNotNull(columnName, "statistics column name");
		Utils.validateNotNull(color, "color");
//		if ((percentage < 0) || (percentage > 100)) {
//			throw new IllegalArgumentException("Illegal percentage given for " + columnName);
//		}
		this.columnName = columnName;
		this.value = percentage;
		this.color = color;
		this.isColTitleShown = isColTitleShown;
	}
	
	public String getColumnName() {
		return columnName;
	}

	public double getValue() {
		return value;
	}
	
	public Color getColor() {
		return color;
	}

	public boolean isColTitleDrawn() {
		return isColTitleShown;
	}
}
