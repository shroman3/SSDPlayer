package breakpoints;

import entities.Device;

public interface IBreakpoint {
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice);
}
