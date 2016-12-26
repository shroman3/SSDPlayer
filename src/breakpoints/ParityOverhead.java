package breakpoints;

import entities.Device;
import manager.RAIDSSDManager;
import manager.RAIDVisualizationSSDManager;
import manager.SSDManager;
import manager.RAIDStatistics.ParityOverheadGetter;

public class ParityOverhead extends BreakpointBase {
	private double mValue = 1.0;
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice) {
		double oldValue = (previousDevice == null) ? Double.MIN_VALUE : ParityOverheadGetter.getParityOverhead(previousDevice);
		double currentValue = ParityOverheadGetter.getParityOverhead(currentDevice);
		return oldValue < mValue && currentValue >= mValue;
	}

	public double getValue() {
		return mValue;
	}

	public void setValue(double value) throws Exception {
		if (!BreakpointsConstraints.isParityOverheadValueLegal(value)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_PARITY_OVERHEAD);
		}
		
		mValue = value;
	}
	
	@Override
	public String getDescription() {
		return "Parity overhead reaches " + mValue;
	}

	@Override
	public String getDisplayName() {
		return "Parity overhead reaches W";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("value", double.class, "Parity overhead"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof ParityOverhead)) return false; 
		ParityOverhead otherCasted = (ParityOverhead) other;
		
		return Double.compare(mValue,otherCasted.getValue()) == 0;
	}

	@Override
	public String getHitDescription() {
		return "Parity overhead reached " + mValue;
	}
	
	@Override
	public boolean isManagerSupported(SSDManager<?, ?, ?, ?, ?> manager) {
		if (manager instanceof RAIDSSDManager 
				|| manager instanceof RAIDVisualizationSSDManager) {
			return true;
		}
		
		return false;
	}
}
