package breakpoints;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.Device;
import general.ConfigProperties;

public class EraseCountBlockBreakpoint implements IBreakpoint {

	private Integer mBlockIndex;
	private Integer mCount;
	
	public EraseCountBlockBreakpoint() {
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		if(previousDevice == null){
			return false;
		}
		
		if (mBlockIndex != null){
			return previousDevice.getBlockByIndex(mBlockIndex).getEraseCounter() != mCount 
					&& currentDevice.getBlockByIndex(mBlockIndex).getEraseCounter() == mCount;			
		}
		
		int numberOfBlocks = ConfigProperties.getBlocksInDevice();
		for (int i=0; i < numberOfBlocks; i++){
			boolean blockReachedCount = previousDevice.getBlockByIndex(i).getEraseCounter() != mCount 
					&& currentDevice.getBlockByIndex(i).getEraseCounter() == mCount;
			if(blockReachedCount){
				return true;
			}
		}
		return false; 
	}

	@Override
	public void readXml(Element xmlElement) {
		NodeList blockIndexNodes = xmlElement.getElementsByTagName("blockIndex");
		if (blockIndexNodes.getLength() == 0) {
			this.mBlockIndex = null;
		}
		else{
			this.mBlockIndex = Integer.parseInt(blockIndexNodes.item(0).getTextContent());
		}
		
		NodeList countNodes = xmlElement.getElementsByTagName("count");
		if (blockIndexNodes.getLength() == 0) {
			throw new RuntimeException("Couldn't find count tag under breakpoint");
		}
		
		this.mCount = Integer.parseInt(countNodes.item(0).getTextContent());
	}

}
