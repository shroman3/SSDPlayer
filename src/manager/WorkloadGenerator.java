/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Or Mauda, Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
 * Copyright (c) 2015, Technion – Israel Institute of Technology
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *******************************************************************************/
package manager;

import java.io.FileNotFoundException;

import entities.Device;


public abstract class WorkloadGenerator<D extends Device<?,?,?,?>, S extends SSDManager<?,?,?,?,D>> implements TraceParser<D,S> {
	private String name;
	private int traceLength;
	protected S manager; // Revised by Or: changed from private to protected, used in ResizableWorkloadGenerator.
	protected D device; // Revised by Or: changed from private to protected, to enable setDevice in ResizableWorkloadGenerator.
	protected int lpRange;
	protected int lp; // Revised by Or: changed from private to protected, used in ResizableWorkloadGenerator.
	protected int temp; // Revised by Or: changed from private to protected, used in ResizableWorkloadGenerator.
	
	protected abstract int getLP();

	public WorkloadGenerator(String name, S manager, int traceLength) {
		this.name = name;
		this.traceLength = traceLength; 
		this.manager = manager;
		lpRange = manager.getLpRange();
		device = manager.getEmptyDevice();
	}
	
	public String getName() {
		return name;
	}
	
	public int getTraceLength() {
		return traceLength;
	}

	@Override
	public void open(String fileName) throws FileNotFoundException {
	}

	@Override
	public void close() {
	}

	@Override
	public String getLastCommand() {
		return "write " + lp + ", temp=" + temp;
	}

	@Override
	public D parseNextCommand() {
		if (device != null) {
			lp = getLP();
			temp = getLPArg(lp);
			device = manager.writeLP(device, lp, temp);
		}
		return device;
	}

	@Override
	public D getCurrentDevice() {
		return device;
	}

	protected int getLPArg(int lp) {
		return 1/*dummy*/;
	}
}
