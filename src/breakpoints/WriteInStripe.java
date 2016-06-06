package breakpoints;

import java.util.List;

import entities.ActionLog;
import entities.Device;
import entities.IDeviceAction;
import entities.WriteInStripeAction;
import manager.RAIDSSDManager;
import manager.RAIDVisualizationSSDManager;
import manager.SSDManager;

public class WriteInStripe extends BreakpointBase {
	private int mStripe;

	public WriteInStripe() {
		super();
	}
	
	@Override
	public boolean breakpointHit(Device<?, ?, ?, ?> previousDevice,
			Device<?, ?, ?, ?> currentDevice) {
		List<IDeviceAction> writeActions = ActionLog.getActionsByType(WriteInStripeAction.class);
		for(IDeviceAction action : writeActions){
			if(((WriteInStripeAction)action).getStripe() == mStripe){
				return true;
			}
		}
		
		return false;
	}

	public int getStripe() {
		return mStripe;
	}
	
	public void setStripe(int stripe) throws Exception {
		if (!BreakpointsConstraints.isStripeLegal(stripe)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_STRIPE);
		}
		
		mStripe = stripe;
	}
	
	@Override
	public String getDescription() {
		return "Logical write in stripe " + mStripe;
	}

	@Override
	public String getDisplayName() {
		return "Logical write in stripe P";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("stripe", int.class, "Stripe"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof WriteInStripe)) return false; 
		WriteInStripe otherCasted = (WriteInStripe) other;
		
		return mStripe == otherCasted.getStripe();
	}
	
	@Override
	public String getHitDescription() {
		return "Logical write in stripe " + mStripe;
	}
	
	@Override
	public boolean isManagerSupported(SSDManager<?, ?, ?, ?, ?> manager) {
		if (manager instanceof RAIDSSDManager 
				|| manager instanceof RAIDVisualizationSSDManager) {
			return true;
		}
		
		return false;
	}
}
