package zoom;

import manager.SSDManager;
import manager.VisualConfig;

public interface IZoomAction {
	public void applyZoom(SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig);
}
