package breakpoints;

import manager.HotColdPartition;
import entities.Device;
import entities.hot_cold.HotColdDevice;
import entities.hot_cold.HotColdPage;
import general.ConfigProperties;

public class HotColdPartitionHoldsPrecentOfPages extends BreakpointBase {
	private int mPartition;
	private int mPrecent;

	public HotColdPartitionHoldsPrecentOfPages() {
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
		double precent = (numberOfPagesInPartition / (double)totalNumberOfPages) * 100;
		return Math.abs(precent  - mPrecent) < 1 ;
	}

	@Override
	public String getDisplayName() {
		return "HotCold partition holds precent of pages";
	}

	@Override
	public String getDescription() {
		return "HotCold partition " + getPartition() + " holds " + getPrecent() + " precent of pages";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("precent", int.class, "Precent of pages"));
		mComponents.add(new BreakpointComponent("partition", int.class, "Partition"));
	}

	public int getPartition() {
		return mPartition;
	}

	public void setPartition(int mPartition) {
		this.mPartition = mPartition;
	}

	public int getPrecent() {
		return mPrecent;
	}

	public void setPrecent(int mPrecent) {
		this.mPrecent = mPrecent;
	}

}
