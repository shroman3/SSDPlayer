package breakpoints;

import entities.Device;
import entities.Plane;

public class PagesWrittenPlane extends BreakpointBase {

	private int mCount;
	private int mPlaneIndex;

	public PagesWrittenPlane(){
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if(previousDevice == null){
			return ((Plane<?, ?>)currentDevice.getPlaneByIndex(mPlaneIndex)).getTotalWritten() == mCount; 
		}
		return ((Plane<?, ?>)currentDevice.getPlaneByIndex(mPlaneIndex)).getTotalWritten() == mCount 
				&& ((Plane<?, ?>)previousDevice.getPlaneByIndex(mPlaneIndex)).getTotalWritten() != mCount;
	}

	@Override
	public String getDisplayName() {
		return "X pages are written in plane";
	}

	@Override
	public String getDescription() {
		return getCount() + " pages are written in plane " + mPlaneIndex;
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("count", int.class, "Number of pages written"));
		mComponents.add(new BreakpointComponent("planeIndex", int.class, "Plane index"));
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int mCount) {
		this.mCount = mCount;
	}

	public int getPlaneIndex() {
		return mPlaneIndex;
	}

	public void setPlaneIndex(int mPlaneIndex) {
		this.mPlaneIndex = mPlaneIndex;
	}

}
