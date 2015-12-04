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
import org.javatuples.Triplet;
import org.javatuples.Tuple;

import entities.BlockStatus;
import entities.BlockStatusGeneral;
import entities.reusable.ReusableBlockStatus;
import entities.reusable_visualization.VisualizationDevice;


public enum SecondWritesSimulationOperations {
	WRITE("W") {
		@Override
		public VisualizationDevice doOperation(VisualizationDevice device, ReusableVisualizationSSDManager manager, String[] operationParts) {
			int write = Integer.parseInt(operationParts[1]);
			if (operationParts.length < 4) {
				throw new IllegalArgumentException("Wrong parameters number for WRITE operation: "+ operationParts);
			}

			int lp = Integer.parseInt(operationParts[2]);
			device = (VisualizationDevice) device.invalidate(lp);

			if (write == 1) {
				Triplet<Integer, Integer, Integer> pageIndex = getIndexTriplet(operationParts[3]);
				return device.firstWrite(pageIndex, lp);
			} else if (write == 2) {
				Triplet<Integer, Integer, Integer> firstPageIndex = getIndexTriplet(operationParts[3]);
				Triplet<Integer, Integer, Integer> secondPageIndex = getIndexTriplet(operationParts[4]);
				return device.secondWrite(firstPageIndex, secondPageIndex, lp);
			} else {
				throw new IllegalArgumentException("Illegal write level given: "+ write);
			}
		}
	},
	MOVE("M") {
		@Override
		public VisualizationDevice doOperation(VisualizationDevice device, ReusableVisualizationSSDManager manager, String[] operationParts) {
			int write = Integer.parseInt(operationParts[1]);
			if (write <= 0 || write > manager.getWriteLevels()) {
				throw new IllegalArgumentException("Illegal write level given: "+ write);
			}
			
			if (operationParts.length < 4) {
				throw new IllegalArgumentException("Wrong parameters number for MOVE operation: "+ operationParts);
			}
			
			int lp = Integer.parseInt(operationParts[2]);
			int gcWriteLevel = device.getLPWriteLevel(device, lp);
			device = (VisualizationDevice) device.invalidate(lp);

			if (write == 1) {
				Triplet<Integer, Integer, Integer> pageIndex = getIndexTriplet(operationParts[3]);
				return device.firstMove(pageIndex, lp, gcWriteLevel, false);
			} else if (write == 2) {
				Triplet<Integer, Integer, Integer> firstPageIndex = getIndexTriplet(operationParts[3]);
				Triplet<Integer, Integer, Integer> secondPageIndex = getIndexTriplet(operationParts[4]);
				return device.secondMove(firstPageIndex, secondPageIndex, lp, gcWriteLevel, false);
			} else {
				throw new IllegalArgumentException("Illegal write level given: "+ write);
			}
		}
	},
	PARTIAL_MOVE("P") {
		@Override
		public VisualizationDevice doOperation(VisualizationDevice device, ReusableVisualizationSSDManager manager, String[] operationParts) {
			if (operationParts.length != 4) {
				throw new IllegalArgumentException("Wrong parameters number for PARTIAL_MOVE operation: "+ operationParts);
			}
			
			int lp = Integer.parseInt(operationParts[1]);
			Triplet<Integer, Integer, Integer> sourceIndex = getIndexTriplet(operationParts[2]);
			Triplet<Integer, Integer, Integer> targetIndex = getIndexTriplet(operationParts[3]);
			
			int writeLevel = device.getPageWriteLevel(lp, device, sourceIndex);

			device = device.invalidatePage(lp, sourceIndex);
			return device.partialMove(targetIndex, lp, writeLevel);
		}
	},
	CHANGE_STATE("S") {
		@Override
		public VisualizationDevice doOperation(VisualizationDevice device, ReusableVisualizationSSDManager manager, String[] operationParts) {
			if (operationParts.length != 3) {
				throw new IllegalArgumentException("Wrong parameters number for CHANGE_STATE operation: "+ operationParts);
			}
			
			Pair<Integer, Integer> blockIndex = getIndexPair(operationParts[1]);
			
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
		public VisualizationDevice doOperation(VisualizationDevice device, ReusableVisualizationSSDManager manager, String[] operationParts) {
			if (operationParts.length != 2) {
				throw new IllegalArgumentException("Wrong parameters number for CHANGE_STATE operation: "+ operationParts);
			}
			
			Pair<Integer, Integer> blockIndex = getIndexPair(operationParts[1]);
			
			device = device.changeGC(blockIndex, true);
			return device.changeStatus(blockIndex, ReusableBlockStatus.RECYCLED);
		}
	},
	END_GC("G") {
		@Override
		public VisualizationDevice doOperation(VisualizationDevice device, ReusableVisualizationSSDManager manager, String[] operationParts) {
			if (operationParts.length != 2) {
				throw new IllegalArgumentException("Wrong parameters number for CHANGE_STATE operation: "+ operationParts);
			}
			Pair<Integer, Integer> blockIndex = getIndexPair(operationParts[1]);
			return device.changeGC(blockIndex, false);
		}
	},
	ERASE("E") {
		@Override
		public VisualizationDevice doOperation(VisualizationDevice device, ReusableVisualizationSSDManager manager, String[] operationParts) {
			if (operationParts.length != 2) {
				throw new IllegalArgumentException("Wrong parameters number for CHANGE_STATE operation: "+ operationParts);
			}
			Pair<Integer, Integer> blockIndex = getIndexPair(operationParts[1]);
			
			return device.eraseBlock(blockIndex);
		}
	};
	
	private static Map<String, SecondWritesSimulationOperations> operationsMap = initializeMap();
	private static Map<String, BlockStatus> statusMap = initializeBlockStatusMap();
	
	public static SecondWritesSimulationOperations getDeviceOperation(String operationSign) {
		SecondWritesSimulationOperations operation = operationsMap.get(operationSign);
		if(operation == null) {
			throw new IllegalArgumentException("Illegal device operation given: " + operationSign);
		}
		return operation;
	}
	
	private String operationSign;

	private SecondWritesSimulationOperations(String operationSign) {
		this.operationSign = operationSign;
	}
	
	public abstract VisualizationDevice doOperation(VisualizationDevice device, ReusableVisualizationSSDManager manager, String[] operationParts);
	
	private static Map<String, SecondWritesSimulationOperations> initializeMap() {
		Map<String, SecondWritesSimulationOperations> operationsMap = new HashMap<String, SecondWritesSimulationOperations>((int) Math.ceil(values().length/0.75));
		for (SecondWritesSimulationOperations operation : values()) {
			operationsMap.put(operation.operationSign, operation);
		}
		return operationsMap;
	}
	
	private static Map<String, BlockStatus> initializeBlockStatusMap() {
		Map<String, BlockStatus> operationsMap = new HashMap<String, BlockStatus>();
		for (BlockStatus status : BlockStatusGeneral.values()) {
			operationsMap.put(status.getStatusName(), status);
		}
		for (BlockStatus status : ReusableBlockStatus.values()) {
			operationsMap.put(status.getStatusName(), status);
		}
		return operationsMap;
	}
	
	private static Tuple getIndex(String blockIndexStr) {
		blockIndexStr = blockIndexStr.replaceAll("[<>]", "");
		String[] indexSplit = blockIndexStr.split(",");
		int planeIndex = Integer.parseInt(indexSplit[0]);
		int blockIndex = Integer.parseInt(indexSplit[1]);
		if (indexSplit.length > 2) {
			return new Triplet<Integer, Integer, Integer>(planeIndex, blockIndex, Integer.parseInt(indexSplit[2]));
		} else {
			return new Pair<Integer, Integer>(planeIndex, blockIndex);
		}
	}
	
	private static Triplet<Integer, Integer, Integer> getIndexTriplet(String indexStr) {
		Tuple tuple = getIndex(indexStr);
		if (!(tuple instanceof Triplet)) {
			throw new IllegalArgumentException("Illegal page index given : "+ indexStr);
		}
		
		@SuppressWarnings("unchecked")
		Triplet<Integer, Integer, Integer> pageIndex = (Triplet<Integer, Integer, Integer>) tuple;
		return pageIndex;
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