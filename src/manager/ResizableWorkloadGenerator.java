package manager;

import java.util.Random;

import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;

import entities.Device;

/**
 * 
 * @author Or Mauda
 *
 */
public abstract class ResizableWorkloadGenerator<D extends Device<?,?,?,?>, S extends SSDManager<?,?,?,?,D>> 
	extends WorkloadGenerator<D, S> implements SettableTraceParser<D, S> {

	private boolean isWriteSizeUniform; // true value indicates the write size
										// distribution is uniform, else zipf.
	private int maxWriteSize;
	
	private Random random;
	private ZipfDistribution zipf;
	
	public ResizableWorkloadGenerator(String name, S manager, int traceLength,
			int maxWriteSize, boolean isWriteSizeUniform) {
		super(name, manager, traceLength);
		
		this.isWriteSizeUniform = isWriteSizeUniform;
		this.maxWriteSize = maxWriteSize;
		
		if (isWriteSizeUniform == true) {
			random = new Random();
		} else {
			JDKRandomGenerator jdkRandomGenerator = new JDKRandomGenerator();
			zipf = new ZipfDistribution(jdkRandomGenerator, maxWriteSize, 1);
		}
	}
	
	@Override
	public void setDevice(D device) {
		this.device = device;
	}
	
	@Override
	protected int getLPArg(int lp) {
		if (isWriteSizeUniform == true) {
			return random.nextInt(maxWriteSize) + 1;
		}
		return zipf.sample();
	}
	
}