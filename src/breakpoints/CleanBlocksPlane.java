package breakpoints;

import entities.Device;

public class CleanBlocksPlane extends BreakpointBase {
	private int mPlaneIndex;
	private int mCount;
	private int mChipIndex;
	
	public CleanBlocksPlane() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if(previousDevice == null){
			return false;
		}
		int prevClean = previousDevice.getChip(mChipIndex)
				.getPlane(mPlaneIndex).getNumOfClean();
		int currentClean = currentDevice.getChip(mChipIndex)
				.getPlane(mPlaneIndex).getNumOfClean();
		return prevClean != currentClean && currentClean == mCount;
	}

	@Override
	public String getDisplayName() {
		return "Number of clean blocks in plane";
	}

	@Override
	public String getDescription() {
		return  getCount() + " clean blocks in plane (<chip,plane>): "
				+ "<" 
				+ mChipIndex + ","
				+ mPlaneIndex
				+ ">";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("chipIndex", int.class, "Chip"));
		mComponents.add(new BreakpointComponent("planeIndex", int.class, "Plane index"));
		mComponents.add(new BreakpointComponent("count", int.class, "Number of clean blocks"));
	}

	public int getChipIndex() {
		return mChipIndex;
	}

	public void setChipIndex(int chipIndex) {
		mChipIndex = chipIndex;
	}
	
	public int getPlaneIndex() {
		return mPlaneIndex;
	}

	public void setPlaneIndex(int mPlaneIndex) {
		this.mPlaneIndex = mPlaneIndex;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int mCount) {
		this.mCount = mCount;
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof CleanBlocksPlane)) return false; 
		CleanBlocksPlane otherCasted = (CleanBlocksPlane) other;
		
		return mPlaneIndex == otherCasted.getPlaneIndex()
				&& mCount == otherCasted.getCount()
				&& mChipIndex == otherCasted.getChipIndex();
	}
}
