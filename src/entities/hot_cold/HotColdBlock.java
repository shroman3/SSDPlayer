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

import java.awt.Color;
import java.util.List;

import entities.Block;
import entities.BlockStatus;
import entities.BlockStatusGeneral;
import entities.EntityInfo;
import manager.HotColdSSDManager;
import manager.HotColdSSDManager.HotColdPartition;
import utils.Utils;
import utils.Utils.*;

public class HotColdBlock extends Block<HotColdPage> {
	public static class Builder extends Block.Builder<HotColdPage> {
		private HotColdBlock block;

		public Builder() {
			setBlock(new HotColdBlock());
		}

		protected Builder(HotColdBlock block) {
			setBlock(new HotColdBlock(block));
		}

		public Builder setManager(HotColdSSDManager manager) {
			super.setManager(manager);
			block.manager = manager;
			return this;
		}

		public Builder setPartition(HotColdPartition partition) {
			block.partition = partition;
			return this;
		}

		@Override
		public HotColdBlock build() {
			validate();
			return new HotColdBlock(block);
		}

		protected void setBlock(HotColdBlock block) {
			super.setBlock(block);
			this.block = block;
		}

		@Override
		protected void validate() {
			super.validate();
			Utils.validateNotNull(block.manager, "manager");
		}
	}

	private HotColdSSDManager manager = null;
	private HotColdPartition partition = null;

	protected HotColdBlock() {
	}

	protected HotColdBlock(HotColdBlock other) {
		super(other);
		manager = other.manager;
		partition = other.partition;
	}

	public HotColdPartition getPartition() {
		return partition;
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	@Override
	public Color getFrameColor() {
		if ((getStatus() != BlockStatusGeneral.CLEAN) && (partition != null)) {
			return partition.getColor();
		}
		return null;
	}

	@Override
	public String getStatusName() {
		if ((getStatus() != BlockStatusGeneral.CLEAN) && (partition != null)) {
			return super.getStatusName() + " " + partition.getDsiplayName();
		}
		return super.getStatusName();
	}

	public HotColdBlock setStatus(BlockStatus status, HotColdPartition partition) {
		HotColdBlock.Builder builder = getSelfBuilder();
		builder.setPartition(partition).setStatus(status);
		return builder.build();
	}

	public float getBlockTemperatureToMaxTempRatio() {
		int maxTemperature = manager.getMaxTemperature();
		return getAveragePageTemperature() / maxTemperature;
	}

	public EntityInfo getInfo() {
		EntityInfo result = super.getInfo();

		result.add("Average page temperature", Float.toString(getAveragePageTemperature()), 2);
		result.add("Status", getDisplayStatusName(), 1);

		result.add("Partition", getPartition() != null ? getPartition().getDsiplayName() : "None", 1);
		return result;
	}

	@Override
	protected entities.Page.Builder getWrittenPageBuilder(int lp, Utils.LpArgs lpArgs, HotColdPage page) {
		HotColdPage.Builder builder = page.getSelfBuilder();
		builder.setTemperature(lpArgs.getTemperatrure());
		return builder;
	}
	
	private float getAveragePageTemperature() {
		float temperatureSum = 0;
		int count = 0;
		for (HotColdPage page : this.getPages()) {
			if (page.getTemperature() >= 0) {
				count++;
				temperatureSum += page.getTemperature();
			}
		}
		if (count == 0) {
			return 0;
		}

		return temperatureSum / count;
	}
	private String getDisplayStatusName() {
		if ((getStatus() != BlockStatusGeneral.CLEAN) && (this.partition != null)) {
			return getStatus().getStatusName() + " " + this.partition.getDsiplayName();
		}
		return getStatus().getStatusName();
	}
}
