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
package entities.reusable_visualization;

import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import entities.BlockStatus;
import entities.Device;


public class VisualizationDevice extends Device<VisualizationPage, VisualizationBlock, VisualizationPlane, VisualizationChip> {
	public static class Builder extends Device.Builder<VisualizationPage, VisualizationBlock, VisualizationPlane, VisualizationChip> {
		private VisualizationDevice device;		

		public Builder() {
			setDevice(new VisualizationDevice());
		}
		
		public Builder(VisualizationDevice device) {
			setDevice(new VisualizationDevice(device));
		}
		
		@Override
		public VisualizationDevice build() {
			validate();
			return new VisualizationDevice(device);
		}
		
		protected void setDevice(VisualizationDevice device) {
			super.setDevice(device);
			this.device = device;
		}
	}
	
	protected VisualizationDevice() {}
	
	protected VisualizationDevice(VisualizationDevice other) {
		super(other);
	}
	
	public VisualizationDevice setChip(VisualizationChip chip, int chipIndex) {
		Builder deviceBuilder = getSelfBuilder();
		List<VisualizationChip> newChipsList = getNewChipsList();
		newChipsList.set(chipIndex, chip);
		deviceBuilder.setChips(newChipsList);
		return deviceBuilder.build();
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}
	
	public int getLPWriteLevel(VisualizationDevice device, int lp) {
		VisualizationChip chip = getChip(getChipIndex(lp));
		return chip.getLPWriteLevel(lp);
	}

	public int getPageWriteLevel(int lp, VisualizationDevice device, Triplet<Integer, Integer, Integer> sourceIndex) {
		VisualizationChip chip = getChip(getChipIndex(lp));
		return chip.getPageWriteLevel(lp, device, sourceIndex);
	}

	public VisualizationDevice invalidatePage(int lp, Triplet<Integer, Integer, Integer> sourceIndex) {
		int chipIndex = getChipIndex(lp);
		VisualizationChip chip = getChip(chipIndex);
		return setChip(chip.invalidatePage(sourceIndex), chipIndex);
	}

	public VisualizationDevice partialMove(Triplet<Integer, Integer, Integer> pageIndex, int lp, int writeLevel) {
		return writeOnPage(pageIndex, lp, writeLevel, true, true, writeLevel);
	}
	
	public VisualizationDevice firstMove(Triplet<Integer, Integer, Integer> pageIndex, int lp, int gcWriteLevel, boolean isPartial) {
		VisualizationDevice device = writeOnPage(pageIndex, lp, 1, true, isPartial, gcWriteLevel);
		Builder deviceBuilder = device.getSelfBuilder();
		deviceBuilder.setTotalMoved(getTotalMoved()+1);
		return deviceBuilder.build();	
	}
	
	public VisualizationDevice secondMove(Triplet<Integer, Integer, Integer> firstPageIndex,
			Triplet<Integer, Integer, Integer> secondPageIndex, int lp, int gcWriteLevel, boolean isPartial) {
		VisualizationDevice device = writeOnPage(firstPageIndex, lp, 2, true, isPartial, gcWriteLevel);
		device = writeOnPage(secondPageIndex, lp, 2, false, isPartial, gcWriteLevel);
		Builder deviceBuilder = device.getSelfBuilder();
		deviceBuilder.setTotalMoved(getTotalMoved()+1);
		return deviceBuilder.build();	
	}
	
	public VisualizationDevice firstWrite(Triplet<Integer, Integer, Integer> pageIndex, int lp) {
		VisualizationDevice device = writeOnPage(pageIndex, lp, 1, false, false, -1);
		Builder deviceBuilder = device.getSelfBuilder();
		deviceBuilder.setTotalWritten(getTotalWritten()+1);
		return deviceBuilder.build();
	}

	public VisualizationDevice secondWrite(Triplet<Integer, Integer, Integer> firstPageIndex,
			Triplet<Integer, Integer, Integer> secondPageIndex, int lp) {
		VisualizationDevice device = writeOnPage(firstPageIndex, lp, 2, false, false, -1);
		device = device.writeOnPage(secondPageIndex, lp, 2, false, false, -1);
		Builder deviceBuilder = device.getSelfBuilder();
		deviceBuilder.setTotalWritten(getTotalWritten()+1);
		return deviceBuilder.build();
	}
	
	private VisualizationDevice writeOnPage(Triplet<Integer, Integer, Integer> pageIndex, int lp, int writeLevel, boolean isGC, boolean isPartial, int gcWriteLevel) {
		int chipIndex = getChipIndex(lp);
		VisualizationChip chip = getChip(chipIndex);
		VisualizationPlane plane = chip.getPlane(pageIndex.getValue0());
		VisualizationBlock block = plane.getBlock(pageIndex.getValue1());
		
		VisualizationPage.Builder builder = block.getPage(pageIndex.getValue2()).getSelfBuilder();
		builder.setLp(lp)
				.setGC(isGC)
				.setValid(true)
				.setClean(false);
		builder.setWriteLevel(writeLevel)
				.setGcWriteLevel(gcWriteLevel)
				.setIsPartial(isPartial);
		
		block = block.setPage(builder.build(), pageIndex.getValue2());
		plane = plane.setBlock(block, pageIndex.getValue1());
		chip = chip.setPlane(plane, pageIndex.getValue0());
		return setChip(chip, chipIndex);
		
	}

	public VisualizationDevice changeStatus(Pair<Integer, Integer> blockIndex, BlockStatus status) {
		VisualizationChip chip = getChip(0);
		return setChip(chip.changeStatus(blockIndex, status), 0);
	}

	public VisualizationDevice changeGC(Pair<Integer, Integer> blockIndex, boolean isInGC) {
		VisualizationChip chip = getChip(0);
		return setChip(chip.changeGC(blockIndex, isInGC), 0);
	}

	public VisualizationDevice eraseBlock(Pair<Integer, Integer> blockIndex) {
		VisualizationChip chip = getChip(0);
		return setChip(chip.eraseBlock(blockIndex), 0);
	}
}
