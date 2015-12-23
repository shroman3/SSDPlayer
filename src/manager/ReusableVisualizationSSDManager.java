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
import java.util.List;

import manager.SecondWriteStatistics.BlockStateDistributionGetter;
import manager.SimulationStatistics.ValidDistributionGetter;
import manager.SimulationStatistics.WriteLevelDistributionGetter;
import entities.BlockStatusGeneral;
import entities.StatisticsGetter;
import entities.reusable_visualization.VisualizationBlock;
import entities.reusable_visualization.VisualizationChip;
import entities.reusable_visualization.VisualizationDevice;
import entities.reusable_visualization.VisualizationPage;
import entities.reusable_visualization.VisualizationPlane;
import general.XMLGetter;
import general.XMLParsingException;

public class ReusableVisualizationSSDManager extends SSDManager<VisualizationPage, VisualizationBlock, VisualizationPlane, VisualizationChip, VisualizationDevice> 
		implements  VisualizationSSDManager {
	private Color firstWriteColor;
	private Color secondWriteColor;

	ReusableVisualizationSSDManager() {}

	public Color getWriteLevelColor(int writeLevel) {
		if (writeLevel == 1) {
			return firstWriteColor;
		} else if (writeLevel == 2) {
			return secondWriteColor;
		}
		return null;
	}

	@Override
	public TraceParserGeneral<VisualizationDevice, ReusableVisualizationSSDManager> getTraseParser() {
		return new VisualizationTraceParser(this);
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
	protected VisualizationDevice getEmptyDevice(List<VisualizationChip> chips) {
		VisualizationDevice.Builder builder = new VisualizationDevice.Builder();
		builder.setChips(chips);
		return builder.build();
	}
	
	@Override
	protected VisualizationChip getEmptyChip(List<VisualizationPlane> planes) {
		VisualizationChip.Builder builder = new VisualizationChip.Builder();
		builder.setPlanes(planes);
		return builder.build();
	}

	@Override
	protected VisualizationPlane getEmptyPlane(List<VisualizationBlock> blocks) {
		VisualizationPlane.Builder builder = new VisualizationPlane.Builder();
		builder.setBlocks(blocks);
		builder.setManager(this);
		return builder.build();
	}

	@Override
	protected VisualizationBlock getEmptyBlock(List<VisualizationPage> pages) {
		VisualizationBlock.Builder builder = new VisualizationBlock.Builder();
		builder.setManager(this).setEraseCounter(0).setInGC(false)
				.setStatus(BlockStatusGeneral.CLEAN).setPagesList(pages);
		return builder.build();
	}

	@Override
	public VisualizationPage getEmptyPage() {
		return new VisualizationPage.Builder().setManager(this).build();
	}

	public int getWriteLevels() {
		return 2;
	}
}