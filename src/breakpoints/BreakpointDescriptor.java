package breakpoints;

import java.util.ArrayList;
import java.util.List;

import general.MessageLog;
import log.Message.ErrorMessage;
import ui.breakpoints.BreakpointUIComponent;

public class BreakpointDescriptor {
	private Class<? extends BreakpointBase> mBreakpointClass;
	private List<BreakpointUIComponent> mUIComponents;

	public BreakpointDescriptor(Class<? extends BreakpointBase> breakpointClass) {
		mBreakpointClass = breakpointClass;
		mUIComponents = new ArrayList<>();

		try {
			@SuppressWarnings("unchecked")
			List<BreakpointComponent> components = (List<BreakpointComponent>) mBreakpointClass
					.getMethod("getComponents").invoke(mBreakpointClass.newInstance());
			for (BreakpointComponent component : components) {
				mUIComponents.add(new BreakpointUIComponent(component.getPropertyName(), component.getParamType(),
						component.getLabel()));
			}
		} catch (Exception e) {
			MessageLog.log(new ErrorMessage(
					"Error instantiating ui descriptor for breakpoint " + this.mBreakpointClass.getSimpleName()));
			MessageLog.log(new ErrorMessage(e.getMessage()));
		}

	}

	public Class<? extends BreakpointBase> getBreakpointClass() {
		return mBreakpointClass;
	}

	public List<BreakpointUIComponent> getUIComponents() {
		return mUIComponents;
	}
}
