package manager;

import java.util.List;

import entities.RAID.RAIDBasicBlock;
import entities.RAID.RAIDBasicChip;
import entities.RAID.RAIDBasicDevice;
import entities.RAID.RAIDBasicPage;
import entities.RAID.RAIDBasicPlane;
import ui.AddressWidget;

public abstract class RAIDBasicSSDManager<
	P extends RAIDBasicPage,
	B extends RAIDBasicBlock<P>,
	T extends RAIDBasicPlane<P,B>,
	C extends RAIDBasicChip<P,B,T>,
	D extends RAIDBasicDevice<P,B,T,C>> extends SSDManager<P,B,T,C,D> {
	
	public List<AddressWidget<P,B,T,C,D,RAIDBasicSSDManager<P,B,T,C,D>>> getAddressGetterWidgets() {
		return null;
	}
	
	public boolean hasStripes() {
		return true;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setDevice(RAIDBasicSSDManager manager, SettableTraceParser parser, RAIDBasicDevice device) {
		Class<?> parserDeviceType = parser.getCurrentDevice().getClass();
		if (parserDeviceType.isAssignableFrom(device.getClass())) {
			parser.setDevice(device);
		} else {
			throw new IllegalArgumentException("Something went wrong in setting device to parser. Device type: " +
								device.getClass() + " Parser Device Type: " + parserDeviceType);
		}
	}
}
