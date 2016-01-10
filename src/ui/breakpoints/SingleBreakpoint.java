package ui.breakpoints;

import breakpoints.BreakpointBase;

public class SingleBreakpoint implements IBreakpointCBoxEntry {
	private Class<? extends BreakpointBase> mBpClass;
	
	public SingleBreakpoint(Class<? extends BreakpointBase> bpClass) {
		mBpClass = bpClass;
	}
	
	@Override
	public String getDisplayName() {
		try {
			return mBpClass.newInstance().getDisplayName();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Class<? extends BreakpointBase> getBPClass() {
		return mBpClass;
	}
	
}
