package zoom;

import manager.SSDManager;

public class DetailedZoomLevel implements IZoomLevel {
	public static final String NAME = "Detailed";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager) {
		//
	}

	@Override
	public String getName() {
		return NAME;
	}

}
