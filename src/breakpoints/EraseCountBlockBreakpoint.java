package breakpoints;

import entities.Device;

public class EraseCountBlockBreakpoint extends BreakpointBase {

	private int mBlockIndex;
	private int mCount;
	
	public EraseCountBlockBreakpoint() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if(previousDevice == null){
			return false;
		}
		
		return previousDevice.getBlockByIndex(getBlockIndex()).getEraseCounter() != getCount() 
				&& currentDevice.getBlockByIndex(getBlockIndex()).getEraseCounter() == getCount();			 
	}

	@Override
	public String getDisplayName() {
		return "Block reaches erease count";
	}

	@Override
	public String getDescription() {
		return getBlockIndex() + " block reaches erase count of " + getCount();
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("blockIndex", int.class, "Block index"));
		mComponents.add(new BreakpointComponent("count", int.class, "Erase count"));
	}

	public int getBlockIndex() {
		return mBlockIndex;
	}

	public void setBlockIndex(int mBlockIndex) {
		this.mBlockIndex = mBlockIndex;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int mCount) {
		this.mCount = mCount;
	}
}
