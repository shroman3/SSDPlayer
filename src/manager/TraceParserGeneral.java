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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import entities.Device;

public abstract class TraceParserGeneral<D extends Device<?,?,?,?>, S extends SSDManager<?,?,?,?,D>> implements TraceParser<D,S> {
	protected BufferedReader br = null;

	private String operationLine;
	private S manager;
	protected D device; //  November 2015: revised by Or Mauda for additional RAID functionality. changed from private to protected, to enable setDevice in RAID. 
	private int lineNo = 0;

	public TraceParserGeneral(S manager) {
		this.manager = manager;
		device = manager.getEmptyDevice();
	}

	protected abstract D parseCommand(String command, int lineNo, D device, S manager) throws IOException;
	public abstract  String getFileExtensions();
	
	@Override
	public void open(String fileName) throws FileNotFoundException {
		br = new BufferedReader(new FileReader(fileName));
	}

	@Override
	public void close() {
		try {
			if (br != null)br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {			
			device = manager.getEmptyDevice();
		}
	}

	@Override
	public String getLastCommand() {
		return operationLine;
	}
	
	@Override
	public D parseNextCommand() throws Exception {
		if (device != null) {	
			if (br == null) {
				throw new RuntimeException("You have to open trace file before you parse");
			}
			if ((operationLine = br.readLine()) == null) {				
				return null;
			}
			if (!operationLine.matches("#.*")) {
				device = parseCommand(operationLine, lineNo, device, manager);
			}
		}
		return device;
	}
	
	@Override
	public D getCurrentDevice() {
		return device;
	}
}