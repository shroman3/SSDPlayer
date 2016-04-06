package breakpoints;

import entities.Device;

public class GCNthTimePlane extends BreakpointBase {
	private int mPlaneIndex;
	private int mValue;
	
	public GCNthTimePlane() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice) {
		if (previousDevice == null) {
			return false;
		}
		
		int prevGCCount = previousDevice.getPlaneByIndex(mPlaneIndex).getTotalGCInvocations();
		int currGCCount = currentDevice.getPlaneByIndex(mPlaneIndex).getTotalGCInvocations();
		
		return prevGCCount != mValue && currGCCount == mValue;
	}
	
	public int getValue() {
		return mValue;
	}

	public void setValue(int value) {
		mValue = value;
	}

	public int getPlaneIndex() {
		return mPlaneIndex;
	}

	public void setPlaneIndex(int planeIndex) {
		mPlaneIndex = planeIndex;
	}

	@Override
	public String getDisplayName() {
		return "Invoke garbage collection in the i-th time in plane P";
	}

	@Override
	public String getDescription() {
		return "Number of garbage collection invocations is " + getValue() + " in plane " + getPlaneIndex();
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("planeIndex", int.class, "Plane"));
		mComponents.add(new BreakpointComponent("value", int.class, "Value"));
	}
}
