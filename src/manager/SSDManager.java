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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import ui.AddressWidget;
import ui.WorkloadWidget;
import entities.Block;
import entities.Chip;
import entities.Device;
import entities.Page;
import entities.Plane;
import entities.StatisticsGetter;
import general.XMLGetter;
import general.XMLParsingException;


/**
 * @author Roman
 * 
 * SSDManager is a base class for every use case simulation.
 *
 * @param <P> - Page
 * @param <B> - Block
 * @param <T> - Plane
 * @param <C> - Chip
 * @param <D> - Device
 */
public abstract class SSDManager<P extends Page, B extends Block<P>, T extends Plane<P,B>, C extends Chip<P,B,T>, D extends Device<P,B,T,C>> {
	private static Map<String, ? extends SSDManager<?,?,?,?,?>> managersMap;
	private static Map<String, ? extends SSDManager<?,?,?,?,?>> simulatorsMap;
	private static List<String> managersList = new ArrayList<>();
	private static List<String> simulatorsList = new ArrayList<>();
	
	/**
	 * Initialize SSD manager using given configuration
	 * @param xmlGetter - configuration getter 
	 */
	public static void initializeManager(XMLGetter xmlGetter) {
		if (managersMap == null) {		
			Reflections reflections = new Reflections(SSDManager.class.getPackage().getName());
			// We use here raw type and do the casting in order for the code to compile.
			@SuppressWarnings("rawtypes")
			Set<Class<? extends SSDManager>> subTypesAux = reflections.getSubTypesOf(SSDManager.class);
			Object auxObj = subTypesAux;
			@SuppressWarnings("unchecked")
			Set<Class<? extends SSDManager<?,?,?,?,?>>> subTypes = (Set<Class<? extends SSDManager<?,?,?,?,?>>>)auxObj;
			
			Map<String, SSDManager<?,?,?,?,?>> managers = new HashMap<String, SSDManager<?,?,?,?,?>>();
			Map<String, SSDManager<?,?,?,?,?>> simulators = new HashMap<String, SSDManager<?,?,?,?,?>>();
			for (Class<? extends SSDManager<?,?,?,?,?>> clazz : subTypes) {
				if (!Modifier.isAbstract(clazz.getModifiers())) {
					try {
						System.out.println("Initializing " + clazz.getSimpleName());
						SSDManager<?,?,?,?,?> manager = clazz.newInstance();
						manager.initValues(xmlGetter);
						if (manager instanceof VisualizationSSDManager) {
							simulators.put(manager.getManagerName(), manager);
							simulatorsList.add(manager.getManagerName());
						} else {
							managers.put(manager.getManagerName(), manager);
							managersList.add(manager.getManagerName());
						}
						manager.statisticsGetters  = manager.initStatisticsGetters();
						System.out.println("Finished initializing " + clazz.getSimpleName() + " - " + manager.getManagerName());
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
			Collections.sort(simulatorsList);
			Collections.sort(managersList);
			managersMap = managers;
			simulatorsMap = simulators;
		}
	}
	
	public static SSDManager<?,?,?,?,?> getManager(String managerName) {
		SSDManager<?, ?, ?, ?, ?> manager = managersMap.get(managerName);
		if(manager == null) {
			manager = simulatorsMap.get(managerName);
		}
		return manager;
	}
	
	/**
	 * @return all the use case simulation managers
	 */
	public static Iterable<String> getAllManagers() {
		return managersList;
	}
	
	/**
	 * @return all the visual simulators
	 */
	public static Iterable<String> getAllSimulators() {
		return simulatorsList;
	}
	
	private List<StatisticsGetter> statisticsGetters;

	private String managerName;
	private int op = -1;
	private int reserved = -1;
	private int gct = -1;
	private int chipsNum = -1;
	private int planesNum = -1;
	private int blocksInPlane = -1;
	private int pagesInBlock = -1;
	private Color cleanColor = null;

	/**
	 * @return get trace parser for this manager
	 */
	abstract public TraceParserGeneral<P, B, T, C, D, ? extends SSDManager<P,B,T,C,D>> getTraseParser();
	/**
	 * @return get list for statistic getters
	 */
	abstract protected List<StatisticsGetter> initStatisticsGetters();
	/**
	 * @param chips - empty chips
	 * @return empty device initializing
	 */
	abstract protected D getEmptyDevice(List<C> chips);
	/**
	 * @param planes - empty planes
	 * @return empty chip for device initializing
	 */
	abstract protected C getEmptyChip(List<T> planes);
	/**
	 * @param blocks - empty blocks
	 * @return empty plane for device initializing
	 */
	abstract protected T getEmptyPlane(List<B> blocks);
	/**
	 * @param pages - empty pages
	 * @return empty block for device initializing
	 */
	abstract protected B getEmptyBlock(List<P> pages);
	/**
	 * @return empty page for device initializing
 	 */
	abstract public P getEmptyPage();


	SSDManager() {
	}

	/**
	 * @return the size of the device in pages
	 */
	public int getLpRange() {
		return chipsNum * planesNum *(blocksInPlane - gct - 2)*pagesInBlock;
	}
	
	public String getManagerName() {
		return managerName;
	}
	
	public double getOP() {
		return op;
	}
	
	public int getReserved() {
		return reserved;
	}

	public int getGCT() {
		return gct;
	}
	
	public int getChipsNum() {
		return chipsNum;		
	}
	
	public int getPlanesNum() {
		return planesNum;
	}
	
	public int getBlocksNum() {
		return blocksInPlane;
	}
	
	public int getPagesNum() {
		return pagesInBlock;
	}

	public Color getCleanColor() {
		return cleanColor;
	}
	
	public Iterable<StatisticsGetter> getStatisticsGetters() {
		return statisticsGetters;
	}
	
	public D getEmptyDevice() {
		return getEmptyDevice(getEmptyChips(getEmptyPlanes(getEmptyBlocks(getEmptyPages()))));
	}
	
	/**
	 * This method simulate the normal write procedure in SSD device
	 * 
	 * @param device
	 *            - the device to write on
	 * @param lp
	 *            -logical page to write
	 * @return the device after the write
	 */
	@SuppressWarnings("unchecked")
	public D writeLP(D device, int lp, int arg) {
		D cleanDevice = (D) device.invokeCleaning();
		cleanDevice = (D) cleanDevice.invalidate(lp);
		cleanDevice = (D) cleanDevice.writeLP(lp, arg);
		return cleanDevice;
	}

	public List<WorkloadWidget<P,B,T,C,D,SSDManager<P,B,T,C,D>>> getWorkLoadGeneratorWidgets() {
		return null;
	}
	
	public List<AddressWidget<P,B,T,C,D,SSDManager<P,B,T,C,D>>> getAddressGetterWidgets() {
		return null;
	}
	
	/**
	 * IMPORTANT to call the super.initValues(xmlGetter) in the extending managers
	 * Initializes SSD managers parameters from the XML config file
	 * @param xmlGetter - XML config file getter
	 * @throws XMLParsingException
	 */
	protected void initValues(XMLGetter xmlGetter) throws XMLParsingException {
		initPhysicalValues(xmlGetter);
		managerName = getStringField(xmlGetter, "name");
		cleanColor = getColorField(xmlGetter, "clean_color");
		
		reserved = (int)(blocksInPlane * ((double)op/(op+100)));
		gct = (int)(blocksInPlane * ((double)gct/(100)));
	}
	
	protected String getStringField(XMLGetter xmlGetter, String field) throws XMLParsingException {
		return xmlGetter.getStringField(getClass().getSimpleName(), field);
	}
	
	protected int getIntField(XMLGetter xmlGetter, String field) throws XMLParsingException {
		return xmlGetter.getIntField(getClass().getSimpleName(), field);
	}
	
	protected List<Integer> getListField(XMLGetter xmlGetter, String field) throws XMLParsingException {
		return xmlGetter.getListField(getClass().getSimpleName(), field);
	}
	
	protected List<Color> getColorsListField(XMLGetter xmlGetter, String field) throws XMLParsingException {
		return xmlGetter.getColorsListField(getClass().getSimpleName(), field);
	}
	
	protected Color getColorField(XMLGetter xmlGetter, String field) throws XMLParsingException {
		return xmlGetter.getColorField(getClass().getSimpleName(), field);
	}
	
	protected List<C> getEmptyChips(List<T> planes) {
		List<C> chips = new ArrayList<C>(getChipsNum());
		C chip = getEmptyChip(planes);
		for (int i = 0; i < getChipsNum(); i++) {
			chips.add(chip);
		}
		return chips;
	}

	protected List<T> getEmptyPlanes(List<B> blocks) {
		List<T> planes = new ArrayList<T>(getPlanesNum());
		T plane = getEmptyPlane(blocks);
		for (int i = 0; i < getPlanesNum(); i++) {
			planes.add(plane);
		}
		return planes;
	}

	protected List<B> getEmptyBlocks(List<P> pages) {
		List<B> blocks = new ArrayList<B>(getBlocksNum());
		B block = getEmptyBlock(pages);
		for (int i = 0; i < getBlocksNum(); i++) {
			blocks.add(block);
		}
		return blocks;
	}

	protected List<P> getEmptyPages() {
		List<P> pages = new ArrayList<P>(getPagesNum());
		P page = getEmptyPage();
		for (int i = 0; i < getPagesNum(); i++) {
			pages.add(page);
		}
		return pages;
	}

	private void initPhysicalValues(XMLGetter xmlGetter) throws XMLParsingException {
		op = xmlGetter.getIntField("physical", "overprovisioning");
		gct = xmlGetter.getIntField("physical", "gc_threshold");
		chipsNum = xmlGetter.getIntField("physical", "chips");
		planesNum = xmlGetter.getIntField("physical", "planes");
		blocksInPlane = xmlGetter.getIntField("physical", "blocks");
		pagesInBlock = xmlGetter.getIntField("physical", "pages");
	}
}
