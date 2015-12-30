package breakpoints;

import manager.SSDManager;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.Device;

public class WritePpBreakpoint implements IBreakpoint {
	private int mPhysicalPage;
	private SSDManager<?, ?, ?, ?, ?> mManager;
	
	public WritePpBreakpoint() {
	} 
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		boolean currentPageIsClean = currentDevice.getPageByIndex(mPhysicalPage).isClean();
		if(previousDevice == null){
			return !currentPageIsClean;
		}
		
		boolean previousPageIsclean = previousDevice.getPageByIndex(mPhysicalPage).isClean();
		return previousPageIsclean && !currentPageIsClean;
	}

	@Override
	public void readXml(Element xmlElement) {
		NodeList physicalPageNodes = xmlElement.getElementsByTagName("physicalPage");
		if (physicalPageNodes.getLength() == 0) {
			throw new RuntimeException("Couldn't find physicalPage tag under breakpoint");
		}
		
		this.mPhysicalPage = Integer.parseInt(physicalPageNodes.item(0).getTextContent());

	}
	
	@Override
	public void setManager(SSDManager<?, ?, ?, ?, ?> manager) {
		mManager = manager;
	}

}
