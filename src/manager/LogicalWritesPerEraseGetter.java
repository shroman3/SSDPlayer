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

import java.util.ArrayList;
import java.util.List;

import ui.GeneralStatisticsGraph;
import ui.RegularHistoryGraph;
import entities.Block;
import entities.Chip;
import entities.Device;
import entities.Plane;
import entities.StatisticsColumn;
import entities.StatisticsGetter;

public class LogicalWritesPerEraseGetter implements StatisticsGetter {
	
	private int maxPagesPerErase;
	private int writesTillFirstErase = 0;

	public LogicalWritesPerEraseGetter(SSDManager<?,?,?,?,?> manager) {
		maxPagesPerErase = (int) (manager.getPagesNum()*1.5);
	}
	
	@Override
	public int getNumberOfColumns() {
		return 1;
	}

	@Override
	public List<StatisticsColumn> getStatistics(Device<?, ?, ?, ?> device) {
		int erases = 0;
		for (Chip<?,?,?> chip : device.getChips()) {
			for (Plane<?,?> plane : chip.getPlanes()) {
				for (Block<?> block : plane.getBlocks()) {					
					erases += block.getEraseCounter();
				}
			}
		}
		
		double pagesToErase = 0;
		if(erases==0) {
			writesTillFirstErase = device.getTotalWritten();
		} else {			
			pagesToErase = ((double)(device.getTotalWritten() - writesTillFirstErase))/erases;
			pagesToErase = (pagesToErase > maxPagesPerErase) ? maxPagesPerErase : pagesToErase;
		}
		List<StatisticsColumn> list = new ArrayList<StatisticsColumn>();
		list.add(new StatisticsColumn("total writes to logical writes", pagesToErase, false));
		return list;
	}

	@Override
	public GeneralStatisticsGraph getStatisticsGraph() {
		return new RegularHistoryGraph("Writes Per Erase", this, maxPagesPerErase, 0);
	}
}