package zoom;

import java.util.ArrayList;
import java.util.List;

abstract public class ZoomLevel implements IZoomLevel {
	protected String mName;
	protected List<String> mSubOptions;
	
	public ZoomLevel() {
		mSubOptions = new ArrayList<>();
	}
	
	@Override
	public String getName() {
		return mName;
	}

	@Override
	public List<String> getSubOptions() {
		return mSubOptions;
	}

	@Override
	public void addSubOption(String subOptionName) {
		mSubOptions.add(subOptionName);
	}
}
