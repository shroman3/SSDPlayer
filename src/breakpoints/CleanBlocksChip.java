package breakpoints;

import entities.Chip;
import entities.Device;

public class CleanBlocksChip extends BreakpointBase {
	private int mChipIndex;
	private int mCount;
	
	public CleanBlocksChip() {
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

	public void setChipIndex(int chipIndex) throws Exception {
		if (!BreakpointsConstraints.isChipIndexLegal(chipIndex)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_CHIP);
		}
		
		mChipIndex = chipIndex;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int count) throws Exception {
		if (!BreakpointsConstraints.isCountValueLegal(count)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_COUNT);
		}
		
		mCount = count;
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof CleanBlocksChip)) return false; 
		CleanBlocksChip otherCasted = (CleanBlocksChip) other;
		
		return mChipIndex == otherCasted.getChipIndex()
				&& mCount == otherCasted.getCount();
	}
	
	@Override
	public String getHitDescription() {
		return "Number of clean blocks in chip " + getChipIndex() + " reached " + getCount();
	}
}
