package breakpoints;

import org.w3c.dom.Element;

import entities.Device;

public interface IBreakpoint {
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice);
	
	public void readXml(Element xmlElement);
}
