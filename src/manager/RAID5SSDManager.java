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
package manager;

import java.util.List;

import entities.RAID.simulation.RAID5Device;
import entities.RAID.simulation.RAIDChip;
import entities.RAID.simulation.RAIDDevice;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAID5SSDManager extends RAIDSSDManager {
	
	protected void setParitiesNumber() {
		this.paritiesNumber = 1;
	}
	
	@Override
	protected void setStripeSize() {
		this.stripeSize = getChipsNum() - paritiesNumber;
	}
	
	@Override
	protected RAIDDevice getEmptyDevice(List<RAIDChip> emptyChips) {
		RAID5Device.Builder builder = new RAID5Device.Builder();
		builder.setChips(emptyChips);
		builder.setStripeSize(getStripeSize());
		builder.setParitiesNumber(getParitiesNumber());
		return builder.build();
	}
}