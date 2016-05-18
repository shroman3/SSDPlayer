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
package entities.RAID;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import entities.Block;
import manager.RAIDBasicSSDManager;
import manager.SSDManager;

/**
 * 
 * @author Or Mauda
 * 
 */
public abstract class RAIDBasicBlock<P extends RAIDBasicPage> extends Block<P>{
	public static abstract class Builder<P extends RAIDBasicPage> extends Block.Builder<P> {
		public abstract RAIDBasicBlock<P> build();

		public Builder<P> setManager(RAIDBasicSSDManager<P, ?, ?, ?, ?> manager) {
			super.setManager((SSDManager<P, ?, ?, ?, ?>) manager);
			return this;
		}

	}
	
	protected RAIDBasicBlock() { }
	
	protected RAIDBasicBlock(RAIDBasicBlock<P> other) {
		super(other);
	}

	abstract public Builder<P> getSelfBuilder();
	
	/**
	 * @param lp - Logical Page to be invalidated
	 * @return new block with the Logical Page specified invalidated
	 * if doesn't contain the specified LP returns itself..
	 */
	@Override
	public Block<P> invalidate(int lp) {
		boolean wasFound = false;
		List<P> pages = new ArrayList<P>(getPagesNum());
		for (P page : getPages()) {
			if (page.isValid() && (page.getLp() == lp)) {
				page = invalidatePage(page);
				wasFound = true;
			}
			pages.add(page);
		}
		Builder<P> builder = getSelfBuilder();
		builder.setPagesList(pages);
		if (wasFound) {
			builder.setValidCounter(getValidCounter()-1);
		}
		return builder.build();
	}

	/**
	 * @param stripe - Logical Page's stripe (part of address)
	 * @param parityNumber - Logical Page's parity number (part of address)
	 * @return new block with the Logical Page specified invalidated
	 * if doesn't contain the specified LP returns itself..
	 */
	public Block<P> invalidate(int stripe, int parityNumber) {
		boolean wasFound = false;
		List<P> pages = new ArrayList<P>(getPagesNum());
		for (P page : getPages()) {
			if (page.isValid() && (page.getStripe() == stripe) && (page.getParityNumber() == parityNumber)) {
				page = invalidatePage(page);
				wasFound = true;
			}
			pages.add(page);
		}
		Builder<P> builder = getSelfBuilder();
		builder.setPagesList(pages);
		if (wasFound) {
			builder.setValidCounter(getValidCounter()-1);
		}
		return builder.build();
	}
	
	protected abstract P invalidatePage(P page);
	
