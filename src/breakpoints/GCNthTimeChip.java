package breakpoints;

import entities.Device;

public class GCNthTimeChip extends BreakpointBase {
	private int mChipIndex;
	private int mValue;
	
	public GCNthTimeChip() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice) {
		if (previousDevice == null) {
			return false;
		}
		
		int prevGCCount = previousDevice.getChipByIndex(mChipIndex).getTotalGCInvocations();
		int currGCCount = currentDevice.getChipByIndex(mChipIndex).getTotalGCInvocations();
		
		return prevGCCount != mValue && currGCCount == mValue;
	}
	
	public int getValue() {
		return mValue;
	}

	public void setValue(int value) {
		mValue = value;
	}

	public int getChipIndex() {
		return mChipIndex;
	}

	public void setChipIndex(int chipIndex) {
		mChipIndex = chipIndex;
	}

	@Override
	public String getDisplayName() {
		return "Invoke garbage collection in the i-th time in chip C";
	}

	@Override
	public String getDescription() {
		return "Number of garbage collection invocations is " + getValue() + " in chip " + getChipIndex();
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("chipIndex", int.class, "Chip"));
		mComponents.add(new BreakpointComponent("value", int.class, "Value"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof GCNthTimeChip)) return false; 
		GCNthTimeChip otherCasted = (GCNthTimeChip) other;
		
		return mChipIndex == otherCasted.getChipIndex()
				&& mValue == otherCasted.getValue();
	}
}
