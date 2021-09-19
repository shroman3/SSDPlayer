/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
 * Copyright (c) 2015, Technion � Israel Institute of Technology
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
package entities.reusable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import manager.ReusableSSDManager;

import org.javatuples.Pair;

import utils.Utils;
import entities.BlockStatusGeneral;
import entities.EntityInfo;
import entities.Plane;
import utils.Utils.*;

public class ReusablePlane extends Plane<ReusableBlock> {
	public static class Builder extends Plane.Builder<ReusableBlock> {
		private ReusablePlane plane;
		
		public Builder() {
			setPlane(new ReusablePlane());
		}
		
		public Builder(ReusablePlane plane) {
			setPlane(new ReusablePlane(plane));
		}

		public ReusablePlane build() {
			validate();
			return new ReusablePlane(plane);
		}
		
		public Builder setManager(ReusableSSDManager manager) {
			super.setManager(manager);
			plane.manager = manager;
			return this;
		}
		
		protected void setPlane(ReusablePlane plane) {
			super.setPlane(plane);
			this.plane = plane;
		}
		
		protected void validate() {
			super.validate();
			Utils.validateNotNull(plane.manager, "manager");
		}

		public Builder setGCExecutions(int gcExecutions) {
			this.plane.gcExecutions = gcExecutions;
			return this;
		}
	}
		
	private int activeRecycledBlockIndex = -1;
	private int numOfRecycled = 0;
	private int lowestEraseRecycledBlockIndex = -1;
	private boolean hasSecondWriteBlock = false;


	private ReusableSSDManager manager;
	
	protected ReusablePlane() {}
		

	private int gcExecutions = 0;

	protected ReusablePlane(ReusablePlane other) {
		super(other);
		this.manager = other.manager;
		this.gcExecutions = other.gcExecutions;
		initValues();
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}
	
	public boolean hasSecondWriteBlock() {
		return hasSecondWriteBlock;
	}
	
	public int getRecycledBlockWithMinErase() {
		return lowestEraseRecycledBlockIndex;
	}

	protected Pair<ReusablePlane,Integer> cleanPlane() {
		List<ReusableBlock> cleanBlocks = getNewBlocksList();
		Pair<Integer, ReusableBlock> pickedToClean =  pickBlockToClean();
		int toMove = 0;
		if ((pickedToClean.getValue1().getStatus() != ReusableBlockStatus.REUSED) && (shouldRecycle())) {
			cleanBlocks.set(pickedToClean.getValue0(), (ReusableBlock) pickedToClean.getValue1().setStatus(ReusableBlockStatus.RECYCLED));
		} else {
			int active = getActiveBlockIndex();
			ReusableBlock activeBlock = null;
			if(active >= 0) {
				activeBlock = cleanBlocks.get(active);
			}

			toMove = pickedToClean.getValue1().getValidCounter();
			Set<Integer> lpMoved = new HashSet<Integer>();
			for (ReusablePage page : pickedToClean.getValue1().getPages()) {
				if (page.isValid() && !lpMoved.contains(page.getLp())) {
					if (active < 0) {
						active = getLowestEraseCleanBlockIndex();
						activeBlock = (ReusableBlock) cleanBlocks.get(active).setStatus(BlockStatusGeneral.ACTIVE);
					}
					LpArgs lpArgs = new LpArgsBuilder().writeLevel(page.getWriteLevel()).buildLpArgs();
					activeBlock = (ReusableBlock) activeBlock.move(page.getLp(), lpArgs);
					lpMoved.add(page.getLp());
					if(!activeBlock.hasRoomForWrite()) {
						activeBlock = (ReusableBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
						cleanBlocks.set(active, activeBlock);
						active = -1;
					}
				}
			}
			if (active < 0) {
				cleanBlocks.set(active, activeBlock);
			}
			cleanBlocks.set(pickedToClean.getValue0(), (ReusableBlock) pickedToClean.getValue1().eraseBlock());
		}
		
		int gcInvocations = (toMove > 0)? getTotalGCInvocations() + 1 : getTotalGCInvocations();
		Builder builder = getSelfBuilder();
		builder.setGCExecutions(getGCExecutions() + 1).setBlocks(cleanBlocks).setTotalGCInvocations(gcInvocations)
		.setTotalWritten(getTotalWritten());
		return new Pair<ReusablePlane, Integer>(builder.build(), toMove);
	}

	public ReusablePlane writeLP(int lp, LpArgs lpArgs) {
		int temperature = lpArgs.getTemperatrure();
		if (manager.isSecondWrite(hasSecondWriteBlock, temperature)) {
			return secondWriteLP(lp);
		}
		return firstWriteLP(lp);
	}

	protected boolean isUsed(ReusableBlock block) {
		return block.getStatus() == ReusableBlockStatus.REUSED || super.isUsed(block);
	}

	/**
	 * Determine whether a victim block should be recycled or erased
	 * @return 	false if (number of recycled blocks > reserved) or (number of clean blocks < 2), 
	 * 			Otherwise, return true
	 */
	private boolean shouldRecycle() {
		return !((numOfRecycled > manager.getReserved()) || (getNumOfClean() < 2));
	}
	
	protected int getWritableBlocksNum() {
		return getNumOfClean() + numOfRecycled;
	}

	private ReusablePlane secondWriteLP(int lp) {
		List<ReusableBlock> updatedBlocks = getNewBlocksList();
		int active = activeRecycledBlockIndex;
		if(active < 0) {
			active = lowestEraseRecycledBlockIndex;
			updatedBlocks.set(active, (ReusableBlock) updatedBlocks.get(active).setStatus(ReusableBlockStatus.ACTIVE_RECYCLED));
		}
		ReusableBlock activeBlock = updatedBlocks.get(active);
		activeBlock = activeBlock.secondWriteLP(lp);
		if(!activeBlock.hasRoomForSecondWrite()) {
			activeBlock = (ReusableBlock) activeBlock.setStatus(ReusableBlockStatus.REUSED);
		}
		updatedBlocks.set(active, activeBlock);
		Builder builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks).setTotalWritten(getTotalWritten() + 1);
		return builder.build();
	}

