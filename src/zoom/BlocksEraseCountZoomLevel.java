package zoom;

import manager.SSDManager;
import manager.VisualConfig;

public class BlocksEraseCountZoomLevel implements IZoomLevel {
	public static final String NAME = "Erase count";
		
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.setShowPages(false);
		visualConfig.setBlocksColorMeaning(VisualConfig.BlockColorMeaning.ERASE_COUNT);
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