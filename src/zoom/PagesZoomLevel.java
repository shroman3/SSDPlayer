package zoom;

import manager.SSDManager;

public class PagesZoomLevel implements IZoomLevel {
	public static final String NAME = "Pages";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager) {
		//
	}

	@Override
	public String getName() {
		return NAME;
	}

}
