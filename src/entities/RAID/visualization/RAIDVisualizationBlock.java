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

import java.awt.Color;
import java.util.List;

import manager.RAIDVisualizationSSDManager;
import utils.Utils;
import entities.BlockStatusGeneral;
import entities.RAID.RAIDBasicBlock;
import entities.RAID.RAIDBasicPage;
import general.Consts;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDVisualizationBlock extends RAIDBasicBlock<RAIDVisualizationPage> {
	public static class Builder extends RAIDBasicBlock.Builder<RAIDVisualizationPage> {
		private RAIDVisualizationBlock block;
		
		public Builder() {
			setBlock(new RAIDVisualizationBlock());
		}
		
		protected Builder(RAIDVisualizationBlock block) {
			setBlock(new RAIDVisualizationBlock(block));
		}
		
		public Builder setManager(RAIDVisualizationSSDManager manager) {
			super.setManager(manager);
			block.manager = manager;
			return this;
		}
		
		@Override
		public RAIDVisualizationBlock build() {
			validate();
			return new RAIDVisualizationBlock(block);
		}

		protected void setBlock(RAIDVisualizationBlock block) {
			super.setBlock(block);
			this.block = block;
		}
		
		@Override
		protected void validate() {
			super.validate();
			Utils.validateNotNull(block.manager, "manager");
		}
	}
	
	private RAIDVisualizationSSDManager manager = null;

	protected RAIDVisualizationBlock() { }
	
	protected RAIDVisualizationBlock(RAIDVisualizationBlock other) {
		super(other);
		this.manager = other.manager;
	}
	
	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}
	
	@Override
	public Color getFrameColor() {
		if(getStatus() == BlockStatusGeneral.ACTIVE) {
			return Consts.Colors.ACTIVE;
		} else if (isInGC()) {
			return Consts.Colors.BLACK;
		}
		return null;
	}
	
	RAIDVisualizationBlock setPage(RAIDVisualizationPage page, int index) {
		List<RAIDVisualizationPage> newPagesList = getNewPagesList();
		newPagesList.set(index, page);
		Builder builder = getSelfBuilder();
		builder.setPagesList(newPagesList);
		if(page.isValid()) {
			builder.setValidCounter(getValidCounter() + 1);
		}
		return builder.build();
	}

	public RAIDVisualizationBlock move(int lp, boolean isHighlighted, int stripe, int parityNumber) {
		int index = 0;
		for (RAIDBasicPage page : getPages()) {
			if (page.isClean()) {
				RAIDVisualizationPage.Builder builder = (RAIDVisualizationPage.Builder) page.getSelfBuilder();
				if (page.isHighlighted() == true) { // Maybe unnecessary 
					builder.setIsHighlighted(false);
					page = builder.build();
				}
				builder.setIsHighlighted(isHighlighted).setStripe(stripe).setParityNumber(parityNumber)
				.setClean(false).setLp(lp).setGC(true).setValid(true);
				return (RAIDVisualizationBlock) addValidPage(index, builder.build());
			}
			++index;
		}
		return null;
	}
	
	public RAIDVisualizationBlock writeLP(int lp) {
		int index = 0;
		for (RAIDBasicPage page : getPages()) {
			if (page.isClean()) {
				RAIDVisualizationPage.Builder builder = (RAIDVisualizationPage.Builder) page.getSelfBuilder();
				builder.setClean(false).setLp(lp).setGC(false).setValid(true);
				return (RAIDVisualizationBlock) addValidPage(index, builder.build());
			}
			++index;
		}
		return null;
	}
	
	@Override
	protected RAIDVisualizationPage invalidatePage(RAIDVisualizationPage page) {
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
		return (RAIDVisualizationPage) builder.build();
	}
}
