package ui.breakpoints;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import breakpoints.BreakpointBase;
import breakpoints.BreakpointFactory;

public class BreakpointsUIFactory {
	private static List<BreakpointsGroup> mGroups;
	private static List<SingleBreakpoint> mSingleBreakpoints;
	
	public static void init() {
		mGroups = new ArrayList<>();
		mSingleBreakpoints = new ArrayList<>();
		
		//For Roee - this is an example of registering a group
		
//		SortedMap<String, Class<? extends BreakpointBase>> writeGroup = new TreeMap<>();
//		writeGroup.put("Physical", WritePpBreakpoint.class);
//		writeGroup.put("Logical", WriteLpBreakpoint.class);
//		registerGroup(writeGroup, "Invoke garbage collection in the i-th time");
		
		registerSingleBreakpoints();
	}
	
	private static void registerSingleBreakpoints() {
		List<Class<? extends BreakpointBase>> classes = BreakpointFactory.getBreakpointClasses();
		
		for (Class<? extends BreakpointBase> bpClass : classes) {
			boolean inGroup = false;
			for (BreakpointsGroup bpGroup : mGroups) {
				if (bpGroup.contains(bpClass)) {
					inGroup = true;
					break;
				}
			}
			
			if (!inGroup) {
				mSingleBreakpoints.add(new SingleBreakpoint(bpClass));
			}
		}
	}

	private static void registerGroup(SortedMap<String, Class<? extends BreakpointBase>> group, 
			String groupDisplayName) {
		mGroups.add(new BreakpointsGroup(group, groupDisplayName));
	}
	
	public static List<BreakpointsGroup> getBreakpointGroups() {
		return mGroups;
	}
	
	public static List<SingleBreakpoint> getSingleBreakpoints() {
		return mSingleBreakpoints;
	}
}
