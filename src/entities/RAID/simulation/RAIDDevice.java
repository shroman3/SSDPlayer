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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.javatuples.Triplet;

import entities.ActionLog;
import entities.Device;
import entities.WriteInStripeAction;
import entities.WriteLpAction;
import entities.RAID.RAIDBasicDevice;

/**
 * 
 * @author Or Mauda
 *
 */
public abstract class RAIDDevice extends RAIDBasicDevice<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip> {
	public abstract static class Builder extends RAIDBasicDevice.Builder<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip> {
		private RAIDDevice device;		

		public abstract RAIDDevice build();

		protected void setDevice(RAIDDevice device) {
			super.setDevice(device);
			this.device = device;
		}

		public void setStripeSize(int stripeSize) {
			device.stripeSize = stripeSize;
		}
		
		public void setParitiesNumber(int paritiesNumber) {
			device.paritiesNumber = paritiesNumber;
		}
	}
	
	protected RAIDDevice() {}
	
	protected RAIDDevice(RAIDDevice other) {
		super(other);
		this.stripeSize = other.stripeSize;
		this.paritiesNumber = other.paritiesNumber;
	}
	
	private int stripeSize;
	private int paritiesNumber;
	
	public int getStripeSize() {
		return stripeSize;
	}
	
	public int getParitiesNumber() {
		return paritiesNumber;
	}
	
	public abstract Builder getSelfBuilder();

	public RAIDDevice invokeCleaning() {
		int dataMoved = 0;
		int parityMoved = 0;
		List<RAIDChip> cleanChips = new ArrayList<RAIDChip>(getChipsNum());
		int i = 0; 
		boolean cleaningInvoked = false;
		for (RAIDChip chip : getChips()) {
			Triplet<RAIDChip, Integer, Integer> clean = chip.cleanRAID(i);
			if (clean == null) {
				cleanChips.add(chip);
			} else {
				cleaningInvoked = true;
				dataMoved += clean.getValue1();
				parityMoved += clean.getValue2();
				cleanChips.add(clean.getValue0());
			}
			i++;
		}
		if (!cleaningInvoked) {
			return this;
		}
		
		Builder builder = getSelfBuilder();
		builder.increaseTotalDataMoved(dataMoved)
				.increaseTotalParityMoved(parityMoved)
				.setChips(cleanChips)
				.setTotalGCInvocations(getTotalGCInvocations() + 1);
		return builder.build();
	}
	

