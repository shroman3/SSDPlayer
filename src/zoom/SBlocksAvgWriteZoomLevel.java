package zoom;

import manager.SSDManager;

public class SBlocksAvgWriteZoomLevel implements IZoomLevel  {
	public static final String NAME = "Small Blocks - average write level";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager) {
		//
	}

	@Override
	public String getName() {
		return NAME;
	}

}
