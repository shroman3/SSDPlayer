package zoom;

import manager.SSDManager;

public class BlocksAvgWriteZoomLevel implements IZoomLevel  {
	public static final String NAME = "Average write level";
	
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
