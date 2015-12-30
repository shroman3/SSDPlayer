package breakpoints;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.BlockStatus;
import entities.BlockStatusGeneral;
import entities.Device;
import general.ConfigProperties;

public class AllocateActiveBlockBreakpoint implements IBreakpoint {
	private int mBlockIndex;
	
	public AllocateActiveBlockBreakpoint() {}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice) {
		int blocksInChip = ConfigProperties.getPlanesInChip() * ConfigProperties.getBlocksInPlane();
		int chipIndex = mBlockIndex / blocksInChip;
		int blockRelativeToChipIndex = mBlockIndex - chipIndex * blocksInChip;
		int planeIndex = blockRelativeToChipIndex / ConfigProperties.getBlocksInPlane();
		int blockIndex = blockRelativeToChipIndex - planeIndex * ConfigProperties.getBlocksInPlane();
		
		BlockStatus currStatus = currentDevice.getChip(chipIndex).getPlane(planeIndex).getBlock(blockIndex).getStatus();
		if (previousDevice == null) {
			if (isBlockActive(currStatus)) return true;
			return false;
		}
		
		BlockStatus prevStatus = previousDevice.getChip(chipIndex).getPlane(planeIndex).getBlock(blockIndex).getStatus();
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
}
