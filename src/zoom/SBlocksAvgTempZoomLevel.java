package zoom;

import manager.SSDManager;

public class SBlocksAvgTempZoomLevel implements IZoomLevel  {
	public static final String NAME = "Small Blocks - average temperature";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager) {
		//
	}

	@Override
	public String getName() {
		return NAME;
	}

}
