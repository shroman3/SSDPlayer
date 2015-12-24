package manager;

import java.text.DecimalFormat;

import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;

import entities.Device;

/**
 * 
 * @author Or Mauda
 *
 */
public class ZipfResizableWorkloadGenerator<D extends Device<?,?,?,?>, S extends SSDManager<?,?,?,?,D>> 
	extends ResizableWorkloadGenerator<D, S> {
	
	private ZipfDistribution zipf;
	private int seed;
	private double exponent;

	public ZipfResizableWorkloadGenerator(S manager, int traceLength,
			int seed, double exponent, int maxWriteSize, boolean isWriteSizeUniform) {
		
		super("Zipf Resizable", manager, traceLength, maxWriteSize, isWriteSizeUniform);
		this.seed = seed;
		this.exponent = exponent;
		JDKRandomGenerator jdkRandomGenerator = new JDKRandomGenerator();
		jdkRandomGenerator.setSeed(seed);
		zipf = new ZipfDistribution(jdkRandomGenerator, lpRange, exponent);
	}

	@Override
	protected int getLP() {
		return zipf.sample()-1;
	}
	
	public String getName() {
		String string = super.getName() + "_seed(" + seed + ")" + "_exp(" + new DecimalFormat("0.##").format(exponent) + ")";
		return string;
	}
	
}