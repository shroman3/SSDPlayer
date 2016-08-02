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
package entities.reusable;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import org.javatuples.Pair;

import entities.Block;
import entities.BlockStatus;
import entities.BlockStatusGeneral;
import general.Consts;
import manager.ReusableSSDManager;
import utils.Utils;

public class ReusableBlock extends Block<ReusablePage> {
	public static class Builder extends Block.Builder<ReusablePage> {
		private ReusableBlock block;
		
		public Builder() {
			setBlock(new ReusableBlock());
		}
		
		protected Builder(ReusableBlock block) {
			setBlock(new ReusableBlock(block));
		}
		
		public Builder setManager(ReusableSSDManager manager) {
			super.setManager(manager);
			block.manager = manager;
			return this;
		}

		protected void setBlock(ReusableBlock block) {
			super.setBlock(block);
			this.block = block;
		}
		
		@Override
		public ReusableBlock build() {
			validate();
			return new ReusableBlock(block);
		}
		
		@Override
		protected void validate() {
			super.validate();
			Utils.validateNotNull(block.manager, "manager");
		}
	}
	
	private ReusableSSDManager manager = null;

	protected ReusableBlock() { }
	
	protected ReusableBlock(ReusableBlock other) {
		super(other);
		manager = other.manager;
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	@Override
	public Color getFrameColor() {
		BlockStatus status = getStatus();
		if((status == BlockStatusGeneral.ACTIVE) || (status == ReusableBlockStatus.ACTIVE_RECYCLED)) {
			return Consts.Colors.ACTIVE;
		}
		return null;
	}
	
	public Pair<Integer, Integer> getRecycledPageIndex() {
		int first = -1;
		int pageIndex =0;
		Iterator<ReusablePage> iterator = getPages().iterator();
		for (; iterator.hasNext();) {
			ReusablePage page = iterator.next();
			if ((!page.isValid()) && (page.getWriteLevel() <= 1)) {
				first = pageIndex;
				break;
			}
			++pageIndex;
		}
		++pageIndex;
		for (; iterator.hasNext();) {
			ReusablePage page = iterator.next();
			if ((!page.isValid()) && (page.getWriteLevel() <= 1)) {
				return new Pair<Integer, Integer>(first, pageIndex);
			}
			++pageIndex;
		}
		return null;
	}

	public ReusableBlock move(Integer lp, int writeLevel) {
		int index = 0;
		for (ReusablePage page : getPages()) {
			if (page.isClean()) {
				ReusablePage.Builder builder = page.getSelfBuilder();
				builder.setWriteLevel(1).setGcWriteLevel(writeLevel).setClean(false).setLp(lp).setGC(true).setValid(true);
				return (ReusableBlock) addValidPage(index, builder.build());
			}
			++index;
		}
		return null;
	}

	public ReusableBlock firstWriteLP(int lp) {
		int index = 0;
		for (ReusablePage page : getPages()) {
			if (page.isClean()) {
				ReusablePage.Builder builder = page.getSelfBuilder();
				builder.setWriteLevel(1).setClean(false).setLp(lp).setGC(false).setValid(true);
				return (ReusableBlock) addValidPage(index, builder.build());
			}
			++index;
		}
		return null;
	}
	
	public ReusableBlock secondWriteLP(int lp) {
		int index = 0;
		int count = 0;
		List<ReusablePage> newPagesList = getNewPagesList();
		for (ReusablePage page : getPages()) {
			if ((!page.isValid()) && (page.getWriteLevel() == 1)) {
				ReusablePage.Builder builder = page.getSelfBuilder();
				builder.setWriteLevel(2).setClean(false).setLp(lp).setGC(false).setValid(true);
				newPagesList.set(index, builder.build());
				++count;
				if (count >=2) {
					Builder blockBuilder = getSelfBuilder();
					blockBuilder.setPagesList(newPagesList).setValidCounter(getValidCounter()+1);
					return blockBuilder.build();
				}
			}
			++index;
		}
		return null;
	}
	
	public boolean hasRoomForSecondWrite() {
		int count = 0;
		for (ReusablePage page : getPages()) {
			if ((!page.isValid()) && (page.getWriteLevel() <= 1)) {
				++count;
				if (count >=2) {
					return true;
				}
			}
		}
		return false;
	}
	

	public float getAveragePageWriteLevel() {
		float writeLevelSum = 0;
		int count = 0;
		for(ReusablePage page : this.getPages()){
			if(page.getWriteLevel() >= 1){
				count++;
				writeLevelSum += page.getWriteLevel();
			}
		}
		if(count == 0){
			return 0;
		}
		
		return writeLevelSum/count;
	}

	public int getWriteLevel() {
		int maxLevel = 0;
		for(ReusablePage page : this.getPages()){
			if(page.getWriteLevel() > maxLevel){
				maxLevel = page.getWriteLevel();
			}
		}
		return maxLevel;
	}
}
