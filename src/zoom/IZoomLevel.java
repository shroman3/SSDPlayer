package zoom;

import java.awt.Color;
import java.util.List;

public interface IZoomLevel extends IZoomAction {
	public String getName();
	
	public String getGroup();
	
	public List<Color> getPalette();
}
