package breakpoints;

import entities.Chip;
import entities.Device;

public class CleanBlocksInChip extends BreakpointBase {
	private int mChipIndex;
	private int mCount;
	
	public CleanBlocksInChip() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if(previousDevice == null){
			return false;
		}
		int prevClean = ((Chip<?, ?, ?>) previousDevice.getChipByIndex(mChipIndex)).getNumOfClean();
		int currentClean = ((Chip<?, ?, ?>) currentDevice.getChipByIndex(mChipIndex)).getNumOfClean();
		return prevClean != currentClean && currentClean == mCount;
	}

	@Override
	public String getDisplayName() {
		return "Number of clean blocks in chip";
	}

	@Override
	public String getDescription() {
		return  getCount() + " clean blocks in chip " + getChipIndex();
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("chipIndex", int.class, "Chip index"));
		mComponents.add(new BreakpointComponent("count", int.class, "Number of clean blocks"));
	}

	public int getChipIndex() {
		return mChipIndex;
	}

	public void setChipIndex(int mChipIndex) {
		this.mChipIndex = mChipIndex;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int mCount) {
		this.mCount = mCount;
	}
}
