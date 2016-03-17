package zoom;

import manager.SSDManager;
import manager.VisualConfig;

public class PagesZoomLevel implements IZoomLevel {
	public static final String NAME = "Pages";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig) {
		visualConfig.restoreXmlValues();
		visualConfig.setShowCounters(false);
		visualConfig.smallerPages();
		visualConfig.setThinCross(true);
		visualConfig.setMovedPattern(false);
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getGroup() {
		return null;
	}
}
