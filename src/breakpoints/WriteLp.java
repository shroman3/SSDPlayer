package breakpoints;

import java.util.List;

import entities.ActionLog;
import entities.Device;
import entities.IDeviceAction;
import entities.WriteLpAction;

public class WriteLp extends BreakpointBase {
	private int lp;

	public WriteLp() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		ActionLog log = currentDevice.getLog();
		List<IDeviceAction> writeActions = log.getActionsByType(WriteLpAction.class);
		for(IDeviceAction action : writeActions){
			if(((WriteLpAction)action).get_lp() == lp){
				return true;
			}
		}
		
		return false;
	}

	public int getLp() {
		return lp;
	}
	
	public void setLp(int logicalPage) {
		lp = logicalPage;
	}
	
	@Override
	public String getDescription() {
		return "Write logical page " + lp;
	}

	@Override
	public String getDisplayName() {
		return "Write logical page L";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("lp", int.class, "Logical page"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof WriteLp)) return false; 
		WriteLp otherCasted = (WriteLp) other;
		
		return lp == otherCasted.getLp();
	}
}
