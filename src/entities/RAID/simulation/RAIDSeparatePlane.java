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
package entities.RAID.simulation;

import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import entities.BlockStatus;
import entities.BlockStatusGeneral;
import utils.Utils.*;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDSeparatePlane extends RAIDPlane {
	public static class Builder extends RAIDPlane.Builder {
		private RAIDSeparatePlane plane;

		public Builder() {
			setPlane(new RAIDSeparatePlane());
		}

		public Builder(RAIDSeparatePlane plane) {
			setPlane(new RAIDSeparatePlane(plane));
		}

		@Override
		public RAIDSeparatePlane build() {
			validate();
			return new RAIDSeparatePlane(plane);
		}

		protected void setPlane(RAIDSeparatePlane plane) {
			super.setPlane(plane);
			this.plane = plane;
		}

		protected void validate() {
			super.validate();
		}
	}

	private int activeParityBlockIndex = -1;

	protected RAIDSeparatePlane() {
	}

	protected RAIDSeparatePlane(RAIDSeparatePlane other) {
		super(other);
		initValues();
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	public RAIDSeparatePlane writeLP(int lp, LpArgs lpArgs) {
		int stripe = lpArgs.getStripe();
		List<RAIDBlock> updatedBlocks = getNewBlocksList();
		int active = getActiveBlockIndex();
		if (active == -1) {
			active = getLowestEraseCleanBlockIndex();
			updatedBlocks.set(active, (RAIDBlock) updatedBlocks.get(active).setStatus(BlockStatusGeneral.ACTIVE));
		}
		RAIDBlock activeBlock = updatedBlocks.get(active);
		activeBlock = activeBlock.writeLP(lp, lpArgs);
		if (!activeBlock.hasRoomForWrite()) {
			activeBlock = (RAIDBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
		}
		updatedBlocks.set(active, activeBlock);
		Builder builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks);
		return builder.build();
	}

	public RAIDSeparatePlane writePP(int stripe, int parityNum) {
		List<RAIDBlock> updatedBlocks = getNewBlocksList();
		RAIDBlock activeBlock;
		int active = getActiveParityBlockIndex();
		if (active != -1) {
			activeBlock = updatedBlocks.get(active);
		} else {
			active = getLowestEraseCleanBlockIndex();
			activeBlock = (RAIDBlock) updatedBlocks.get(active).setStatus(RAIDSeparateBlockStatus.ACTIVE_PARITY);
		}
		activeBlock = activeBlock.writePP(stripe, parityNum);
		if (!activeBlock.hasRoomForWrite()) {
			activeBlock = (RAIDBlock) activeBlock.setStatus(RAIDSeparateBlockStatus.USED_PARITY);
		}
		updatedBlocks.set(active, activeBlock);

		Builder builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks);
		return builder.build();
	}

	public Triplet<RAIDPlane, Integer, Integer> cleanRAID() {
		if (!invokeCleaning()) {
			return null;
		}
		return cleanRAIDPlane();
	}

	protected Triplet<RAIDPlane, Integer, Integer> cleanRAIDPlane() {
		List<RAIDBlock> cleanBlocks = getNewBlocksList();
		Pair<Integer, RAIDBlock> pickedToClean = pickBlockToClean();
		int parityToMove = pickedToClean.getValue1().getParityValidCounter();
		int dataToMove = pickedToClean.getValue1().getDataValidCounter();
		int active = getActiveBlockIndex();

		BlockStatus activeStatus = BlockStatusGeneral.ACTIVE;
		if (pickedToClean.getValue1().getStatus() == RAIDSeparateBlockStatus.USED_PARITY) {
			active = getActiveParityBlockIndex();
			activeStatus = RAIDSeparateBlockStatus.ACTIVE_PARITY;
		}
		
		RAIDBlock activeBlock = null;
		if (active != -1) {
			activeBlock = cleanBlocks.get(active);
		}
		
		for (RAIDPage page : pickedToClean.getValue1().getPages()) {
			if (page.isValid()) {
				if (active == -1) {
					active = getLowestEraseCleanBlockIndex();
					activeBlock = (RAIDBlock) cleanBlocks.get(active).setStatus(activeStatus);
				} 
				activeBlock = activeBlock.move(page.getLp(), page.getParityNumber(), page.getStripe(),
						page.isHighlighted());
				if (!activeBlock.hasRoomForWrite()) {
					// This is the appropriate used status, either USED or USED_PARITY
					cleanBlocks.set(active, (RAIDBlock) activeBlock.setStatus(pickedToClean.getValue1().getStatus()));
					active = -1; 
				}
			}
		}
		if (active != -1) {
			cleanBlocks.set(active, activeBlock);
		}
		cleanBlocks.set(pickedToClean.getValue0(), (RAIDBlock) pickedToClean.getValue1().eraseBlock());
		Builder builder = getSelfBuilder();
		builder.setBlocks(cleanBlocks);
		return new Triplet<>(builder.build(), dataToMove, parityToMove);
	}

	protected int getActiveParityBlockIndex() {
		return activeParityBlockIndex;
	}

	private void initValues() {
		int i = 0;
		for (RAIDBlock block : getBlocks()) {
			if (block.getStatus() == RAIDSeparateBlockStatus.ACTIVE_PARITY) {
				activeParityBlockIndex = i;
			}
			++i;
		}
	}

	@Override
	protected boolean isUsed(RAIDBlock block) {
		return (block.getStatus() == RAIDSeparateBlockStatus.USED_PARITY) || super.isUsed(block);
	}
}
