package zoom;

import manager.SSDManager;

public class SBlocksValidCountZoomLevel implements IZoomLevel  {
	public static final String NAME = "Small Blocks - valid count";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager) {
		//
	}

	@Override
	public String getName() {
		return NAME;
	}

}
