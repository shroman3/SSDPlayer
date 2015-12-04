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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import manager.SecondWriteStatistics.BlockStateDistributionGetter;
import manager.SecondWriteStatistics.ValidDistributionGetter;
import manager.SecondWriteStatistics.WriteLevelDistributionGetter;
import ui.WorkloadWidget;
import entities.BlockStatusGeneral;
import entities.StatisticsGetter;
import entities.reusable.ReusableBlock;
import entities.reusable.ReusableChip;
import entities.reusable.ReusableDevice;
import entities.reusable.ReusablePage;
import entities.reusable.ReusablePlane;
import general.XMLGetter;
import general.XMLParsingException;

public class ReusableSSDManager extends SSDManager<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice> {
	private Color firstWriteColor;
	private Color secondWriteColor;

	ReusableSSDManager() {
	}

	public Color getWriteLevelColor(int writeLevel) {
		if (writeLevel == 1) {
			return firstWriteColor;
		} else if (writeLevel == 2) {
			return secondWriteColor;
		}
		return null;
	}


	@Override
	public TraceParserGeneral<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice, ReusableSSDManager> getTraseParser() {
		return new BasicTraceParser<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice, ReusableSSDManager>(this);
	}

	@Override
	public ReusablePage getEmptyPage() {
		return new ReusablePage.Builder().setManager(this).build();
	}
	
	@Override
	public List<WorkloadWidget<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice, 
		SSDManager<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice>>> getWorkLoadGeneratorWidgets() {
		List<WorkloadWidget<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice,SSDManager<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice>>> creators = new ArrayList<>();
		creators.add(new UniformWorkloadWidget<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice,SSDManager<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice>>(this));
		creators.add(new ZipfWorkloadWidget<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice,SSDManager<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice>>(this));
		return creators;
	}
	
	@Override
	protected List<StatisticsGetter> initStatisticsGetters() {
		List<StatisticsGetter> statisticsGetters = new ArrayList<StatisticsGetter>();
		statisticsGetters.add(new LogicalWritesPerEraseGetter(this));
		statisticsGetters.add(new WriteLevelDistributionGetter(this));
		statisticsGetters.add(new BlockStateDistributionGetter(this));
		statisticsGetters.add(new ValidDistributionGetter(this, 1));
		statisticsGetters.add(new ValidDistributionGetter(this, 2));
		return statisticsGetters;
	}

	protected void initValues(XMLGetter xmlGetter) throws XMLParsingException {
		super.initValues(xmlGetter);
		firstWriteColor = getColorField(xmlGetter, "first_write");
		secondWriteColor = getColorField(xmlGetter, "second_write");
	}

	@Override
	protected ReusableDevice getEmptyDevice(List<ReusableChip> chips) {
		ReusableDevice.Builder builder = new ReusableDevice.Builder();
		builder.setChips(chips);
		return builder.build();
	}
	
	@Override
	protected ReusableChip getEmptyChip(List<ReusablePlane> planes) {
		ReusableChip.Builder builder = new ReusableChip.Builder();
		builder.setPlanes(planes);
		return builder.build();
	}

	@Override
	protected ReusablePlane getEmptyPlane(List<ReusableBlock> blocks) {
		ReusablePlane.Builder builder = new ReusablePlane.Builder();
		builder.setBlocks(blocks);
		builder.setManager(this);
		return builder.build();
	}

	@Override
	protected ReusableBlock getEmptyBlock(List<ReusablePage> pages) {
		ReusableBlock.Builder builder = new ReusableBlock.Builder();
		builder.setManager(this).setEraseCounter(0).setInGC(false)
				.setStatus(BlockStatusGeneral.CLEAN).setPagesList(pages);
		return builder.build();
	}

	public boolean isSecondWrite(boolean hasSecondWriteBlock, int temperature) {
		return hasSecondWriteBlock;
	}
}