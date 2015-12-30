package breakpoints;

import manager.SSDManager;
import manager.WriteAmplificationGetter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.Device;
import entities.StatisticsGetter;

public class WriteAmplificationBreakpoint implements IBreakpoint {

	private SSDManager<?, ?, ?, ?, ?> mManager;
	private float mValue;

	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		for(StatisticsGetter getter : mManager.getStatisticsGetters()){
			if(WriteAmplificationGetter.class.isInstance(getter)){
				return getter.getStatistics(currentDevice).get(0).getValue() == mValue;
			}
		}
		return false;
	}

	@Override
	public void readXml(Element xmlElement) {
		NodeList physicalPageNodes = xmlElement.getElementsByTagName("value");
		if (physicalPageNodes.getLength() == 0) {
			throw new RuntimeException("Couldn't find value tag under breakpoint");
		}
		
		this.mValue = Float.parseFloat(physicalPageNodes.item(0).getTextContent());

	}

	@Override
	public void setManager(SSDManager<?, ?, ?, ?, ?> manager) {
		mManager = manager;
	}

}
