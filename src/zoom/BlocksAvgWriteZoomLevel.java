package zoom;

import manager.SSDManager;

public class BlocksAvgWriteZoomLevel implements IZoomLevel  {
	public static final String NAME = "Blocks - average write level";
	
	@Override
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager) {
		System.out.println("Applying Blocks - average write level");
	}

	@Override
	public String getName() {
		return NAME;
	}

}
