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
	public boolean breakpointHit(Device<?> previousDevice, Device<?> currentDevice) {
		List<IDeviceAction> cleanActions = ActionLog.getActionsByType(CleanAction.class);
		for (IDeviceAction action : cleanActions) {
			CleanAction cleanAction = (CleanAction) action;
			if (cleanAction.getChipIndex() == mChipIndex && cleanAction.getPlaneIndex() == mPlaneIndex
					&& cleanAction.getValidPAges() == mCount) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String getDisplayName() {
		return "Victim block in plane has number of valid pages";
	}

	@Override
	public String getDescription() {
		return "Plane " + mPlaneIndex + " in chip " + mChipIndex + " victim block has " + mCount + " valid pages";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("count", int.class, "Number of valid pages"));
		mComponents.add(new BreakpointComponent("chipIndex", int.class, "chip index"));
		mComponents.add(new BreakpointComponent("planeIndex", int.class, "plane index"));

	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof VictimBlockHasValidPagesPlane))
			return false;
		VictimBlockHasValidPagesPlane otherCasted = (VictimBlockHasValidPagesPlane) other;

		return getChipIndex() == otherCasted.getChipIndex() && mCount == otherCasted.getCount()
				&& mPlaneIndex == otherCasted.getPlaneIndex();
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

	public int getPlaneIndex() {
		return mPlaneIndex;
	}

	public void setPlaneIndex(int planeIndex) throws Exception {
		if (!BreakpointsConstraints.isPlaneIndexLegal(planeIndex)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_PLANE);
		}

		mPlaneIndex = planeIndex;
	}

	@Override
	public String getHitDescription() {
		return "Victim block in plane (<Chip,Plane>): " + "<" + mChipIndex + "," + mPlaneIndex + "> " + "reached "
				+ mCount + " valid pages";
	}
}
