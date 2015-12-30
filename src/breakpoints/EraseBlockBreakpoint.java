package breakpoints;

import manager.SSDManager;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.Device;

public class EraseBlockBreakpoint implements IBreakpoint {
	private int mBlockIndex;
	private SSDManager<?, ?, ?, ?, ?> mManager;
	
	public EraseBlockBreakpoint() {
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

	@Override
	public void readXml(Element xmlElement) {
		NodeList blockIndexNodes = xmlElement.getElementsByTagName("blockIndex");
		if (blockIndexNodes.getLength() == 0) {
			throw new RuntimeException("Couldn't find blockIndex tag under breakpoint");
		}
		
		this.mBlockIndex = Integer.parseInt(blockIndexNodes.item(0).getTextContent());
	}
	
	@Override
	public void setManager(SSDManager<?, ?, ?, ?, ?> manager) {
		mManager = manager;
	}

}
