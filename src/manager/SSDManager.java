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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import entities.Block;
import entities.Chip;
import entities.Device;
import entities.Page;
import entities.Plane;
import entities.StatisticsGetter;
import general.XMLGetter;
import general.XMLParsingException;
import ui.WorkloadWidget;
import zoom.BlocksEraseCountZoomLevel;
import zoom.BlocksValidCountZoomLevel;
import zoom.DetailedZoomLevel;
import zoom.IZoomLevel;
import zoom.PagesZoomLevel;
import zoom.SmallBlocksEraseCountZoomLevel;
import zoom.SmallBlocksValidCountZoomLevel;


/**
 * @author Roman
 * 
 * SSDManager is a abstract base class for every FTL use case.
 * 
 * All the non abstract subclasses of this class will be loaded using reflection library.
 * The UI classes use the static methods of this class to get all the possible use cases in the simulator.
 * This class includes static methods like getManagerByName, getAllManagerNames etc.
 * The static members are managersMap - holding all of the managers by their name(specified in the config),
 * simulatorsList - list of names of the simulation SSD managers, 
 * visualisationsList - list of names of the visualization SSD managers,
 * 
 * The non static part presents the interface which every use case SSDManager will have to implement.
 * Methods like getTraseParser(), initStatisticsGetters(), etc.
 * The type is Generic in terms of the entities it uses so there will be static typing as strict as possible.
 * Every SSDManager is defined to use a very specific set of entities, 
 * which will be defined with the implementation of the use case.
 *
 * @param <P> - Page
 * @param <B> - Block
 * @param <T> - Plane
 * @param <C> - Chip
 * @param <D> - Device
 */
public abstract class SSDManager<P extends Page, B extends Block<P>, T extends Plane<P,B>, C extends Chip<P,B,T>, D extends Device<P,B,T,C>> {
	private static Map<String, ? extends SSDManager<?,?,?,?,?>> managersMap;
	private static List<String> simulatorsList = new ArrayList<>();
	private static List<String> visualizationsList = new ArrayList<>();
	private static String currentManagerName =  null;
	
	/**
	 * Initialize SSD manager using given configuration.
	 * First loads all of the subclasses of the SSDManager.
	 * After creates instance of the non abstract ones, calls their initialization method 
	 * and adds them to the managersMap, by their name(specified in the config file).
	 *   
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
			for (Class<? extends SSDManager<?,?,?,?,?>> clazz : subTypes) {
				if (!Modifier.isAbstract(clazz.getModifiers())) {
					try {
						System.out.println("Initializing " + clazz.getSimpleName());
						SSDManager<?,?,?,?,?> manager = clazz.newInstance();
						manager.initValues(xmlGetter);
						if (manager instanceof VisualizationSSDManager) {
							visualizationsList.add(manager.getManagerName());
						} else {
							simulatorsList.add(manager.getManagerName());
						}
						managers.put(manager.getManagerName(), manager);
						manager.statisticsGetters  = manager.initStatisticsGetters();
						System.out.println("Finished initializing " + clazz.getSimpleName() + " - " + manager.getManagerName());
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
			Collections.sort(visualizationsList);
			Collections.sort(simulatorsList);
			managersMap = managers;
		}
	}
	
	public static SSDManager<?,?,?,?,?> getManager(String managerName) {
		return managersMap.get(managerName);
	}
	
	public static void setCurrentManager(String managerName) {
		currentManagerName = managerName;
	}
	
	public static SSDManager<?, ?, ?, ?, ?> getCurrentManager(){
		return getManager(currentManagerName);
	}
	
	/**
	 * @return all the use case simulation managers
	 */
	public static Iterable<String> getAllSimulationManagerNames() {
		return simulatorsList;
	}
	
