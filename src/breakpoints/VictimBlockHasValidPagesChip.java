package breakpoints;

import java.util.List;

import entities.ActionLog;
import entities.CleanAction;
import entities.Device;
import entities.IDeviceAction;

public class VictimBlockHasValidPagesChip extends BreakpointBase {
	private int mCount;
	private int mChipIndex;
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		List<IDeviceAction> cleanActions = ActionLog.getActionsByType(CleanAction.class);
		for(IDeviceAction action : cleanActions){
			CleanAction cleanAction = (CleanAction)action;
			if(cleanAction.getChipIndex() == mChipIndex  
					&& cleanAction.getValidPAges() == mCount){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String getDisplayName() {
		return "victim block in chip has number of valid pages";
	}

	@Override
	public String getDescription() {
		return "chip " + mChipIndex + " victim block has " + mCount + " valid pages";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("count", int.class, "Number of valid pages"));
		mComponents.add(new BreakpointComponent("chipIndex", int.class, "chip index"));

	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof VictimBlockHasValidPagesChip)) return false; 
		VictimBlockHasValidPagesChip otherCasted = (VictimBlockHasValidPagesChip) other;
		
		return getChipIndex() == otherCasted.getChipIndex()
				&& mCount == otherCasted.getCount();
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int mCount) {
		this.mCount = mCount;
	}

	public int getChipIndex() {
		return mChipIndex;
	}

	public void setChipIndex(int mChipIndex) {
		this.mChipIndex = mChipIndex;
	}
}
