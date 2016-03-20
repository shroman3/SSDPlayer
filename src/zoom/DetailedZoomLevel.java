package zoom;

import manager.SSDManager;

public class DetailedZoomLevel implements IZoomLevel {
	public static final String NAME = "Detailed";
	
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
