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

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Triplet;

import entities.Page;
import general.Consts;

/**
 * 
 * @author Or Mauda
 *
 */
public abstract class RAIDBasicPage extends Page {
	public static abstract class Builder extends Page.Builder {
		private RAIDBasicPage page;
		
		public abstract RAIDBasicPage build();

		protected void setPage(RAIDBasicPage page) {
			super.setPage(page);
			this.page = page;
		}
		
		public Builder setParityNumber(int parityNum) {
			page.parityNumber = parityNum;
			return this;
		}
		
		public Builder setStripe(int stripe) {
			page.stripe = stripe;
			return this;
		}
		
		public Builder setIsHighlighted(boolean isHighlighted) {
			page.isHighlighted = isHighlighted;
			return this;
		}
		
		protected void validate() {
		}
	}
	
	private boolean isHighlighted = false;
	
	/** The parity number. 
	 *  parityNumber equals 0 means the page is a data page. -1 means no parity
	 *  */
	private int parityNumber = -1;
	
	/** The stripe number.
	 * 	in RAID - must be a positive number or zero. */
	private int stripe = -1;
	
	/** The highlighted stripes - a list of the currently highlighted stripes.
	 *  Nodes are: [stripe, highlightedDataLogicalPage, highlightedParityPagesNumbers] (used in RAID) */
	protected static LinkedList<Triplet<Integer, List<Integer>, List<Integer>>> highlightedStripes;
	
	protected RAIDBasicPage() {}
	
	protected RAIDBasicPage(RAIDBasicPage other) {
		super(other);
		parityNumber = other.parityNumber;
		isHighlighted = other.isHighlighted;
		stripe = other.stripe;
		updateHighlightedStripes();
	}
	
	public static void resetHighlights() {
		highlightedStripes = null;
	}
	
	/**
	 * Update the highlighted stripes static list.
	 */
	private void updateHighlightedStripes() {
		if (isClean()) {
			return;
		}
		if (highlightedStripes == null) {
			highlightedStripes = new LinkedList<Triplet<Integer, List<Integer>, List<Integer>>>();
		}
		boolean isStripeHighlighted = false;
		Triplet<Integer, List<Integer>, List<Integer>> stripeNode = null;
		for (Triplet<Integer, List<Integer>, List<Integer>> triplet : highlightedStripes) {
			if (triplet.getValue0() == stripe) {
				isStripeHighlighted = true;
				stripeNode = triplet;
				break;
			}
		}
		if (stripeNode == null) {
			stripeNode = new Triplet<Integer, List<Integer>, List<Integer>>(-1, new LinkedList<Integer>(), new LinkedList<Integer>());
		}
		
		if (parityNumber == 0) { // means it's a data page
			updateHighlightedStripesWithDataPage(stripeNode, isStripeHighlighted, getLp());
		} else {
			updateHighlightedStripesWithParityPage(stripeNode, isStripeHighlighted, parityNumber);
		}
	}
	
	/**
	 * Update highlighted stripes static list using a data page.
	 *
	 * @param stripeNode the stripe node in the static list highlightedStripes
	 * @param isStripeHighlighted - true if the stripe of this page is highlighted, otherwise false
	 * @param lp the lp
	 */
	private void updateHighlightedStripesWithDataPage(Triplet<Integer, List<Integer>, List<Integer>> stripeNode, boolean isStripeHighlighted, int lp) {
		if (stripeNode.getValue2() == null) {
			stripeNode.setAt2(new LinkedList<Integer>());
		}
		
		int stripeIndex = highlightedStripes.indexOf(stripeNode);
			
		List<Integer> newlogicalPages = new LinkedList<Integer>();
		if (isHighlighted == true) {
			if (isStripeHighlighted == false) { // means this page is highlighted, but its stripe is not highlighted. We should light this stripe for the first time!
				newlogicalPages.add(lp);
				highlightedStripes.addLast(new Triplet<Integer, List<Integer>, List<Integer>>(stripe, newlogicalPages, stripeNode.getValue2()));
			} else { // means this page is highlighted, and its stripe is also highlighted. We should add this page to highlightedStripes.
				if (highlightedStripes.get(stripeIndex).getValue1().contains(lp) == false) {
					highlightedStripes.remove(stripeIndex);
					newlogicalPages = stripeNode.getValue1();
					if (stripeNode.getValue1().contains(lp) == false) {
						newlogicalPages.add(lp);
					}
					highlightedStripes.add(stripeIndex, new Triplet<Integer, List<Integer>, List<Integer>>(stripe, newlogicalPages, stripeNode.getValue2()));
				}
			}
		} else if (isStripeHighlighted == true) { // means this page is not highlighted, but its stripe is highlighted.
			
			if (stripeNode.getValue1().contains(lp) == true) { // means this page is already in the highlightedStripes, but isHighlighted == false. Lets un-highlight it.
				highlightedStripes.remove(stripeIndex);
				if (stripeNode.getValue1().size() > 1) { // if it's not the last one, remove the page from the stripe in highlightedStripes
					newlogicalPages = stripeNode.getValue1();
					newlogicalPages.remove(newlogicalPages.indexOf(lp));
					highlightedStripes.add(stripeIndex, new Triplet<Integer, List<Integer>, List<Integer>>(stripe, newlogicalPages, stripeNode.getValue2()));
				}
			} else { // means this page is not in the highlightedStripes, and isHighlighted == false. Lets highlight it (because its stripe is highlighted)!
				isHighlighted = true;
				highlightedStripes.remove(stripeIndex);
				newlogicalPages = stripeNode.getValue1();
				newlogicalPages.add(lp);
				highlightedStripes.add(stripeIndex, new Triplet<Integer, List<Integer>, List<Integer>>(stripe, newlogicalPages, stripeNode.getValue2()));
			}
		}
	}
	
