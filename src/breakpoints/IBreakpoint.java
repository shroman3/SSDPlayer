package breakpoints;

import manager.SSDManager;

import org.w3c.dom.Element;

import entities.Device;

public interface IBreakpoint {
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice);
	
	//TODO: Dolev- readXml and setManager should be implemented in baseBreakpoint. 
	//I didn't do it because I know you're about to implement baseBreakpoint and didn't want to have conflicts :)  
	public void readXml(Element xmlElement);
	
	public void setManager(SSDManager<?, ?, ?, ?, ?> manager);
}
