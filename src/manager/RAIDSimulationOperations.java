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

import java.util.HashMap;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.javatuples.Tuple;

import entities.BlockStatus;
import entities.BlockStatusGeneral;
import entities.RAID.visualization.RAIDVisualizationDevice;

/**
 * 
 * @author Or Mauda
 *
 */
public enum RAIDSimulationOperations {
	WRITE("W") {
		@Override
		public RAIDVisualizationDevice doOperation(RAIDVisualizationDevice device, RAIDVisualizationSSDManager manager, String[] operationParts) {
			if (operationParts.length != 6) {
				throw new IllegalArgumentException("Wrong parameters number for WRITE operation: "+ operationParts);
			}
			
			int write = Integer.parseInt(operationParts[1]);
			if (write != 1) {
				throw new IllegalArgumentException("Illegal write level, given: "+ write);
			}

			int stripe = Integer.parseInt(operationParts[5]);
			if (stripe < 0) {
				throw new IllegalArgumentException("stripe number must be positive, given: " + stripe);
			}
			
			Quartet<Integer, Integer, Integer, Integer> pageIndex = getIndexQuartet(operationParts[3]);
			if (operationParts[4].equals("D")) {
				int lp = Integer.parseInt(operationParts[2]);
				Boolean toHighlight = device.isPageHighlighted(lp);
				device = (RAIDVisualizationDevice) device.invalidate(lp);
				device = device.writeData(pageIndex, lp, stripe, toHighlight); 
				return device;
			} else if (operationParts[4].equals("P")) {
				Pair<Integer, Integer> parityAddress = getIndexPair(operationParts[2]);
				Boolean toHighlight = device.isPageHighlighted(parityAddress.getValue1(), parityAddress.getValue0());
				device = (RAIDVisualizationDevice) device.invalidate(parityAddress.getValue0(), parityAddress.getValue1());
				device = device.writeParity(pageIndex, parityAddress, stripe, toHighlight); 
				return device;
			} else {
				throw new IllegalArgumentException("Write must be of P/D page");
			}
		}
	},
	MOVE("M") {
		@Override
		public RAIDVisualizationDevice doOperation(RAIDVisualizationDevice device, RAIDVisualizationSSDManager manager, String[] operationParts) {
			if (operationParts.length != 6) {
				throw new IllegalArgumentException("Wrong parameters number for MOVE operation: "+ operationParts);
			}
			
			int write = Integer.parseInt(operationParts[1]);
			if (write != 1) {
				throw new IllegalArgumentException("Illegal write level given: "+ write);
			}
			
			int stripe = Integer.parseInt(operationParts[5]);
			if (stripe < 0) {
				throw new IllegalArgumentException("stripe number must be positive, given: " + stripe);
			}
			
			Quartet<Integer, Integer, Integer, Integer> pageIndex = getIndexQuartet(operationParts[3]);
			if (operationParts[4].equals("D")) {
				int lp = Integer.parseInt(operationParts[2]);
				Boolean toHighlight = device.isPageHighlighted(lp);
				device = (RAIDVisualizationDevice) device.invalidate(lp);
				return device.moveData(pageIndex, lp, stripe, toHighlight);
			} else if (operationParts[4].equals("P")) {
				Pair<Integer, Integer> parityAddress = getIndexPair(operationParts[2]);
				Boolean toHighlight = device.isPageHighlighted(parityAddress.getValue1(), parityAddress.getValue0());
				device = (RAIDVisualizationDevice) device.invalidate(parityAddress.getValue0(), parityAddress.getValue1());
				return device.moveParity(pageIndex, parityAddress, stripe, toHighlight);
			} else {
				throw new IllegalArgumentException("Move must be of P/D page");
			}
		}
	},
	CHANGE_STATE("S") {
		@Override
		public RAIDVisualizationDevice doOperation(RAIDVisualizationDevice device, RAIDVisualizationSSDManager manager, String[] operationParts) {
			if (operationParts.length != 3) {
				throw new IllegalArgumentException("Wrong parameters number for CHANGE_STATE operation: "+ operationParts);
			}
			
			Triplet<Integer, Integer, Integer> blockIndex = getIndexTriplet(operationParts[1]);
			
			String statusName = operationParts[2];
			BlockStatus status = statusMap.get(statusName.toLowerCase());
			if (status == null) {
				throw new IllegalArgumentException("No such status!");
			}
			return device.changeStatus(blockIndex, status);
		}
	},
	BEGIN_GC("B") {
		@Override
		public RAIDVisualizationDevice doOperation(RAIDVisualizationDevice device, RAIDVisualizationSSDManager manager, String[] operationParts) {
			if (operationParts.length != 2) {
				throw new IllegalArgumentException("Wrong parameters number for BEGIN_GC operation: "+ operationParts);
			}
			
			Triplet<Integer, Integer, Integer> blockIndex = getIndexTriplet(operationParts[1]);
			
			return device.changeGC(blockIndex, true);
		}
	},
	END_GC("G") {
		@Override
		public RAIDVisualizationDevice doOperation(RAIDVisualizationDevice device, RAIDVisualizationSSDManager manager, String[] operationParts) {
			if (operationParts.length != 2) {
				throw new IllegalArgumentException("Wrong parameters number for END_GC operation: "+ operationParts);
			}
			Triplet<Integer, Integer, Integer> blockIndex = getIndexTriplet(operationParts[1]);
			return device.changeGC(blockIndex, false);
		}
	},
	ERASE("E") {
		@Override
		public RAIDVisualizationDevice doOperation(RAIDVisualizationDevice device, RAIDVisualizationSSDManager manager, String[] operationParts) {
			if (operationParts.length != 2) {
				throw new IllegalArgumentException("Wrong parameters number for ERASE operation: "+ operationParts);
			}
			Triplet<Integer, Integer, Integer> blockIndex = getIndexTriplet(operationParts[1]);
			
			return device.eraseBlock(blockIndex);
		}
	};
	
