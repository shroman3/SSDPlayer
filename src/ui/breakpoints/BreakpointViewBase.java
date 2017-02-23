package ui.breakpoints;

import java.awt.Dimension;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import breakpoints.BreakpointBase;
import breakpoints.BreakpointFactory;
import general.MessageLog;
import log.Message.ErrorMessage;

public class BreakpointViewBase extends JPanel {
	private static final long serialVersionUID = 1L;
	private BreakpointBase mBreakpoint;
	private List<BreakpointUIComponent> mUIComponents;
	private JPanel mFieldsPanel;
	private JDialog mErrorDialog;
	
	public BreakpointViewBase() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		
		mErrorDialog = new JDialog(SwingUtilities.windowForComponent(this), "Invalid input");
		mErrorDialog.setModal(true);
		mErrorDialog.setSize(new Dimension(400, 100));
		mErrorDialog.setLocationRelativeTo(SwingUtilities.windowForComponent(this));
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
			MessageLog.log(new ErrorMessage("Error instantiate breakpoint with given parameters."));
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
					MessageLog.log(new ErrorMessage("Error instantiate breakpoint with given parameters."));
					return null;
				}
			} catch (Exception e) {
				MessageLog.log(new ErrorMessage(showError(e)));
				return null;
			}
		}
		
		return mBreakpoint;
	}

	private String showError(Exception e) {
		String error = "";
		
		if (e.getCause() == null) {
			error = "Please provide a number";
		} else {
			error = e.getCause().getMessage();
		}
			
		JLabel errorLabel = new JLabel(error);
		errorLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		mErrorDialog.add(errorLabel);

		mErrorDialog.setVisible(true);
		return error;
	}
}