	/**
	 * Checks if the page with parity number and stripe is highlighted.
	 *
	 * @param parityNumber the parity number
	 * @param stripe the stripe
	 * @return the pair [wasFound, isHighlighted] (isHighlighted matter only when wasFound==true)
	 */
	public Pair<Boolean, Boolean> isHighlighted(int parityNumber, int stripe) {
		for (P page : getPages()) {
			if (page.getParityNumber() == parityNumber && page.getStripe() == stripe) {
				if (page.isHighlighted() == true) {
					return new Pair<Boolean, Boolean>(true, true);
				} else {
					return new Pair<Boolean, Boolean>(true, false);
				}
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
		for (P page : getPages()) {
			if (page.getLp() == lp) {
				if (page.isHighlighted() == true) {
					return new Pair<Boolean, Boolean>(true, true);
				} else {
					return new Pair<Boolean, Boolean>(true, false);
				}
			}
		}
		return new Pair<Boolean, Boolean>(false, false);
	}
	
	/**
	 * Sets the highlight field of all pages with 'stripe' value.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param stripe the stripe to be highlighted or un-highlighted.
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated block.
	 */
	public Triplet<Integer, List<P>, RAIDBasicBlock<P>> setHighlightByPhysicalP(boolean toHighlight, int stripe) {
		List<P> stripePages = new ArrayList<P>(0);
		List<P> blockPages = new ArrayList<P>(getPagesNum());
		Triplet<Integer, List<P>, RAIDBasicBlock<P>> details = new Triplet<Integer, List<P>, RAIDBasicBlock<P>>(-1, null, null); 
		for (P page : getPages()) {
			if (page.getStripe() == stripe) {
				details = details.setAt0(stripe);
				page = setHighlightedPage(page, toHighlight);
				stripePages.add(page);
			}
			blockPages.add(page);
		}
		Builder<P> builder = getSelfBuilder();
		builder.setPagesList(blockPages);
		details = details.setAt2(builder.build());
		details = details.setAt1(stripePages);
		return details;
	}
	
	/**
	 * Sets the highlight field of all pages in this block with the same stripe as the page with lp.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param lp the lp
	 * @param stripe the stripe to be highlighted or un-highlighted. IMPORTANT: set to -1 if unknown
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated block.
	 */
	public Triplet<Integer, List<P>, RAIDBasicBlock<P>> setHighlightByLogicalP(boolean toHighlight, int lp, int stripe) {
		boolean wasFound = false;
		List<P> stripePages = new ArrayList<P>(0);
		List<P> blockPages = new ArrayList<P>(getPagesNum());
		Triplet<Integer,List<P>,RAIDBasicBlock<P>> details = new Triplet<Integer, List<P>, RAIDBasicBlock<P>>(-1, null, null);
		for (P page : getPages()) {
			if (wasFound == false) {
				if (page.getLp() == lp || (page.getStripe() == stripe && stripe != -1)) {
					wasFound = true;
					details = details.setAt0(page.getStripe());
					page = setHighlightedPage(page, toHighlight);
					stripePages.add(page);
				}
			} else {				
				if ((page.getStripe() == details.getValue0()) || (page.getStripe() == stripe && stripe != -1)) {
					page = setHighlightedPage(page, toHighlight);
					stripePages.add(page);
				}
			}
			blockPages.add(page);
		}
		Builder<P> builder = getSelfBuilder();
		builder.setPagesList(blockPages);
		details = details.setAt2(builder.build());
		details = details.setAt1(stripePages);
		return details;
	}
	
	/**
	 * Sets the highlight field of all pages in this block with the same stripe as the given parity page.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param parityNumber the parityNumber
	 * @param stripe the stripe to be highlighted or un-highlighted.
	 * @param parityMatters - if it's false, then highlight this stripe and ignore the parity. otherwise, don't ignore the parity. 
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated block.
	 */
	public Triplet<Integer, List<P>, RAIDBasicBlock<P>> setHighlightByParityP(boolean toHighlight, int parityNumber, int stripe, boolean parityMatters) {
		List<P> stripePages = new ArrayList<P>(0);
		List<P> blockPages = new ArrayList<P>(getPagesNum());
		Triplet<Integer,List<P>,RAIDBasicBlock<P>> details = new Triplet<Integer, List<P>, RAIDBasicBlock<P>>(-1, null, null);
		for (P page : getPages()) {
			if (page.getStripe() == stripe) {
				if (parityMatters == false || (parityMatters == true && parityNumber == page.getParityNumber())
						|| page.getParityNumber() == 0)
					details = details.setAt0(page.getStripe());
					page = setHighlightedPage(page, toHighlight);
					stripePages.add(page);
			}
			blockPages.add(page);
		}
		Builder<P> builder = getSelfBuilder();
		builder.setPagesList(blockPages);
		details = details.setAt2(builder.build());
		details = details.setAt1(stripePages);
		return details;
	}
	
	@SuppressWarnings("unchecked")
	private P setHighlightedPage(P page, boolean toHighlight) {
		RAIDBasicPage.Builder builder = (entities.RAID.RAIDBasicPage.Builder) page.getSelfBuilder();
		builder.setIsHighlighted(toHighlight);
		return (P) builder.build();
	}
}
