package zoom;

import manager.SSDManager;

public class SBlocksValidCountZoomLevel implements IZoomLevel  {
	public static final String NAME = "Valid count";
	
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
		return "Small Blocks";
	}

}
