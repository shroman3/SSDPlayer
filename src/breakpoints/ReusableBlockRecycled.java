package breakpoints;

import entities.Device;
import entities.reusable.ReusableBlock;
import entities.reusable.ReusableBlockStatus;
import entities.reusable.ReusableDevice;

public class ReusableBlockRecycled extends BreakpointBase {

	private int mBlockIndex;

	public ReusableBlockRecycled() {
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
		
		ReusableBlock currentBlock = (ReusableBlock) currentDevice.getBlockByIndex(mBlockIndex);
		ReusableBlock prevBlock = (ReusableBlock) previousDevice.getBlockByIndex(mBlockIndex);
		return (currentBlock.getStatus() == ReusableBlockStatus.RECYCLED 
				&& prevBlock.getStatus() != ReusableBlockStatus.RECYCLED);
	}

	@Override
	public String getDisplayName() {
		return "Reusable block B is recycled";
	}

	@Override
	public String getDescription() {
		return "Reusable block " + getBlockIndex() + " is recycled";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("blockIndex", int.class, "Block"));
	}

	public int getBlockIndex() {
		return mBlockIndex;
	}

	public void setBlockIndex(int mBlockIndex) {
		this.mBlockIndex = mBlockIndex;
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof ReusableBlockRecycled)) return false; 
		ReusableBlockRecycled otherCasted = (ReusableBlockRecycled) other;
		
		return mBlockIndex == otherCasted.getBlockIndex();
	}

}
