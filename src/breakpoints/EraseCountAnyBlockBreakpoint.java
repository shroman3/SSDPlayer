package breakpoints;

import entities.Device;
import general.ConfigProperties;

public class EraseCountAnyBlockBreakpoint extends BreakpointBase {

	private int mCount;
	
	public EraseCountAnyBlockBreakpoint() {
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if(previousDevice == null){
			return false;
		}
		
		int numberOfBlocks = ConfigProperties.getBlocksInDevice();
		for (int i=0; i < numberOfBlocks; i++){
			boolean blockReachedCount = previousDevice.getBlockByIndex(i).getEraseCounter() != getCount() 
					&& currentDevice.getBlockByIndex(i).getEraseCounter() == getCount();
			if(blockReachedCount){
				return true;
			}
		}
		return false; 
	}

	@Override
	public String getDisplayName() {
		return "Any block reaches erease count";
	}

	@Override
	public String getDescription() {
		return "Any block reaches erase count of " + getCount();
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("count", int.class, "Erase count"));
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int mCount) {
		this.mCount = mCount;
	}
}
