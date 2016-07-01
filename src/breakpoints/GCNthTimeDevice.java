package breakpoints;

import entities.Device;

public class GCNthTimeDevice extends BreakpointBase {
	private int mValue;
	
	public GCNthTimeDevice() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice) {
		if (previousDevice == null) {
			return false;
		}
		
		int prevGCCount = previousDevice.getTotalGCInvocations();
		int currGCCount = currentDevice.getTotalGCInvocations();
		
		return prevGCCount != mValue && currGCCount == mValue;
	}
	
	public int getValue() {
		return mValue;
	}

	public void setValue(int value) throws Exception {
		if (!BreakpointsConstraints.isGCCountLegal(value)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_COUNT);
		}
		
		mValue = value;
	}

	@Override
	public String getDisplayName() {
		return "Invoke garbage collection in the i-th time in the device";
	}

	@Override
	public String getDescription() {
		return "Number of garbage collection invocations is " + getValue() + " in the device";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("value", int.class, "Value"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof GCNthTimeDevice)) return false; 
		GCNthTimeDevice otherCasted = (GCNthTimeDevice) other;
		
		return mValue == otherCasted.getValue();
	}

	@Override
	public String getHitDescription() {
		return "Number of garbage collection invocations in the device reached " + getValue();
	}
}
