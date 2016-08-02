package breakpoints;

import entities.Device;
import manager.LogicalWritesPerEraseGetter;

public class WritesPerErase extends BreakpointBase {
	private double mValue = 1.0;
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice) {
		double oldValue = previousDevice == null ? Double.MIN_VALUE : LogicalWritesPerEraseGetter.getLogicalWritesPerErase(previousDevice);
		double currentValue = LogicalWritesPerEraseGetter.getLogicalWritesPerErase(currentDevice);
		return oldValue < mValue && currentValue >= mValue;
	}

	public double getValue() {
		return mValue;
	}

	public void setValue(double value) throws Exception {
		if (!BreakpointsConstraints.isWritesPerEraseValueLegal(value)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_WRITES_PER_ERASE);
		}
		
		mValue = value;
	}
	
	@Override
	public String getDescription() {
		return "Writes per erase reach " + mValue;
	}

	@Override
	public String getDisplayName() {
		return "Writes per erase reach W";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("value", double.class, "Writes per erase"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof WritesPerErase)) return false; 
		WritesPerErase otherCasted = (WritesPerErase) other;
		
		return Double.compare(mValue,otherCasted.getValue()) == 0;
	}

	@Override
	public String getHitDescription() {
		return "Writes per erase reached " + mValue;
	}
}
