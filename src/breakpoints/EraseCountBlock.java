package breakpoints;

import entities.Device;

public class EraseCountBlock extends BreakpointBase {

	private int mChipIndex;
	private int mPlaneIndex;
	private int mBlockIndex;
	private int mCount;
	
	public EraseCountBlock() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if(previousDevice == null){
			return false;
		}
		
		return previousDevice.getChip(mChipIndex)
				.getPlane(mPlaneIndex)
				.getBlock(mBlockIndex).getEraseCounter() != getCount() 
			&& currentDevice.getChip(mChipIndex)
				.getPlane(mPlaneIndex)
				.getBlock(mBlockIndex).getEraseCounter() == getCount();			 
	}

	@Override
	public String getDisplayName() {
		return "Block B reaches erase count";
	}

	@Override
	public String getDescription() {
		return "Block (<chip,plane,block>): "
				+ "<" 
				+ mChipIndex + ","
				+ mPlaneIndex + ","
				+ mBlockIndex
				+ "> reaches erase count of " + getCount();
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("chipIndex", int.class, "Chip"));
		mComponents.add(new BreakpointComponent("planeIndex", int.class, "Plane"));
		mComponents.add(new BreakpointComponent("blockIndex", int.class, "Block index"));
		mComponents.add(new BreakpointComponent("count", int.class, "Erase count"));
	}

	public int getBlockIndex() {
		return mBlockIndex;
	}

	public void setBlockIndex(int mBlockIndex) {
		this.mBlockIndex = mBlockIndex;
	}
	
	public int getPlaneIndex() {
		return mPlaneIndex;
	}

	public void setPlaneIndex(int planeIndex) {
		mPlaneIndex = planeIndex;
	}
	
	public int getChipIndex() {
		return mChipIndex;
	}

	public void setChipIndex(int chipIndex) {
		mChipIndex = chipIndex;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int mCount) {
		this.mCount = mCount;
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof EraseCountBlock)) return false; 
		EraseCountBlock otherCasted = (EraseCountBlock) other;
		
		return mCount == otherCasted.getCount()
				&& mBlockIndex == otherCasted.getBlockIndex()
				&& mPlaneIndex == otherCasted.getPlaneIndex()
				&& mChipIndex == otherCasted.getChipIndex();
	}
}
