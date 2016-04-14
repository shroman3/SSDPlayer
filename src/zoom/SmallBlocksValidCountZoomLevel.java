package zoom;

import manager.SSDManager;
import manager.VisualConfig;

public class SmallBlocksValidCountZoomLevel implements IZoomLevel  {
	public static final String NAME = "Valid count";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.smallerPages();
		visualConfig.setShowPages(false);
		visualConfig.setBlocksColorMeaning(VisualConfig.BlockColorMeaning.VALID_COUNT);
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getGroup() {
		return "Small Blocks";
	}

}
