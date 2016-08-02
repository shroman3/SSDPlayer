package breakpoints;

import entities.Device;
import entities.StatisticsGetter;
import manager.ParityOverheadGetter;
import manager.RAIDSSDManager;
import manager.RAIDVisualizationSSDManager;
import manager.SSDManager;

public class ParityOverhead extends BreakpointBase {
	private double mValue = 1.0;
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		for(StatisticsGetter getter : SSDManager.getCurrentManager().getStatisticsGetters()){
			if(ParityOverheadGetter.class.isInstance(getter)){
				double oldValue = (previousDevice == null) ? Double.MIN_VALUE : getter.getStatistics(previousDevice).get(0).getValue();
				double currentValue = getter.getStatistics(currentDevice).get(0).getValue();
				return oldValue < mValue && currentValue >= mValue;
			}
		}
		
		return false;
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
