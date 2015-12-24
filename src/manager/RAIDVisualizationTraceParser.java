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

import java.io.IOException;

import entities.RAID.visualization.RAIDVisualizationDevice;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDVisualizationTraceParser extends TraceParserGeneral<RAIDVisualizationDevice, RAIDVisualizationSSDManager> 
		implements SettableTraceParser<RAIDVisualizationDevice, RAIDVisualizationSSDManager> {

	public RAIDVisualizationTraceParser(RAIDVisualizationSSDManager manager) {
		super(manager);
	}

	@Override
	protected RAIDVisualizationDevice parseCommand(String command, int line, RAIDVisualizationDevice device, RAIDVisualizationSSDManager manager) throws IOException {
		String[] operationParts = command.split("[ \t]+");
		RAIDSimulationOperations operation = RAIDSimulationOperations.getDeviceOperation(operationParts[0]);
		return operation.doOperation(device, manager, operationParts);
	}

	@Override
	public String getFileExtensions() {
		return "rlog";
	}
	
	@Override
	public void setDevice(RAIDVisualizationDevice device) {
		this.device = device;
	}
}
