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

import ui.WorkloadWidget;
import entities.BlockStatusGeneral;
import entities.StatisticsGetter;
import entities.basic.BasicBlock;
import entities.basic.BasicChip;
import entities.basic.BasicDevice;
import entities.basic.BasicPage;
import entities.basic.BasicPlane;
import general.XMLGetter;
import general.XMLParsingException;

public class GreedySSDManager extends SSDManager<BasicPage, BasicBlock, BasicPlane, BasicChip, BasicDevice> {
	private Color writtenPageColor;

	GreedySSDManager() {
		setSupportedZoomLevels();
	}

	public Color getWritenPageColor() {
		return writtenPageColor;
	}
	
	@Override
	public TraceParserGeneral<BasicDevice, GreedySSDManager> getTraseParser() {
		return new BasicTraceParser<BasicDevice, GreedySSDManager>(this);
	}

	protected void initValues(XMLGetter xmlGetter) throws XMLParsingException {
		super.initValues(xmlGetter);
		writtenPageColor = getColorField(xmlGetter, "written_color");
	}

	@Override
	public BasicPage getEmptyPage() {
		return new BasicPage.Builder().setManager(this).build();
	}
	
	@Override
	public List<WorkloadWidget<BasicDevice,	SSDManager<BasicPage, BasicBlock, BasicPlane, BasicChip, BasicDevice>>> getWorkLoadGeneratorWidgets() {
		List<WorkloadWidget<BasicDevice,SSDManager<BasicPage, BasicBlock, BasicPlane, BasicChip, BasicDevice>>> creators = new ArrayList<>();
		creators.add(new UniformWorkloadWidget<BasicDevice,SSDManager<BasicPage, BasicBlock, BasicPlane, BasicChip, BasicDevice>>(this));
		creators.add(new ZipfWorkloadWidget<BasicDevice,SSDManager<BasicPage, BasicBlock, BasicPlane, BasicChip, BasicDevice>>(this));
		return creators;
	}
	
	@Override
	protected List<StatisticsGetter> initStatisticsGetters() {
		List<StatisticsGetter> statisticsGetters = new ArrayList<StatisticsGetter>();
		statisticsGetters.add(new LogicalWritesPerEraseGetter(this));
		statisticsGetters.add(new WriteAmplificationGetter());
		statisticsGetters.add(new ValidDistributionGetter(this));
		return statisticsGetters;
	}
	
	@Override
	protected BasicDevice getEmptyDevice(List<BasicChip> emptyChips) {
		BasicDevice.Builder builder = new BasicDevice.Builder();
		builder.setChips(emptyChips);
		return builder.build();
	}

	@Override
	protected BasicChip getEmptyChip(List<BasicPlane> planes) {
		BasicChip.Builder builder = new BasicChip.Builder();
		builder.setPlanes(planes);
		return builder.build();
	}

	@Override
	protected BasicPlane getEmptyPlane(List<BasicBlock> blocks) {
		BasicPlane.Builder builder = new BasicPlane.Builder();
		builder.setBlocks(blocks);
		builder.setManager(this);
		return builder.build();
	}

	@Override
	protected BasicBlock getEmptyBlock(List<BasicPage> pages) {
		BasicBlock.Builder builder = new BasicBlock.Builder();
		builder.setManager(this).setEraseCounter(0).setInGC(false)
				.setStatus(BlockStatusGeneral.CLEAN).setPagesList(pages);
		return builder.build();
	}
}