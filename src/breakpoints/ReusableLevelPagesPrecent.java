package breakpoints;

import entities.Device;
import entities.reusable.ReusableDevice;
import entities.reusable.ReusablePage;
import general.ConfigProperties;

public class ReusableLevelPagesPrecent extends BreakpointBase {
	private int mLevel;
	private int mPrecent;

	public ReusableLevelPagesPrecent() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if (!(currentDevice instanceof ReusableDevice)){
			return false;
		}
		
		int totalNumberOfPages = 0, numberOfPagesInLevel = 0;
		
		for (int i=0; i<ConfigProperties.getPagesInDevice(); i++){
			ReusablePage currentPage = (ReusablePage)currentDevice.getPageByIndex(i);
			if(!currentPage.isValid()){
				continue;
			}
			totalNumberOfPages++;
			if(currentPage.getWriteLevel() == mLevel){
				numberOfPagesInLevel++;
			}
		}
		if(totalNumberOfPages == 0){
			return false;
		}
		double precent = (numberOfPagesInLevel / (double)totalNumberOfPages) * 100;
		return Math.abs(precent  - mPrecent) < 1 ;
	}

	@Override
	public String getDisplayName() {
		return "Reusable percent of pages in write level";
	}

	@Override
	public String getDescription() {
		return "Reusable " + mPrecent + " percent of pages in write level " + mLevel;
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("precent", int.class, "Precent of pages"));
		mComponents.add(new BreakpointComponent("level", int.class, "Level"));
	}

	public int getLevel() {
		return mLevel;
	}

	public void setLevel(int mLevel) {
		this.mLevel = mLevel;
	}

	public int getPrecent() {
		return mPrecent;
	}

	public void setPrecent(int mPrecent) {
		this.mPrecent = mPrecent;
	}

}
