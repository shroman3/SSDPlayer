package breakpoints;

import java.util.List;

import manager.SSDManager;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.ActionLog;
import entities.Device;
import entities.IDeviceAction;
import entities.WriteLpAction;

public class WriteLpBreakpoint implements IBreakpoint {
	private int lp;
	private SSDManager<?, ?, ?, ?, ?> mManager;

	public WriteLpBreakpoint(){
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		ActionLog log = currentDevice.getLog();
		List<IDeviceAction> writeActions = log.getActionsByType(WriteLpAction.class);
		for(IDeviceAction action : writeActions){
			if(((WriteLpAction)action).get_lp() == lp){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void readXml(Element xmlElement) {
		NodeList lpNodes = xmlElement.getElementsByTagName("logicalPage");
		if (lpNodes.getLength() == 0) {
			throw new RuntimeException("Couldn't find logicalPage tag under breakpoint");
		}
		
		this.lp = Integer.parseInt(lpNodes.item(0).getTextContent());
	}
	
	@Override
	public void setManager(SSDManager<?, ?, ?, ?, ?> manager) {
		mManager = manager;
	}

}
