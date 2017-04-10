package breakpoints;

import entities.Device;
import general.ConfigProperties;

public class EraseCountAnyBlock extends BreakpointBase {

	private int mCount;
	private int mChipIndex;
	private int mPlaneIndex;
	private int mBlockIndex;

	public EraseCountAnyBlock() {
	}

	@Override
	public boolean breakpointHit(Device<?> previousDevice, Device<?> currentDevice) {
		if (previousDevice == null) {
			return false;
		}

		int numberOfBlocks = ConfigProperties.getBlocksInDevice();

		for (int i = 0; i < numberOfBlocks; i++) {
			boolean blockReachedCount = previousDevice.getBlockByIndex(i).getEraseCounter() != getCount()
					&& currentDevice.getBlockByIndex(i).getEraseCounter() == getCount();
			if (blockReachedCount) {
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
		return "Any block reaches erase count";
	}

	@Override
	public String getDescription() {
		return "Any block reaches erase count of " + getCount();
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("count", int.class, "Erase count"));
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int count) throws Exception {
		if (!BreakpointsConstraints.isCountValueLegal(count)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_COUNT);
		}

		mCount = count;
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof EraseCountAnyBlock))
			return false;
		EraseCountAnyBlock otherCasted = (EraseCountAnyBlock) other;

		return mCount == otherCasted.getCount();
	}

	@Override
	public String getHitDescription() {
		return "Block <chip,plane,block>: " + "<" + mChipIndex + "," + mPlaneIndex + "," + mBlockIndex
				+ "> reached erase count " + getCount();
	}
}
