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

import entities.BlockStatusGeneral;
import entities.EntityInfo;
import entities.RAID.RAIDBasicPlane;
import utils.Utils.*;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDPlane extends RAIDBasicPlane<RAIDPage, RAIDBlock> {
	public static class Builder extends RAIDBasicPlane.Builder<RAIDPage, RAIDBlock> {
		private RAIDPlane plane;
		
		public Builder() {
			setPlane(new RAIDPlane());
		}
		
		public Builder(RAIDPlane plane) {
			setPlane(new RAIDPlane(plane));
		}

		public RAIDPlane build() {
			validate();
			return new RAIDPlane(plane);
		}
		
		protected void setPlane(RAIDPlane plane) {
			super.setPlane(plane);
			this.plane = plane;
		}
		
		protected void validate() {
			super.validate();
		}

		public Builder setTotalDataWritten(int totalDataWritten) {
			this.plane.totalDataWritten = totalDataWritten;
			return this;
		}

		public Builder setTotalParityWritten(int totalParityWritten) {
			this.plane.totalParityWritten = totalParityWritten;
			return this;
		}
	}

	private int totalDataWritten;
	private int totalParityWritten;

	protected RAIDPlane() {}
		
	protected RAIDPlane(RAIDPlane other) {
		super(other);
		this.totalDataWritten = other.totalDataWritten;
		this.totalParityWritten = other.totalParityWritten;
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	public RAIDPlane writeLP(int lp, LpArgs lpArgs) {
		int stripe = lpArgs.getStripe();
		List<RAIDBlock> updatedBlocks = getNewBlocksList();
		int active = getActiveBlockIndex();
		if (active == -1) {
			active = getLowestEraseCleanBlockIndex();
			updatedBlocks.set(active, (RAIDBlock) updatedBlocks.get(active).setStatus(BlockStatusGeneral.ACTIVE));
		}
		RAIDBlock activeBlock = updatedBlocks.get(active);
		activeBlock = (RAIDBlock) activeBlock.writeLP(lp, lpArgs);
		if(!activeBlock.hasRoomForWrite()) {
			activeBlock = (RAIDBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
		}
		updatedBlocks.set(active, activeBlock);
		Builder builder = getSelfBuilder();
		builder.setTotalDataWritten(getTotalDataWritten() + 1).setBlocks(updatedBlocks)
				.setTotalWritten(getTotalWritten() + 1);
		return builder.build();
	}
	
	public RAIDPlane writePP(int stripe, int parityNum) {
		List<RAIDBlock> updatedBlocks = getNewBlocksList();
		int active = getActiveBlockIndex();
		if (active == -1) {
			active = getLowestEraseCleanBlockIndex();
			updatedBlocks.set(active, (RAIDBlock) updatedBlocks.get(active).setStatus(BlockStatusGeneral.ACTIVE));
		}
		RAIDBlock activeBlock = updatedBlocks.get(active);
		activeBlock = activeBlock.writePP(stripe, parityNum);
		if(!activeBlock.hasRoomForWrite()) {
			activeBlock = (RAIDBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
		}
		updatedBlocks.set(active, activeBlock);
		Builder builder = getSelfBuilder();
		builder.setTotalParityWritten(getTotalParityWritten() + 1).setBlocks(updatedBlocks)
				.setTotalWritten(getTotalWritten() + 1);
		return builder.build();
	}
	
	public Triplet<RAIDPlane, Integer, Integer> cleanRAID() {
		if(!invokeCleaning()) {
			return null;
		}
		return cleanRAIDPlane();
	}

	public int getTotalDataWritten() {
		return this.totalDataWritten;
	}

	public int getTotalParityWritten() {
		return this.totalParityWritten;
	}

	public EntityInfo getInfo() {
		EntityInfo result = super.getInfo();

		result.add("Total parity pages written", Integer.toString(getTotalParityWritten()), 2);
		result.add("Total data pages written", Integer.toString(getTotalDataWritten()), 2);
		return result;
	}
	
	@Override
	protected Pair<RAIDPlane, Integer> cleanPlane() {
		throw new UnsupportedOperationException();
	}
	
	protected Triplet<RAIDPlane, Integer, Integer> cleanRAIDPlane() {
		List<RAIDBlock> cleanBlocks = getNewBlocksList();
		Pair<Integer, RAIDBlock> pickedToClean =  pickBlockToClean();
		int parityToMove = pickedToClean.getValue1().getParityValidCounter();
		int dataToMove = pickedToClean.getValue1().getDataValidCounter();
		int active = getActiveBlockIndex();
		RAIDBlock activeBlock = null;
		if (active != -1) {
			activeBlock = cleanBlocks.get(active);
		}
		
		for (RAIDPage page : pickedToClean.getValue1().getPages()) {
			if (page.isValid()) {						
				if (active == -1) {
					active = getLowestEraseCleanBlockIndex();
					activeBlock = (RAIDBlock) cleanBlocks.get(active).setStatus(BlockStatusGeneral.ACTIVE);
				}
				activeBlock = activeBlock.move(page.getLp(), page.getParityNumber(), page.getStripe(), page.isHighlighted());
				if(!activeBlock.hasRoomForWrite()) {
					cleanBlocks.set(active, (RAIDBlock) activeBlock.setStatus(BlockStatusGeneral.USED));
					active = -1;
				}
			}
		}
		if (active != -1) {
			cleanBlocks.set(active, activeBlock);
		}
		
		cleanBlocks.set(pickedToClean.getValue0(), (RAIDBlock) pickedToClean.getValue1().eraseBlock());
		Builder builder = getSelfBuilder();
		builder.setBlocks(cleanBlocks).setTotalWritten(getTotalWritten())
				.setTotalGCInvocations(getTotalGCInvocations() + 1);
		return new Triplet<>(builder.build(), dataToMove, parityToMove);
	}
	
	RAIDPlane setBlock(RAIDBlock block, int index) {
		List<RAIDBlock> newBlocksList = getNewBlocksList();
		newBlocksList.set(index, block);
		Builder builder = getSelfBuilder();
		builder.setBlocks(newBlocksList).setTotalWritten(getTotalWritten());
		return builder.build();
	}
}
