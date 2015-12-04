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
package entities.reusable_visualization;

import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import entities.BlockStatus;
import entities.Chip;

public class VisualizationChip extends Chip<VisualizationPage, VisualizationBlock, VisualizationPlane> {
	public static class Builder extends Chip.Builder<VisualizationPage, VisualizationBlock, VisualizationPlane> {
		private VisualizationChip chip;
		
		public Builder () {
			setChip(new VisualizationChip());
		}
		
		public Builder (VisualizationChip chip) {
			setChip(new VisualizationChip(chip));
		}
		
		public VisualizationChip build() {
			return new VisualizationChip(chip);
		}
		
		protected void setChip(VisualizationChip chip) {
			super.setChip(chip);
			this.chip = chip;
		}
	}
	
	
	protected VisualizationChip() {}	
	
	protected VisualizationChip(VisualizationChip other) {
		super(other);
	}

	VisualizationChip setPlane(VisualizationPlane plane, int index) {
		List<VisualizationPlane> newPlanesList = getNewPlanesList();
		newPlanesList.set(index, plane);
		Builder builder = getSelfBuilder();
		builder.setPlanes(newPlanesList);
		return builder.build();
	}
	
	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	public int getLPWriteLevel(int lp) {
		for (VisualizationPlane plane : getPlanes()) {
			for (VisualizationBlock block : plane.getBlocks()) {
				for (VisualizationPage page : block.getPages()) {
					if (page.getLp() == lp) {
						return page.getWriteLevel();
					}
				}
			}
		}
		throw new IllegalArgumentException("No such LP in the chip! LP=" + lp);
	}
	
	public int getPageWriteLevel(int lp, VisualizationDevice device, Triplet<Integer, Integer, Integer> sourceIndex) {
		VisualizationPlane plane = getPlane(sourceIndex.getValue0());
		VisualizationBlock block = plane.getBlock(sourceIndex.getValue1());
		VisualizationPage page = block.getPage(sourceIndex.getValue2());
		return page.getWriteLevel();
	}

	public VisualizationChip invalidatePage(Triplet<Integer, Integer, Integer> sourceIndex) {
		VisualizationPlane plane = getPlane(sourceIndex.getValue0());
		VisualizationBlock block = plane.getBlock(sourceIndex.getValue1());
		VisualizationPage page = block.getPage(sourceIndex.getValue2());
		page = (VisualizationPage) page.getSelfBuilder().setValid(false).build();
		block = block.setPage(page, sourceIndex.getValue2());
		plane = plane.setBlock(block, sourceIndex.getValue1());
		return setPlane(plane, sourceIndex.getValue0());
	}

	public VisualizationChip changeStatus(Pair<Integer, Integer> blockIndex, BlockStatus status) {
		VisualizationPlane plane = getPlane(blockIndex.getValue0());
		VisualizationBlock block = plane.getBlock(blockIndex.getValue1());
		block = (VisualizationBlock) block.getSelfBuilder().setStatus(status).build();
		plane = plane.setBlock(block, blockIndex.getValue1());
		return setPlane(plane, blockIndex.getValue0());
	}

	public VisualizationChip changeGC(Pair<Integer, Integer> blockIndex, boolean isInGC) {
		VisualizationPlane plane = getPlane(blockIndex.getValue0());
		VisualizationBlock block = plane.getBlock(blockIndex.getValue1());
		block = (VisualizationBlock) block.getSelfBuilder().setInGC(isInGC).build();
		plane = plane.setBlock(block, blockIndex.getValue1());
		return setPlane(plane, blockIndex.getValue0());
	}

	public VisualizationChip eraseBlock(Pair<Integer, Integer> blockIndex) {
		VisualizationPlane plane = getPlane(blockIndex.getValue0());
		VisualizationBlock block = plane.getBlock(blockIndex.getValue1());
		plane = plane.setBlock((VisualizationBlock) block.eraseBlock(), blockIndex.getValue1());
		return setPlane(plane, blockIndex.getValue0());
	}
}