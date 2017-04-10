package breakpoints;

import entities.Device;
import manager.WriteAmplificationGetter;

public class WriteAmplification extends BreakpointBase {
	private double mValue = 1.0;

	public WriteAmplification() {
		super();
	}

	@Override
	public boolean breakpointHit(Device<?> previousDevice, Device<?> currentDevice) {
		double oldValue = previousDevice == null ? Double.MIN_VALUE
				: WriteAmplificationGetter.computeWA(previousDevice);
		double currentValue = WriteAmplificationGetter.computeWA(currentDevice);

		return oldValue < mValue && currentValue >= mValue;
	}

	public double getValue() {
		return mValue;
	}

	public void setValue(double value) throws Exception {
		if (!BreakpointsConstraints.isWriteAmplificationValueLegal(value)) {
			throw BreakpointsConstraints.reportSetterException(SetterError.ILLEGAL_WRITE_AMP);
		}

		mValue = value;
	}

	@Override
	public String getDescription() {
		return "Write Amplification reaches " + mValue;
	}

	@Override
	public String getDisplayName() {
		return "Write Amplification reaches W";
	}

	@Override
	public void addComponents() {
		mComponents.add(new BreakpointComponent("value", double.class, "Value"));
	}

	@Override
	public boolean isEquals(IBreakpoint other) {
		if (!(other instanceof WriteAmplification))
			return false;
		WriteAmplification otherCasted = (WriteAmplification) other;

		return Double.compare(mValue, otherCasted.getValue()) == 0;
	}

	@Override
	public String getHitDescription() {
		return "Write amplification reached " + mValue;
	}
}
