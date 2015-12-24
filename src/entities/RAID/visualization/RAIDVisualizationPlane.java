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

import entities.BlockStatusGeneral;
import entities.RAID.RAIDBasicPlane;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDVisualizationPlane extends RAIDBasicPlane<RAIDVisualizationPage, RAIDVisualizationBlock> {
	public static class Builder extends RAIDBasicPlane.Builder<RAIDVisualizationPage, RAIDVisualizationBlock> {
		private RAIDVisualizationPlane plane;
		
		public Builder() {
			setPlane(new RAIDVisualizationPlane());
		}
		
		public Builder(RAIDVisualizationPlane plane) {
			setPlane(new RAIDVisualizationPlane(plane));
		}

		public RAIDVisualizationPlane build() {
			validate();
			return new RAIDVisualizationPlane(plane);
		}
		
		protected void setPlane(RAIDVisualizationPlane plane) {
			super.setPlane(plane);
			this.plane = plane;
		}
		
		protected void validate() {
			super.validate();
		}
	}
	
	protected RAIDVisualizationPlane() {}
		
	protected RAIDVisualizationPlane(RAIDVisualizationPlane other) {
		super(other);
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}
	
	RAIDVisualizationPlane setBlock(RAIDVisualizationBlock block, int index) {
		List<RAIDVisualizationBlock> newBlocksList = getNewBlocksList();
		newBlocksList.set(index, block);
		Builder builder = getSelfBuilder();
		builder.setBlocks(newBlocksList);
		return builder.build();
	}

	public RAIDVisualizationPlane writeLP(int lp, int dummy) {
		List<RAIDVisualizationBlock> updatedBlocks = getNewBlocksList();
		int active = getActiveBlockIndex();
		if (active == -1) {
			active = getLowestEraseCleanBlockIndex();
			updatedBlocks.set(active, (RAIDVisualizationBlock) updatedBlocks.get(active).setStatus(BlockStatusGeneral.ACTIVE));
		}
		RAIDVisualizationBlock activeBlock = updatedBlocks.get(active);
		activeBlock = activeBlock.writeLP(lp);
		if(!activeBlock.hasRoomForWrite()) {
			activeBlock = (RAIDVisualizationBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
		}
		updatedBlocks.set(active, activeBlock);
		Builder builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks);
		return builder.build();
	}
	
	@Override
	protected Pair<RAIDVisualizationPlane, Integer> cleanPlane() {
		List<RAIDVisualizationBlock> cleanBlocks = getNewBlocksList();
		Pair<Integer, RAIDVisualizationBlock> pickedToClean =  pickBlockToClean();
		int toMove = pickedToClean.getValue1().getValidCounter();
		int active = getActiveBlockIndex();
		RAIDVisualizationBlock activeBlock = cleanBlocks.get(active);
		
		for (RAIDVisualizationPage page : pickedToClean.getValue1().getPages()) {
			if (page.isValid()) {						
				activeBlock = activeBlock.move(page.getLp(), page.isHighlighted(), page.getStripe(), page.getParityNumber());
			}
		}
		if(!activeBlock.hasRoomForWrite()) {
			activeBlock = (RAIDVisualizationBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
		}
		cleanBlocks.set(active, activeBlock);
		cleanBlocks.set(pickedToClean.getValue0(), (RAIDVisualizationBlock) pickedToClean.getValue1().eraseBlock());
		Builder builder = getSelfBuilder();
		builder.setBlocks(cleanBlocks);
		return new Pair<>(builder.build(), toMove);
	}
}
