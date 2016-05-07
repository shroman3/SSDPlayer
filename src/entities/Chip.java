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
public abstract class Chip<P extends Page, B extends Block<P>, T extends Plane<P,B>> {
	public abstract static class Builder<P extends Page, B extends Block<P>, T extends Plane<P,B>> {
		private Chip<P,B,T> chip;

		abstract public Chip<P,B,T> build();
		
		public Builder<P,B,T> setPlanes(List<T> planesList) {
			chip.planesList = new ArrayList<T>(planesList);
			return this;
		}
		
		public Builder<P,B,T> setTotalWritten(int totalWritten) {
			chip.totalWritten = totalWritten;
			return this;
		}

		public Builder<P,B,T> setTotalGCInvocations(int number) {
			chip.totalGCInvocations = number;
			return this;
		}
		
		protected void setChip(Chip<P,B,T> chip) {
			this.chip = chip;
		}
	}
	
	private List<T> planesList;
	private int totalWritten = 0;
	private int totalGCInvocations = 0;
	
	protected Chip() {}	
	
	protected Chip(Chip<P,B,T> other) {
		this.planesList = new ArrayList<T>(other.planesList);
		this.totalWritten = other.getTotalWritten();
		this.totalGCInvocations = other.totalGCInvocations;
	}
	
	abstract public Builder<P,B,T> getSelfBuilder();

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
	public Pair<Chip<P, B, T>, Integer> clean(int chipIndex) {
		List<T> cleanPlanes = new ArrayList<T>();
		int moved = 0;
		int i = 0;
		for (T plane : getPlanes()) {
			Pair<? extends Plane<P, B>, Integer> clean = plane.clean();
			moved += clean.getValue1();
			if(moved > 0){
				ActionLog.addAction(new CleanAction(chipIndex, i, clean.getValue1()));
			}
			cleanPlanes.add((T) clean.getValue0());
			i++;
		}
		if(moved == 0){
			return new Pair<Chip<P, B, T>, Integer>(this, moved);
		}
		int gcInvocations = (moved > 0)? getTotalGCInvocations() + 1 : getTotalGCInvocations();
		Builder<P,B,T> builder = getSelfBuilder();
		builder.setPlanes(cleanPlanes).setTotalGCInvocations(gcInvocations);
		return new Pair<Chip<P, B, T>, Integer>(builder.build(), moved);
	}
	
	@SuppressWarnings("unchecked")
	public Chip<P, B, T> invalidate(int lp) {
		List<T> updatedPlanes = new ArrayList<T>();
		for (T plane : getPlanes()) {
			updatedPlanes.add((T) plane.invalidate(lp));
		}
		Builder<P,B,T> builder = getSelfBuilder();
		builder.setPlanes(updatedPlanes);
		return builder.build();
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
	
	@SuppressWarnings("unchecked")
	public Chip<P, B, T> writeLP(int lp, int arg) {
		int index = getMinValidCountPlaneIndex();
		
		T newPlane = (T) getPlane(index).writeLP(lp, arg);
		List<T> updatedPlanes = getNewPlanesList();
		updatedPlanes.set(index, newPlane);
		Builder<P, B, T> builder = getSelfBuilder();
		builder.setPlanes(updatedPlanes).setTotalWritten(getTotalWritten()  + 1);;
		return builder.build();
	}


	public int getNumOfClean() {
		int cleanBlocks = 0;
		for (Plane<?, ?> plane : planesList){
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
}