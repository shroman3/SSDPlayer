package breakpoints;

import manager.HotColdPartition;
import entities.Device;
import entities.hot_cold.HotColdDevice;
import entities.hot_cold.HotColdPage;
import general.ConfigProperties;

public class HotColdPartitionHoldsPercentOfPages extends BreakpointBase {
	private int mPartition = 1;
	private int mPercent;

	public HotColdPartitionHoldsPercentOfPages() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if (!(currentDevice instanceof HotColdDevice)){
			return false;
		}
		
		int totalNumberOfPages = 0, numberOfPagesInPartition = 0;
		HotColdPartition partition = ((HotColdDevice)currentDevice).getPartitions().get(mPartition);
		for (int i=0; i<ConfigProperties.getPagesInDevice(); i++){
			HotColdPage currentPage = (HotColdPage)currentDevice.getPageByIndex(i);
			if(!currentPage.isValid()){
				continue;
			}
			totalNumberOfPages++;
			if(partition.isIn(currentPage.getTemperature())){
				numberOfPagesInPartition++;
			}
		}
		if(totalNumberOfPages == 0){
			return false;
		}
		double percent = (numberOfPagesInPartition / (double)totalNumberOfPages) * 100;
		return Math.abs(percent  - mPercent) < 1 ;
	}

	@Override
	public String getDisplayName() {
		return "HotCold partition holds percent of pages";
	}

	@Override
	public String getDescription() {
		return "HotCold partition " + getPartition() + " holds " + getPercent() + " percent of pages";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("percent", int.class, "Percent of pages"));
		mComponents.add(new BreakpointComponent("partition", int.class, "Partition"));
	}

	public int getPartition() {
		return mPartition;
	}

	public void setPartition(int mPartition) {
		this.mPartition = mPartition;
	}

	public int getPercent() {
		return mPercent;
	}

	public void setPercent(int mPercent) {
		this.mPercent = mPercent;
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof HotColdPartitionHoldsPercentOfPages)) return false; 
		HotColdPartitionHoldsPercentOfPages otherCasted = (HotColdPartitionHoldsPercentOfPages) other;
		
		return mPercent == otherCasted.getPercent()
				&& mPartition == otherCasted.getPartition();
	}

}
