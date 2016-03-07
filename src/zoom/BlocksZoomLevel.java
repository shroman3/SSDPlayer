package zoom;

public class BlocksZoomLevel extends ZoomLevel {
	
	public BlocksZoomLevel() {
		super();
		mName = "Blocks";
	}
	
	public void addSubOption(BlocksZoomSubOption option) {
		addSubOption(option.getName());
	}
}
