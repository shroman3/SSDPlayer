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

import manager.HotColdPartition;
import manager.HotColdSSDManager;
import ui.GeneralStatisticsGraph;
import ui.RegularHistoryGraph;
import entities.StatisticsColumn;
import entities.hot_cold.HotColdChip;
import entities.hot_cold.HotColdDevice;
import entities.hot_cold.HotColdPlane;

public class HotColdWriteAmplificationGetter extends HotColdStatisticsGetter {
	public HotColdWriteAmplificationGetter(HotColdSSDManager manager) {
		super(manager);
	}

	@Override
	public int getNumberOfColumns() {
		return manager.getPartitionsNum();
	}

	@Override
	List<StatisticsColumn> getHotColdStatistics(HotColdDevice device) {
		int[] logical = new int[getNumberOfColumns()];
		int[] gc = new int[getNumberOfColumns()];
		for (HotColdChip chip : device.getChips()) {
			for (HotColdPlane plane : chip.getPlanes()) {
				for (HotColdPartition partition : manager.getPartitions()) {
					gc[manager.indexOfPartition(partition)] += plane.getTotalMoved(partition);
				}
			}
		}
		for (HotColdPartition partition : manager.getPartitions()) {
			logical[manager.indexOfPartition(partition)] += device.getTotalWritten(partition);
		}
		
		List<StatisticsColumn> list = new ArrayList<StatisticsColumn>(getNumberOfColumns());
		for (int i = 0; i < getNumberOfColumns(); i++) {
			int total = logical[i] + gc[i];
			HotColdPartition partition = manager.getPartitionbyIndex(i);
			list.add(new StatisticsColumn(partition.getDsiplayName(), 
					logical[i]==0 ? 1 : ((double)total)/logical[i],
					true, partition.getColor()));
		}
		return list;
	}

	@Override
	public GeneralStatisticsGraph getStatisticsGraph() {
		return new RegularHistoryGraph("HotCold Write Amplification", this, 1.5, 1);
	}
}