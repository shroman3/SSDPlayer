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

import java.util.ArrayList;
import java.util.List;

import entities.reusable.ReusableDevice;
import general.XMLGetter;
import general.XMLParsingException;
import ui.WorkloadWidget;
import zoom.BlocksAvgTempZoomLevel;
import zoom.SmallBlocksAvgTempZoomLevel;

public class HotColdReusableSSDManager extends ReusableSSDManager {
	private int tempLimit;

	public int getTempLimit() {
		return tempLimit;
	}
	
	@Override
	public FileTraceParser<ReusableDevice, ReusableSSDManager> getFileTraseParser() {
		return new HotColdTraceParser<ReusableDevice, ReusableSSDManager>(this);
	}
	
	@Override
	public List<WorkloadWidget<ReusableDevice, SSDManager<?, ?, ?, ?, ReusableDevice>>> getWorkLoadGeneratorWidgets() {
		List<WorkloadWidget<ReusableDevice,SSDManager<?, ?, ?, ?, ReusableDevice>>> creators = new ArrayList<>();
		creators.add(new UniformWorkloadWidget<ReusableDevice,SSDManager<?, ?, ?, ?, ReusableDevice>>(this));
		creators.add(new ZipfWorkloadWidget<ReusableDevice,SSDManager<?, ?, ?, ?, ReusableDevice>>(this));
		return creators;
	}
	
	protected void initValues(XMLGetter xmlGetter) throws XMLParsingException {
		super.initValues(xmlGetter);
		tempLimit = getIntField(xmlGetter, "temp_limit");
	}
	
	public boolean isSecondWrite(boolean hasSecondWriteBlock, int temperature) {
		return super.isSecondWrite(hasSecondWriteBlock, temperature) && (temperature < getTempLimit());
	}

	@Override
	protected void setSupportedZoomLevels() {
		super.setSupportedZoomLevels();
		supportedZoomLevels.add(new BlocksAvgTempZoomLevel());
		supportedZoomLevels.add(new SmallBlocksAvgTempZoomLevel());
	}
}