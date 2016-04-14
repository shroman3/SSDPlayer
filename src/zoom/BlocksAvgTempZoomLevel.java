package zoom;

import manager.SSDManager;
import manager.VisualConfig;

public class BlocksAvgTempZoomLevel implements IZoomLevel  {
	public static final String NAME = "Average temperature";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.setShowPages(false);
		visualConfig.setBlocksColorMeaning(VisualConfig.BlockColorMeaning.AVERAGE_TEMPERATURE);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getGroup() {
		return "Blocks";
	}
}
