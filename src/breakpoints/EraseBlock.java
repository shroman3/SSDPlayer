package breakpoints;

import entities.Device;

public class EraseBlock extends BreakpointBase {
	private int mBlockIndex;
	private int mPlaneIndex;
	private int mChipIndex;

	public EraseBlock() {
		super();
	}

	@Override
	public boolean breakpointHit(Device<?> previousDevice, Device<?> currentDevice) {
		if (previousDevice == null) {
			return false;
		}
		int previousBlockEraseCount = previousDevice.getChipByIndex(mChipIndex).getPlane(mPlaneIndex)
				.getBlock(mBlockIndex).getEraseCounter();
		int currentBlockEraseCount = currentDevice.getChipByIndex(mChipIndex).getPlane(mPlaneIndex)
				.getBlock(mBlockIndex).getEraseCounter();
		return previousBlockEraseCount < currentBlockEraseCount;
	}

	public int getBlockIndex() {
		return mBlockIndex;
	}

	public void setBlockIndex(int blockIndex) throws Exception {
		if (!BreakpointsConstraints.isBlockIndexLegal(blockIndex)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_BLOCK);
		}

		mBlockIndex = blockIndex;
	}

	public int getPlaneIndex() {
		return mPlaneIndex;
	}

	public void setPlaneIndex(int planeIndex) throws Exception {
		if (!BreakpointsConstraints.isPlaneIndexLegal(planeIndex)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_PLANE);
		}

		mPlaneIndex = planeIndex;
	}

	public int getChipIndex() {
		return mChipIndex;
	}

	public void setChipIndex(int chipIndex) throws Exception {
		if (!BreakpointsConstraints.isChipIndexLegal(chipIndex)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_CHIP);
		}

		mChipIndex = chipIndex;
	}

	@Override
	public String getDisplayName() {
		return "Erase block B";
	}

	@Override
	public String getDescription() {
		return "Erase block <chip,plane,block>: " + "<" + mChipIndex + "," + mPlaneIndex + "," + mBlockIndex + ">";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("chipIndex", int.class, "Chip"));
		mComponents.add(new BreakpointComponent("planeIndex", int.class, "Plane"));
		mComponents.add(new BreakpointComponent("blockIndex", int.class, "Block"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof EraseBlock))
			return false;
		EraseBlock otherCasted = (EraseBlock) other;

		return mBlockIndex == otherCasted.getBlockIndex() && mPlaneIndex == otherCasted.getPlaneIndex()
				&& mChipIndex == otherCasted.getChipIndex();
	}

	@Override
	public String getHitDescription() {
		return "Block <chip,plane,block>: " + "<" + mChipIndex + "," + mPlaneIndex + "," + mBlockIndex + "> was erased";
	}
}
