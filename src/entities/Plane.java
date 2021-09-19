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
package entities;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

import manager.SSDManager;
import utils.Utils;
import utils.Utils.*;

/**
 * @author Roman
 * 
 * Plane basically is ordered collection of blocks, but this entity contains counters
 * and information for the activities that should be handled by it: 
 * number of clean blocks, active block index, etc.
 * Also immutable
 * 
 * @param <B> - block type that the plane stores
 */
public abstract class Plane<B extends Block<?>> {
	public abstract static class Builder<B extends Block<?>> {
		private Plane<B> plane;

		abstract public Plane<B> build();
		
		public Builder<B> setManager(SSDManager<?,B,?,?,?> manager) {
			plane.manager = manager;
			return this;
		}
		
		public Builder<B> setBlocks(List<B> blocksList) {
			plane.blocksList = new ArrayList<B>(blocksList);
			return this;
		}
		
		public Builder<B> setTotalWritten(int totalWritten) {
			plane.totalWritten = totalWritten;
			return this;
		}

		public Builder<B> setTotalGCInvocations(int number) {
			plane.totalGCInvocations = number;
			return this;
		}
		
		protected void setPlane(Plane<B> plane) {
			this.plane = plane;
		}

		protected void validate() {
			Utils.validateNotNull(plane.manager, "manager");
		}
	}

	private List<B> blocksList;
	private SSDManager<?,B,?,?,?> manager;

	private int validPagesCounter = 0;
	private int numOfClean = 0;
	private int activeBlockIndex = -1;
	private int lowestEraseCleanBlockIndex = -1;
	private int lowestValidBlockIndex = -1;
	private int totalWritten = 0;
	private int totalGCInvocations = 0;

	protected Plane() {}

	protected Plane(Plane<B> other) {
		this.blocksList = new ArrayList<B>(other.blocksList);
		this.manager = other.manager;
		this.totalWritten = other.totalWritten;		
		this.totalGCInvocations = other.totalGCInvocations;
		initValues();
	}
	
	/**
	 * @return builder for a copy of this plane(used in order to create a modified copy)
	 */
	abstract public Builder<B> getSelfBuilder();
	/**
	 * @param lp - the logical page to be written 
	 * @param lpArgs - additional data(hot/cold or something else..)
	 * @return new Plane with the lp written on it
	 */
	abstract public Plane<B> writeLP(int lp, LpArgs lpArgs);
	/**
	 * @return tuple: first -  Plane after being cleaned 
	 * 				  second - the number of moved pages in process of garbage collection
	 */
	abstract protected Pair<? extends Plane<B>, Integer> cleanPlane();

	public Iterable<B> getBlocks() {
		return blocksList;
	}
	
	public B getBlock(int i) {
		return blocksList.get(i);
	}
	
	public int getBlocksNum() {
		return blocksList.size();
	}
	
	public int getValidPagesCounter() {
		return validPagesCounter;
	}
	
	public int getPagesInBlock() {
		if(blocksList.isEmpty()) {
			return 0;
		}
		return blocksList.get(0).getPagesNum();
	}

	public List<B> getNewBlocksList() {
		return new ArrayList<B>(blocksList);
	}
	
	/**
	 * @param lp - Logical Page to be invalidated
	 * @return new plane with the Logical Page specified invalidated from the blocks
	 */
	@SuppressWarnings("unchecked")
	public Plane<B> invalidate(int lp) {
		List<B> updatedBlocks = new ArrayList<B>();
		for (B block : getBlocks()) {
			updatedBlocks.add((B) block.invalidate(lp));
		}
		Builder<B> builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks);
		return builder.build();
	}
	
	/**
	 * @return <clean plane, number of pages moved in garbage collection>
	 * if no cleaning is invoked returns itself and 0 pages moved
	 */
	public Pair<? extends Plane<B>, Integer> clean() {
		if(!invokeCleaning()) {
			return null;
		}
		return cleanPlane();
	}

	public int getTotalWritten() {
		return totalWritten;
	}

	public EntityInfo getInfo() {
		EntityInfo result = new EntityInfo();

		result.add("Total logical pages written", Integer.toString(getTotalWritten()), 2);
		result.add("Clean blocks", Integer.toString(getNumOfClean()), 3);
		result.add("Number of blocks", Integer.toString(getBlocksNum()), 1);
		result.add("Valid count", Integer.toString(getValidPagesCounter()), 4);
		result.add("Block erasures", Integer.toString(getNumOfBlockErasures()), 3);
		result.add("GC invocations", Integer.toString(getGCExecutions()), 3);

		return result;
	}

	public int getNumOfBlockErasures() {
		int numOfErasures = 0;
		for (Block<?> block : getBlocks()) {
			numOfErasures += block.getEraseCounter();
		}
		return numOfErasures;
	}

	public int getGCExecutions() {
		return getTotalGCInvocations();
	}

	public int getNumOfClean() {
		return numOfClean;
	}

	public int getTotalGCInvocations() {
		return totalGCInvocations;
	}
	
	protected int getWritableBlocksNum() {
		return getNumOfClean();
	}

	protected int getActiveBlockIndex() {
		return activeBlockIndex;
	}

	protected int getLowestValidBlockIndex() {
		return lowestValidBlockIndex;
	}
	
	protected int getLowestEraseCleanBlockIndex() {
		Utils.validateNotNegative(lowestEraseCleanBlockIndex, "Looks like device you've configured is too small for the trace. \nlowestEraseCleanBlockIndex");
		return lowestEraseCleanBlockIndex;
	}
	
	/**
	 * Choose a victim block for garbage collection
	 * @return block index of block with smallest sum of valid counters
	 */
	protected Pair<Integer, B> pickBlockToClean() {
		int victimBlock = getLowestValidBlockIndex();
		return new Pair<Integer, B>(victimBlock, getBlock(victimBlock));
	}
	
	protected boolean invokeCleaning() {
		return getWritableBlocksNum() < manager.getGCT() && getLowestValidBlockIndex() != -1;
	}

	protected boolean isUsed(B block) {
		return block.getStatus() == BlockStatusGeneral.USED;
	}

	/**
	 * On creation this methods initializes the counters of the plane
	 */
	private void initValues() {
		int minValid = manager.getPagesNum();
		int minValidErased = 0;
		int minEraseClean = Integer.MAX_VALUE;

		int i = 0;
		for (B block : getBlocks()) {
			validPagesCounter += block.getValidCounter();
			if (block.getStatus() == BlockStatusGeneral.ACTIVE) {
				activeBlockIndex = i;
			}else if (block.getStatus() == BlockStatusGeneral.CLEAN) {
				if (block.getEraseCounter() < minEraseClean) {
					minEraseClean = block.getEraseCounter();
					lowestEraseCleanBlockIndex = i;
				}
				++numOfClean;
			} else if(isUsed(block)) {
				if (block.getValidCounter() < minValid) {
					minValid = block.getValidCounter();
					minValidErased = block.getEraseCounter();
					lowestValidBlockIndex = i;
				} else if (block.getValidCounter() == minValid && block.getEraseCounter() < minValidErased) {
					minValid = block.getValidCounter();
					minValidErased = block.getEraseCounter();
					lowestValidBlockIndex = i;
				}
			}
			++i;
		}
	}
}
