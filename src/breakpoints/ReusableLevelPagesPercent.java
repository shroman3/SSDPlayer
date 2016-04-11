package breakpoints;

import entities.Device;
import entities.reusable.ReusableDevice;
import entities.reusable.ReusablePage;
import general.ConfigProperties;

public class ReusableLevelPagesPercent extends BreakpointBase {
	private int mLevel;
	private int mPercent;

	public ReusableLevelPagesPercent() {
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
		double percent = (numberOfPagesInLevel / (double)totalNumberOfPages) * 100;
		return Math.abs(percent  - mPercent) < 1 ;
	}

	@Override
	public String getDisplayName() {
		return "Reusable percent of pages in write level";
	}

	@Override
	public String getDescription() {
		return "Reusable " + mPercent + " percent of pages in write level " + mLevel;
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("percent", int.class, "Percent of pages"));
		mComponents.add(new BreakpointComponent("level", int.class, "Level"));
	}

	public int getLevel() {
		return mLevel;
	}

	public void setLevel(int mLevel) {
		this.mLevel = mLevel;
	}

	public int getPercent() {
		return mPercent;
	}

	public void setPercent(int mPercent) {
		this.mPercent = mPercent;
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof ReusableLevelPagesPercent)) return false; 
		ReusableLevelPagesPercent otherCasted = (ReusableLevelPagesPercent) other;
		
		return mLevel == otherCasted.getLevel()
				&& mPercent == otherCasted.getPercent();
	}

}
