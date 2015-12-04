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

import utils.Utils;

/**
 * November 2015: revised by Or Mauda for additional RAID functionality.
 */
public abstract class Device<P extends Page, B extends Block<P>, T extends Plane<P,B>, C extends Chip<P,B,T>> {	
	public abstract static class Builder<P extends Page, B extends Block<P>, T extends Plane<P,B>, D extends Chip<P,B,T>> {
		private Device<P,B,T,D> device;

		abstract public Device<P,B,T,D> build();
		
		public Builder<P,B,T,D> setTotalMoved(int totalMoved) {
			device.totalMoved = totalMoved;
			return this;
		}
		
		public Builder<P,B,T,D> setTotalWritten(int totalWritten) {
			device.totalWritten = totalWritten;
			return this;
		}
		
		public Builder<P,B,T,D> setTotalDataWritten(int totalDataWritten) {
			device.totalDataWritten = totalDataWritten;
			device.totalWritten = device.totalWritten + 1;
			return this;
		}
		
		public Builder<P,B,T,D> setTotalParityWritten(int totalParityWritten) {
			device.totalParityWritten = totalParityWritten;
			device.totalWritten = device.totalWritten + 1;
			return this;
		}
		
		public Builder<P,B,T,D> setChips(List<D> chipsList) {
			device.chipsList = new ArrayList<D>(chipsList);
			return this;
		}
		
		protected void validate() {
			Utils.validateNotNull(device.chipsList, "Chips");
		}
		
		protected void setDevice(Device<P,B,T,D> device) {
			this.device = device;
		}
	}
	
	private List<C> chipsList = null;
	private int totalMoved = 0;
	private int totalWritten = 0;
	private int totalDataWritten = 0;
	private int totalParityWritten = 0;
	
	protected Device() {}	
	
	protected Device(Device<P,B,T,C> other) {
		this.chipsList = new ArrayList<C>(other.chipsList);
		this.totalMoved = other.totalMoved; 
		this.totalWritten = other.totalWritten;
		this.totalDataWritten = other.totalDataWritten;
		this.totalParityWritten = other.totalParityWritten;
	}

	abstract public Builder<P,B,T,C> getSelfBuilder();

	public Iterable<C> getChips() {
		return chipsList;
	}
	
	public C getChip(int i) {
		return chipsList.get(i);
	}

	public List<C> getNewChipsList() {
		return new ArrayList<C>(chipsList);
	}
	
	public int getTotalMoved() {
		return totalMoved;
	}
	
	public int getTotalWritten() {
		return totalWritten;
	}
	
	public int getTotalDataWritten() {
		return totalDataWritten;
	}
	
	public int getTotalParityWritten() {
		return totalParityWritten;
	}
	
	public int getChipsNum() {
		return chipsList.size();
	}
	
	@SuppressWarnings("unchecked")
	public Device<P,B,T,C> invokeCleaning() {
		int moved = 0;
		List<C> cleanChips = new ArrayList<C>(getChipsNum());
		for (C chip : getChips()) {
			Pair<Chip<P,B,T>,Integer> clean = chip.clean();
			moved += clean.getValue1();
			cleanChips.add((C) clean.getValue0());
		}
		Builder<P,B,T,C> builder = getSelfBuilder();
		builder.setChips(cleanChips).setTotalMoved(totalMoved + moved);
		return builder.build();
	}
	
	@SuppressWarnings("unchecked")
	public Device<P,B,T,C>  invalidate(int lp) {
		List<C> updatedChips = new ArrayList<C>();
		for (C chip : getChips()) {
			updatedChips.add((C) chip.invalidate(lp));
		}
		Builder<P,B,T,C>  builder = getSelfBuilder();
		builder.setChips(updatedChips);
		return builder.build();
	}
	
	@SuppressWarnings("unchecked")
	public Device<P,B,T,C>  invalidate(int stripe, int parityNumber) {
		List<C> updatedChips = new ArrayList<C>();
		for (C chip : getChips()) {
			updatedChips.add((C) chip.invalidate(stripe, parityNumber));
		}
		Builder<P,B,T,C>  builder = getSelfBuilder();
		builder.setChips(updatedChips);
		return builder.build();
	}
	
