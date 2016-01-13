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

import org.javatuples.Pair;

import utils.Utils;
import entities.BlockStatusGeneral;
import entities.Plane;

public class HotColdPlane extends Plane<HotColdPage, HotColdBlock> {
	public static class Builder extends Plane.Builder<HotColdPage, HotColdBlock> {
		private HotColdPlane plane;
		
		public Builder() {
			setPlane(new HotColdPlane());
		}
		
		public Builder(HotColdPlane plane) {
			setPlane(new HotColdPlane(plane));
		}

		public HotColdPlane build() {
			validate();
			return new HotColdPlane(plane);
		}
		
		public Builder setManager(HotColdSSDManager manager) {
			super.setManager(manager);
			plane.manager = manager;
			return this;
		}

		
		public Builder setTotalMoved(HotColdPartition partition, int totalMoved) {
			plane.totalMovedMap.put(partition, totalMoved);
			return this;
		}

		protected void setPlane(HotColdPlane plane) {
			super.setPlane(plane);
			this.plane = plane;
		}
		
		protected void validate() {
			super.validate();
			Utils.validateNotNull(plane.manager, "manager");
		}
	}
	
	private HotColdSSDManager manager;
	private Map<HotColdPartition, Integer> activeBlocksMap;
	private Map<HotColdPartition, Integer> totalMovedMap = new HashMap<HotColdPartition, Integer>();
	protected HotColdPlane() {}
		

	protected HotColdPlane(HotColdPlane other) {
		super(other);
		this.manager = other.manager;
		this.totalMovedMap = new HashMap<HotColdPartition, Integer>(other.totalMovedMap); 
		initValues();
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	public int getTotalMoved(HotColdPartition partition) {
		return totalMovedMap.get(partition);
	}

	public HotColdPlane writeLP(int lp, int temperature) {
		List<HotColdBlock> updatedBlocks = getNewBlocksList();
		HotColdPartition partition = manager.getPartition(temperature);
		Integer active = activeBlocksMap.get(partition);
		if (active == null) {
			active = getLowestEraseCleanBlockIndex();
			updatedBlocks.set(active, updatedBlocks.get(active).setStatus(BlockStatusGeneral.ACTIVE, partition));
		}
		HotColdBlock activeBlock = updatedBlocks.get(active);
		activeBlock = activeBlock.writeLP(lp, temperature);
		if(!activeBlock.hasRoomForWrite()) {
			activeBlock = (HotColdBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
		}
		updatedBlocks.set(active, activeBlock);
		Builder builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks).setTotalWritten(getTotalWritten() + 1);
		return builder.build();
	}
	
	@Override
	protected Pair<HotColdPlane,Integer> cleanPlane() {
		List<HotColdBlock> cleanBlocks = getNewBlocksList();
		Pair<Integer, HotColdBlock> pickedToClean =  pickBlockToClean();
		int toMove = pickedToClean.getValue1().getValidCounter();
		HotColdPartition partition = pickedToClean.getValue1().getPartition();
		Integer active = activeBlocksMap.get(partition);
		for (HotColdPage page : pickedToClean.getValue1().getPages()) {
			if (page.isValid()) {						
				if (active == null) {
					active = getLowestEraseCleanBlockIndex();
					cleanBlocks.set(active, cleanBlocks.get(active).setStatus(BlockStatusGeneral.ACTIVE, partition));
				}
				HotColdBlock activeBlock = cleanBlocks.get(active);
				activeBlock = activeBlock.move(page.getLp(), page.getTemperature());
				if(!activeBlock.hasRoomForWrite()) {
					activeBlock = (HotColdBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
					cleanBlocks.set(active, activeBlock);
					active = null;
				} else {
					cleanBlocks.set(active, activeBlock);					
				}
			}
		}
		cleanBlocks.set(pickedToClean.getValue0(), (HotColdBlock) pickedToClean.getValue1().eraseBlock());
		Builder builder = getSelfBuilder();
		builder.setTotalMoved(partition, totalMovedMap.get(partition) + toMove);
		builder.setBlocks(cleanBlocks);
		return new Pair<>(builder.build(), toMove);
	}
	
	private void initValues() {
		activeBlocksMap = new HashMap<HotColdPartition, Integer>();
		int i = 0;
		for (HotColdBlock block: getBlocks()) {
			if (block.getStatus() == BlockStatusGeneral.ACTIVE) {
				HotColdPartition partition = block.getPartition();
				activeBlocksMap.put(partition, i);
			}
			++i;
		}
	}
}
