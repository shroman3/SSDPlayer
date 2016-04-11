package breakpoints;

import entities.Device;

public class WritePp extends BreakpointBase {
	private int mPhysicalPage;
	
	public WritePp() {
		super();
	} 
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		boolean currentPageIsClean = currentDevice.getPageByIndex(mPhysicalPage).isClean();
		if(previousDevice == null){
			return !currentPageIsClean;
		}
		
		boolean previousPageIsclean = previousDevice.getPageByIndex(mPhysicalPage).isClean();
		return previousPageIsclean && !currentPageIsClean;
	}

	public int getPhysicalPage() {
		return mPhysicalPage;
	}

	public void setPhysicalPage(int physicalPage) {
		this.mPhysicalPage = physicalPage;
	}

	@Override
	public String getDescription() {
		return "Write physical page " + mPhysicalPage;
	}

	@Override
	public String getDisplayName() {
		return "Write physical page P";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("physicalPage", int.class, "Physical page"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof WritePp)) return false; 
		WritePp otherCasted = (WritePp) other;
		
		return mPhysicalPage == otherCasted.getPhysicalPage();
	}
}