	/**
	 * Checks if the page with logical page 'lp' is highlighted.
	 *
	 * @param lp the logical page
	 * @return true, if is highlighted
	 */
	public boolean isPageHighlighted(int lp) {
		Pair<Boolean, Boolean> chipAnswer;
		for (C chip : getChips()) {
			chipAnswer = chip.isHighlighted(lp);
			if (chipAnswer.getValue0() == true && chipAnswer.getValue1() == true) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the page with parity number and stripe is highlighted.
	 *
	 * @param parityNumber the parity number
	 * @param stripe the stripe
	 * @return true, if is highlighted
	 */
	public boolean isPageHighlighted(int parityNumber, int stripe) {
		Pair<Boolean, Boolean> chipAnswer;
		for (C chip : getChips()) {
			chipAnswer = chip.isHighlighted(parityNumber, stripe);
			if (chipAnswer.getValue0() == true && chipAnswer.getValue1() == true) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Sets the highlight field of all pages with the same stripe as the page with index (chipIndex, planeIndex, blockIndex, pageIndex).
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param chipIndex the chip index
	 * @param planeIndex the plane index
	 * @param blockIndex the block index
	 * @param pageIndex the page index
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated device.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, Device<P,B,T,C>> setHighlightByPhysicalP(boolean toHighlight,int chipIndex, int planeIndex, int blockIndex, int pageIndex) {
		int stripe = getChip(chipIndex).getPlane(planeIndex).getBlock(blockIndex).getPage(pageIndex).getStripe();
		Triplet<Integer, List<P>, Device<P, B, T, C>> details = new Triplet<Integer, List<P>, Device<P, B, T, C>>(-1, null, null);
		List<P> stripePages = new ArrayList<P>(0);
		List<C> deviceChips = new ArrayList<C>();
		Triplet<Integer,List<P>,Chip<P,B,T>> chipDetails;
		for (C chip : getChips()) {
			chipDetails = chip.setHighlightByPhysicalP(toHighlight, stripe);
			if (chipDetails.getValue0() != -1) { // means we found a chip which includes a page on our stripe
				details = details.setAt0(chipDetails.getValue0());
				stripePages.addAll(chipDetails.getValue1()); // add this chip relevant pages
			}
			deviceChips.add((C) chipDetails.getValue2());
		}
		Builder<P,B,T,C>  builder = getSelfBuilder();
		builder.setChips(deviceChips);
		details = details.setAt2(builder.build());
		details = details.setAt1(stripePages);
		return details;
	}
	
	/**
	 * Sets the highlight field of all pages on the same stripe as the page with lp.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param lp the lp
	 * @param stripe the stripe to be highlighted or un-highlighted.
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated device.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, Device<P,B,T,C>> setHighlightByLogicalP(boolean toHighlight, int lp) {
		int stripe = -1; // means the stripe is still unknown
		List<P> pages = new ArrayList<P>(0);
		List<C> deviceChips = new ArrayList<C>();
		Triplet<Integer, List<P>, Device<P, B, T, C>> details = new Triplet<Integer, List<P>, Device<P, B, T, C>>(stripe, null, null);
		Triplet<Integer,List<P>,Chip<P,B,T>> chipDetails;
		for (C chip : getChips()) {
			chipDetails = chip.setHighlightByLogicalP(toHighlight, lp, stripe);
			if (chipDetails.getValue0() != -1) { // means we found a block which includes a chip on our stripe
				details = details.setAt0(chipDetails.getValue0()); // set our stripe
				pages.addAll(chipDetails.getValue1()); // add this chip relevant pages
				stripe = chipDetails.getValue0();
			}
			deviceChips.add((C) chipDetails.getValue2());
		}
		Builder<P,B,T,C>  builder = getSelfBuilder();
		builder.setChips(deviceChips);
		details = details.setAt2(builder.build());
		details = details.setAt1(pages);
		return details;
	}
	
	/**
	 * Sets the highlight field of all pages on the same stripe as the given parity page.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param parityNumber the parityNumber (Currently not relevant)
	 * @param stripe the stripe to be highlighted or un-highlighted.
	 * @param parityMatters - if it's false, then highlight this stripe and ignore the parity. otherwise, don't ignore the parity. 
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated device.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, Device<P,B,T,C>> setHighlightByParityP(boolean toHighlight, int parityNumber, int stripe, boolean parityMatters) {
		List<P> pages = new ArrayList<P>(0);
		List<C> deviceChips = new ArrayList<C>();
		Triplet<Integer, List<P>, Device<P, B, T, C>> details = new Triplet<Integer, List<P>, Device<P, B, T, C>>(-1, null, null);
		Triplet<Integer,List<P>,Chip<P,B,T>> chipDetails;
		for (C chip : getChips()) {
			chipDetails = chip.setHighlightByParityP(toHighlight, parityNumber, stripe, parityMatters);
			if (chipDetails.getValue0() == stripe) { // means we found a chip which includes a page on our stripe
				details = details.setAt0(chipDetails.getValue0()); // set our stripe
				pages.addAll(chipDetails.getValue1()); // add this chip relevant pages
				//stripe = chipDetails.getValue0();
			}
			deviceChips.add((C) chipDetails.getValue2());
		}
		Builder<P,B,T,C>  builder = getSelfBuilder();
		builder.setChips(deviceChips);
		details = details.setAt2(builder.build());
		details = details.setAt1(pages);
		return details;
	}

	protected int getChipIndex(int lp) {
		return lp%getChipsNum();
	}
	
	@SuppressWarnings("unchecked")
	public Device<P,B,T,C> writeLP(int lp, int arg) {
		int chipIndex = getChipIndex(lp);
		List<C> updatedChips = getNewChipsList();
		updatedChips.set(chipIndex, (C) getChip(chipIndex).writeLP(lp, arg));
		Builder<P, B, T, C> builder = getSelfBuilder();
		builder.setChips(updatedChips).setTotalWritten(totalWritten + 1);
		return builder.build();
	}
}
