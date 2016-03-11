package zoom;

import manager.SSDManager;

public class BlocksAvgTempZoomLevel implements IZoomLevel  {
	public static final String NAME = "Blocks - average temperature";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager) {
		System.out.println("Applying Blocks - average temperature");
	}

	@Override
	public String getName() {
		return NAME;
	}

}
