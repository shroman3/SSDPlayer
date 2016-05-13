package zoom;

import java.awt.Color;
import java.util.List;

import general.Consts;
import manager.SSDManager;
import manager.VisualConfig;

public class BlocksAvgTempZoomLevel implements IZoomLevel  {
	public static final String NAME = "Average Temperature";
	
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

	@Override
	public List<Color> getPalette() {
		return Consts.ColorRange;
	}
}
