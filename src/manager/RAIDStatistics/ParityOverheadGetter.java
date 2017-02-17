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
package manager.RAIDStatistics;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import entities.Device;
import entities.StatisticsColumn;
import entities.RAID.RAIDBasicDevice;
import manager.RAIDBasicSSDManager;
import ui.GeneralStatisticsGraph;
import ui.RegularHistoryGraph;

/**
 * 
 * @author Or Mauda
 *
 */
public class ParityOverheadGetter<D extends RAIDBasicDevice<?,?,?,?>, S extends RAIDBasicSSDManager<?,?,?,?,D>> extends RAIDStatisticsGetter<D,S> {
	public ParityOverheadGetter(S manager, Class<?> diviceclass) {
		super(manager, diviceclass);
	}

	@Override
	public int getNumberOfColumns() {
		return 1;
	}

	@Override
	public List<StatisticsColumn> getRAIDStatistics(D device) {
		int total = device.getTotalParityWritten() + device.getTotalDataWritten() + device.getTotalParityMoved() + device.getTotalDataMoved();
		List<StatisticsColumn> list = new ArrayList<StatisticsColumn>();
		list.add(new StatisticsColumn("data + parity writes to data writes", 
										total==0 ? 1 : ((double)total)/((double)(device.getTotalDataWritten()+ device.getTotalDataMoved())), false));
		return list;
	}

	@Override
	public GeneralStatisticsGraph getStatisticsGraph() {
		return new RegularHistoryGraph("Parity Overhead Histogram", this, 1.5, 1);
	}
	
 	public static double getParityOverhead(Device<?,?,?,?> device) {
		if (!(device instanceof RAIDBasicDevice)) {
			return 1;
		}
		int total = ((RAIDBasicDevice<?,?,?,?>) device).getTotalParityWritten() + ((RAIDBasicDevice<?,?,?,?>) device).getTotalDataWritten()+ ((RAIDBasicDevice<?,?,?,?>) device).getTotalDataMoved()
				+ ((RAIDBasicDevice<?,?,?,?>) device).getTotalParityMoved();
		double parityOverhead = total==0 ? 1 : ((double)total)/ (((RAIDBasicDevice<?,?,?,?>)device).getTotalDataWritten()
						+ ((RAIDBasicDevice<?,?,?,?>) device).getTotalDataMoved());
		return parityOverhead;
 	}

	@Override
	public Entry<String, String> getInfoEntry(Device<?, ?, ?, ?> device) {
		return new AbstractMap.SimpleEntry("Parity Overhead", Double.toString(getParityOverhead(device)));
	}
}
