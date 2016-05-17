package zoom;

import java.awt.Color;
import java.util.List;
import manager.ReusableSSDManager;
import manager.SSDManager;
import manager.VisualConfig;

public class SmallBlocksAvgWriteZoomLevel implements IZoomLevel  {
	public static final String NAME = "Average Write Level";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.setShowCounters(false);
		visualConfig.setShowPages(false);
		visualConfig.extraSmallerPages();
		visualConfig.setBlocksColorMeaning(VisualConfig.BlockColorMeaning.AVERAGE_WRITE_LEVEL);
		if(manager instanceof ReusableSSDManager){
			ReusableSSDManager reusableManager = ((ReusableSSDManager) manager);
			visualConfig.setBlocksColorRange(utils.UIUtils.createRange(reusableManager.getWriteLevelColor(1), reusableManager.getWriteLevelColor(2)));
			visualConfig.setRangeHighValue(2);
			visualConfig.setRangeLowValue(1);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getGroup() {
		return "Small Blocks";
	}

	@Override
	public List<Color> getPalette() {
		return null;
	}
}
