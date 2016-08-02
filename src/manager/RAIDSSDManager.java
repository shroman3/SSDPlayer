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
import entities.RAID.simulation.RAIDBlock;
import entities.RAID.simulation.RAIDChip;
import entities.RAID.simulation.RAIDDevice;
import entities.RAID.simulation.RAIDPage;
import entities.RAID.simulation.RAIDPlane;
import general.XMLGetter;
import general.XMLParsingException;
import ui.AddressWidget;
import ui.WorkloadWidget;

/**
 * 
 * @author Or Mauda
 *
 */
public abstract class RAIDSSDManager extends RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice> {
	private Color dataPageColor;
	private List<Color> paritiesColors;
	private Color stripeFrameColor;
	private Color stripeFrameStepColor;
	private final int maxColorsNum = 1000;
	private Boolean showOldParity; // indicates whether the invalid parity pages should be highlighted
	private Boolean showOldData; // indicates whether the invalid data pages should be highlighted
	protected int stripeSize;
	protected int paritiesNumber;

	RAIDSSDManager() {
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
	public TraceParserGeneral<RAIDDevice, RAIDSSDManager> getTraseParser() {
		return new RAIDSimulationTraceParser(this);
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
	public List<WorkloadWidget<RAIDDevice, SSDManager<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice>>> getWorkLoadGeneratorWidgets() {
		List<WorkloadWidget<RAIDDevice, SSDManager<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice>>> creators = new ArrayList<>();
		creators.add(new UniformResizableWorkloadWidget<RAIDDevice, SSDManager<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice>>(this));
		creators.add(new ZipfResizableWorkloadWidget<RAIDDevice, SSDManager<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice>>(this));
		return creators;
	}
	
	@Override
	public int getLpRange() {
		return (getChipsNum() / 2) * getPlanesNum() *(getBlocksNum() - getGCT() - 2)*getPagesNum();
	}
	
	@Override
	public List<AddressWidget<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice, 
		RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice>>> getAddressGetterWidgets() {
		
		List<AddressWidget<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice, 
		RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice>>> addressGetters = new ArrayList<>();
		
		addressGetters.add(new PhysicalAddressWidget<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice,
				RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice>>(this));
		
		addressGetters.add(new LogicalAddressWidget<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice,
				RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice>>(this));
		
		addressGetters.add(new ParityAddressWidget<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice,
				RAIDBasicSSDManager<RAIDPage, RAIDBlock, RAIDPlane, RAIDChip, RAIDDevice>>(this));
		
		return addressGetters;
	}
	
	@Override
	protected List<StatisticsGetter> initStatisticsGetters() {
		List<StatisticsGetter> statisticsGetters = new ArrayList<StatisticsGetter>();
		statisticsGetters.add(new ParityOverheadGetter());
		statisticsGetters.add(new LogicalWritesPerEraseGetter(this));
		statisticsGetters.add(new WriteAmplificationGetter());
		statisticsGetters.add(new ValidDistributionGetter(this));
		return statisticsGetters;
	}
	
	@Override
	protected abstract RAIDDevice getEmptyDevice(List<RAIDChip> emptyChips);

	@Override
	protected RAIDChip getEmptyChip(List<RAIDPlane> planes) {
		RAIDChip.Builder builder = new RAIDChip.Builder();
		builder.setPlanes(planes);
		return builder.build();
	}

	@Override
	protected RAIDPlane getEmptyPlane(List<RAIDBlock> blocks) {
		RAIDPlane.Builder builder = new RAIDPlane.Builder();
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