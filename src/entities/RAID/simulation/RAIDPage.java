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

import java.awt.Color;
import java.util.List;

import org.javatuples.Triplet;

import manager.RAIDSSDManager;
import utils.Utils;
import entities.RAID.RAIDBasicPage;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDPage extends RAIDBasicPage {
	public static class Builder extends RAIDBasicPage.Builder {
		private RAIDPage page;
		
		public Builder() {
			setPage(new RAIDPage());
		}
		
		Builder(RAIDPage page) {
			setPage(new RAIDPage(page));
		}

		protected void setPage(RAIDPage page) {
			super.setPage(page);
			this.page = page;
		}

		public Builder setManager(RAIDSSDManager manager) {
			page.manager = manager;
			return this;
		}
		
		public RAIDPage build() {
			validate();
			return new RAIDPage(page);
		}
		
		protected void validate() {
			Utils.validateNotNull(page.manager, "manager");
		}
	}
	
	private RAIDSSDManager manager = null;
	
	RAIDPage() {}
	
	RAIDPage(RAIDPage other) {
		super(other);
		manager = other.manager;
	}

	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	@Override
	public Color getBGColor() {
		if (isClean()) {
			return manager.getCleanColor();
		}
		else if (getParityNumber() <= 0) {
			return manager.getDataPageColor();
		}
		return manager.getParityPageColor(getParityNumber());
	}
	
	@Override
	public Color getStripeFrameColor() {
		boolean isStripeHighlighted = false;
		if (highlightedStripes == null) {
			return super.getStripeColor();
		}
		try {
			for (Triplet<Integer, List<Integer>, List<Integer>> triplet : highlightedStripes) {
					if (triplet.getValue0() == this.getStripe()) {
						isStripeHighlighted = true;
						break;
					}
			}
		} catch (java.util.ConcurrentModificationException | java.util.NoSuchElementException | NullPointerException e) {
			// A known bug. More details in the programmer guide.
		}
		if (isStripeHighlighted == false) {
			return super.getStripeColor();
		}
		return manager.getStripeFrameColor(getStripe());
	}
}
