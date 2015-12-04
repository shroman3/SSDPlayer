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

import general.Consts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import manager.RAIDSSDManager;
import manager.RAIDVisualizationSSDManager;
import manager.SSDManager;
import utils.Utils;

/**
 * @author Roman
 * November 2015: revised by Or Mauda for additional RAID functionality.
 *
 * Block basically is ordered collection of pages, but this entity stores more 
 * information like eraseCounter, block status, etc.
 * Also immutable
 * @param <P> - page type the block stores.
 */
public abstract class Block<P extends Page> {
	public static abstract class Builder<P extends Page> {
		private Block<P> block;
		
		public Builder<P> setEraseCounter(int eraseCounter) {
			block.eraseCounter = eraseCounter;
			return this;
		}

		public Builder<P> setStatus(BlockStatus status) {
			block.status = status;
			return this;
		}

		public Builder<P> setInGC(boolean inGC) {
			block.inGC = inGC;
			return this;
		}
		
		public Builder<P> setPagesList(List<P> pagesList) {
			block.pagesList = new ArrayList<P>(pagesList);
			return this;
		}
		
		public Builder<P> setValidCounter(int validCounter) {
			block.validCounter = validCounter;
			return this;
		}
		
		public Builder<P> setManager(SSDManager<P, ?, ?, ?, ?> manager) {
			block.manager = manager;
			return this;
		}
		
		public abstract Block<P> build();
		
		protected void validate() {
			Utils.validateNotNull(block.pagesList, "pagesList");
			Utils.validateNotNull(block.status, "status");
			Utils.validateNotNegative(block.eraseCounter, "erase Counter");
		}

		protected void setBlock(Block<P> block) {
			this.block = block;
		}
	}
	
	private List<P> pagesList;
	private int eraseCounter = -1;
	/**
	 * Number of logical valid pages stored in the block
	 */
	private int validCounter = 0;
	private BlockStatus status = null; 
	private boolean inGC = false;
	private SSDManager<P, ?, ?, ?, ?> manager = null;

	protected Block() { }
	
	protected Block(Block<P> other) {
		pagesList = new ArrayList<P>(other.pagesList);
		eraseCounter = other.eraseCounter;
		status = other.status;
		inGC = other.inGC;
		validCounter = other.validCounter;
		manager = other.manager;
	}

	abstract public Builder<P> getSelfBuilder();

	public Iterable<P> getPages() {
		return pagesList;
	}
	
	public P getPage(int i) {
		return pagesList.get(i);
	}
	
	public int getPagesNum() {
		return pagesList.size();
	}

	public int getEraseCounter() {
		return eraseCounter;
	}

	public int getValidCounter() {
		return validCounter;
	}

	public BlockStatus getStatus() {
		return status;
	}

	public boolean isInGC() {
		return inGC;
	}
	
	public List<P> getNewPagesList() {
		return new ArrayList<P>(pagesList);
	}

	public int getCleanPageIndex() {
		for (int i =0; i < pagesList.size()-1; ++i) {
			Page page = pagesList.get(i);
			if (page.isClean()) {
				return i;
			}
		}
		return -1;
	}

	public Color getBGColor() {
		return Consts.Colors.BG;
	}

	public String getStatusName() {
		return status.getDsiplayName();
	}

	public Color getStatusColor() {
		return status.getColor();
	}

	public Color getFrameColor() {
		return null;
	}
	
