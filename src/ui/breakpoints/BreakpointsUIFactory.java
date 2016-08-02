package ui.breakpoints;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import breakpoints.BreakpointBase;
import breakpoints.BreakpointFactory;
import breakpoints.CleanBlocksChip;
import breakpoints.CleanBlocksDevice;
import breakpoints.CleanBlocksPlane;
import breakpoints.GCNthTimeChip;
import breakpoints.GCNthTimeDevice;
import breakpoints.GCNthTimePlane;
import breakpoints.PagesWrittenChip;
import breakpoints.PagesWrittenDevice;
import breakpoints.PagesWrittenPlane;

public class BreakpointsUIFactory {
	private static List<BreakpointsGroup> mGroups;
	private static List<SingleBreakpoint> mSingleBreakpoints;
	
	public static void init() {
		mGroups = new ArrayList<>();
		mSingleBreakpoints = new ArrayList<>();
		
		LinkedHashMap<String, Class<? extends BreakpointBase>> gcGroup = new LinkedHashMap<>();
		gcGroup.put("Device", GCNthTimeDevice.class);
		gcGroup.put("Chip", GCNthTimeChip.class);
		gcGroup.put("Plane", GCNthTimePlane.class);
		registerGroup(gcGroup, "Invoke garbage collection in the i-th time");
		
		LinkedHashMap<String, Class<? extends BreakpointBase>> cleanBlocksGroup = new LinkedHashMap<>();
		cleanBlocksGroup.put("Device", CleanBlocksDevice.class);
		cleanBlocksGroup.put("Chip", CleanBlocksChip.class);
		cleanBlocksGroup.put("Plane", CleanBlocksPlane.class);
		registerGroup(cleanBlocksGroup, "Number of clean blocks is X");
		
		LinkedHashMap<String, Class<? extends BreakpointBase>> writtenPagesGroup = new LinkedHashMap<>();
		writtenPagesGroup.put("Device", PagesWrittenDevice.class);
		writtenPagesGroup.put("Chip", PagesWrittenChip.class);
		writtenPagesGroup.put("Plane", PagesWrittenPlane.class);
		registerGroup(writtenPagesGroup, "L pages are written");
		
		registerSingleBreakpoints();
	}
	
	private static void registerSingleBreakpoints() {
		List<Class<? extends BreakpointBase>> classes = BreakpointFactory.getBreakpointClasses();
		
		for (int i = 0; i < classes.size(); i++) {
			Class<? extends BreakpointBase> bpClass = classes.get(i);
			
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

	private static void registerGroup(LinkedHashMap<String, Class<? extends BreakpointBase>> group, 
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
