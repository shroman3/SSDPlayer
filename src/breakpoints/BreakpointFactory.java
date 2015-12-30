package breakpoints;

import java.util.HashMap;

import org.w3c.dom.Element;

public class BreakpointFactory {
	private HashMap<String, Class<? extends IBreakpoint>> mTypeMap;
	
	public BreakpointFactory() {
		mTypeMap = new HashMap<>();
		register(WriteLpBreakpoint.class);
		register(WritePpBreakpoint.class);
		register(AllocateActiveBlockBreakpoint.class);
		register(EraseBlockBreakpoint.class);
		register(WriteAmplificationBreakpoint.class);
		register(WritesPerEraseBreakpoint.class);
	}
	
	public IBreakpoint getBreakpoint(String type, Element breakpointElement) throws InstantiationException, IllegalAccessException {
		IBreakpoint breakpoint = (IBreakpoint) mTypeMap.get(type).newInstance();
		breakpoint.readXml(breakpointElement);
		return breakpoint;
	}
	
	public void register(Class<? extends IBreakpoint> bpClass) {
		mTypeMap.put(bpClass.getSimpleName(), bpClass);
	}
}