	/**
	 * @param lp - Logical Page to be invalidated
	 * @return new block with the Logical Page specified invalidated
	 * if doesn't contain the specified LP returns itself..
	 */
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
	public Triplet<Integer, List<P>, Block<P>> setHighlightByPhysicalP(boolean toHighlight, int stripe) {
		List<P> stripePages = new ArrayList<P>(0);
		List<P> blockPages = new ArrayList<P>(getPagesNum());
		Triplet<Integer, List<P>, Block<P>> details = new Triplet<Integer, List<P>, Block<P>>(-1, null, null); 
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
	public Triplet<Integer, List<P>, Block<P>> setHighlightByLogicalP(boolean toHighlight, int lp, int stripe) {
		boolean wasFound = false;
		List<P> stripePages = new ArrayList<P>(0);
		List<P> blockPages = new ArrayList<P>(getPagesNum());
		Triplet<Integer, List<P>, Block<P>> details = new Triplet<Integer, List<P>, Block<P>>(-1, null, null);
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
	public Triplet<Integer, List<P>, Block<P>> setHighlightByParityP(boolean toHighlight, int parityNumber, int stripe, boolean parityMatters) {
		List<P> stripePages = new ArrayList<P>(0);
		List<P> blockPages = new ArrayList<P>(getPagesNum());
		Triplet<Integer, List<P>, Block<P>> details = new Triplet<Integer, List<P>, Block<P>>(-1, null, null);
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

	/**
	 * @return new clean block with updated counters
	 */
	public Block<P> eraseBlock() {
		Builder<P> builder = getSelfBuilder();
		builder.setPagesList(getEmptyPages())
			.setEraseCounter(getEraseCounter() + 1)
			.setValidCounter(0)
			.setStatus(BlockStatusGeneral.CLEAN);
		return builder.build();
	}
	
	/**
	 * @return whether the block has free page to write on..
	 */
	public boolean hasRoomForWrite() {
		for (P page : getPages()) {
			if (page.isClean()) {
				return true;
			}
		}
		return false;
	}

	public Block<P> setStatus(BlockStatus status) {
		Builder<P> builder = getSelfBuilder();
		builder.setStatus(status);
		return builder.build();
	}
	
	/**
	 * @param index - to set the page
	 * @param page - the page to set
	 * @return new Block with page specified in the specified index, 
	 * with updated counters
	 */
	protected Block<P> addValidPage(int index, P page) {
		List<P> newPagesList = getNewPagesList();
		newPagesList.set(index, page);
		Builder<P> blockBuilder = getSelfBuilder();
		blockBuilder.setPagesList(newPagesList).setValidCounter(getValidCounter()+1);
		return blockBuilder.build();
	}
	
	//Using getSelfBuilder so the build should return the same type - P
	@SuppressWarnings("unchecked")
	private P invalidatePage(P page) {
		Page.Builder builder = page.getSelfBuilder();
		builder.setValid(false);
		
		
		Boolean showOldData, showOldParity;
		// if we're in RAID manager
		if (manager.getManagerName().toLowerCase().contains("raid") == true) {
			if (manager.getManagerName().toLowerCase().contains("raid simulation") == true) {
				// RAID Visualization (that's correct - visualization is simulation)
				showOldParity = ((RAIDVisualizationSSDManager) manager).toShowOldParity();
				showOldData = ((RAIDVisualizationSSDManager) manager).toShowOldData();
			} else {
				// RAID Simulation
				showOldParity = ((RAIDSSDManager) manager).toShowOldParity();
				showOldData = ((RAIDSSDManager) manager).toShowOldData();
			}
			
			if ((page.getParityNumber() <= 0 && showOldData == false) // we shouldn't highlight invalidated data pages
					|| (page.getParityNumber() > 0 && showOldParity == false)) { // we shouldn't highlight invalidated parity pages
				// when data and/or parity page is invalidated and it shouldn't be a part of the stripe
				builder.setStripe(-1);
				builder.setIsHighlighted(false);
			}
		}
		
		builder.setLp(-1);
		return (P) builder.build();
	}
	
	@SuppressWarnings("unchecked")
	private P setHighlightedPage(P page, boolean toHighlight) {
		Page.Builder builder = page.getSelfBuilder();
		builder.setIsHighlighted(toHighlight);
		return (P) builder.build();
	}
		
	private List<P> getEmptyPages() {
		List<P> pages = new ArrayList<P>(getPagesNum());
		P page = manager.getEmptyPage();
		for (int i = 0; i < getPagesNum(); i++) {
			pages.add(page);
		}	
		return pages;
	}
}
