package zoom;

import java.util.List;

public interface IZoomLevel {
	public String getName();
	
	public List<String> getSubOptions();
	
	public void addSubOption(String subOptionName);
}