	@Override
	public Device<RAIDPage,RAIDBlock,RAIDPlane,RAIDChip> writeLP(int lp, int size) {
		/* write the data pages */
		boolean isFirstWrite = true; // if size > 1, this field gets false after the first write 
		RAIDDevice tempDevice = (RAIDDevice) getSelfBuilder().build();
		
		Set<Integer> stripesInvolved = new HashSet<Integer>();
		
		List<RAIDChip> updatedChips = tempDevice.getNewChipsList();
		
		for(int currentLP = lp; currentLP < lp + size; currentLP++) {
			// for every logical page we need to write
			Boolean isHighlighted = isPageHighlighted(currentLP);
			if (isFirstWrite == false) {
				tempDevice = (RAIDDevice) tempDevice.invokeCleaning();
				tempDevice = (RAIDDevice) tempDevice.invalidate(currentLP);
			}
			
			updatedChips = tempDevice.getNewChipsList();
			
			int currentStripe = getPageStripe(currentLP);
			stripesInvolved.add(currentStripe);
			ActionLog.addAction(new WriteInStripeAction(currentStripe));
			ActionLog.addAction(new WriteLpAction(lp));
			
			int chipIndex = getChipIndex(currentLP);
			RAIDChip chip = tempDevice.getChip(chipIndex);
			
			// write the logical page to the fitting chip
			updatedChips.set(chipIndex, (RAIDChip) chip.writeLP(currentLP, currentStripe));
			
			Builder builder = (Builder) tempDevice.getSelfBuilder();
			builder.increaseTotalDataWritten().setChips(updatedChips);
			tempDevice = (RAIDDevice) builder.build();
			tempDevice = (RAIDDevice) tempDevice.setHighlightByLogicalP(isHighlighted, currentLP).getValue2();
			isFirstWrite = false;
		}
		
		/* write the parity pages */	
		for(int currentLP = lp; currentLP < lp + size; currentLP++) {
			int stripe = getPageStripe(currentLP);
			
			if (stripesInvolved.contains(stripe) == false) {
				// if the page's stripe is not in stripesInvolved it means we already took care of this stripe's parity pages.
				continue;
			}
			
			// lets update this stripe's parity pages
			for(int parityNumber = 1; parityNumber <= paritiesNumber; parityNumber++) { // decide which parity needs an update
				if (!parityNeedUpdate(currentLP, parityNumber)) {
					continue;
				}
				Boolean isHighlighted = isPageHighlighted(parityNumber, stripe);
				//TODO: Roma - Check if needed here
//				tempDevice = (RAIDDevice) tempDevice.invokeCleaning();
				tempDevice = (RAIDDevice) tempDevice.invalidate(stripe, parityNumber);
				
				updatedChips = tempDevice.getNewChipsList();
				
				int chipIndex = getParityChipIndex(currentLP, parityNumber);
				RAIDChip currentChip = tempDevice.getChip(chipIndex);
				
				// write the parity page to the fitting chip
				updatedChips.set(chipIndex, (RAIDChip) currentChip.writePP(stripe, parityNumber));
				
				Builder builder = (Builder) tempDevice.getSelfBuilder();
				builder.increaseTotalParityWritten().setChips(updatedChips);
				tempDevice = (RAIDDevice) builder.build();
				tempDevice = (RAIDDevice) tempDevice.setHighlightByParityP(isHighlighted, parityNumber, stripe, true).getValue2();
			}

			// we just took care of this stripe's parity pages, we don't need this stripe anymore and have to remove it.
			stripesInvolved.remove(stripe);
		}

		return tempDevice;
	}
	
	/**
	 * Determines whether a parity page need to be updated.
	 *
	 * @param lp a logical page written with the same stripe as the parity page.
	 * @param parityNumber the parity number of the parity page.
	 * @return true, if this parity page requires an update after a logical page write, otherwise return false.
	 */
	protected abstract boolean parityNeedUpdate(int lp, int parityNumber);
	
	/**
	 * Gets the page stripe.
	 *
	 * @param lp the logical page
	 * @return the page stripe
	 */
	protected abstract int getPageStripe(int lp);
	
	/**
	 * Gets the chip of a logical page. Default is RAID1.
	 *
	 * @param lp the logical page
	 * @return the page stripe
	 */
	@Override
	protected abstract int getChipIndex(int lp);
	

	/**
	 * Gets the parity chip index. Default is RAID1.
	 *
	 * @param lp a logical page in the stripe of parityNumber 
	 * @param parityNumber the parity number
	 * @return the parity chip index
	 */
	protected abstract int getParityChipIndex(int lp, int parityNumber);
	
//	public RAIDDevice setChip(RAIDChip chip, int chipIndex) {
//		Builder deviceBuilder = (Builder) getSelfBuilder();
//		List<RAIDChip> newChipsList = getNewChipsList();
//		newChipsList.set(chipIndex, chip);
//		deviceBuilder.setChips(newChipsList);
//		return (RAIDDevice) deviceBuilder.build();
//	}
//	
//	public RAIDDevice changeGC(Triplet<Integer, Integer, Integer> blockIndex, boolean isInGC) {
//		RAIDChip chip = getChip(blockIndex.getValue0());
//		Pair<Integer, Integer> blockInChip = new Pair<Integer, Integer>(blockIndex.getValue1(), blockIndex.getValue2());
//		return setChip(chip.changeGC(blockInChip, isInGC), blockIndex.getValue0());
//	}
//	
//	public RAIDDevice eraseBlock(Triplet<Integer, Integer, Integer> blockIndex) {
//		RAIDChip chip = getChip(blockIndex.getValue0());
//		Pair<Integer, Integer> blockInChip = new Pair<Integer, Integer>(blockIndex.getValue1(), blockIndex.getValue2());
//		return setChip(chip.eraseBlock(blockInChip), blockIndex.getValue0());
//	}

}