	private ReusablePlane firstWriteLP(int lp) {
		List<ReusableBlock> updatedBlocks = getNewBlocksList();
		int active = getActiveBlockIndex();
		if(active < 0) {
			active = getLowestEraseCleanBlockIndex();
			updatedBlocks.set(active, (ReusableBlock) updatedBlocks.get(active).setStatus(BlockStatusGeneral.ACTIVE));
		}
		ReusableBlock activeBlock = updatedBlocks.get(active);
		activeBlock = activeBlock.firstWriteLP(lp);
		if(!activeBlock.hasRoomForWrite()) {
			activeBlock = (ReusableBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
		}
		updatedBlocks.set(active, activeBlock);
		Builder builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks).setTotalWritten(getTotalWritten() + 1);
		return builder.build();
	}
	
	private void initValues() {
		int minEraseRecycled = Integer.MAX_VALUE;
		int i = 0;
		for (ReusableBlock block: getBlocks()) {
			if (block.getStatus() == ReusableBlockStatus.ACTIVE_RECYCLED) {
				activeRecycledBlockIndex = i;
				hasSecondWriteBlock = true;
			} else	if (block.getStatus() == ReusableBlockStatus.RECYCLED) {
				if ((manager.getPagesNum()-block.getValidCounter()) >= 2) {					
					if (block.getEraseCounter() < minEraseRecycled) {
						minEraseRecycled = block.getEraseCounter();
						lowestEraseRecycledBlockIndex = i;
					}
					hasSecondWriteBlock = true;
				}
				++numOfRecycled;
			}
			++i;
		}
	}
	public EntityInfo getInfo() {
		EntityInfo result = super.getInfo();

		result.add("Recycled blocks", Integer.toString(getNumOfRecycledBlocks()), 3);
		return result;
	}

	public int getNumOfRecycledBlocks() {
		int numOfRecycledBlocks = 0;
		for (ReusableBlock block : getBlocks()) {
			if ((block.getStatus() == ReusableBlockStatus.ACTIVE_RECYCLED)
					|| (block.getStatus() == ReusableBlockStatus.REUSED)) {
				numOfRecycledBlocks++;
			}
		}
		return numOfRecycledBlocks;
	}

	public int getGCExecutions() {
		return this.gcExecutions;
	}
}
