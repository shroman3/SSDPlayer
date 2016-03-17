package ui.breakpoints;

import java.util.LinkedHashMap;

import breakpoints.BreakpointBase;

public class BreakpointsGroup implements IBreakpointCBoxEntry {
	private LinkedHashMap<String, Class<? extends BreakpointBase>> mGroup;
	private String mDisplayName;
	private String mSelectedItem;
	
	public BreakpointsGroup(LinkedHashMap<String, Class<? extends BreakpointBase>> group, 
			String displayName) {
		mGroup = group;
		mDisplayName = displayName;
	}
	
	public String getSelectedItem() {
		if (mSelectedItem == null) return mGroup.keySet().iterator().next();
		
		return mSelectedItem;
	}
	
	public void setSelectedItem(String selectedItem) {
		mSelectedItem = selectedItem;
	}
	
	public LinkedHashMap<String, Class<? extends BreakpointBase>> getGroup() {
		return mGroup;
	}
	
	public boolean contains(Class<? extends BreakpointBase> bpClass) {
		for (Class<? extends BreakpointBase> otherClass : mGroup.values()) {
			if (otherClass.getName().equals(bpClass.getName())) return true;
		}
		
		return false;
	}
	
	@Override
	public String getDisplayName() {
		return mDisplayName;
	}
	
	public String getFirstKeyWithValueOrDefault(Class<? extends BreakpointBase> bpClass,
			String defaultValue) {
		for (String key : mGroup.keySet()) {
			if (mGroup.get(key).getName().equals(bpClass.getName())) return key;
		}
		
		return defaultValue;
	}
}