	/**
	 * @return all the visualization managers
	 */
	public static Iterable<String> getAllVisualizationManagerNames() {
		return visualizationsList;
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
	
	protected Set<IZoomLevel> supportedZoomLevels = new LinkedHashSet<>();

	/**
	 * @return get trace parser for this manager
	 */
	abstract public TraceParserGeneral<D, ? extends SSDManager<P,B,T,C,D>> getTraseParser();
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
	 * Calculates and returns the logical pages addresses range of the device.
	 * Calculates using the physical sizes and the Over-Provisioning.
	 * @return the size of the device in pages
	 */
	public int getLpRange() {
		return chipsNum * planesNum *(blocksInPlane - gct - 2)*pagesInBlock;
	}
	
	/**
	 * @return name of the SSDManager specified in the config.
	 */
	public String getManagerName() {
		return managerName;
	}
	
	/**
	 * @return Over-Provisioning - specified in the config
	 */
	public double getOP() {
		return op;
	}
	
	/**
	 * @return number of blocks reserved for Over-Provisioning
	 */
	public int getReserved() {
		return reserved;
	}

	/**
	 * @return - Garbage Collection threshold, specified in the config in percents, 
	 * returned in blocks number.
	 */
	public int getGCT() {
		return gct;
	}
	
	/**
	 * @return Number of Chips in the Device, specified in the config.
	 */
	public int getChipsNum() {
		return chipsNum;		
	}
	
	/**
	 * @return Number of Planes in each Chip, specified in the config. 
	 */
	public int getPlanesNum() {
		return planesNum;
	}
	
	/**
	 * @return Number of Blocks in each Plane, specified in the config.
	 */
	public int getBlocksNum() {
		return blocksInPlane;
	}
	
	/**
	 * @return Number of Pages in each Block, specified in the config.
	 */
	public int getPagesNum() {
		return pagesInBlock;
	}

	/**
	 * @return Color of a clean Page.
	 */
	public Color getCleanColor() {
		return cleanColor;
	}
	
	/**
	 * Each use case manager initializes a list of statistics he would like to present.
	 * This method is here to allow other parts of the simulation get those statistics getters.
	 * @return Returns the statistics getters
	 */
	public Iterable<StatisticsGetter> getStatisticsGetters() {
		return statisticsGetters;
	}

	/**
	 * This method simulate the normal write procedure in SSD device.
	 * In order to change the basic writing algorithm change overload this method. 
	 * This operation may invoke the Garbage Collection as a side effect.
	 * 
	 * @param device - the device to write on
	 * @param lp -logical page to write
	 * @return the new device after the write.
	 */
	@SuppressWarnings("unchecked")
	public D writeLP(D device, int lp, int arg) {
		D cleanDevice = (D) device.invokeCleaning();
		cleanDevice = (D) cleanDevice.invalidate(lp);
		cleanDevice = (D) cleanDevice.writeLP(lp, arg);
		return cleanDevice;
	}

	/**
	 * Each use case SSDManager may have specific workload generators he is applicable to.
	 * This is the method to overload in order to add workload generators of your liking. 
	 * @return List of Workload Generators applicable with current SSDManager.
	 */
	public List<WorkloadWidget<D,SSDManager<P,B,T,C,D>>> getWorkLoadGeneratorWidgets() {
		return null;
	}
	
	/**
	 * @return whether stripes view is needed
	 */
	public boolean hasStripes() {
		return false;
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
	
	protected void setSupportedZoomLevels() {
		supportedZoomLevels.add(new DetailedZoomLevel());
		supportedZoomLevels.add(new PagesZoomLevel());
		supportedZoomLevels.add(new BlocksValidCountZoomLevel());
		supportedZoomLevels.add(new BlocksEraseCountZoomLevel());
		supportedZoomLevels.add(new SmallBlocksValidCountZoomLevel());
		supportedZoomLevels.add(new SmallBlocksEraseCountZoomLevel());
	}
	
	public Set<IZoomLevel> getSupportedZoomLevels() {
		return supportedZoomLevels;
	}
	
	D getEmptyDevice() {
		return getEmptyDevice(getEmptyChips(getEmptyPlanes(getEmptyBlocks(getEmptyPages()))));
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
