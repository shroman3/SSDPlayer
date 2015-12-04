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
package entities.basic;

import java.awt.Color;

import manager.GreedySSDManager;
import utils.Utils;
import entities.Block;
import entities.BlockStatusGeneral;
import general.Consts;

public class BasicBlock extends Block<BasicPage> {
	public static class Builder extends Block.Builder<BasicPage> {
		private BasicBlock block;
		
		public Builder() {
			setBlock(new BasicBlock());
		}
		
		protected Builder(BasicBlock block) {
			setBlock(new BasicBlock(block));
		}
		
		public Builder setManager(GreedySSDManager manager) {
			super.setManager(manager);
			block.manager = manager;
			return this;
		}
		
		@Override
		public BasicBlock build() {
			validate();
			return new BasicBlock(block);
		}

		protected void setBlock(BasicBlock block) {
			super.setBlock(block);
			this.block = block;
		}
		
		@Override
		protected void validate() {
			super.validate();
			Utils.validateNotNull(block.manager, "manager");
		}
	}
	
	private GreedySSDManager manager = null;

	protected BasicBlock() { }
	
	protected BasicBlock(BasicBlock other) {
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
		}
		return null;
	}

	public BasicBlock move(int lp) {
		int index = 0;
		for (BasicPage page : getPages()) {
			if (page.isClean()) {
				BasicPage.Builder builder = page.getSelfBuilder();
				builder.setClean(false).setLp(lp).setGC(true).setValid(true);
				return (BasicBlock) addValidPage(index, builder.build());
			}
			++index;
		}
		return null;
	}
	
	public BasicBlock writeLP(int lp) {
		int index = 0;
		for (BasicPage page : getPages()) {
			if (page.isClean()) {
				BasicPage.Builder builder = page.getSelfBuilder();
				builder.setClean(false).setLp(lp).setGC(false).setValid(true);
				return (BasicBlock) addValidPage(index, builder.build());
			}
			++index;
		}
		return null;
	}
}
