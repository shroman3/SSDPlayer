package breakpoints;

import manager.SSDManager;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.BlockStatus;
import entities.BlockStatusGeneral;
import entities.Device;

public class AllocateActiveBlockBreakpoint implements IBreakpoint {
	private int mBlockIndex;
	private SSDManager<?, ?, ?, ?, ?> mManager;
	
	public AllocateActiveBlockBreakpoint() {}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice) {
		BlockStatus currStatus = currentDevice.getBlockByIndex(mBlockIndex).getStatus();
		if (previousDevice == null) {
			if (isBlockActive(currStatus)) return true;
			return false;
		}
		
		BlockStatus prevStatus = previousDevice.getBlockByIndex(mBlockIndex).getStatus();
		if (!isBlockActive(prevStatus) && isBlockActive(currStatus)) return true;
		
		return false;
	}

	@Override
	public void readXml(Element xmlElement) {
		NodeList blockIndexNodes = xmlElement.getElementsByTagName("blockIndex");
		if (blockIndexNodes.getLength() == 0) {
			throw new RuntimeException("Couldn't find blockIndex tag under breakpoint");
		}
		
		this.mBlockIndex = Integer.parseInt(blockIndexNodes.item(0).getTextContent());
	}
	
	private boolean isBlockActive(BlockStatus prevStatus) {
		return prevStatus.getStatusName().equals(BlockStatusGeneral.ACTIVE.getStatusName());
	}

	@Override
	public void setManager(SSDManager<?, ?, ?, ?, ?> manager) {
		mManager = manager;
	}
}
