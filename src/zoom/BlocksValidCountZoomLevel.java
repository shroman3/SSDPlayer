package zoom;

import general.ConfigProperties;
import general.Consts;
import manager.SSDManager;
import manager.VisualConfig;

public class BlocksValidCountZoomLevel implements IZoomLevel {
	public static final String NAME = "Valid Count";
		
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.setShowPages(false);
		visualConfig.setShowCounters(false);
		visualConfig.smallerPages();
		visualConfig.setBlocksColorRange(Consts.defaultColorRange);
		visualConfig.setRangeHighValue(ConfigProperties.getPagesInBlock());
		visualConfig.setRangeLowValue(0);
		visualConfig.setBlocksColorMeaning(VisualConfig.BlockColorMeaning.VALID_COUNT);
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
