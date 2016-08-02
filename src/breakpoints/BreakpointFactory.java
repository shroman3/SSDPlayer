package breakpoints;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.w3c.dom.Element;

import ui.breakpoints.BreakpointUIComponent;

public class BreakpointFactory {
	private static HashMap<String, BreakpointDescriptor> mTypeMap;
	
	public static void initBreakpointFactory() {
		mTypeMap = new HashMap<String, BreakpointDescriptor>();
		
		Reflections reflections = new Reflections(BreakpointBase.class.getPackage().getName());
		// We use here raw type and do the casting in order for the code to compile.
		Set<Class<? extends BreakpointBase>> subTypes = reflections.getSubTypesOf(BreakpointBase.class);
		
		for (Class<? extends BreakpointBase> clazz : subTypes) {			
			if (!Modifier.isAbstract(clazz.getModifiers())) {
				register(clazz);
			}
		}
	}
	
	public static BreakpointBase getBreakpoint(String type, Element breakpointElement) throws Exception {
		BreakpointBase breakpoint = (BreakpointBase) mTypeMap.get(type).getBreakpointClass().newInstance();
		breakpoint.readXml(breakpointElement);
		return breakpoint;
	}
	
	public static List<Class<? extends BreakpointBase>> getBreakpointClasses() {
		List<Class<? extends BreakpointBase>> result = new ArrayList<>();
		for (BreakpointDescriptor descriptor : mTypeMap.values()) {
			result.add(descriptor.getBreakpointClass());
		}
		
		return result;
	}
	
	public static List<BreakpointUIComponent> getBreakpointUIComponents(Class<? extends IBreakpoint> bpClass) {
		return mTypeMap.get(bpClass.getSimpleName()).getUIComponents();
	}
	
	private static void register(Class<? extends BreakpointBase> bpClass) {
		mTypeMap.put(bpClass.getSimpleName(), new BreakpointDescriptor(bpClass));
	}
}
