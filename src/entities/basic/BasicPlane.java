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
package entities.basic;

import java.util.List;

import org.javatuples.Pair;

import entities.ActionLog;
import entities.BlockStatusGeneral;
import entities.CleanAction;
import entities.Plane;

public class BasicPlane extends Plane<BasicPage, BasicBlock> {
	public static class Builder extends Plane.Builder<BasicPage, BasicBlock> {
		private BasicPlane plane;
		
		public Builder() {
			setPlane(new BasicPlane());
		}
		
		public Builder(BasicPlane plane) {
			setPlane(new BasicPlane(plane));
		}

		public BasicPlane build() {
			validate();
			return new BasicPlane(plane);
		}
		
		protected void setPlane(BasicPlane plane) {
			super.setPlane(plane);
			this.plane = plane;
		}
		
		protected void validate() {
			super.validate();
		}
	}
	
	protected BasicPlane() {}
		
	protected BasicPlane(BasicPlane other) {
		super(other);
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	public BasicPlane writeLP(int lp, int dummy) {
		List<BasicBlock> updatedBlocks = getNewBlocksList();
		int active = getActiveBlockIndex();
		if (active == -1) {
			active = getLowestEraseCleanBlockIndex();
			updatedBlocks.set(active, (BasicBlock) updatedBlocks.get(active).setStatus(BlockStatusGeneral.ACTIVE));
		}
		BasicBlock activeBlock = updatedBlocks.get(active);
		activeBlock = activeBlock.writeLP(lp);
		if(!activeBlock.hasRoomForWrite()) {
			activeBlock = (BasicBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
		}
		updatedBlocks.set(active, activeBlock);
		Builder builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks).setTotalWritten(getTotalWritten() + 1);
		return builder.build();
	}
	
	@Override
	protected Pair<BasicPlane,Integer> cleanPlane() {
		List<BasicBlock> cleanBlocks = getNewBlocksList();
		Pair<Integer, BasicBlock> pickedToClean =  pickBlockToClean();
		int toMove = pickedToClean.getValue1().getValidCounter();
		int active = getActiveBlockIndex();
		BasicBlock activeBlock = cleanBlocks.get(active);
		
		for (BasicPage page : pickedToClean.getValue1().getPages()) {
			if (page.isValid()) {						
				activeBlock = activeBlock.move(page.getLp());
			}
		}
		if(!activeBlock.hasRoomForWrite()) {
			activeBlock = (BasicBlock) activeBlock.setStatus(BlockStatusGeneral.USED);
		}
		cleanBlocks.set(active, activeBlock);
		cleanBlocks.set(pickedToClean.getValue0(), (BasicBlock) pickedToClean.getValue1().eraseBlock());
		Builder builder = getSelfBuilder();
		int gcInvocations = (toMove > 0)? getTotalGCInvocations() + 1 : getTotalGCInvocations();
		builder.setBlocks(cleanBlocks).setTotalGCInvocations(gcInvocations);
		return new Pair<>(builder.build(), toMove);
	}
}
