package zoom;

import general.ConfigProperties;
import general.Consts;
import manager.SSDManager;
import manager.VisualConfig;

public class SmallBlocksValidCountZoomLevel implements IZoomLevel  {
	public static final String NAME = "Valid Count";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.setShowCounters(false);
		visualConfig.extraSmallerPages();
		visualConfig.setShowPages(false);
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
		return "Small Blocks";
	}
}
