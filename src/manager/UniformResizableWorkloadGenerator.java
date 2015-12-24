package manager;

import java.util.Random;

import entities.Device;

/**
 * 
 * @author Or Mauda
 *
 */
public class UniformResizableWorkloadGenerator<D extends Device<?,?,?,?>, S extends SSDManager<?,?,?,?,D>>
	extends ResizableWorkloadGenerator<D, S> {

	private Random random;
	private int seed;
	
	public UniformResizableWorkloadGenerator(S manager, int traceLength,
			int seed, int maxWriteSize, boolean isWriteSizeUniform) {
		
		super("Uniform Resizeable", manager, traceLength, maxWriteSize, isWriteSizeUniform);
		this.seed = seed;
		random = new Random(seed);
	}

	@Override
	protected int getLP() {
		return random.nextInt(lpRange);
	}
	
	public String getName() {
		return super.getName() + "_seed(" + seed + ")";
	}
	
}