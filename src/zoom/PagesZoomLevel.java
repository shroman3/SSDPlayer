package zoom;

import manager.SSDManager;

public class PagesZoomLevel implements IZoomLevel {
	public static final String NAME = "Pages";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager) {
		System.out.println("Applying " + getGroup() + " " + NAME);
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
