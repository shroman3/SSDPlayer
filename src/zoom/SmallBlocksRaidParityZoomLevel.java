package zoom;

import java.awt.Color;

import manager.RAIDSSDManager;
import manager.SSDManager;
import manager.VisualConfig;
import utils.UIUtils;

public class SmallBlocksRaidParityZoomLevel implements IZoomLevel {
	public static final String NAME = "Raid Parity";

	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.setShowPages(false);
		visualConfig.setShowCounters(false);
		visualConfig.extraSmallerPages();
		visualConfig.setBlocksColorMeaning(VisualConfig.BlockColorMeaning.PARITY_COUNT);
		if ((manager instanceof RAIDSSDManager)) {
			RAIDSSDManager raidManager = (RAIDSSDManager) manager;
			Color dataColor = raidManager.getDataPageColor();
			Color parityColor = raidManager.getParityPageColor(1);
			visualConfig.setBlocksColorRange(UIUtils.createRange(dataColor, parityColor));
			visualConfig.setRangeHighValue(1);
			visualConfig.setRangeLowValue(0);
		}
	}

	public String getName() {
		return NAME;
	}

	public String getGroup() {
		return "Small Blocks";
	}
}
