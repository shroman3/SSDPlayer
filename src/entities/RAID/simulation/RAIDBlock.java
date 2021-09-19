/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Or Mauda, Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
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
package entities.RAID.simulation;

import entities.RAID.RAIDBasicBlock;
import entities.RAID.RAIDBasicPage;
import manager.RAIDBasicSSDManager;
import utils.Utils;
import utils.Utils.*;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDBlock extends RAIDBasicBlock<RAIDPage> {
	public static class Builder extends RAIDBasicBlock.Builder<RAIDPage> {
		private RAIDBlock block;
		
		public Builder() {
			setBlock(new RAIDBlock());
		}
		
		protected Builder(RAIDBlock block) {
			setBlock(new RAIDBlock(block));
		}
		
		public Builder setManager(RAIDBasicSSDManager<RAIDPage, ?, ?, ?, ?> manager) {
			super.setManager(manager);
			block.manager = manager;
			return this;
		}
	
		@Override
		public RAIDBlock build() {
			validate();
			return new RAIDBlock(block);
		}

		protected void setBlock(RAIDBlock block) {
			super.setBlock(block);
			this.block = block;
		}
		
		@Override
		protected void validate() {
			super.validate();
			Utils.validateNotNull(block.manager, "manager");
		}
	}
	
	private RAIDBasicSSDManager<RAIDPage,?,?,?,?> manager = null;

	protected RAIDBlock() { }
	
	protected RAIDBlock(RAIDBlock other) {
		super(other);
		this.manager = other.manager;
	}
	
	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}
	
//	RAIDBlock setPage(RAIDPage page, int index) {
//		List<RAIDPage> newPagesList = getNewPagesList();
//		newPagesList.set(index, page);
//		Builder builder = getSelfBuilder();
//		builder.setPagesList(newPagesList);
//		if(page.isValid()) {
//			builder.setValidCounter(getValidCounter() + 1);
//		}
//		return builder.build();
//	}

	public RAIDBlock move(int lp, int parityNumber, int stripe, boolean isHighlighted) {
		int index = 0;
		for (RAIDBasicPage page : getPages()) {
			if (page.isClean()) {
				RAIDPage.Builder builder = (RAIDPage.Builder) page.getSelfBuilder();
				if (page.isHighlighted() == true) {
					builder.setIsHighlighted(false);
					page = builder.build();
				}
				builder.setStripe(stripe).setParityNumber(parityNumber).setIsHighlighted(isHighlighted).setClean(false).setLp(lp).setGC(true).setValid(true);
				return (RAIDBlock) addValidPage(index, builder.build());
			}
			++index;
		}
		return null;
	}
	
	public RAIDBlock writeLP(int lp, LpArgs lpArgs) {
		int stripe = lpArgs.getStripe();
		int index = 0;
		for (RAIDBasicPage page : getPages()) {
			if (page.isClean()) {
				RAIDPage.Builder builder = (RAIDPage.Builder) page.getSelfBuilder();
				builder.setStripe(stripe).setParityNumber(0).setClean(false).setLp(lp).setGC(false).setValid(true);
				return (RAIDBlock) addValidPage(index, builder.build());
			}
			++index;
		}
		return null;
	}

	public RAIDBlock writePP(int stripe, int parityNum) {
		int index = 0;
		for (RAIDBasicPage page : getPages()) {
			if (page.isClean()) {
				RAIDPage.Builder builder = (RAIDPage.Builder) page.getSelfBuilder();
				builder.setStripe(stripe).setParityNumber(parityNum).setClean(false).setInvalidLp().setGC(false).setValid(true);
				return (RAIDBlock) addValidPage(index, builder.build());
			}
			++index;
		}
		return null;
	}

	@Override
	public RAIDBlock eraseBlock() {
		RAIDBlock block = (RAIDBlock) super.eraseBlock();
		Builder builder = block.getSelfBuilder();
		builder.setDataValidCounter(0)
			.setParityValidCounter(0);
		return builder.build();
	}
	
	protected RAIDPage invalidatePage(RAIDPage page) {
		RAIDBasicPage.Builder builder = page.getSelfBuilder();
		builder.setValid(false);
		
		Boolean showOldData, showOldParity;
		showOldParity = manager.toShowOldParity();
		showOldData = manager.toShowOldData();
		
		if ((page.getParityNumber() <= 0 && showOldData == false) // we shouldn't highlight invalidated data pages
				|| (page.getParityNumber() > 0 && showOldParity == false)) { // we shouldn't highlight invalidated parity pages
			// when data and/or parity page is invalidated and it shouldn't be a part of the stripe
			builder.setStripe(-1);
			builder.setIsHighlighted(false);
		}
		
		builder.setInvalidLp();
		return (RAIDPage) builder.build();
	}

	public float getParityRatio() {
		float notClean = 0;
		float parityCount = 0;
		for (RAIDPage page : getPages()) {
			if (!page.isClean()) {
				notClean += 1;
				if (page.getParityNumber() > 0) {
					parityCount += 1;
				}
			}
		}

		if (notClean == 0) {
			return 0;
		}

		return parityCount / notClean;
	}

	@Override
	protected RAIDBlock addValidPage(int index, RAIDPage page) {
		RAIDBlock block = (RAIDBlock) super.addValidPage(index, page);
		Builder blockBuilder = block.getSelfBuilder();
		if(page.isParity()) {
			blockBuilder.setParityValidCounter(getParityValidCounter() + 1);
		} else {
			blockBuilder.setDataValidCounter(getDataValidCounter() + 1);
		}
		return blockBuilder.build();
	}
}
