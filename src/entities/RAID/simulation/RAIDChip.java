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
package entities.RAID.simulation;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Triplet;

import entities.ActionLog;
import entities.Chip;
import entities.CleanAction;
import entities.RAID.RAIDBasicChip;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDChip extends RAIDBasicChip<RAIDPage, RAIDBlock, RAIDPlane> {
	public static class Builder extends RAIDBasicChip.Builder<RAIDPage, RAIDBlock, RAIDPlane> {
		private RAIDChip chip;
		
		public Builder () {
			setChip(new RAIDChip());
		}
		
		public Builder (RAIDChip chip) {
			setChip(new RAIDChip(chip));
		}
		
		public RAIDChip build() {
			return new RAIDChip(chip);
		}
		
		protected void setChip(RAIDChip chip) {
			super.setChip(chip);
			this.chip = chip;
		}
	}
	
	protected RAIDChip() {}	
	
	protected RAIDChip(RAIDChip other) {
		super(other);
	}
	
	RAIDChip setPlane(RAIDPlane plane, int index) {
		List<RAIDPlane> newPlanesList = getNewPlanesList();
		newPlanesList.set(index, plane);
		Builder builder = getSelfBuilder();
		builder.setPlanes(newPlanesList);
		return builder.build();
	}

	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	public Triplet<RAIDChip, Integer, Integer> cleanRAID(int chipIndex) {
		List<RAIDPlane> cleanPlanes = new ArrayList<RAIDPlane>(getPlanesNum());
		int dataMoved = 0;
		int parityMoved = 0;
		int i = 0;
		boolean cleaningInvoked = false;
		for (RAIDPlane plane : getPlanes()) {
			Triplet<RAIDPlane, Integer, Integer> clean = plane.cleanRAID();
			if (clean == null){
				cleanPlanes.add(plane);
			} else {
				cleaningInvoked = true;
				dataMoved += clean.getValue1();
				parityMoved += clean.getValue2();
				ActionLog.addAction(new CleanAction(chipIndex, i, clean.getValue1()));
				cleanPlanes.add(clean.getValue0());
			}
			i++;
		}
		if(!cleaningInvoked){
			return null;
		}
		Builder builder = getSelfBuilder();
		builder.setPlanes(cleanPlanes).setTotalGCInvocations(getTotalGCInvocations() + 1);
		return new Triplet<RAIDChip, Integer, Integer>(builder.build(), dataMoved, parityMoved);
	}

	public Chip<RAIDPage,RAIDBlock,RAIDPlane> writePP(int stripe, int parityNum) {
		int index = getMinValidCountPlaneIndex();
		return setPlane((RAIDPlane) getPlane(index).writePP(stripe, parityNum), index);
	}
}