package zoom;

import manager.SSDManager;

public class BlocksEraseCountZoomLevel implements IZoomLevel {
	public static final String NAME = "Blocks - erase count";
		
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager) {
		System.out.println("Applying Blocks - erase count");
	}

	@Override
	public String getName() {
		return NAME;
	}

}
