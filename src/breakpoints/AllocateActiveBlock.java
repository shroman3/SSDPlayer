package breakpoints;

import entities.BlockStatus;
import entities.BlockStatusGeneral;
import entities.Device;

public class AllocateActiveBlock extends BreakpointBase {
	private int mBlockIndex;
	private int mPlaneIndex;
	private int mChipIndex;
	
	public AllocateActiveBlock() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice) {
		BlockStatus currStatus = currentDevice.getChip(mChipIndex)
				.getPlane(mPlaneIndex)
				.getBlock(mBlockIndex).getStatus();
		if (previousDevice == null) {
			if (isBlockActive(currStatus)) {
				return true;
			}
			
			return false;
		}
		
		BlockStatus prevStatus = previousDevice.getChip(mChipIndex)
				.getPlane(mPlaneIndex)
				.getBlock(mBlockIndex).getStatus();
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
	
	private boolean isBlockActive(BlockStatus prevStatus) {
		return prevStatus.getStatusName().equals(BlockStatusGeneral.ACTIVE.getStatusName());
	}

	@Override
	public String getDescription() {
		return "Allocate block (<chip,plane,block>): "
				+ "<" 
				+ mChipIndex + ","
				+ mPlaneIndex + ","
				+ mBlockIndex
				+ "> as active";
	}
	
	@Override
	public String getDisplayName() {
		return "Allocate block B as active";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("chipIndex", int.class, "Chip"));
		mComponents.add(new BreakpointComponent("planeIndex", int.class, "Plane"));
		mComponents.add(new BreakpointComponent("blockIndex", int.class, "Block"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof AllocateActiveBlock)) return false; 
		AllocateActiveBlock otherCasted = (AllocateActiveBlock) other;
		
		return mBlockIndex == otherCasted.getBlockIndex()
				&& mPlaneIndex == otherCasted.getPlaneIndex()
				&& mChipIndex == otherCasted.getChipIndex();
	}
}
