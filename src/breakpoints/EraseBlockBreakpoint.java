package breakpoints;

import manager.SSDManager;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.Device;

public class EraseBlockBreakpoint extends BreakpointBase {
	private int mBlockIndex;
	
	public EraseBlockBreakpoint() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if(previousDevice == null){
			return false;
		}
		int previousBlockEraseCount = previousDevice.getBlockByIndex(mBlockIndex).getEraseCounter();
		int currentBlockEraseCount = currentDevice.getBlockByIndex(mBlockIndex).getEraseCounter();
		return previousBlockEraseCount < currentBlockEraseCount;
	}

	public int getBlockIndex() {
		return mBlockIndex;
	}

	public void setBlockIndex(int blockIndex) {
		mBlockIndex = blockIndex;
	}

	@Override
	public String getDisplayName() {
		return "Erase block B";
	}

	@Override
	public String getDescription() {
		return "Erase block " + mBlockIndex;
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("blockIndex", int.class, "Block"));
	}
}
