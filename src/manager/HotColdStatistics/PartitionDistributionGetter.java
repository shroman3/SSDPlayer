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
package manager.HotColdStatistics;

import java.util.ArrayList;
import java.util.List;

import manager.HotColdSSDManager;
import ui.GeneralStatisticsGraph;
import ui.StatisticsGraph;
import entities.BlockStatusGeneral;
import entities.StatisticsColumn;
import entities.hot_cold.HotColdBlock;
import entities.hot_cold.HotColdChip;
import entities.hot_cold.HotColdDevice;
import entities.hot_cold.HotColdPlane;

public class PartitionDistributionGetter extends HotColdStatisticsGetter {
	public PartitionDistributionGetter(HotColdSSDManager manager) {
		super(manager);
	}

	@Override
	public int getNumberOfColumns() {
		return manager.getPartitionsNum() + 1;
	}

	@Override
	List<StatisticsColumn> getHotColdStatistics(HotColdDevice device) {
		int[] counters = new int[getNumberOfColumns()];		
		for (HotColdChip chip : device.getChips()) {
			for (HotColdPlane plane : chip.getPlanes()) {				
				for (HotColdBlock block : plane.getBlocks()) {
					if (block.getStatus() == BlockStatusGeneral.CLEAN) {
						counters[0]++;
					} else {
						counters[manager.indexOfPartition(block.getPartition())+1]++;
					}
				}
			}
		}
		
		int overallBlocks = manager.getBlocksNum()*manager.getPlanesNum()*manager.getChipsNum();
		List<StatisticsColumn> list = new ArrayList<StatisticsColumn>();
		list.add(new StatisticsColumn(BlockStatusGeneral.CLEAN.getDsiplayName(), ((double)counters[0]*100)/overallBlocks));
		for (int i = 1; i < counters.length; i++) {
			list.add(new StatisticsColumn(manager.getPartitionbyIndex(i-1).getDsiplayName(), ((double)counters[i]*100)/overallBlocks));
		}
		return list;
	}

	@Override
	public GeneralStatisticsGraph getStatisticsGraph() {
		return new StatisticsGraph("Partition Dist.", this);
	}
}