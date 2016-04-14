package zoom;

import manager.SSDManager;
import manager.VisualConfig;

public class SmallBlocksAvgWriteZoomLevel implements IZoomLevel  {
	public static final String NAME = "Average write level";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.smallerPages();
		visualConfig.setShowPages(false);
		visualConfig.setBlocksColorMeaning(VisualConfig.BlockColorMeaning.AVERAGE_WRITE_LEVEL);
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
