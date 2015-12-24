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

import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.javatuples.Pair;

import entities.BlockStatus;
import entities.RAID.RAIDBasicDevice;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDVisualizationDevice extends RAIDBasicDevice<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip> {
	public static class Builder extends RAIDBasicDevice.Builder<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip> {
		private RAIDVisualizationDevice device;		

		public Builder() {
			setDevice(new RAIDVisualizationDevice());
		}
		
		public Builder(RAIDVisualizationDevice device) {
			setDevice(new RAIDVisualizationDevice(device));
		}
		
		@Override
		public RAIDVisualizationDevice build() {
			validate();
			return new RAIDVisualizationDevice(device);
		}
		
		protected void setDevice(RAIDVisualizationDevice device) {
			super.setDevice(device);
			this.device = device;
		}
	}
	
	protected RAIDVisualizationDevice() {}
	
	protected RAIDVisualizationDevice(RAIDVisualizationDevice other) {
		super(other);
	}
	
	public RAIDVisualizationDevice setChip(RAIDVisualizationChip chip, int chipIndex) {
		Builder deviceBuilder = getSelfBuilder();
		List<RAIDVisualizationChip> newChipsList = getNewChipsList();
		newChipsList.set(chipIndex, chip);
		deviceBuilder.setChips(newChipsList);
		return deviceBuilder.build();
	}
	
	public RAIDVisualizationDevice moveData(Quartet<Integer, Integer, Integer, Integer> pageIndex, int lp, int stripe, boolean toHighlight) {
		RAIDVisualizationDevice device = writeOnPage(pageIndex, lp, 0, stripe, true, toHighlight);
		Builder deviceBuilder = device.getSelfBuilder();
		deviceBuilder.setTotalMoved(getTotalMoved()+1);
		return deviceBuilder.build();
	}
	
	public RAIDVisualizationDevice moveParity(Quartet<Integer, Integer, Integer, Integer> pageIndex, Pair<Integer, Integer> parityAddress, int stripe, boolean toHighlight) {
		if (parityAddress.getValue0() != stripe) {
			throw new IllegalArgumentException("The stripe in logical parity page adress and in <stripe> must be the same!");
		}
		RAIDVisualizationDevice device = writeOnPage(pageIndex, -1, parityAddress.getValue1(), stripe, true, toHighlight);
		Builder deviceBuilder = device.getSelfBuilder();
		deviceBuilder.setTotalMoved(getTotalMoved()+1);
		return deviceBuilder.build();
	}
	
	public RAIDVisualizationDevice writeData(Quartet<Integer, Integer, Integer, Integer> pageIndex, int lp, int stripe, boolean toHighlight) {
		RAIDVisualizationDevice device = writeOnPage(pageIndex, lp, 0, stripe, false, toHighlight);
		Builder deviceBuilder = device.getSelfBuilder();
		deviceBuilder.setTotalDataWritten(getTotalDataWritten()+1);
		return deviceBuilder.build();
	}
	
	public RAIDVisualizationDevice writeParity(Quartet<Integer, Integer, Integer, Integer> pageIndex, Pair<Integer, Integer> parityAddress, int stripe, boolean toHighlight) {
		if (parityAddress.getValue0() != stripe) {
			throw new IllegalArgumentException("The stripe in logical parity page adress and in <stripe> must be the same!");
		}
		RAIDVisualizationDevice device = writeOnPage(pageIndex, -1, parityAddress.getValue1(), stripe, false, toHighlight);
		Builder deviceBuilder = device.getSelfBuilder();
		deviceBuilder.setTotalParityWritten(getTotalParityWritten()+1);
		return deviceBuilder.build();
	}
	
	private RAIDVisualizationDevice writeOnPage(Quartet<Integer, Integer, Integer, Integer> pageIndex, int lp, int parityNumber, int stripe, boolean isGC, boolean toHighlight) {
		//int chipIndex = getChipIndex(lp);
		RAIDVisualizationChip chip = getChip(pageIndex.getValue0());
		RAIDVisualizationPlane plane = chip.getPlane(pageIndex.getValue1());
		RAIDVisualizationBlock block = plane.getBlock(pageIndex.getValue2());
		
		RAIDVisualizationPage.Builder builder = block.getPage(pageIndex.getValue3()).getSelfBuilder();
		builder.setLp(lp)
				.setGC(isGC)
				.setValid(true)
				.setClean(false);
		builder.setStripe(stripe)
				.setParityNumber(parityNumber)
				.setIsHighlighted(toHighlight);
		
		block = block.setPage(builder.build(), pageIndex.getValue3());
		plane = plane.setBlock(block, pageIndex.getValue2());
		chip = chip.setPlane(plane, pageIndex.getValue1());
		return setChip(chip, pageIndex.getValue0());
	}
	
	public RAIDVisualizationDevice changeStatus(Triplet<Integer, Integer, Integer> blockIndex, BlockStatus status) {
		RAIDVisualizationChip chip = getChip(blockIndex.getValue0());
		Pair<Integer, Integer> blockInChip = new Pair<Integer, Integer>(blockIndex.getValue1(), blockIndex.getValue2());
		return setChip(chip.changeStatus(blockInChip, status), blockIndex.getValue0());
	}
	
	public RAIDVisualizationDevice changeGC(Triplet<Integer, Integer, Integer> blockIndex, boolean isInGC) {
		RAIDVisualizationChip chip = getChip(blockIndex.getValue0());
		Pair<Integer, Integer> blockInChip = new Pair<Integer, Integer>(blockIndex.getValue1(), blockIndex.getValue2());
		return setChip(chip.changeGC(blockInChip, isInGC), blockIndex.getValue0());
	}
	
	public RAIDVisualizationDevice eraseBlock(Triplet<Integer, Integer, Integer> blockIndex) {
		RAIDVisualizationChip chip = getChip(blockIndex.getValue0());
		Pair<Integer, Integer> blockInChip = new Pair<Integer, Integer>(blockIndex.getValue1(), blockIndex.getValue2());
		return setChip(chip.eraseBlock(blockInChip), blockIndex.getValue0());
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}
}