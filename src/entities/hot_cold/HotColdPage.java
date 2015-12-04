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
package entities.hot_cold;

import java.awt.Color;

import utils.Utils;
import manager.HotColdSSDManager;
import entities.Page;

public class HotColdPage extends Page {
	public static class Builder extends Page.Builder {
		private HotColdPage page;
		
		public Builder() {
			setPage(new HotColdPage());
		}
		
		Builder(HotColdPage page) {
			setPage(new HotColdPage(page));
		}

		protected void setPage(HotColdPage page) {
			super.setPage(page);
			this.page = page;
		}

		public Builder setTemperature(int temperature) {
			if(temperature <= 0 || temperature >10) {
				throw new IllegalArgumentException("Temperature should between 1 and 10, given: "+temperature);
			}
			page.temperature = temperature;
			return this;
		}

		public Builder setManager(HotColdSSDManager manager) {
			page.manager = manager;
			return this;
		}
		
		public HotColdPage build() {
			validate();
			return new HotColdPage(page);
		}
		
		protected void validate() {
			Utils.validateNotNull(page.manager, "manager");
		}
	}
	
	private HotColdSSDManager manager = null;
	private int temperature = -1;
	
	HotColdPage() {}
	
	HotColdPage(HotColdPage other) {
		super(other);
		temperature = other.temperature;
		manager = other.manager;
	}

	public int getTemperature() {
		return temperature;
	}
	
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	@Override
	public Color getBGColor() {
		if (isClean()) {
			return manager.getCleanColor();
		} 
		return manager.getTemperatureColor(temperature);
	}
}
