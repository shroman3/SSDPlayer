package breakpoints;

import manager.SSDManager;
import manager.WriteAmplificationGetter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.Device;
import entities.StatisticsGetter;

public class WriteAmplificationBreakpoint implements IBreakpoint {
	private double mValue;

	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		for(StatisticsGetter getter : SSDManager.getCurrentManager().getStatisticsGetters()){
			if(WriteAmplificationGetter.class.isInstance(getter)){
				double oldValue = previousDevice == null ? Double.MIN_VALUE : getter.getStatistics(previousDevice).get(0).getValue();
				double currentValue = getter.getStatistics(currentDevice).get(0).getValue();
				return oldValue < mValue && currentValue >= mValue;
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
		
		this.mValue = Double.parseDouble(physicalPageNodes.item(0).getTextContent());

	}

}
