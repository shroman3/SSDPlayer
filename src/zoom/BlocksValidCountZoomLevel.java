package zoom;

import manager.SSDManager;

public class BlocksValidCountZoomLevel implements IZoomLevel {
	public static final String NAME = "Blocks - valid count";
		
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager) {
		System.out.println("Applying Blocks - valid count");
	}

	@Override
	public String getName() {
		return NAME;
	}

}
