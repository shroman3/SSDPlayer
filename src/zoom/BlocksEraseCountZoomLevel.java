package zoom;

import manager.SSDManager;

public class BlocksEraseCountZoomLevel implements IZoomLevel {
	public static final String NAME = "Erase count";
		
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
		return "Blocks";
	}
}
