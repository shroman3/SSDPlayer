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

import java.awt.Color;
import java.util.List;

import manager.ReusableVisualizationSSDManager;
import utils.Utils;
import entities.Block;
import entities.BlockStatus;
import entities.BlockStatusGeneral;
import entities.reusable.ReusableBlockStatus;
import general.Consts;

public class VisualizationBlock extends Block<VisualizationPage> {
	public static class Builder extends Block.Builder<VisualizationPage> {
		private VisualizationBlock block;
		
		public Builder() {
			setBlock(new VisualizationBlock());
		}
		
		protected Builder(VisualizationBlock block) {
			setBlock(new VisualizationBlock(block));
		}

		public Builder setManager(ReusableVisualizationSSDManager manager) {
			super.setManager(manager);
			block.manager = manager;
			return this;
		}

		protected void setBlock(VisualizationBlock block) {
			super.setBlock(block);
			this.block = block;
		}
		
		@Override
		public VisualizationBlock build() {
			validate();
			return new VisualizationBlock(block);
		}
		
		@Override
		protected void validate() {
			super.validate();
			Utils.validateNotNull(block.manager, "manager");
		}
	}
	
	private ReusableVisualizationSSDManager manager = null;

	protected VisualizationBlock() { }
	
	protected VisualizationBlock(VisualizationBlock other) {
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
		} else if (isInGC()) {
			return Consts.Colors.BLACK;
		}
		return null;
	}
	
	VisualizationBlock setPage(VisualizationPage page, int index) {
		List<VisualizationPage> newPagesList = getNewPagesList();
		newPagesList.set(index, page);
		Builder builder = getSelfBuilder();
		builder.setPagesList(newPagesList);
		if(page.isValid()) {
			builder.setValidCounter(getValidCounter() + 1);
		}
		return builder.build();
	}
}
