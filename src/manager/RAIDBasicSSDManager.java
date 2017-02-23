package manager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import entities.RAID.RAIDBasicBlock;
import entities.RAID.RAIDBasicChip;
import entities.RAID.RAIDBasicDevice;
import entities.RAID.RAIDBasicPage;
import entities.RAID.RAIDBasicPlane;
import general.XMLGetter;
import general.XMLParsingException;
import ui.AddressWidget;

public abstract class RAIDBasicSSDManager<P extends RAIDBasicPage, B extends RAIDBasicBlock<P>, T extends RAIDBasicPlane<P, B>, C extends RAIDBasicChip<P, B, T>, D extends RAIDBasicDevice<P, B, T, C>>
		extends SSDManager<P, B, T, C, D> {
	private Color dataPageColor;
	private List<Color> paritiesColors;
	private Color stripeFrameColor;
	private Color stripeFrameStepColor;
	private final int maxColorsNum = 1000;
	private Boolean showOldParity; // indicates whether the invalid parity pages should be highlighted
	private Boolean showOldData; // indicates whether the invalid data pages should be highlighted

	public List<AddressWidget<P, B, T, C, D, RAIDBasicSSDManager<P, B, T, C, D>>> getAddressGetterWidgets() {
		return null;
	}

	public boolean hasStripes() {
		return true;
	}
	public Color getDataPageColor() {
		return dataPageColor;
	}
	
	public Color getParityPageColor(int parityNumber) {
		return paritiesColors.get(parityNumber-1);
	}
	
	public Color getStripeFrameColor(int index) {
		int updatedIndex = index % maxColorsNum;
		int r = (stripeFrameColor.getRed() + updatedIndex * stripeFrameStepColor.getRed()) % 256;
		int g = (stripeFrameColor.getGreen() + updatedIndex * stripeFrameStepColor.getGreen()) % 256;
		int b = (stripeFrameColor.getBlue() + updatedIndex * stripeFrameStepColor.getBlue()) % 256;
		return new Color(r, g, b);
	}
	
	public Boolean toShowOldParity() {
		return showOldParity;
	}
	
	public Boolean toShowOldData() {
		return showOldData;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setDevice(RAIDBasicSSDManager manager, SettableTraceParser parser, RAIDBasicDevice device) {
		Class<?> parserDeviceType = parser.getCurrentDevice().getClass();
		if (parserDeviceType.isAssignableFrom(device.getClass())) {
			parser.setDevice(device);
		} else {
			throw new IllegalArgumentException("Something went wrong in setting device to parser. Device type: "
					+ device.getClass() + " Parser Device Type: " + parserDeviceType);
		}
	}
	
	@Override
	protected void initValues(XMLGetter xmlGetter) throws XMLParsingException {
		super.initValues(xmlGetter);
		dataPageColor = getColorField(xmlGetter, "data_color");
		paritiesColors = new ArrayList<Color>(getColorsListField(xmlGetter, "parity_color"));
		stripeFrameColor = getColorField(xmlGetter, "stripe_frame_color");
		stripeFrameStepColor = getColorField(xmlGetter, "stripe_frame_step");
		
		showOldParity = xmlGetter.getBooleanField(VisualConfig.VISUAL_CONFIG, "show_old_parity");
		showOldData = xmlGetter.getBooleanField(VisualConfig.VISUAL_CONFIG, "show_old_data");
	}
}
