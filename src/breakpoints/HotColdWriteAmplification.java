package breakpoints;

import entities.Device;
import entities.hot_cold.HotColdDevice;
import manager.HotColdReusableSSDManager;
import manager.HotColdSSDManager;
import manager.SSDManager;
import manager.HotColdStatistics.HotColdWriteAmplificationGetter;

public class HotColdWriteAmplification extends BreakpointBase {
	private double mValue = 1.0;
	private int mPartitionIndex = 1;

	public HotColdWriteAmplification() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice) {
		if ((!(currentDevice instanceof HotColdDevice)) || (!(currentDevice instanceof HotColdDevice))) {
			return false;
		}
		double oldValue = previousDevice == null ? Double.MIN_VALUE : 
			HotColdWriteAmplificationGetter.getHotColdWA((HotColdDevice) previousDevice)[mPartitionIndex];
		double currentValue = HotColdWriteAmplificationGetter.getHotColdWA((HotColdDevice) currentDevice)[mPartitionIndex];
		return oldValue < mValue && currentValue >= mValue;
	}

	public double getValue() {
		return mValue;
	}

	public void setValue(double value) throws Exception {
		if (!BreakpointsConstraints.isWriteAmplificationValueLegal(value)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_WRITE_AMP);
		}
		
		mValue = value;
	}
	
	public int getPartitionIndex() {
		return mPartitionIndex;
	}
	
	public void setPartitionIndex(int partitionIndex) throws Exception {
		if (!BreakpointsConstraints.isPartitionIndexLegal(partitionIndex)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_PARTITION);
		}
		
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
	
	@Override
	public String getHitDescription() {
		return "Hot-Cold partition write amplification reached " + mValue;
	}
}
