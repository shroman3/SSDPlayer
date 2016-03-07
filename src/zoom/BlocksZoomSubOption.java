package zoom;

public enum BlocksZoomSubOption {
	VALID_COUNT("Valid count"), 
	ERASE_COUNT("Erase count"), 
	AVG_TEMP("Average temperature"), 
	AVG_WRITE_LVL("Average write level");

	private String mName;

	BlocksZoomSubOption(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}
}
