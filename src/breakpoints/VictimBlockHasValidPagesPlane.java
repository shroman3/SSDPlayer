package breakpoints;

import java.util.List;

import entities.ActionLog;
import entities.CleanAction;
import entities.Device;
import entities.IDeviceAction;

public class VictimBlockHasValidPagesPlane extends BreakpointBase {
	private int mCount;
	private int mChipIndex;
	private int mPlaneIndex;
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		List<IDeviceAction> cleanActions = ActionLog.getActionsByType(CleanAction.class);
		for(IDeviceAction action : cleanActions){
			CleanAction cleanAction = (CleanAction)action;
			if(cleanAction.getChipIndex() == mChipIndex 
					&& cleanAction.getPlaneIndex() == mPlaneIndex 
					&& cleanAction.getValidPAges() == mCount){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String getDisplayName() {
		return "victim block in plane has number of valid pages";
	}

	@Override
	public String getDescription() {
		return "plane " + mPlaneIndex + "in chip " + mChipIndex + " victim block has " + mCount + " valid pages";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("count", int.class, "Number of valid pages"));
		mComponents.add(new BreakpointComponent("chipIndex", int.class, "chip index"));
		mComponents.add(new BreakpointComponent("planeIndex", int.class, "plane index"));

	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof VictimBlockHasValidPagesPlane)) return false; 
		VictimBlockHasValidPagesPlane otherCasted = (VictimBlockHasValidPagesPlane) other;
		
		return getChipIndex() == otherCasted.getChipIndex()
				&& mCount == otherCasted.getCount()
				&& mPlaneIndex == otherCasted.getPlaneIndex();
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

	public int getPlaneIndex() {
		return mPlaneIndex;
	}

	public void setPlaneIndex(int mPlaneIndex) {
		this.mPlaneIndex = mPlaneIndex;
	}

}
