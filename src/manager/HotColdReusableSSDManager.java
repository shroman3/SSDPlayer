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

import entities.reusable.ReusableBlock;
import entities.reusable.ReusableChip;
import entities.reusable.ReusableDevice;
import entities.reusable.ReusablePage;
import entities.reusable.ReusablePlane;
import general.XMLGetter;
import general.XMLParsingException;

import java.util.ArrayList;
import java.util.List;

import ui.WorkloadWidget;

public class HotColdReusableSSDManager extends ReusableSSDManager {
	private int tempLimit;

	public int getTempLimit() {
		return tempLimit;
	}
	
	@Override
	public TraceParserGeneral<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice, ReusableSSDManager> getTraseParser() {
		return new HotColdTraceParser<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice, ReusableSSDManager>(this);
	}
	
	@Override
	public List<WorkloadWidget<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice, 
		SSDManager<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice>>> getWorkLoadGeneratorWidgets() {
		List<WorkloadWidget<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice,SSDManager<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice>>> creators = new ArrayList<>();
		creators.add(new UniformWorkloadWidget<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice,SSDManager<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice>>(this));
		creators.add(new ZipfWorkloadWidget<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice,SSDManager<ReusablePage, ReusableBlock, ReusablePlane, ReusableChip, ReusableDevice>>(this));
		return creators;
	}
	
	protected void initValues(XMLGetter xmlGetter) throws XMLParsingException {
		super.initValues(xmlGetter);
		tempLimit = getIntField(xmlGetter, "temp_limit");
	}
	
	public boolean isSecondWrite(boolean hasSecondWriteBlock, int temperature) {
		return super.isSecondWrite(hasSecondWriteBlock, temperature) && (temperature < getTempLimit());
	}

}