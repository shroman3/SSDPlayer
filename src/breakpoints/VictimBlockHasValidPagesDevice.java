package breakpoints;

import java.util.List;

import entities.ActionLog;
import entities.CleanAction;
import entities.Device;
import entities.IDeviceAction;

public class VictimBlockHasValidPagesDevice extends BreakpointBase {
	private int mCount;
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		List<IDeviceAction> cleanActions = ActionLog.getActionsByType(CleanAction.class);
		for(IDeviceAction action : cleanActions){
			CleanAction cleanAction = (CleanAction)action;
			if(cleanAction.getValidPAges() == mCount){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String getDisplayName() {
		return "victim block in device has number of valid pages";
	}

	@Override
	public String getDescription() {
		return "victim block has " + mCount + " valid pages";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("count", int.class, "Number of valid pages"));

	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof VictimBlockHasValidPagesDevice)) return false; 
		VictimBlockHasValidPagesDevice otherCasted = (VictimBlockHasValidPagesDevice) other;
		
		return mCount == otherCasted.getCount();
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int mCount) {
		this.mCount = mCount;
	}

}
