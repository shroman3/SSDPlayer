package ui.breakpoints;

import java.lang.reflect.Method;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import breakpoints.BreakpointBase;
import breakpoints.BreakpointFactory;

public class BreakpointViewBase extends JPanel {
	private static final long serialVersionUID = 1L;
	private BreakpointBase mBreakpoint;
	private List<BreakpointUIComponent> mUIComponents;
	private JPanel mFieldsPanel;
	
	public BreakpointViewBase() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
	}
	
	public void setBreakpoint(BreakpointBase breakpoint) {
		mBreakpoint = breakpoint;
		mUIComponents = BreakpointFactory.getBreakpointUIComponents(mBreakpoint.getClass());
		displayBreakpoint();
	}

	private void displayBreakpoint() {
		if (mFieldsPanel != null) {
			remove(mFieldsPanel);
		}
		
		mFieldsPanel = new JPanel();
		mFieldsPanel.setLayout(new BoxLayout(mFieldsPanel, BoxLayout.Y_AXIS));
		add(mFieldsPanel);
		
		try {
			for (BreakpointUIComponent component : mUIComponents) {
				JPanel componentPanel = new JPanel();
				componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.X_AXIS));
				
				JLabel lpLabel = new JLabel(component.getLabel() + ":");
				lpLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
				
				componentPanel.add(lpLabel);
				JTextField textField = component.getComponent();
				Method method = mBreakpoint.getClass().getMethod(component.getGetterName());
				textField.setText(method.invoke(mBreakpoint).toString());
				
				componentPanel.add(textField);
				mFieldsPanel.add(componentPanel);
			}
		} catch (Exception e) {
			System.err.println("Error instantiate breakpoint with given parameters.");
		}
	}
	
	public BreakpointBase createBreakpoint() {
		for (BreakpointUIComponent uiComponent : mUIComponents) {
			JTextField textField = uiComponent.getComponent();
			if (textField == null) continue;
			
			String text = textField.getText();
			try {
				Method method = mBreakpoint.getClass().getMethod(uiComponent.getSetterName(), uiComponent.getParamType());
				if (uiComponent.getParamType().equals(int.class)) {
					method.invoke(mBreakpoint, Integer.parseInt(text));
				} else if (uiComponent.getParamType().equals(double.class)) {
					method.invoke(mBreakpoint, Double.parseDouble(text));
				} else {
					System.err.println("Error instantiate breakpoint with given parameters.");
					return null;
				}
			} catch (Exception e) {
				System.err.println("Error instantiate breakpoint with given parameters.");
				return null;
			}
		}
		
		return mBreakpoint;
	}
}
