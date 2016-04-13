package breakpoints;

import entities.Device;
import entities.StatisticsGetter;
import manager.SSDManager;
import manager.WriteAmplificationGetter;

public class WriteAmplification extends BreakpointBase {
	private double mValue = 1.0;

	public WriteAmplification() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		for(StatisticsGetter getter : SSDManager.getCurrentManager().getStatisticsGetters()){
			if(WriteAmplificationGetter.class.isInstance(getter)){
				double oldValue = previousDevice == null ? Double.MIN_VALUE : getter.getStatistics(previousDevice).get(0).getValue();
				double currentValue = getter.getStatistics(currentDevice).get(0).getValue();
				return oldValue < mValue && currentValue >= mValue;
			}
		}
		
		return false;
	}

	public double getValue() {
		return mValue;
	}

	public void setValue(double value) {
		mValue = value;
	}
	
	@Override
	public String getDescription() {
		return "Write Amplification reaches " + mValue;
	}

	@Override
	public String getDisplayName() {
		return "Write Amplification reaches W";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("value", double.class, "Value"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof WriteAmplification)) return false; 
		WriteAmplification otherCasted = (WriteAmplification) other;
		
		return Double.compare(mValue,otherCasted.getValue()) == 0;
	}
}
