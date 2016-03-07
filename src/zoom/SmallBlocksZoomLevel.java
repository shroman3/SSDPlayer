package zoom;

public class SmallBlocksZoomLevel extends ZoomLevel {
	public SmallBlocksZoomLevel() {
		super();
		mName = "Small blocks";
	}
	
	public void addSubOption(BlocksZoomSubOption option) {
		addSubOption(option.getName());
	}
}
