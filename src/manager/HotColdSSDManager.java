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

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.BlockStatusGeneral;
import entities.StatisticsGetter;
import entities.hot_cold.HotColdBlock;
import entities.hot_cold.HotColdChip;
import entities.hot_cold.HotColdDevice;
import entities.hot_cold.HotColdPage;
import entities.hot_cold.HotColdPlane;
import general.XMLGetter;
import general.XMLParsingException;
import manager.HotColdStatistics.HotColdWriteAmplificationGetter;
import manager.HotColdStatistics.PartitionDistributionGetter;
import ui.WorkloadWidget;

public class HotColdSSDManager extends SSDManager<HotColdPage, HotColdBlock, HotColdPlane, HotColdChip, HotColdDevice> {
	private int minTemperature;
	private int maxTemperature;
	private int deltaTemperature;
	private Map<Integer, HotColdPartition> partitionsMap;
	private Map<Integer, Color> colorsMap;
	private List<HotColdPartition> partitions;
	

	HotColdSSDManager() {
	}

	public Color getTemperatureColor(int temperature) {
		return colorsMap.get(temperature);
	}
	
	@Override
	public HotColdTraceParser<HotColdDevice, HotColdSSDManager> getTraseParser() {
		return new HotColdTraceParser<HotColdDevice, HotColdSSDManager>(this);
	}

	public HotColdPartition getPartition(int temperature) {
		return partitionsMap.get(temperature);
	}

	public List<HotColdPartition> getPartitions() {
		return partitions;
	}

	public int getPartitionsNum() {
		return partitions.size();
	}
	
	public int indexOfPartition(HotColdPartition partition) {
		return partitions.indexOf(partition);
	}
	
	public HotColdPartition getPartitionbyIndex(int index) {
		return partitions.get(index);
	}
	
	@Override
	public HotColdPage getEmptyPage() {
		return new HotColdPage.Builder().setManager(this).build();
	}

	@Override
	public List<WorkloadWidget<HotColdDevice, SSDManager<HotColdPage, HotColdBlock, HotColdPlane, HotColdChip, HotColdDevice>>> getWorkLoadGeneratorWidgets() {
		List<WorkloadWidget<HotColdDevice,SSDManager<HotColdPage, HotColdBlock, HotColdPlane, HotColdChip, HotColdDevice>>> creators = new ArrayList<>();
		creators.add(new UniformWorkloadWidget<HotColdDevice,SSDManager<HotColdPage, HotColdBlock, HotColdPlane, HotColdChip, HotColdDevice>>(this));
		creators.add(new ZipfWorkloadWidget<HotColdDevice,SSDManager<HotColdPage, HotColdBlock, HotColdPlane, HotColdChip, HotColdDevice>>(this));
		return creators;
	}
	
	@Override
	protected List<StatisticsGetter> initStatisticsGetters() {
		List<StatisticsGetter> statisticsGetters = new ArrayList<StatisticsGetter>();
		statisticsGetters.add(new LogicalWritesPerEraseGetter(this));
		statisticsGetters.add(new WriteAmplificationGetter());
		statisticsGetters.add(new HotColdWriteAmplificationGetter(this));
		statisticsGetters.add(new PartitionDistributionGetter(this));
		statisticsGetters.add(new ValidDistributionGetter(this));
		return statisticsGetters;
	}

	protected void initValues(XMLGetter xmlGetter) throws XMLParsingException {
		super.initValues(xmlGetter);
		minTemperature = getIntField(xmlGetter, "min_temperature");
		maxTemperature = getIntField(xmlGetter, "max_temperature");
		deltaTemperature = maxTemperature - minTemperature + 1;
		initColorsMap(getColorField(xmlGetter, "cold"), 
				getColorField(xmlGetter, "hot"), 
				getColorField(xmlGetter, "intermediate"));
		initPartitions(getListField(xmlGetter, "partition"));
	}

	private void initColorsMap(Color coldColor, Color hotColor, Color intermediateColor) {
		colorsMap = new HashMap<Integer, Color>(deltaTemperature);
		for (int i = minTemperature; i <= maxTemperature; ++i) {
			colorsMap.put(i, calculateTemperatureColor(i, coldColor, hotColor, intermediateColor));
		}
	}

	private void initPartitions(List<Integer> list) {
		partitions = new ArrayList<HotColdPartition>(list.size());
		int start = minTemperature;
		for (int end : list) {
			if ((start > end) || (end > maxTemperature)) {
				throw new RuntimeException("Illegal temperature given for Hot Cold manager");
			}
			partitions.add(new HotColdPartition(start, end, getTemperatureColor(end)));
			start = end+1;
		}
		
		partitionsMap = new HashMap<Integer, HotColdPartition>();
		for (int temperature = minTemperature; temperature <= maxTemperature; ++temperature) {
			for (HotColdPartition hotColdPartition : partitions) {
				if(hotColdPartition.isIn(temperature)) {
					partitionsMap.put(temperature, hotColdPartition);
					break;
				}
			}
			if (!partitionsMap.containsKey(temperature)) {
				throw new RuntimeException("Illegal temperature given for Hot Cold manager");
			}
		}
	}

	private Color calculateTemperatureColor(int temperature, Color coldColor, Color hotColor, Color intermediateColor) {
		temperature -= minTemperature;
		double weight = ((double)temperature)/deltaTemperature;
		if (weight < 0.5) {
			weight = weight*2;
			return new Color(linear(weight, coldColor.getRed(), intermediateColor.getRed()), 
							linear(weight, coldColor.getGreen(), intermediateColor.getGreen()), 
							linear(weight, coldColor.getBlue(), intermediateColor.getBlue()));
		} else {
			weight = (weight-0.5)*2;
			return new Color(linear(weight, intermediateColor.getRed(), hotColor.getRed()),
					linear(weight, intermediateColor.getGreen(), hotColor.getGreen()),
					linear(weight, intermediateColor.getBlue(), hotColor.getBlue()));
		}
	}
	
	/**
	 * @param temperature from 0 to 9
	 * @param start - first limit
	 * @param end - second limit
	 * @return the linear interpolation of the temperature given
	 */
	private int linear(double weight, int start, int end) {
		return (int) (end*weight + start*(1-weight));
	}

	@Override
	protected HotColdDevice getEmptyDevice(List<HotColdChip> chips) {
		HotColdDevice.Builder builder = new HotColdDevice.Builder();
		builder.setManager(this).setChips(chips);
		for (HotColdPartition partition : partitions) {
			builder.setTotalWritten(partition, 0);
		}
		return builder.build();
	}
	
	@Override
	protected HotColdChip getEmptyChip(List<HotColdPlane> planes) {
		HotColdChip.Builder builder = new HotColdChip.Builder();
		builder.setPlanes(planes);
		return builder.build();
	}

	@Override
	protected HotColdPlane getEmptyPlane(List<HotColdBlock> blocks) {
		HotColdPlane.Builder builder = new HotColdPlane.Builder();
		builder.setManager(this).setBlocks(blocks);
		for (HotColdPartition partition : partitions) {
			builder.setTotalMoved(partition, 0);
		}
		return builder.build();
	}

	@Override
	protected HotColdBlock getEmptyBlock(List<HotColdPage> pages) {
		HotColdBlock.Builder builder = new HotColdBlock.Builder();
		builder.setManager(this).setEraseCounter(0).setInGC(false)
				.setStatus(BlockStatusGeneral.CLEAN).setPagesList(pages);
		return builder.build();
	}
}