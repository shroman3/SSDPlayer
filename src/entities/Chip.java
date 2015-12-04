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
package entities;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;


/**
 * @author Roman
 * November 2015: revised by Or Mauda for additional RAID functionality.
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
		
		protected void setChip(Chip<P,B,T> chip) {
			this.chip = chip;
		}
	}
	
	private List<T> planesList;
	
	protected Chip() {}	
	
	protected Chip(Chip<P,B,T> other) {
		this.planesList = new ArrayList<T>(other.planesList);
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
	public Pair<Chip<P, B, T>, Integer> clean() {
		List<T> cleanPlanes = new ArrayList<T>();
		int moved = 0;
		for (T plane : getPlanes()) {
			Pair<? extends Plane<P, B>, Integer> clean = plane.clean();
			moved += clean.getValue1();
			cleanPlanes.add((T) clean.getValue0());
		}
		Builder<P,B,T> builder = getSelfBuilder();
		builder.setPlanes(cleanPlanes);
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
	
	@SuppressWarnings("unchecked")
	public Chip<P, B, T> invalidate(int stripe, int parityNumber) {
		List<T> updatedPlanes = new ArrayList<T>();
		for (T plane : getPlanes()) {
			updatedPlanes.add((T) plane.invalidate(stripe, parityNumber));
		}
		Builder<P,B,T> builder = getSelfBuilder();
		builder.setPlanes(updatedPlanes);
		return builder.build();
	}
	
	/**
	 * Checks if the page with parity number and stripe is highlighted.
	 *
	 * @param parityNumber the parity number
	 * @param stripe the stripe
	 * @return the pair [wasFound, isHighlighted] (isHighlighted matter only when wasFound==true)
	 */
	public Pair<Boolean,Boolean> isHighlighted(int parityNumber, int stripe) {
		Pair<Boolean, Boolean> planeAnswer;
		for (T plane : getPlanes()) {
			planeAnswer = plane.isHighlighted(parityNumber, stripe);
			if (planeAnswer.getValue0() == true) {
				return planeAnswer;
			}
		}
		return new Pair<Boolean, Boolean>(false, false);
	}
	
	/**
	 * Checks if the page with logical page 'lp' is highlighted.
	 *
	 * @param lp the logical page
	 * @return the pair [wasFound, isHighlighted] (isHighlighted matter only when wasFound==true)
	 */
	public Pair<Boolean, Boolean> isHighlighted(int lp) {
		Pair<Boolean, Boolean> planeAnswer;
		for (T plane : getPlanes()) {
			planeAnswer = plane.isHighlighted(lp);
			if (planeAnswer.getValue0() == true) {
				return planeAnswer;
			}
		}
		return new Pair<Boolean, Boolean>(false, false);
	}
	
	/**
	 * Sets the highlight field of all pages with 'stripe' value.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param stripe the stripe to be highlighted or un-highlighted.
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated chip.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, Chip<P, B, T>> setHighlightByPhysicalP(boolean toHighlight, int stripe) {
		List<P> pages = new ArrayList<P>(0);
		List<T> chipPlanes = new ArrayList<T>();
		Triplet<Integer, List<P>, Chip<P, B, T>> details = new Triplet<Integer, List<P>, Chip<P, B, T>>(-1, null, null);
		Triplet<Integer, List<P>, Plane<P,B>> planeDetails;
		for (T plane : getPlanes()) {
			planeDetails = plane.setHighlightByPhysicalP(toHighlight, stripe);
			if (planeDetails.getValue0() != -1) { // means we found a plane which includes a page on our stripe
				details = details.setAt0(planeDetails.getValue0()); // set our stripe
				pages.addAll(planeDetails.getValue1()); // add this plane relevant pages
			}
			chipPlanes.add((T) planeDetails.getValue2());
		}
		Builder<P,B,T> builder = getSelfBuilder();
		builder.setPlanes(chipPlanes);
		details = details.setAt2(builder.build());
		details = details.setAt1(pages);
		return details;
	}
	
	/**
	 * Sets the highlight field of all pages on the same stripe as the page with lp.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param lp the lp
	 * @param stripe the stripe to be highlighted or un-highlighted. IMPORTANT: set to -1 if unknown
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated chip.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, Chip<P, B, T>> setHighlightByLogicalP(boolean toHighlight, int lp, int stripe) {
		List<P> pages = new ArrayList<P>(0);
		List<T> chipPlanes = new ArrayList<T>();
		Triplet<Integer, List<P>, Chip<P, B, T>> details = new Triplet<Integer, List<P>, Chip<P, B, T>>(stripe, null, null);
		Triplet<Integer, List<P>, Plane<P,B>> planeDetails;
		for (T plane : getPlanes()) {
			planeDetails = plane.setHighlightByLogicalP(toHighlight, lp, stripe);
			if (planeDetails.getValue0() != -1) { // means we found a plane which includes a page on our stripe
				details = details.setAt0(planeDetails.getValue0()); // set our stripe
				pages.addAll(planeDetails.getValue1()); // add this plane relevant pages
				stripe = planeDetails.getValue0();
			}
			chipPlanes.add((T) planeDetails.getValue2());
		}
		Builder<P,B,T> builder = getSelfBuilder();
		builder.setPlanes(chipPlanes);
		details = details.setAt2(builder.build());
		details = details.setAt1(pages);
		return details;
	}
	
	/**
	 * Sets the highlight field of all pages on the same stripe as the given parity page.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param parityNumber the parityNumber
	 * @param stripe the stripe to be highlighted or un-highlighted.
	 * @param parityMatters - if it's false, then highlight this stripe and ignore the parity. otherwise, don't ignore the parity. 
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated chip.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, Chip<P, B, T>> setHighlightByParityP(boolean toHighlight, int parityNumber, int stripe, boolean parityMatters) {
		List<P> pages = new ArrayList<P>(0);
		List<T> chipPlanes = new ArrayList<T>();
		Triplet<Integer, List<P>, Chip<P, B, T>> details = new Triplet<Integer, List<P>, Chip<P, B, T>>(-1, null, null);
		Triplet<Integer, List<P>, Plane<P,B>> planeDetails;
		for (T plane : getPlanes()) {
			planeDetails = plane.setHighlightByParityP(toHighlight, parityNumber, stripe, parityMatters);
			if (planeDetails.getValue0() == stripe) { // means we found a plane which includes a page on our stripe
				details = details.setAt0(planeDetails.getValue0()); // set our stripe
				pages.addAll(planeDetails.getValue1()); // add this plane relevant pages
				//stripe = planeDetails.getValue0();
			}
			chipPlanes.add((T) planeDetails.getValue2());
		}
		Builder<P,B,T> builder = getSelfBuilder();
		builder.setPlanes(chipPlanes);
		details = details.setAt2(builder.build());
		details = details.setAt1(pages);
		return details;
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
		return setPlane(index, (T) getPlane(index).writeLP(lp, arg));
	}

	public Chip<P, B, T> setPlane(int index, T t) {
		List<T> updatedPlanes = getNewPlanesList();
		updatedPlanes.set(index, t);
		Builder<P, B, T> builder = getSelfBuilder();
		builder.setPlanes(updatedPlanes);
		return builder.build();
	}
	
}