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
package entities.hot_cold;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import manager.HotColdPartition;
import manager.HotColdSSDManager;
import entities.ActionLog;
import entities.Device;


public class HotColdDevice extends Device<HotColdPage, HotColdBlock, HotColdPlane, HotColdChip> {
	public static class Builder extends Device.Builder<HotColdPage, HotColdBlock, HotColdPlane, HotColdChip> {
		private HotColdDevice device;

		public Builder() {
			setDevice(new HotColdDevice());
			resetLog();
		}
		
		public Builder(HotColdDevice device) {
			setDevice(new HotColdDevice(device));
			resetLog();
		}
		
		public Builder setManager(HotColdSSDManager manager) {
			device.manager = manager;
			return this;
		}
		
		public Builder setTotalWritten(HotColdPartition partition, int totalWritten) {
			device.totalWrittenMap.put(partition, totalWritten);
			return this;
		}
		
		@Override
		public HotColdDevice build() {
			validate();
			return new HotColdDevice(device);
		}
		
		protected void setDevice(HotColdDevice device) {
			super.setDevice(device);
			this.device = device;
		}
	}
	
	private Map<HotColdPartition, Integer> totalWrittenMap = new HashMap<HotColdPartition, Integer>();
	private HotColdSSDManager manager;

	protected HotColdDevice() {}
	
	protected HotColdDevice(HotColdDevice other) {
		super(other);
		this.totalWrittenMap = new HashMap<HotColdPartition, Integer>(other.totalWrittenMap);
		this.manager = other.manager;
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	@Override
	public HotColdDevice writeLP(int lp, int temperature) {
		HotColdDevice device = (HotColdDevice) super.writeLP(lp, temperature);
		HotColdPartition partition = manager.getPartition(temperature);
		ActionLog a = device.getLog();
		return (HotColdDevice) device.getSelfBuilder()
				.setTotalWritten(partition, totalWrittenMap.get(partition) + 1)
				.setLog(a)
				.build();
	}

	public int getTotalWritten(HotColdPartition partition) {
		return totalWrittenMap.get(partition);
	}
	
	public List<HotColdPartition> getPartitions(){
		return manager.getPartitions();
	}
}