	/**
	 * Update highlighted stripes static list using a parity page.
	 *
	 * @param stripeNode the stripe node in the static list highlightedStripes
	 * @param isStripeHighlighted - true if the stripe of this page is highlighted, otherwise false
	 * @param parityNumber the parity number
	 */
	private void updateHighlightedStripesWithParityPage(Triplet<Integer, List<Integer>, List<Integer>> stripeNode, boolean isStripeHighlighted, int parityNumber) {
		if (stripeNode.getValue1() == null) {
			stripeNode.setAt1(new LinkedList<Integer>());
		}

		int stripeIndex = highlightedStripes.indexOf(stripeNode);
		
		List<Integer> newParityNumbers = new LinkedList<Integer>();
		if (isHighlighted == true) {
			if (isStripeHighlighted == false) { // means this page is highlighted, but its stripe is not highlighted. We should light this stripe for the first time!
				newParityNumbers.add(parityNumber);
				highlightedStripes.addLast(new Triplet<Integer, List<Integer>, List<Integer>>(stripe, stripeNode.getValue1(), newParityNumbers));
			} else { // means this page is highlighted, and its stripe is also highlighted. We should add this page to highlightedStripes.
				if (highlightedStripes.get(stripeIndex).getValue2().contains(parityNumber) == false) {
					highlightedStripes.remove(stripeIndex);
					newParityNumbers = stripeNode.getValue2();
					if (stripeNode.getValue2().contains(parityNumber) == false) {
						newParityNumbers.add(parityNumber);
					}
					highlightedStripes.add(stripeIndex, new Triplet<Integer, List<Integer>, List<Integer>>(stripe, stripeNode.getValue1(), newParityNumbers));
				}
			}
		} else if (isStripeHighlighted == true) { // means this page is not highlighted, but its stripe is highlighted.
			
			if (stripeNode.getValue2().contains(parityNumber) == true) { // means this page is already in the highlightedStripes, but isHighlighted == false. Lets un-highlight it.
				highlightedStripes.remove(stripeIndex);
				if (stripeNode.getValue2().size() > 1) { // if it's not the last one, remove the page from the stripe in highlightedStripes
					newParityNumbers = stripeNode.getValue2();
					newParityNumbers.remove(newParityNumbers.indexOf(parityNumber));
					highlightedStripes.add(stripeIndex, new Triplet<Integer, List<Integer>, List<Integer>>(stripe, stripeNode.getValue1(), newParityNumbers));
				}
			} else { // means this page is not in the highlightedStripes, and isHighlighted == false. Lets highlight it (because its stripe is highlighted)!
				isHighlighted = true;
				highlightedStripes.remove(stripeIndex);
				newParityNumbers = stripeNode.getValue2();
				newParityNumbers.add(parityNumber);
				highlightedStripes.add(stripeIndex, new Triplet<Integer, List<Integer>, List<Integer>>(stripe, stripeNode.getValue1(), newParityNumbers));
			}
		}
	}

	public int getParityNumber() {
		return parityNumber;
	}
	
	public int getStripe() {
		return stripe;
	}
	
	public boolean isHighlighted() {
		return isHighlighted;
	}

	public String getTitle() {
		if (parityNumber > 0) {
			return "" + stripe;
		}
		return super.getTitle();
	}
	
	public Color getStripeFrameColor() {
		return Consts.Colors.PAGE_TEXT;
	}
}
