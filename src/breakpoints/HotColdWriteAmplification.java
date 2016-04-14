package breakpoints;

import entities.Device;
import entities.StatisticsGetter;
import manager.HotColdReusableSSDManager;
import manager.HotColdSSDManager;
import manager.SSDManager;
import manager.HotColdStatistics.HotColdWriteAmplificationGetter;

public class HotColdWriteAmplification extends BreakpointBase {
	private double mValue = 1.0;
	private int mPartitionIndex = 0;

	public HotColdWriteAmplification() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		for(StatisticsGetter getter : SSDManager.getCurrentManager().getStatisticsGetters()){
			if(HotColdWriteAmplificationGetter.class.isInstance(getter)){
				double oldValue = previousDevice == null ? Double.MIN_VALUE : getter.getStatistics(previousDevice).get(mPartitionIndex).getValue();
				double currentValue = getter.getStatistics(currentDevice).get(mPartitionIndex).getValue();
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
	
	public int getPartitionIndex() {
		return mPartitionIndex;
	}
	
	public void setPartitionIndex(int partitionIndex) {
		mPartitionIndex = partitionIndex;
	}
	
	@Override
	public String getDescription() {
		return "Write Amplification of partition " + mPartitionIndex + " reaches " + mValue;
	}

	@Override
	public String getDisplayName() {
		return "Hot-Cold partition write amplification reaches W";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("partitionIndex", int.class, "Partition Index"));
		mComponents.add(new BreakpointComponent("value", double.class, "Value"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof HotColdWriteAmplification)) return false; 
		HotColdWriteAmplification otherCasted = (HotColdWriteAmplification) other;
		
		return Double.compare(mValue,otherCasted.getValue()) == 0
				&& mPartitionIndex == otherCasted.getPartitionIndex();
	}
	
	@Override
	public boolean isManagerSupported(SSDManager<?, ?, ?, ?, ?> manager) {
		if (manager instanceof HotColdSSDManager 
				|| manager instanceof HotColdReusableSSDManager) {
			return true;
		}
		
		return false;
	}
}
