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
import entities.Chip;
import entities.RAID.RAIDBasicChip;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDChip extends RAIDBasicChip<RAIDPage, RAIDBlock, RAIDPlane> {
	public static class Builder extends RAIDBasicChip.Builder<RAIDPage, RAIDBlock, RAIDPlane> {
		private RAIDChip chip;
		
		public Builder () {
			setChip(new RAIDChip());
		}
		
		public Builder (RAIDChip chip) {
			setChip(new RAIDChip(chip));
		}
		
		public RAIDChip build() {
			return new RAIDChip(chip);
		}
		
		protected void setChip(RAIDChip chip) {
			super.setChip(chip);
			this.chip = chip;
		}
	}
	
	protected RAIDChip() {}	
	
	protected RAIDChip(RAIDChip other) {
		super(other);
	}
	
	RAIDChip setPlane(RAIDPlane plane, int index) {
		List<RAIDPlane> newPlanesList = getNewPlanesList();
		newPlanesList.set(index, plane);
		Builder builder = getSelfBuilder();
		builder.setPlanes(newPlanesList);
		return builder.build();
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}
	
	public RAIDChip invalidatePage(Triplet<Integer, Integer, Integer> sourceIndex) {
		RAIDPlane plane = getPlane(sourceIndex.getValue0());
		RAIDBlock block = plane.getBlock(sourceIndex.getValue1());
		RAIDPage page = block.getPage(sourceIndex.getValue2());
		page = (RAIDPage) page.getSelfBuilder().setValid(false).build();
		block = block.setPage(page, sourceIndex.getValue2());
		plane = plane.setBlock(block, sourceIndex.getValue1());
		return setPlane(plane, sourceIndex.getValue0());
	}
	
	public RAIDChip changeStatus(Pair<Integer, Integer> blockIndex, BlockStatus status) {
		RAIDPlane plane = getPlane(blockIndex.getValue0());
		RAIDBlock block = plane.getBlock(blockIndex.getValue1());
		block = (RAIDBlock) block.getSelfBuilder().setStatus(status).build();
		plane = plane.setBlock(block, blockIndex.getValue1());
		return setPlane(plane, blockIndex.getValue0());
	}
	
	public RAIDChip changeGC(Pair<Integer, Integer> blockIndex, boolean isInGC) {
		RAIDPlane plane = getPlane(blockIndex.getValue0());
		RAIDBlock block = plane.getBlock(blockIndex.getValue1());
		block = (RAIDBlock) block.getSelfBuilder().setInGC(isInGC).build();
		plane = plane.setBlock(block, blockIndex.getValue1());
		return setPlane(plane, blockIndex.getValue0());
	}
	
	public RAIDChip eraseBlock(Pair<Integer, Integer> blockIndex) {
		RAIDPlane plane = getPlane(blockIndex.getValue0());
		RAIDBlock block = plane.getBlock(blockIndex.getValue1());
		plane = plane.setBlock((RAIDBlock) block.eraseBlock(), blockIndex.getValue1());
		return setPlane(plane, blockIndex.getValue0());
	}

	public Chip<RAIDPage,RAIDBlock,RAIDPlane> writePP(int stripe, int parityNum) {
		int index = getMinValidCountPlaneIndex();
		return setPlane((RAIDPlane) getPlane(index).writePP(stripe, parityNum), index);
	}
}