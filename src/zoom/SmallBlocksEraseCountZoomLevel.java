package zoom;

import general.ConfigProperties;
import general.Consts;
import manager.SSDManager;
import manager.VisualConfig;

public class SmallBlocksEraseCountZoomLevel implements IZoomLevel  {
	public static final String NAME = "Erase Count";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.setShowCounters(false);
		visualConfig.extraSmallerPages();
		visualConfig.setShowPages(false);
		visualConfig.setBlocksColorRange(Consts.defaultColorRange);
		visualConfig.setRangeHighValue(ConfigProperties.getMaxErasures());
		visualConfig.setRangeLowValue(0);
		visualConfig.setBlocksColorMeaning(VisualConfig.BlockColorMeaning.ERASE_COUNT);
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
