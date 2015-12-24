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

import entities.RAID.simulation.RAID6Device;
import entities.RAID.simulation.RAIDDevice;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAID6Device extends RAIDDevice {
	
	public static class Builder extends RAIDDevice.Builder {
		private RAID6Device device;

		protected void setDevice(RAIDDevice device) {
			super.setDevice(device);
			this.device = (RAID6Device) device;
		}
		
		public Builder() {
			setDevice(new RAID6Device());
		}
		
		public Builder(RAID6Device device) {
			setDevice(new RAID6Device(device));
		}
		
		@Override
		public RAID6Device build() {
			validate();
			return new RAID6Device( (RAID6Device) device);
		}
	}
	
	protected RAID6Device() {}
	
	protected RAID6Device(RAID6Device other) {
		super(other);
	}
	
	/**
	 * Determines whether a parity page need to be updated. Default is always update, override to change.
	 *
	 * @param lp the logical page written.
	 * @param parityNumber the parity number of the parity page.
	 * @return true, if this parity page requires an update after a logical page write, otherwise return false.
	 */
	protected boolean parityNeedUpdate(int lp, int parityNumber) {
		return true;
	}
	
	/**
	 * Gets the chip of a logical page.
	 *
	 * @param lp the logical page
	 * @return the page stripe
	 */
	@Override
	protected int getChipIndex(int lp) {
		int chipsNum = getChipsNum();
		int firstParityChip = getParityChipIndex(lp, 1);
		int secondParityChip = getParityChipIndex(lp, 2);
		int tempChip = lp % (chipsNum - 2);
		if (tempChip < firstParityChip && tempChip >= secondParityChip) { // parity chips are in the 2 edges
			return tempChip + 1;
		} else if (tempChip >= firstParityChip || tempChip >= secondParityChip) { // parity chips are in the left
			return tempChip + 2;
		} else { // parity chips are in the right 
			return tempChip;
		}
	}
	
	/**
	 * Gets the page stripe.
	 *
	 * @param lp the logical page
	 * @return the page stripe
	 */
	protected int getPageStripe(int lp) {
		return lp/getStripeSize();
	}
	

	/**
	 * Gets the parity chip index.
	 *
	 * @param lp a logical page in the stripe of parityNumber 
	 * @param parityNumber the parity number
	 * @return the parity chip index
	 */
	protected int getParityChipIndex(int lp, int parityNumber) {
		int stripe = getPageStripe(lp);
		int chipsNum = getChipsNum();
		return ((chipsNum - 3) % chipsNum - (stripe % chipsNum) + parityNumber + chipsNum) % chipsNum;
	}
	
	@Override
	public Builder getSelfBuilder() {
		return new Builder(this);
	}
	
}
