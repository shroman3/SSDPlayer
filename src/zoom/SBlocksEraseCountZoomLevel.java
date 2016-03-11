package zoom;

import manager.SSDManager;

public class SBlocksEraseCountZoomLevel implements IZoomLevel  {
	public static final String NAME = "Small Blocks - erase count";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager) {
		//
	}

	@Override
	public String getName() {
		return NAME;
	}

}
