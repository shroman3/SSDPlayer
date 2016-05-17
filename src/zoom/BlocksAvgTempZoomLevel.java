package zoom;

import java.awt.Color;
import java.util.List;
import manager.HotColdSSDManager;
import general.Consts;
import manager.SSDManager;
import manager.VisualConfig;

public class BlocksAvgTempZoomLevel implements IZoomLevel  {
	public static final String NAME = "Average Temperature";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.setShowCounters(false);
		visualConfig.setShowPages(false);
		visualConfig.smallerPages();
		visualConfig.setBlocksColorMeaning(VisualConfig.BlockColorMeaning.AVERAGE_TEMPERATURE);
		if(manager instanceof HotColdSSDManager){
			HotColdSSDManager hotColdManager = ((HotColdSSDManager) manager);
			Color coldColor = hotColdManager.getTemperatureColor(hotColdManager.getMinTemperature());
			Color hotColor = hotColdManager.getTemperatureColor(hotColdManager.getMaxTemperature());
			visualConfig.setBlocksColorRange(utils.UIUtils.createRange(coldColor, hotColor));
			visualConfig.setRangeHighValue(hotColdManager.getMaxTemperature());
			visualConfig.setRangeLowValue(hotColdManager.getMinTemperature());
		}
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
		return Consts.defaultColorRange;
	}
}