	private static Map<String, RAIDSimulationOperations> operationsMap = initializeMap();
	private static Map<String, BlockStatus> statusMap = initializeBlockStatusMap();
	
	public static RAIDSimulationOperations getDeviceOperation(String operationSign) {
		RAIDSimulationOperations operation = operationsMap.get(operationSign);
		if(operation == null) {
			throw new IllegalArgumentException("Illegal device operation given: " + operationSign);
		}
		return operation;
	}
	
	private String operationSign;

	private RAIDSimulationOperations(String operationSign) {
		this.operationSign = operationSign;
	}
	
	public abstract RAIDVisualizationDevice doOperation(RAIDVisualizationDevice device, RAIDVisualizationSSDManager manager, String[] operationParts);
	
	private static Map<String, RAIDSimulationOperations> initializeMap() {
		Map<String, RAIDSimulationOperations> operationsMap = new HashMap<String, RAIDSimulationOperations>((int) Math.ceil(values().length/0.75));
		for (RAIDSimulationOperations operation : values()) {
			operationsMap.put(operation.operationSign, operation);
		}
		return operationsMap;
	}
	
	private static Map<String, BlockStatus> initializeBlockStatusMap() {
		Map<String, BlockStatus> operationsMap = new HashMap<String, BlockStatus>();
		for (BlockStatus status : BlockStatusGeneral.values()) {
			operationsMap.put(status.getStatusName(), status);
		}
		return operationsMap;
	}
	
	private static Tuple getIndex(String blockIndexStr) {
		blockIndexStr = blockIndexStr.replaceAll("[<>]", "");
		String[] indexSplit = blockIndexStr.split(",");
		int chipIndex = Integer.parseInt(indexSplit[0]);
		int planeIndex = Integer.parseInt(indexSplit[1]);
		if (indexSplit.length == 2) {
			return new Pair<Integer, Integer>(chipIndex, planeIndex);
		}
		int blockIndex = Integer.parseInt(indexSplit[2]);
		if (indexSplit.length > 3) {
			return new Quartet<Integer, Integer, Integer, Integer>(chipIndex, planeIndex, blockIndex, Integer.parseInt(indexSplit[3]));
		} else {
			return new Triplet<Integer, Integer, Integer>(chipIndex, planeIndex, blockIndex);
		}
	}
	
	private static Quartet<Integer, Integer, Integer, Integer> getIndexQuartet(String indexStr) {
		Tuple tuple = getIndex(indexStr);
		if (!(tuple instanceof Quartet)) {
			throw new IllegalArgumentException("Illegal page index given : "+ indexStr);
		}
		
		@SuppressWarnings("unchecked")
		Quartet<Integer, Integer, Integer, Integer> pageIndex = (Quartet<Integer, Integer, Integer, Integer>) tuple;
		return pageIndex;
	}
	
	private static Triplet<Integer, Integer, Integer> getIndexTriplet(String blockIndexStr) {
		Tuple tuple = getIndex(blockIndexStr);
		if (!(tuple instanceof Triplet)) {
			throw new IllegalArgumentException("Wrong block index given : "+ blockIndexStr);
		}
		
		@SuppressWarnings("unchecked")
		Triplet<Integer, Integer, Integer> blockIndex = (Triplet<Integer, Integer, Integer>) tuple;
		return blockIndex;
	}
	
	private static Pair<Integer, Integer> getIndexPair(String blockIndexStr) {
		Tuple tuple = getIndex(blockIndexStr);
		if (!(tuple instanceof Pair)) {
			throw new IllegalArgumentException("Wrong block index given : "+ blockIndexStr);
		}
		
		@SuppressWarnings("unchecked")
		Pair<Integer, Integer> blockIndex = (Pair<Integer, Integer>) tuple;
		return blockIndex;
	}
}