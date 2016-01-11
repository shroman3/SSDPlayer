package ui.breakpoints;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import breakpoints.BreakpointBase;
import breakpoints.BreakpointFactory;
import breakpoints.ChipGCNthTime;
import breakpoints.CleanBlocksInChip;
import breakpoints.CleanBlocksInDevice;
import breakpoints.CleanBlocksInPlane;
import breakpoints.DeviceGCNthTime;
import breakpoints.PagesWrittenInChip;
import breakpoints.PagesWrittenInDevice;
import breakpoints.PagesWrittenInPlane;
import breakpoints.PlaneGCNthTime;

public class BreakpointsUIFactory {
	private static List<BreakpointsGroup> mGroups;
	private static List<SingleBreakpoint> mSingleBreakpoints;
	
	public static void init() {
		mGroups = new ArrayList<>();
		mSingleBreakpoints = new ArrayList<>();
		
		LinkedHashMap<String, Class<? extends BreakpointBase>> gcGroup = new LinkedHashMap<>();
		gcGroup.put("Device", DeviceGCNthTime.class);
		gcGroup.put("Chip", ChipGCNthTime.class);
		gcGroup.put("Plane", PlaneGCNthTime.class);
		registerGroup(gcGroup, "Invoke garbage collection in the i-th time");
		
		LinkedHashMap<String, Class<? extends BreakpointBase>> cleanBlocksGroup = new LinkedHashMap<>();
		cleanBlocksGroup.put("Device", CleanBlocksInDevice.class);
		cleanBlocksGroup.put("Chip", CleanBlocksInChip.class);
		cleanBlocksGroup.put("Plane", CleanBlocksInPlane.class);
		registerGroup(cleanBlocksGroup, "Number of clean blocks is X");
		
		LinkedHashMap<String, Class<? extends BreakpointBase>> writtenPagesGroup = new LinkedHashMap<>();
		writtenPagesGroup.put("Device", PagesWrittenInDevice.class);
		writtenPagesGroup.put("Chip", PagesWrittenInChip.class);
		writtenPagesGroup.put("Plane", PagesWrittenInPlane.class);
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
