package breakpoints;

import entities.Device;
import entities.reusable.ReusableBlock;
import entities.reusable.ReusableDevice;
import general.ConfigProperties;
import manager.ReusableSSDManager;
import manager.ReusableVisualizationSSDManager;
import manager.SSDManager;

public class ReusableLevelBlocksPercent extends BreakpointBase {
	private int mLevel;
	private int mPercent;

	public ReusableLevelBlocksPercent() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if (!(currentDevice instanceof ReusableDevice)){
			return false;
		}
		
		int totalNumberOfBlocks = 0, numberOfBlocksInLevel = 0;
		
		for (int i=0; i<ConfigProperties.getBlocksInDevice(); i++){
			ReusableBlock currentBlock = (ReusableBlock)currentDevice.getBlockByIndex(i);
			if(currentBlock.getWriteLevel() == mLevel){
				numberOfBlocksInLevel++;
			}
			totalNumberOfBlocks++;
		
		}
		if(totalNumberOfBlocks == 0){
			return false;
		}
		double percent = (numberOfBlocksInLevel / (double)totalNumberOfBlocks) * 100;
		return Math.abs(percent  - mPercent) < 1 ;
	}

	@Override
	public String getDisplayName() {
		return "Percent of reusable blocks in write level";
	}

	@Override
	public String getDescription() {
		return mPercent + " percent of reusable blocks in write level " + mLevel;
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("percent", int.class, "Percent of blocks"));
		mComponents.add(new BreakpointComponent("level", int.class, "Level"));
	}

	public int getLevel() {
		return mLevel;
	}

	public void setLevel(int level) throws Exception {
		if (!BreakpointsConstraints.isWriteLevelLegal(level)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_WRITE_LEVEL);
		}
		
		mLevel = level;
	}

	public int getPercent() {
		return mPercent;
	}

	public void setPercent(int percent) throws Exception {
		if (!BreakpointsConstraints.isPercentValueLegal(percent)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_PERCENT);
		}
		
		mPercent = percent;
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof ReusableLevelBlocksPercent)) return false; 
		ReusableLevelBlocksPercent otherCasted = (ReusableLevelBlocksPercent) other;
		
		return mLevel == otherCasted.getLevel()
				&& mPercent == otherCasted.getPercent();
	}

	@Override
	public boolean isManagerSupported(SSDManager<?, ?, ?, ?, ?> manager) {
		if (manager instanceof ReusableSSDManager 
				|| manager instanceof ReusableVisualizationSSDManager) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String getHitDescription() {
		return mPercent + " percent of reusable blocks in write level " + mLevel;
	}
}
