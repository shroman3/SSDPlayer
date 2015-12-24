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

import java.awt.Color;
import java.awt.TexturePaint;

import utils.UIUtils;
import utils.Utils;


/**
 * @author Roman
 * November 2015: revised by Or Mauda for additional RAID functionality.
 * 
 * The most small measure in the player, Page is immutable entity.
 * It contains the basic info on what it contains: isClean, isValid, isGC, logical page.
 */
public abstract class Page {
	public static abstract class Builder {
		private Page page;
		
		abstract public Page build();
		
		public Builder setClean(boolean isClean) {
			page.isClean = isClean;
			return this;
		}
		
		public Builder setGC(boolean isGC) {
			page.isGC = isGC;
			return this;
		}
		
		public Builder setValid(boolean isValid) {
			page.isValid = isValid;
			return this;
		}

		public Builder setInvalidLp() {
			page.lp = -1;
			return this;
		}
		
		public Builder setLp(int lp) {
			Utils.validateNotNegative(lp, "logical page");
			page.lp = lp;
			return this;
		}
		
		protected void setPage(Page page) {
			this.page = page;
		}
	}
	
	private int lp = -1;
	private boolean isClean = true;
	private boolean isValid = false;
	private boolean isGC = false;
	
	protected Page() {}
	
	protected Page(Page other) {
		isClean = other.isClean;
		isValid = other.isValid;
		isGC = other.isGC;
		lp = other.lp;
	}


	abstract public Color getBGColor();
	abstract public Builder getSelfBuilder();

	public boolean isClean() {
		return isClean;
	}

	public boolean isGC() {
		return isGC;
	}

	public int getLp() {
		return lp;
	}

	public boolean isValid() {
		return isValid;
	}

	public String getTitle() {
		if (isClean()) {
			return "";
		}
		return "" + lp;
	}
	
	public TexturePaint getPageTexture(Color color) {
		if (isGC()) {
			return UIUtils.getGCTexture(color);
		} 
		return null;
	}

	/**
	 * @return whether the page is highlighted.
	 */
	public boolean isHighlighted() {
		return false;
	}
	
	/**
	 * Gets the stripe color.
	 *
	 * @return the stripe color. This method is overridden in RAID pages.
	 */
	public Color getStripeColor() {
		return null;
	}
	
	/**
	 * Gets the stripe frame color.
	 * @return the stripe frame color. This method is overridden in RAID pages.
	 */
	public Color getStripeFrameColor() {
		return null;
	}
}
