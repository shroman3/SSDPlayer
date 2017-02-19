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
package entities.RAID.hot_cold;

import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import entities.BlockStatus;
import entities.BlockStatusGeneral;
import entities.RAID.RAIDBasicPlane;
import entities.RAID.simulation.RAIDBlock;
import entities.RAID.simulation.RAIDPage;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDHotColdPlane extends RAIDBasicPlane<RAIDPage, RAIDBlock> {
	public static class Builder extends RAIDBasicPlane.Builder<RAIDPage, RAIDBlock> {
		private RAIDHotColdPlane plane;

		public Builder() {
			setPlane(new RAIDHotColdPlane());
		}

		public Builder(RAIDHotColdPlane plane) {
			setPlane(new RAIDHotColdPlane(plane));
		}

		public RAIDHotColdPlane build() {
			validate();
			return new RAIDHotColdPlane(plane);
		}

		protected void setPlane(RAIDHotColdPlane plane) {
			super.setPlane(plane);
			this.plane = plane;
		}

		protected void validate() {
			super.validate();
		}
	}

	private int activeParityBlockIndex = -1;

	protected RAIDHotColdPlane() {
	}

	protected RAIDHotColdPlane(RAIDHotColdPlane other) {
		super(other);
		initValues();
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	public RAIDHotColdPlane writeLP(int lp, int stripe) {
		List<RAIDBlock> updatedBlocks = getNewBlocksList();
		int active = getActiveBlockIndex();
		if (active == -1) {
			active = getLowestEraseCleanBlockIndex();
			updatedBlocks.set(active, (RAIDBlock) updatedBlocks.get(active).setStatus(BlockStatusGeneral.ACTIVE));
		}
		RAIDBlock activeBlock = updatedBlocks.get(active);
		activeBlock = activeBlock.writeLP(lp, stripe);
		if (!activeBlock.hasRoomForWrite()) {
			activeBlock = (RAIDBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
		}
		updatedBlocks.set(active, activeBlock);
		Builder builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks);
		return builder.build();
	}

	public RAIDHotColdPlane writePP(int stripe, int parityNum) {
		List<RAIDBlock> updatedBlocks = getNewBlocksList();
		RAIDBlock activeBlock;
		int active = getActiveParityBlockIndex();
		if (active != -1) {
			activeBlock = updatedBlocks.get(active);
		} else {
			active = getLowestEraseCleanBlockIndex();
			activeBlock = (RAIDBlock) updatedBlocks.get(active).setStatus(RAIDHotColdBlockStatus.ACTIVE_PARITY);
		}
		activeBlock = activeBlock.writePP(stripe, parityNum);
		if (!activeBlock.hasRoomForWrite()) {
			activeBlock = (RAIDBlock) activeBlock.setStatus(RAIDHotColdBlockStatus.USED_PARITY);
		}
		updatedBlocks.set(active, activeBlock);

		Builder builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks);
		return builder.build();
	}

	public Triplet<RAIDHotColdPlane, Integer, Integer> cleanRAID() {
		if (!invokeCleaning()) {
			return null;
		}
		return cleanRAIDPlane();
	}

	@Override
	protected Pair<RAIDHotColdPlane, Integer> cleanPlane() {
		throw new UnsupportedOperationException();
	}

	protected Triplet<RAIDHotColdPlane, Integer, Integer> cleanRAIDPlane() {
		List<RAIDBlock> cleanBlocks = getNewBlocksList();
		Pair<Integer, RAIDBlock> pickedToClean = pickBlockToClean();
		int parityToMove = pickedToClean.getValue1().getParityValidCounter();
		int dataToMove = pickedToClean.getValue1().getDataValidCounter();
		int active = getActiveBlockIndex();

		BlockStatus activeStatus = BlockStatusGeneral.ACTIVE;
		if (pickedToClean.getValue1().getStatus() == RAIDHotColdBlockStatus.USED_PARITY) {
			active = getActiveParityBlockIndex();
			activeStatus = RAIDHotColdBlockStatus.ACTIVE_PARITY;
		}
		
		RAIDBlock activeBlock;
		if (active == -1) {
			active = getLowestEraseCleanBlockIndex();
			activeBlock = (RAIDBlock) cleanBlocks.get(active).setStatus(activeStatus);
		} else {			
			activeBlock = cleanBlocks.get(active);
		}
		
		for (RAIDPage page : pickedToClean.getValue1().getPages()) {
			if (page.isValid()) {
				activeBlock = activeBlock.move(page.getLp(), page.getParityNumber(), page.getStripe(),
						page.isHighlighted());
			}
			if (!activeBlock.hasRoomForWrite()) {
				// This is the appropriate used status, either USED or USED_PARITY
				cleanBlocks.set(active, (RAIDBlock) activeBlock.setStatus(pickedToClean.getValue1().getStatus()));
				active = getLowestEraseCleanBlockIndex();
				activeBlock = (RAIDBlock) cleanBlocks.get(active).setStatus(activeStatus);
			}
		}

		cleanBlocks.set(active, activeBlock);
		cleanBlocks.set(pickedToClean.getValue0(), (RAIDBlock) pickedToClean.getValue1().eraseBlock());
		Builder builder = getSelfBuilder();
		builder.setBlocks(cleanBlocks);
		return new Triplet<>(builder.build(), dataToMove, parityToMove);
	}

	protected int getActiveParityBlockIndex() {
		return activeParityBlockIndex;
	}

	RAIDHotColdPlane setBlock(RAIDBlock block, int index) {
		List<RAIDBlock> newBlocksList = getNewBlocksList();
		newBlocksList.set(index, block);
		Builder builder = getSelfBuilder();
		builder.setBlocks(newBlocksList);
		return builder.build();
	}

	private void initValues() {
		int i = 0;
		for (RAIDBlock block : getBlocks()) {
			if (block.getStatus() == RAIDHotColdBlockStatus.ACTIVE_PARITY) {
				activeParityBlockIndex = i;
			}
			++i;
		}
	}

	@Override
	protected boolean isUsed(RAIDBlock block) {
		return (block.getStatus() == RAIDHotColdBlockStatus.USED_PARITY) || super.isUsed(block);
	}
}
