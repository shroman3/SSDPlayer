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

import manager.ReusableVisualizationSSDManager;

import org.javatuples.Pair;

import utils.Utils;
import entities.Plane;

public class VisualizationPlane extends Plane<VisualizationPage, VisualizationBlock> {
	public static class Builder extends Plane.Builder<VisualizationPage, VisualizationBlock> {
		private VisualizationPlane plane;
		
		public Builder() {
			setPlane(new VisualizationPlane());
		}
		
		public Builder(VisualizationPlane plane) {
			setPlane(new VisualizationPlane(plane));
		}

		public VisualizationPlane build() {
			validate();
			return new VisualizationPlane(plane);
		}
		
		public Builder setManager(ReusableVisualizationSSDManager manager) {
			super.setManager(manager);
			plane.manager = manager;
			return this;
		}
		
		protected void setPlane(VisualizationPlane plane) {
			super.setPlane(plane);
			this.plane = plane;
		}
		
		protected void validate() {
			super.validate();
			Utils.validateNotNull(plane.manager, "manager");
		}
	}
	
	private ReusableVisualizationSSDManager manager;
	
	protected VisualizationPlane() {}
		

	protected VisualizationPlane(VisualizationPlane other) {
		super(other);
		this.manager = other.manager;
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	VisualizationPlane setBlock(VisualizationBlock block, int index) {
		List<VisualizationBlock> newBlocksList = getNewBlocksList();
		newBlocksList.set(index, block);
		Builder builder = getSelfBuilder();
		builder.setBlocks(newBlocksList);
		return builder.build();
	}

	@Override
	public Plane<VisualizationPage, VisualizationBlock> writeLP(int lp, int arg) {
		return null;
	}

	
	@Override
	protected Pair<? extends Plane<VisualizationPage, VisualizationBlock>, Integer> cleanPlane() {
		return null;
	}
}
