package breakpoints;

import entities.Device;
import entities.reusable.ReusableBlock;
import entities.reusable.ReusableBlockStatus;
import entities.reusable.ReusableDevice;
import general.ConfigProperties;
import manager.ReusableSSDManager;
import manager.ReusableVisualizationSSDManager;
import manager.SSDManager;

public class ReusableAnyBlockRecycled extends BreakpointBase {

	private int mChipIndex;
	private int mPlaneIndex;
	private int mBlockIndex;

	public ReusableAnyBlockRecycled() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if(!(currentDevice instanceof ReusableDevice)){
			return false;
		}
		if(previousDevice == null){
			return false;
		}
		
		int numberOfBlocks = ConfigProperties.getBlocksInDevice();
		for (int i = 0; i < numberOfBlocks; i++){
			ReusableBlock currentBlock = (ReusableBlock) currentDevice.getBlockByIndex(i);
			ReusableBlock prevBlock = (ReusableBlock) previousDevice.getBlockByIndex(i);
			if (currentBlock.getStatus() == ReusableBlockStatus.RECYCLED 
					&& prevBlock.getStatus() != ReusableBlockStatus.RECYCLED) {
				int blocksInChip = ConfigProperties.getBlocksInPlane() * ConfigProperties.getPlanesInChip();
				int leftoverBlocks = i;
				mChipIndex = leftoverBlocks / blocksInChip;
				leftoverBlocks -= mChipIndex * blocksInChip;
				
				mPlaneIndex = leftoverBlocks / ConfigProperties.getBlocksInPlane();
				leftoverBlocks -= mPlaneIndex * ConfigProperties.getBlocksInPlane();
				mBlockIndex = leftoverBlocks;
				
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String getDisplayName() {
		return "Any reusable block is recycled";
	}

	@Override
	public String getDescription() {
		return "Any reusable block is recycled";
	}

	@Override
	public void addComponents() {
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof ReusableAnyBlockRecycled)) return false; 
		
		return true;
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
		return "Reusable block (<chip,plane,block>): "
				+ "<" 
				+ mChipIndex + ","
				+ mPlaneIndex + ","
				+ mBlockIndex
				+ "> was recycled";
	}
}
