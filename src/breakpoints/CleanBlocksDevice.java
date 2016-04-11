package breakpoints;

import entities.Device;

public class CleanBlocksDevice extends BreakpointBase {
	private int mCount;
	
	public CleanBlocksDevice() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if(previousDevice == null){
			return false;
		}
		int prevClean =  previousDevice.getNumOfClean();
		int currentClean = currentDevice.getNumOfClean();
		return prevClean != currentClean && currentClean == mCount;
	}

	@Override
	public String getDisplayName() {
		return "Number of clean blocks in device";
	}

	@Override
	public String getDescription() {
		return  getCount() + " clean blocks in device";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("count", int.class, "Number of clean blocks"));
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int mCount) {
		this.mCount = mCount;
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof CleanBlocksDevice)) return false; 
		CleanBlocksDevice otherCasted = (CleanBlocksDevice) other;
		
		return mCount == otherCasted.getCount();
	}
}
