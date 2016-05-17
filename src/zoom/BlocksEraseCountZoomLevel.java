package zoom;

import java.awt.Color;
import java.util.List;
import general.ConfigProperties;
import general.Consts;
import manager.SSDManager;
import manager.VisualConfig;

public class BlocksEraseCountZoomLevel implements IZoomLevel {
	public static final String NAME = "Erase Count";
		
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.setShowPages(false);
		visualConfig.setShowCounters(false);
		visualConfig.smallerPages();
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
		return "Blocks";
	}

	@Override
	public List<Color> getPalette() {
		return null;
	}
}
