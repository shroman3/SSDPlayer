/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
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

import entities.ActionLog;
import entities.Device;

public class BasicTraceParser<D extends Device<?,?,?,?>, S extends SSDManager<?,?,?,?,D>> extends TraceParserGeneral<D,S> {

	public BasicTraceParser(S manager) {
		super(manager);
	}

	@Override
	protected D parseCommand(String command, int line, D device, S manager) throws IOException {
		String[] operationParts = command.split("[ \t]+");
		if ((operationParts.length == 5) && (operationParts[4].equals("W"))) {
			try {
				int lp = Integer.parseInt(operationParts[2]);
				return manager.writeLP(device, lp, 0/*dummy*/);
			} catch (NumberFormatException e) {
				System.out.println("\n\nIllegal Logical Page given: " + operationParts[2] + " line:" + line);
			}
		}
		System.out.println("\n\nIllegal trace line: " + command + " line:" + line);
		return null;
	}

	@Override
	public String getFileExtensions() {
		return "trace";
	}

}
