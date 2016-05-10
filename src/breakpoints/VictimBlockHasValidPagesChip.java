package breakpoints;

import java.util.List;

import entities.ActionLog;
import entities.CleanAction;
import entities.Device;
import entities.IDeviceAction;

public class VictimBlockHasValidPagesChip extends BreakpointBase {
	private int mCount;
	private int mChipIndex;
	private int mPlaneIndex;
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		List<IDeviceAction> cleanActions = ActionLog.getActionsByType(CleanAction.class);
		for(IDeviceAction action : cleanActions){
			CleanAction cleanAction = (CleanAction) action;
			if(cleanAction.getChipIndex() == mChipIndex  
					&& cleanAction.getValidPAges() == mCount){
				mPlaneIndex = cleanAction.getPlaneIndex();
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String getDisplayName() {
		return "Victim block in chip has number of valid pages";
	}

	@Override
	public String getDescription() {
		return "Chip " + mChipIndex + " victim block has " + mCount + " valid pages";
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

	public void setCount(int count) throws Exception {
		if (!BreakpointsConstraints.isCountValueLegal(count)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_COUNT);
		}
		
		mCount = count;
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
	
	@Override
	public String getHitDescription() {
		return "Victim block in plane (<Chip,Plane>): "
					+ "<" + mChipIndex + "," + mPlaneIndex + "> "
					+ "reached " + mCount + " valid pages";
	}
}
