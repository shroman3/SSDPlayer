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

import entities.BlockStatusGeneral;
import entities.StatisticsGetter;
import entities.RAID.hot_cold.RAIDHotColdChip;
import entities.RAID.hot_cold.RAIDHotColdDevice;
import entities.RAID.hot_cold.RAIDHotColdPlane;
import entities.RAID.simulation.RAIDBlock;
import entities.RAID.simulation.RAIDPage;
import general.XMLGetter;
import general.XMLParsingException;
import manager.RAIDStatistics.ParityOverheadGetter;
import ui.AddressWidget;
import ui.WorkloadWidget;

/**
 * 
 * @author Or Mauda
 *
 */
public abstract class RAIDHotColdSSDManager extends RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice> {
	private Color dataPageColor;
	private List<Color> paritiesColors;
	private Color stripeFrameColor;
	private Color stripeFrameStepColor;
	private final int maxColorsNum = 1000;
	private Boolean showOldParity; // indicates whether the invalid parity pages should be highlighted
	private Boolean showOldData; // indicates whether the invalid data pages should be highlighted
	protected int stripeSize;
	protected int paritiesNumber;

	RAIDHotColdSSDManager() {
	}

	public Color getDataPageColor() {
		return dataPageColor;
	}
	
	public Color getParityPageColor(int parityNumber) {
		return paritiesColors.get(parityNumber-1);
	}
	
	public Color getStripeFrameColor(int index) {
		int updatedIndex = index % maxColorsNum;
		int r = (stripeFrameColor.getRed() + updatedIndex * stripeFrameStepColor.getRed()) % 256;
		int g = (stripeFrameColor.getGreen() + updatedIndex * stripeFrameStepColor.getGreen()) % 256;
		int b = (stripeFrameColor.getBlue() + updatedIndex * stripeFrameStepColor.getBlue()) % 256;
		return new Color(r, g, b);
	}
	
	public int getStripeSize() {
		return stripeSize;
	}
	
	public int getParitiesNumber() {
		return paritiesNumber;
	}
	
	public Boolean toShowOldParity() {
		return showOldParity;
	}
	
	public Boolean toShowOldData() {
		return showOldData;
	}
	
	protected abstract void setParitiesNumber();
	
	protected abstract void setStripeSize();
	
	@Override
	public TraceParserGeneral<RAIDHotColdDevice, RAIDHotColdSSDManager> getTraseParser() {
		return new RAIDHotColdTraceParser(this);
	}

	protected void initValues(XMLGetter xmlGetter) throws XMLParsingException {
		super.initValues(xmlGetter);
		dataPageColor = getColorField(xmlGetter, "data_color");
		paritiesColors = new ArrayList<Color>(getColorsListField(xmlGetter, "parity_color"));
		setParitiesNumber();
		setStripeSize();
		stripeFrameColor = getColorField(xmlGetter, "stripe_frame_color");
		stripeFrameStepColor = getColorField(xmlGetter, "stripe_frame_step");
		showOldParity = xmlGetter.getBooleanField(VisualConfig.VISUAL_CONFIG, "show_old_parity");
		showOldData = xmlGetter.getBooleanField(VisualConfig.VISUAL_CONFIG, "show_old_data");
	}

	@Override
	public RAIDPage getEmptyPage() {
		return new RAIDPage.Builder().setManager(this).build();
	}
	
	@Override
	public List<WorkloadWidget<RAIDHotColdDevice, SSDManager<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice>>> getWorkLoadGeneratorWidgets() {
		List<WorkloadWidget<RAIDHotColdDevice, SSDManager<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice>>> creators = new ArrayList<>();
		creators.add(new UniformResizableWorkloadWidget<RAIDHotColdDevice, SSDManager<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice>>(this));
		creators.add(new ZipfResizableWorkloadWidget<RAIDHotColdDevice, SSDManager<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice>>(this));
		return creators;
	}
	
	@Override
	public int getLpRange() {
		int logicalChips = (int) (getChipsNum()*((double)getStripeSize()/(getStripeSize() + getParitiesNumber())));
		return (logicalChips * getPlanesNum() *(getBlocksNum() - getReserved())*getPagesNum());
	}
	
	@Override
	public List<AddressWidget<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice, 
		RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice>>> getAddressGetterWidgets() {
		
		List<AddressWidget<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice, 
		RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice>>> addressGetters = new ArrayList<>();
		
		addressGetters.add(new PhysicalAddressWidget<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice,
				RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice>>(this));
		
		addressGetters.add(new LogicalAddressWidget<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice,
				RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice>>(this));
		
		addressGetters.add(new ParityAddressWidget<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice,
				RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDHotColdPlane, RAIDHotColdChip, RAIDHotColdDevice>>(this));
		
		return addressGetters;
	}
	
	@Override
	protected List<StatisticsGetter> initStatisticsGetters() {
		List<StatisticsGetter> statisticsGetters = new ArrayList<StatisticsGetter>();
		statisticsGetters.add(new ParityOverheadGetter<>(this, RAIDHotColdDevice.class));
		statisticsGetters.add(new LogicalWritesPerEraseGetter(this));
		statisticsGetters.add(new WriteAmplificationGetter());
		statisticsGetters.add(new ValidDistributionGetter(this));
		return statisticsGetters;
	}
	
	@Override
	protected abstract RAIDHotColdDevice getEmptyDevice(List<RAIDHotColdChip> emptyChips);

	@Override
	protected RAIDHotColdChip getEmptyChip(List<RAIDHotColdPlane> planes) {
		RAIDHotColdChip.Builder builder = new RAIDHotColdChip.Builder();
		builder.setPlanes(planes);
		return builder.build();
	}

	@Override
	protected RAIDHotColdPlane getEmptyPlane(List<RAIDBlock> blocks) {
		RAIDHotColdPlane.Builder builder = new RAIDHotColdPlane.Builder();
		builder.setBlocks(blocks);
		builder.setManager(this);
		return builder.build();
	}

	@Override
	protected RAIDBlock getEmptyBlock(List<RAIDPage> pages) {
		RAIDBlock.Builder builder = new RAIDBlock.Builder();
		builder.setManager(this).setEraseCounter(0).setInGC(false)
				.setStatus(BlockStatusGeneral.CLEAN).setPagesList(pages);
		return builder.build();
	}

}