package manager;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import entities.Device;
import ui.WorkloadWidget;

public abstract class ResizableWorkloadWidget<D extends Device<?,?,?,?>, S extends SSDManager<?,?,?,?,D>>
	extends WorkloadWidget<D,S>{
	
	private static final long serialVersionUID = 1L;
	
	private JFormattedTextField maxWriteSize;
	private boolean isWriteSizeUniform;
	
	private ButtonGroup radioGroup;
	private JPanel radioPanel;

	public ResizableWorkloadWidget(String name, S manager) {
		super(name, manager);
		initWriteSize();
	}
	
	
	private void initWriteSize() {
		
		maxWriteSize = new JFormattedTextField(new DecimalFormat());
		maxWriteSize.setValue(1);
		addField(maxWriteSize, "Max Write Size");
		
		
        radioPanel = new JPanel();
        add(new JLabel("Write Size Distribution"));
        add(radioPanel);
        
        radioGroup = new ButtonGroup();
        
    	JRadioButton radioUniform = new JRadioButton("Uniform");
    	radioUniform.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {         
            	if (e.getStateChange()==1) {
            		isWriteSizeUniform = true;
                }
            }
    	});
    	radioUniform.setSelected(true);
    	
    	radioGroup.add(radioUniform);
    	radioPanel.add(radioUniform);
    	
    	JRadioButton radioZipf = new JRadioButton("Zipf");
    	radioUniform.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {         
            	if (e.getStateChange()==1) {
            		isWriteSizeUniform = false;
                }
            }
    	});
    	
    	radioGroup.add(radioZipf);
    	radioPanel.add(radioZipf);
	}
	
	protected int getMaxWriteSize() {
		return ((Number)maxWriteSize.getValue()).intValue();
	}
	
	protected boolean isWriteSizeUniform() {
		return isWriteSizeUniform;
	}
	
	@Override
	public void validateParms() {
		super.validateParms();
		if (getMaxWriteSize() < 1) {
			throw new IllegalArgumentException("Max Write Size should be at least 1");
		}
	}
}