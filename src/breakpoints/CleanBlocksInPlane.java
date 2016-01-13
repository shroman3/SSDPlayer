package breakpoints;

import entities.Device;

public class CleanBlocksInPlane extends BreakpointBase {
	private int mPlaneIndex;
	private int mCount;
	
	public CleanBlocksInPlane() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if(previousDevice == null){
			return false;
		}
		int prevClean = previousDevice.getPlaneByIndex(mPlaneIndex).getNumOfClean();
		int currentClean = currentDevice.getPlaneByIndex(mPlaneIndex).getNumOfClean();
		return prevClean != currentClean && currentClean == mCount;
	}

	@Override
	public String getDisplayName() {
		return "Number of clean blocks in plane";
	}

	@Override
	public String getDescription() {
		return  getCount() + " clean blocks in plane " + getPlaneIndex();
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("planeIndex", int.class, "Plane index"));
		mComponents.add(new BreakpointComponent("count", int.class, "Number of clean blocks"));
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
}
