package zoom;

import manager.SSDManager;
import manager.VisualConfig;

public class DetailedZoomLevel implements IZoomLevel {
	public static final String NAME = "Detailed";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.setRangeLowValue(null);
		visualConfig.setRangeHighValue(null);
		visualConfig.setBlocksColorRange(null);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getGroup() {
		return null;
	}
}
