package breakpoints;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import manager.SSDManager;

public abstract class BreakpointBase implements IBreakpoint {
	protected List<BreakpointComponent> mComponents = new ArrayList<>();
	protected boolean mIsHit;

	public BreakpointBase() {
		addComponents();
	}
	
	@Override
	public void readXml(Element xmlElement) throws Exception {
		for (BreakpointComponent component : mComponents) {
			NodeList nodes = xmlElement.getElementsByTagName(component.getPropertyName());
			if (nodes.getLength() == 0) {
				throw new RuntimeException("Couldn't find " + component.getPropertyName() + " tag under breakpoint");
			}
			
			Method method = this.getClass().getMethod(component.getSetterName(), component.getParamType());
			if (component.getParamType().equals(int.class)) {
				method.invoke(this, Integer.parseInt(nodes.item(0).getTextContent()));
			} else if (component.getParamType().equals(double.class)) {
				method.invoke(this, Double.parseDouble(nodes.item(0).getTextContent()));
			}
		}
	}
	
	@Override
	public List<BreakpointComponent> getComponents() {
		return mComponents;
	}
	
	@Override
	public boolean isHit() {
		return mIsHit;
	}
	
	@Override
	public void setIsHit(boolean value) {
		mIsHit = value;
	}
	
	@Override
	public boolean isManagerSupported(SSDManager<?, ?, ?, ?, ?> manager) {
		return true;
	}
}
