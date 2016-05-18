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
package entities.RAID.visualization;

import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import entities.BlockStatus;
import entities.RAID.RAIDBasicChip;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDVisualizationChip extends RAIDBasicChip<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane> {
	public static class Builder extends RAIDBasicChip.Builder<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane> {
		private RAIDVisualizationChip chip;
		
		public Builder () {
			setChip(new RAIDVisualizationChip());
		}
		
		public Builder (RAIDVisualizationChip chip) {
			setChip(new RAIDVisualizationChip(chip));
		}
		
		public RAIDVisualizationChip build() {
			return new RAIDVisualizationChip(chip);
		}
		
		protected void setChip(RAIDVisualizationChip chip) {
			super.setChip(chip);
			this.chip = chip;
		}
	}
	
	protected RAIDVisualizationChip() {}	
	
	protected RAIDVisualizationChip(RAIDVisualizationChip other) {
		super(other);
	}
	
	RAIDVisualizationChip setPlane(RAIDVisualizationPlane plane, int index) {
		List<RAIDVisualizationPlane> newPlanesList = getNewPlanesList();
		newPlanesList.set(index, plane);
		Builder builder = getSelfBuilder();
		builder.setPlanes(newPlanesList);
		return builder.build();
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}
	
	public RAIDVisualizationChip invalidatePage(Triplet<Integer, Integer, Integer> sourceIndex) {
		RAIDVisualizationPlane plane = getPlane(sourceIndex.getValue0());
		RAIDVisualizationBlock block = plane.getBlock(sourceIndex.getValue1());
		RAIDVisualizationPage page = block.getPage(sourceIndex.getValue2());
		page = (RAIDVisualizationPage) page.getSelfBuilder().setValid(false).build();
		block = block.setPage(page, sourceIndex.getValue2());
		plane = plane.setBlock(block, sourceIndex.getValue1());
		return setPlane(plane, sourceIndex.getValue0());
	}
	
	public RAIDVisualizationChip changeStatus(Pair<Integer, Integer> blockIndex, BlockStatus status) {
		RAIDVisualizationPlane plane = getPlane(blockIndex.getValue0());
		RAIDVisualizationBlock block = plane.getBlock(blockIndex.getValue1());
		block = (RAIDVisualizationBlock) block.getSelfBuilder().setStatus(status).build();
		plane = plane.setBlock(block, blockIndex.getValue1());
		return setPlane(plane, blockIndex.getValue0());
	}
	
	public RAIDVisualizationChip changeGC(Pair<Integer, Integer> blockIndex, boolean isInGC) {
		RAIDVisualizationPlane plane = getPlane(blockIndex.getValue0());
		RAIDVisualizationBlock block = plane.getBlock(blockIndex.getValue1());
		block = (RAIDVisualizationBlock) block.getSelfBuilder().setInGC(isInGC).build();
		plane = plane.setBlock(block, blockIndex.getValue1());
		return setPlane(plane, blockIndex.getValue0());
	}
	
	public RAIDVisualizationChip eraseBlock(Pair<Integer, Integer> blockIndex) {
		RAIDVisualizationPlane plane = getPlane(blockIndex.getValue0());
		RAIDVisualizationBlock block = plane.getBlock(blockIndex.getValue1());
		plane = plane.setBlock((RAIDVisualizationBlock) block.eraseBlock(), blockIndex.getValue1());
		return setPlane(plane, blockIndex.getValue0());
	}
}