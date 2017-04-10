package breakpoints;

import entities.Device;
import entities.Plane;

public class PagesWrittenPlane extends BreakpointBase {

	private int mCount;
	private int mPlaneIndex;
	private int mChipIndex;

	public PagesWrittenPlane() {
		super();
	}

	@Override
	public boolean breakpointHit(Device<?> previousDevice, Device<?> currentDevice) {
		if (previousDevice == null) {
			return ((Plane<?>) currentDevice.getChip(mChipIndex).getPlane(mPlaneIndex)).getTotalWritten() == mCount;
		}
		return ((Plane<?>) currentDevice.getChip(mChipIndex).getPlane(mPlaneIndex)).getTotalWritten() == mCount
				&& ((Plane<?>) previousDevice.getChip(mChipIndex).getPlane(mPlaneIndex)).getTotalWritten() != mCount;
	}

	@Override
	public String getDisplayName() {
		return "X pages are written in plane";
	}

	@Override
	public String getDescription() {
		return getCount() + " pages are written in plane (<chip,plane>): " + "<" + mChipIndex + "," + mPlaneIndex + ">";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("count", int.class, "Number of pages written"));
		mComponents.add(new BreakpointComponent("planeIndex", int.class, "Plane index"));
		mComponents.add(new BreakpointComponent("chipIndex", int.class, "Chip"));
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

	public int getPlaneIndex() {
		return mPlaneIndex;
	}

	public void setPlaneIndex(int planeIndex) throws Exception {
		if (!BreakpointsConstraints.isPlaneIndexLegal(planeIndex)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_PLANE);
		}

		mPlaneIndex = planeIndex;
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
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof PagesWrittenPlane))
			return false;
		PagesWrittenPlane otherCasted = (PagesWrittenPlane) other;

		return mCount == otherCasted.getCount() && mPlaneIndex == otherCasted.getPlaneIndex()
				&& mChipIndex == otherCasted.getChipIndex();
	}

	@Override
	public String getHitDescription() {
		return getCount() + " pages were written in plane (<chip,plane>): " + "<" + mChipIndex + "," + mPlaneIndex
				+ ">";
	}
}
