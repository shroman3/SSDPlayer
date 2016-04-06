package breakpoints;

import entities.BlockStatus;
import entities.BlockStatusGeneral;
import entities.Device;

public class AllocateActiveBlock extends BreakpointBase {
	private int mBlockIndex;
	
	public AllocateActiveBlock() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice) {
		BlockStatus currStatus = currentDevice.getBlockByIndex(mBlockIndex).getStatus();
		if (previousDevice == null) {
			if (isBlockActive(currStatus)) {
				return true;
			}
			
			return false;
		}
		
		BlockStatus prevStatus = previousDevice.getBlockByIndex(mBlockIndex).getStatus();
		if (!isBlockActive(prevStatus) && isBlockActive(currStatus)) {
			return true;
		}
		
		return false;
	}

	public int getBlockIndex() {
		return mBlockIndex;
	}
	
	public void setBlockIndex(int blockIndex) {
		mBlockIndex = blockIndex;
	}
	
	private boolean isBlockActive(BlockStatus prevStatus) {
		return prevStatus.getStatusName().equals(BlockStatusGeneral.ACTIVE.getStatusName());
	}

	@Override
	public String getDescription() {
		return "Allocate block " + mBlockIndex + " as active";
	}
	
	@Override
	public String getDisplayName() {
		return "Allocate block B as active";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("blockIndex", int.class, "Block"));
	}
}
