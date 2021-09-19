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
package entities;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import utils.Utils.*;


/**
 * @author Roman
 * 
 * Chip is ordered collection of planes
 * which is suppose to share the load equally between the planes it got.
 * Also immutable
 * 
 * @param <P> - page type the block stores.
 * @param <B> - block type that the plane stores.
 * @param <T> - plane type that the chip stores.
 */
public abstract class Chip<T extends Plane<?>> {
	public abstract static class Builder<T extends Plane<?>> {
		private Chip<T> chip;

		abstract public Chip<T> build();
		
		public Builder<T> setPlanes(List<T> planesList) {
			chip.planesList = new ArrayList<T>(planesList);
			return this;
		}
		
		public Builder<T> setTotalWritten(int totalWritten) {
			chip.totalWritten = totalWritten;
			return this;
		}

		public Builder<T> setTotalGCInvocations(int number) {
			chip.totalGCInvocations = number;
			return this;
		}
		
		protected void setChip(Chip<T> chip) {
			this.chip = chip;
		}
	}
	
	private List<T> planesList;
	private int totalWritten = 0;
	private int totalGCInvocations = 0;
	
	protected Chip() {}	
	
	protected Chip(Chip<T> other) {
		this.planesList = new ArrayList<T>(other.planesList);
		this.totalWritten = other.getTotalWritten();
		this.totalGCInvocations = other.totalGCInvocations;
	}
	
	abstract public Builder<T> getSelfBuilder();

	public Iterable<T> getPlanes() {
		return planesList;
	}
	
	public T getPlane(int i) {
		return planesList.get(i);
	}
	
	public int getPlanesNum() {
		return planesList.size();
	}
	
	public int getBlocksInPlane() {
		if(planesList.isEmpty()) {
			return 0;
		}
		return planesList.get(0).getBlocksNum();
	}

	public List<T> getNewPlanesList() {
		return new ArrayList<T>(planesList);
	}
	
	@SuppressWarnings("unchecked")
	public Pair<? extends Chip<T>, Integer> clean(int chipIndex) {
		List<T> cleanPlanes = new ArrayList<T>(getPlanesNum());
		int moved = 0;
		int i = 0;
		boolean cleaningInvoked = false;
		for (T plane : getPlanes()) {
			Pair<? extends Plane<?>, Integer> clean = plane.clean();
			if (clean == null){
				cleanPlanes.add(plane);
			} else {
				cleaningInvoked = true;
				moved += clean.getValue1();
				ActionLog.addAction(new CleanAction(chipIndex, i, clean.getValue1()));
				cleanPlanes.add((T) clean.getValue0());
			}
			i++;
		}
		if(!cleaningInvoked){
			return null;
		}
		Builder<T> builder = getSelfBuilder();
		builder.setPlanes(cleanPlanes).setTotalGCInvocations(getTotalGCInvocations() + 1);
		return new Pair<Chip<T>, Integer>(builder.build(), moved);
	}
	
	@SuppressWarnings("unchecked")
	public Chip<T> invalidate(int lp) {
		List<T> updatedPlanes = new ArrayList<T>();
		for (T plane : getPlanes()) {
			updatedPlanes.add((T) plane.invalidate(lp));
		}
		Builder<T> builder = getSelfBuilder();
		builder.setPlanes(updatedPlanes);
		return builder.build();
	}

	@SuppressWarnings("unchecked")
	public Chip<T> writeLP(int lp, LpArgs lpArgs) {
		int index = getMinValidCountPlaneIndex();
		
		T newPlane = (T) getPlane(index).writeLP(lp, lpArgs);
		List<T> updatedPlanes = getNewPlanesList();
		updatedPlanes.set(index, newPlane);
		Builder<T> builder = getSelfBuilder();
		builder.setPlanes(updatedPlanes).setTotalWritten(getTotalWritten()  + 1);;
		return builder.build();
	}


	public int getNumOfClean() {
		int cleanBlocks = 0;
		for (Plane<?> plane : planesList){
			cleanBlocks += plane.getNumOfClean();
		}
		return cleanBlocks;
	}

	public int getTotalWritten() {
		return totalWritten;
	}

	public int getTotalGCInvocations() {
		return totalGCInvocations;
	}

	public EntityInfo getInfo() {
		EntityInfo result = new EntityInfo();

		result.add("Total logical pages written", Integer.toString(getTotalWritten()), 2);
		result.add("Clean blocks", Integer.toString(getNumOfClean()), 3);
		result.add("GC invocations", Integer.toString(getGCExecutions()), 4);
		result.add("Number of planes", Integer.toString(getPlanesNum()), 1);
		result.add("Block erasures", Integer.toString(getNumOfBlockErasures()), 3);

		return result;
	}

	public int getNumOfBlockErasures() {
		int numOfErasures = 0;
		for (Plane<?> plane : getPlanes()) {
			numOfErasures += plane.getNumOfBlockErasures();
		}
		return numOfErasures;
	}

	public int getGCExecutions() {
		return getTotalGCInvocations();
	}
	
	protected int getMinValidCountPlaneIndex() {
		int minIndex = 0;
		int minValue = Integer.MAX_VALUE;
		
		int planeIndex = 0;
		for (T plane : getPlanes()) {
			int temp = plane.getValidPagesCounter();
			if (temp < minValue) {
				minIndex = planeIndex;
				minValue = temp;
			}
			++planeIndex;
		}
		return minIndex;
	}
}
