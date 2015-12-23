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
import entities.RAID.visualization.RAIDVisualizationBlock;
import entities.RAID.visualization.RAIDVisualizationChip;
import entities.RAID.visualization.RAIDVisualizationDevice;
import entities.RAID.visualization.RAIDVisualizationPage;
import entities.RAID.visualization.RAIDVisualizationPlane;
import general.XMLGetter;
import general.XMLParsingException;
import ui.AddressWidget;

/**
 * 
 * @author Or Mauda
 *
 */
public class RAIDVisualizationSSDManager extends RAIDBasicSSDManager<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip, RAIDVisualizationDevice>  
		implements  VisualizationSSDManager {
	private Color dataPageColor;
	private List<Color> paritiesColors;
	private Color stripeFrameColor;
	private Color stripeFrameStepColor;
	private int stripeSize;
	private int paritiesNumber;
	private Boolean showOldParity; // indicates whether the invalid parity pages should be highlighted
	private Boolean showOldData; // indicates whether the invalid data pages should be highlighted

	RAIDVisualizationSSDManager() {
	}

	public Color getDataPageColor() {
		return dataPageColor;
	}
	
	public Color getParityPageColor(int parityNumber) {
		return paritiesColors.get(parityNumber-1);
	}
	
	public Color getStripeFrameColor(int index) {
		int r = (stripeFrameColor.getRed() + index * stripeFrameStepColor.getRed()) % 256;
		int g = (stripeFrameColor.getGreen() + index * stripeFrameStepColor.getGreen()) % 256;
		int b = (stripeFrameColor.getBlue() + index * stripeFrameStepColor.getBlue()) % 256;
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
	
	@Override
	public TraceParserGeneral<RAIDVisualizationDevice, RAIDVisualizationSSDManager> getTraseParser() {
		return new RAIDVisualizationTraceParser(this);
	}

	protected void initValues(XMLGetter xmlGetter) throws XMLParsingException {
		super.initValues(xmlGetter);
		dataPageColor = getColorField(xmlGetter, "data_color");
		paritiesColors = new ArrayList<Color>(getColorsListField(xmlGetter, "parity_color"));
		stripeSize = getIntField(xmlGetter, "stripe_size");
		paritiesNumber = getChipsNum() - stripeSize;
		stripeFrameColor = getColorField(xmlGetter, "stripe_frame_color");
		stripeFrameStepColor = getColorField(xmlGetter, "stripe_frame_step");
		showOldParity = xmlGetter.getBooleanField(VisualConfig.VISUAL_CONFIG, "show_old_parity");
		showOldData = xmlGetter.getBooleanField(VisualConfig.VISUAL_CONFIG, "show_old_data");
	}

	@Override
	public RAIDVisualizationPage getEmptyPage() {
		return new RAIDVisualizationPage.Builder().setManager(this).build();
	}
	
	@Override
	public List<AddressWidget<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip, RAIDVisualizationDevice, 
	RAIDBasicSSDManager<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip, RAIDVisualizationDevice>>> getAddressGetterWidgets() {
		
		List<AddressWidget<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip, RAIDVisualizationDevice,
		RAIDBasicSSDManager<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip, RAIDVisualizationDevice>>> addressGetters = new ArrayList<>();
		
		addressGetters.add(new PhysicalAddressWidget<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip, RAIDVisualizationDevice,
				RAIDBasicSSDManager<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip, RAIDVisualizationDevice>>(this));
		
		addressGetters.add(new LogicalAddressWidget<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip, RAIDVisualizationDevice,
				RAIDBasicSSDManager<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip, RAIDVisualizationDevice>>(this));
		
		addressGetters.add(new ParityAddressWidget<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip, RAIDVisualizationDevice,
				RAIDBasicSSDManager<RAIDVisualizationPage, RAIDVisualizationBlock, RAIDVisualizationPlane, RAIDVisualizationChip, RAIDVisualizationDevice>>(this));
		
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
	protected RAIDVisualizationDevice getEmptyDevice(List<RAIDVisualizationChip> emptyChips) {
		RAIDVisualizationDevice.Builder builder = new RAIDVisualizationDevice.Builder();
		builder.setChips(emptyChips);
		return builder.build();
	}

	@Override
	protected RAIDVisualizationChip getEmptyChip(List<RAIDVisualizationPlane> planes) {
		RAIDVisualizationChip.Builder builder = new RAIDVisualizationChip.Builder();
		builder.setPlanes(planes);
		return builder.build();
	}

	@Override
	protected RAIDVisualizationPlane getEmptyPlane(List<RAIDVisualizationBlock> blocks) {
		RAIDVisualizationPlane.Builder builder = new RAIDVisualizationPlane.Builder();
		builder.setBlocks(blocks);
		builder.setManager(this);
		return builder.build();
	}

	@Override
	protected RAIDVisualizationBlock getEmptyBlock(List<RAIDVisualizationPage> pages) {
		RAIDVisualizationBlock.Builder builder = new RAIDVisualizationBlock.Builder();
		builder.setManager(this).setEraseCounter(0).setInGC(false)
				.setStatus(BlockStatusGeneral.CLEAN).setPagesList(pages);
		return builder.build();
	}
}