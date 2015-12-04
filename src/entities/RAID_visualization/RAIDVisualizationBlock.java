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
package entities.RAID_visualization;

import java.awt.Color;
import java.util.List;

import manager.RAIDVisualizationSSDManager;
import utils.Utils;
import entities.Block;
import entities.BlockStatusGeneral;
import general.Consts;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDVisualizationBlock extends Block<RAIDVisualizationPage> {
	public static class Builder extends Block.Builder<RAIDVisualizationPage> {
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
		for (RAIDVisualizationPage page : getPages()) {
			if (page.isClean()) {
				RAIDVisualizationPage.Builder builder = page.getSelfBuilder();
				if (page.isHighlighted() == true) { // Maybe unnecessary 
					builder.setIsHighlighted(false);
					page = builder.build();
				}
				builder.setClean(false).setLp(lp).setGC(true).setValid(true).setIsHighlighted(isHighlighted)
					.setStripe(stripe).setParityNumber(parityNumber);
				return (RAIDVisualizationBlock) addValidPage(index, builder.build());
			}
			++index;
		}
		return null;
	}
	
	public RAIDVisualizationBlock writeLP(int lp) {
		int index = 0;
		for (RAIDVisualizationPage page : getPages()) {
			if (page.isClean()) {
				RAIDVisualizationPage.Builder builder = page.getSelfBuilder();
				builder.setClean(false).setLp(lp).setGC(false).setValid(true);
				return (RAIDVisualizationBlock) addValidPage(index, builder.build());
			}
			++index;
		}
		return null;
	}
}
