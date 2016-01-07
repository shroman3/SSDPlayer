package ui.breakpoints;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import breakpoints.BreakpointComponent;

public class BreakpointUIComponent extends BreakpointComponent {
	private JTextField mComponent;
	
	public BreakpointUIComponent(String propertyName, Class<?> paramType, String label) {
		super(propertyName, paramType, label);
		mComponent = new JTextField(10);
		mComponent.setBackground(UIManager.getColor("nimbusSelectedText"));
		mComponent.setForeground(UIManager.getColor("text"));
		mComponent.setBorder(new LineBorder(UIManager.getColor("nimbusBorder")));
		mComponent.setCaretColor(UIManager.getColor("text"));
	}
	
	public JTextField getComponent() {
		return mComponent;
	}
}
