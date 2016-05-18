package manager;

import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;

import entities.Device;

public class UniformResizableWorkloadWidget<D extends Device<?,?,?,?>, S extends SSDManager<?,?,?,?,D>> extends ResizableWorkloadWidget<D, S> {

	private static final long serialVersionUID = 1L;
	
	private JFormattedTextField seedInput;

	public UniformResizableWorkloadWidget(S manager) {
		this("Uniform", manager);
	}
	
	public UniformResizableWorkloadWidget(String name, S manager) {
		super(name, manager);
		seedInput = new JFormattedTextField(new DecimalFormat());
		seedInput.setValue(0);
		addField(seedInput, "Random Seed");
	}

	@Override
	public WorkloadGenerator<D, S> createWorkloadGenerator() {
		return new UniformResizableWorkloadGenerator<D,S>(manager, getWorkloadLength(), getSeed(), getMaxWriteSize(), isWriteSizeUniform());
	}
	
	protected int getSeed() {
		return ((Number)seedInput.getValue()).intValue();
	}
	
	public void validateParms() {
		super.validateParms();
		int seed = ((Number)seedInput.getValue()).intValue();
		if (seed < 0) {
			throw new IllegalArgumentException("Seed should be non negative");
		}
	}
	
}