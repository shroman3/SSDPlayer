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
package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import breakpoints.BreakpointBase;
import breakpoints.BreakpointsConstraints;
import breakpoints.BreakpointsDeserializer;
import entities.Device;
import entities.StatisticsGetter;
import general.ConfigProperties;
import general.Consts;
import general.MessageLog;
import general.OneObjectCallback;
import general.TwoObjectsCallback;
import general.XMLGetter;
import general.XMLParsingException;
import manager.SSDManager;
import manager.VisualConfig;
import ui.zoom.ZoomLevelPanel;

public class MainSimulationView extends JFrame {
	private static final long serialVersionUID = 251948453746299747L;
	private static final String VERSION = "1.2.1";
	private static final String CONFIG_XML = "resources/ssd_config.xml";
	private static final String BREAKPOINTS_XML = "resources/ssd_breakpoints.xml";
	private static String managerName;
	private static String inputTrace;
	private static String outputFile;
	private VisualConfig visualConfig;
	private List<BreakpointBase> initialBreakpoints;
	private JPanel devicePanel;
	private JPanel statisticsPanel;
	private DeviceView deviceView;
	private StatisticsView statisticsView;
	private TracePlayer tracePlayer;
	private TracePlayerCLI tracePlayerCLI;
	private JPanel southInnerPanel;
	private ZoomLevelPanel zoomLevelPanel;

	private static List<String> getValueForFlag(List<String> arguments, String flag){
		List<String> value = new ArrayList<>();
		int flag_index = arguments.indexOf(flag);
		if(flag_index == -1){
			return null;
		}
		ListIterator<String> iterator = arguments.listIterator(flag_index);
		iterator.next();
		String str = iterator.next();
		while(str != null && str.charAt(0) != '-'){
			value.add(str);
			if(iterator.hasNext()) {
				str = iterator.next();
			} else {
				break;
			}
		}
		return value;
	}

	//-C resources/ssd_config.xml -F
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				List<String> arguments = Arrays.asList(args);
				List<String> config_xml = getValueForFlag(arguments, "-C");
				if(config_xml == null || config_xml.size() != 1){
					throw new Exception("There should be exactly one config file");
				}
				List<String> inputFiles = getValueForFlag(arguments, "-F");
				if(inputFiles == null || inputFiles.size() != 2){
					throw new Exception("wrong input files format");
				}
				List<String> outputFiles = getValueForFlag(arguments, "-O");
				if(outputFiles == null || outputFiles.size() != 1){
					throw new Exception("there should be exactly one output file");
				}
				XMLGetter xmlGetter = new XMLGetter(config_xml.get(0));
				managerName = inputFiles.get(0);
				inputTrace = inputFiles.get(1);
				outputFile = outputFiles.get(0);

				Consts.initialize(xmlGetter);
				ConfigProperties.initialize(xmlGetter);
				BreakpointsConstraints.initialize(xmlGetter);
				SSDManager.initializeManager(xmlGetter, false);
				String checkResult = checkXmlValues(xmlGetter);
				if (checkResult != null) {
					throw new Exception("Bad XML values");

				}

				final VisualConfig visualConfig = new VisualConfig(xmlGetter);
				MainSimulationView window = new MainSimulationView(visualConfig, false);


			} catch(Exception e){
				String error = "Unable to load config XML file\n" + e.getMessage();
				System.out.println(e.getMessage());
				displayErrorFrame(e.getMessage());

			}
		} else {
			try {
				XMLGetter xmlGetter = new XMLGetter(CONFIG_XML);
				Consts.initialize(xmlGetter);
				initLookAndFeel();
				ConfigProperties.initialize(xmlGetter);
				BreakpointsConstraints.initialize(xmlGetter);
				SSDManager.initializeManager(xmlGetter, true);
				String checkResult = checkXmlValues(xmlGetter);
				if (checkResult != null) {
					displayErrorFrame(checkResult);
					return;
				}

				final VisualConfig visualConfig = new VisualConfig(xmlGetter);

				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							MainSimulationView window = new MainSimulationView(visualConfig, true);
							window.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/ui/images/SSDPlayer.ico")));
							window.setVisible(true);
						} catch (Exception e) {
							displayErrorFrame("Unable to load Simulation \n" + e.toString());
						}
					}
				});
			} catch(Exception e){
				String error = "Unable to load config XML file(resources/ssd_config.xml)\n" + e.getMessage();
				System.out.println(error);
				displayErrorFrame(error);

			}
		}
	}

	private void initializeForCLI(){

		tracePlayerCLI = new TracePlayerCLI(visualConfig,
				new TwoObjectsCallback<Device<?>, Iterable<StatisticsGetter>>() {
					@Override
					public void message(Device<?> device, Iterable<StatisticsGetter> statisticsGetters) {
						resetDeviceCLI(device, statisticsGetters);

					}
				}, new OneObjectCallback<Device<?>>() {
			@Override
			public void message(Device<?> device) {
				updateDeviceCLI(device);
			}
		}, new OneObjectCallback<Boolean>() {
			@Override
			public void message(Boolean repaintDevice) {
				deviceView.repaintDevice();
			}
		},
				managerName,
				inputTrace,
				outputFile
				);/* "Greedy",
				"C:\\Users\\zelik\\Desktop\\semester G\\236388 - project in storage systems\\SSDPlayer_v1.2.1\\traces\\Small_Uniform.trace",
				"C:\\Users\\zelik\\Desktop\\semester G\\236388 - project in storage systems\\SSDPlayer\\output\\CLI_Small_Uniform");*/

	}

	// Check xml values are legal.   
	private static String checkXmlValues(XMLGetter xmlGetter) {
		try{
			int value = xmlGetter.getIntField("physical","overprovisioning");
			if(value < 0 || value > 100){
				return "overposishioning has illegal value";
			}
		} catch (XMLParsingException | NumberFormatException e) {
			return "overprovisioning is not specified in config";
		}

		try{
			int value = xmlGetter.getIntField("physical","gc_threshold");
			if(value < 0 || value > 100){
				return "gc_threshold has illegal value";
			}
		} catch (XMLParsingException | NumberFormatException e) { }

		try{
			int value = xmlGetter.getIntField("physical","gc_threshold_blocks");
			if(value < 0){
				return "gc_threshold_blocks has illegal value";
			}
		} catch (XMLParsingException | NumberFormatException e) { }

		try{
			int value = xmlGetter.getIntField("physical","chips");
			if(value < 0){
				return "chips has illegal value";
			}
		} catch (XMLParsingException | NumberFormatException e) {
			return "chips is not specified in config";
		}

		try{
			int value = xmlGetter.getIntField("physical","planes");
			if(value < 0){
				return "planes has illegal value";
			}
		} catch (XMLParsingException | NumberFormatException e) {
			return "planes is not specified in config";
		}

		try{
			int value = xmlGetter.getIntField("physical","blocks");
			if(value < 0){
				return "blocks has illegal value";
			}
		} catch (XMLParsingException | NumberFormatException e) {
			return "blocks is not specified in config";
		}

		try{
			int value = xmlGetter.getIntField("physical","pages");
			if(value < 0){
				return "pages has illegal value";
			}
		} catch (XMLParsingException | NumberFormatException e) {
			return "pages is not specified in config";
		}
		return null;
	}

	private static void displayErrorFrame(String string) {
		JLabel errorLabel = new JLabel(string);
		errorLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		JFrame errorFrame = new JFrame("Unable to load config XML");
		errorFrame.getContentPane().add(errorLabel, "Center");
		errorFrame.pack();

		Dimension windowSize = errorFrame.getSize();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();

		int dx = centerPoint.x - windowSize.width / 2;
		int dy = centerPoint.y - windowSize.height / 2;
		errorFrame.setLocation(dx, dy);

		errorFrame.setDefaultCloseOperation(3);
		errorFrame.setVisible(true);
	}

	public MainSimulationView(VisualConfig visualConfig, boolean withUI) {
		super("SSDPlayer " + VERSION);
		if (withUI) {
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					tracePlayer.stopTrace();
				}
			});
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.visualConfig = visualConfig;
			initialize();
		} else {
			this.visualConfig = visualConfig;
			initializeForCLI();
			//continue here to use the trace functions (which will be public)
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setExtendedState(MAXIMIZED_BOTH);
		getContentPane().setLayout(new BorderLayout(3,3));
		
		devicePanel = new JPanel(new FlowLayout());
		JScrollPane scrollableDevicePane = new JScrollPane(devicePanel);
		scrollableDevicePane.setBorder(BorderFactory.createEmptyBorder());
		getContentPane().add(scrollableDevicePane, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
		getContentPane().add(southPanel, BorderLayout.SOUTH);
		
		tracePlayer = new TracePlayer(visualConfig,
                new TwoObjectsCallback<Device<?>, Iterable<StatisticsGetter>>() {
                        @Override
                        public void message(Device<?> device, Iterable<StatisticsGetter> statisticsGetters) {
                                resetDevice(device, statisticsGetters);
                                if (zoomLevelPanel != null) {
                                	zoomLevelPanel.setZoomLevel(tracePlayer.getZoomLevel());
                                }
                        }
                }, new OneObjectCallback<Device<?>>() {
                        @Override
                        public void message(Device<?> device) {
                                updateDevice(device);
                        }
                }, new OneObjectCallback<Boolean>() {
                        @Override
                        public void message(Boolean repaintDevice) {
                                deviceView.repaintDevice();
                                devicePanel.updateUI();
                    			zoomLevelPanel.setZoomLevel(tracePlayer.getZoomLevel());
                        }
                });

		southPanel.add(tracePlayer);
		southInnerPanel = new JPanel();
		southInnerPanel.setLayout(new BoxLayout(southInnerPanel, BoxLayout.X_AXIS));

		LogView logView = new LogView();
		MessageLog.initialize(logView);
		MessageLog.setTracePlayer(tracePlayer);
		
		initialBreakpoints = BreakpointsDeserializer.deserialize(BREAKPOINTS_XML);
		tracePlayer.setInitialBreakpoints(initialBreakpoints);
		
		JPanel logPanel = new JPanel(new FlowLayout());
		logPanel.setMinimumSize(new Dimension(320, 162));
		logPanel.setPreferredSize(new Dimension(320, 162));
		logPanel.setMaximumSize(new Dimension(320, 162));
		logPanel.add(logView);
		
		JScrollPane scrollableMessagesPane = new JScrollPane(logPanel);
		setEdgesPaneSize(scrollableMessagesPane);

		statisticsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		zoomLevelPanel = new ZoomLevelPanel(tracePlayer.getZoomLevel(), visualConfig);
		
		JScrollPane scrollableZoomPane = new JScrollPane(zoomLevelPanel);
		setEdgesPaneSize(scrollableZoomPane);
		
		JScrollPane scrollableStatisticsPane = new JScrollPane(statisticsPanel);
		scrollableStatisticsPane.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Consts.getInstance().colors.BORDER));
		
		southInnerPanel.add(scrollableZoomPane);
		southInnerPanel.add(scrollableStatisticsPane);
		southInnerPanel.add(scrollableMessagesPane);
		southPanel.add(southInnerPanel);
		
		setMinimumSize(new Dimension(550, 550));
	}


	private void setEdgesPaneSize(JScrollPane scrollableMessagesPane) {
		scrollableMessagesPane.setMinimumSize(new Dimension(320, 162));
		scrollableMessagesPane.setPreferredSize(new Dimension(320, 162));
		scrollableMessagesPane.setMaximumSize(new Dimension(320, 162));
		scrollableMessagesPane.setBorder(BorderFactory.createEmptyBorder());
	}

	private void resetDevice(final Device<?> device, final Iterable<StatisticsGetter> statisticsGetters) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				devicePanel.removeAll();
				deviceView = new DeviceView(visualConfig, device);
				devicePanel.add(deviceView);
				devicePanel.updateUI();
				
				statisticsPanel.removeAll();
				statisticsView = new StatisticsView(visualConfig, statisticsGetters);
				statisticsView.setAlignmentY(Component.CENTER_ALIGNMENT);
				statisticsPanel.add(statisticsView);
				statisticsPanel.updateUI();
			}
		});
	}

	private void resetDeviceCLI(final Device<?> device, final Iterable<StatisticsGetter> statisticsGetters) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				deviceView = new DeviceView(visualConfig, device);

				statisticsView = new StatisticsView(visualConfig, statisticsGetters);
				statisticsView.setAlignmentY(Component.CENTER_ALIGNMENT);

			}
		});
	}

	private void updateDevice(final Device<?> device) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				deviceView.setDevice(device);
				statisticsView.updateStatistics(device);
				statisticsPanel.updateUI();
			}
		});
	}

	private void updateDeviceCLI(final Device<?> device) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				deviceView.setDevice(device);
				statisticsView.updateStatistics(device);
			}
		});
	}

	private static void initLookAndFeel() {
		UIManager.put("nimbusBase", Consts.getInstance().colors.OUTER_BG);
		UIManager.put("nimbusFocus", Consts.getInstance().colors.HIGHLIGHT);
		UIManager.put("nimbusBlueGrey", Consts.getInstance().colors.HIGHLIGHT);
		UIManager.put("control", Consts.getInstance().colors.OUTER_BG);
		UIManager.put("text", Consts.getInstance().colors.CONTROL_TEXT);
		UIManager.put("nimbusDisabledText", Consts.getInstance().colors.INNER_BG);
		UIManager.put("nimbusLightBackground", Consts.getInstance().colors.HIGHLIGHT);
		UIManager.put("nimbusSelectedText", Consts.getInstance().colors.INNER_BG);
		UIManager.put("nimbusSelectionBackground", Consts.getInstance().colors.CONTROL_TEXT);
		UIManager.put("info", Consts.getInstance().colors.OUTER_BG);
		UIManager.put("nimbusBorder", Consts.getInstance().colors.BORDER);
		UIManager.put("nimbusLightBackground", Consts.getInstance().colors.HIGHLIGHT);
		UIManager.put("controlLHighlight", Consts.getInstance().colors.HIGHLIGHT);
		UIManager.put("ComboBox.background", Consts.getInstance().colors.OUTER_BG);
		UIManager.put("Button.background", Consts.getInstance().colors.OUTER_BG);
		UIManager.put("ScrollBar.minimumThumbSize", new Dimension(32, 32));
		
		UIManager.put("defaultFont", Consts.getInstance().fonts.CAPTION);
		UIManager.put("Button.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("ToggleButton.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("RadioButton.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("CheckBox.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("ColorChooser.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("ComboBox.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("Label.font", Consts.getInstance().fonts.CAPTION_BOLD);
		UIManager.put("List.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("MenuBar.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("MenuItem.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("RadioButtonMenuItem.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("CheckBoxMenuItem.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("Menu.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("PopupMenu.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("OptionPane.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("Panel.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("ProgressBar.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("ScrollPane.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("Viewport.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("TabbedPane.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("Table.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("TableHeader.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("TextField.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("PasswordField.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("TextArea.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("TextPane.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("EditorPane.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("TitledBorder.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("ToolBar.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("ToolTip.font", Consts.getInstance().fonts.CAPTION);
		UIManager.put("Tree.font", Consts.getInstance().fonts.CAPTION);
		
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		    if ("Nimbus".equals(info.getName())) {
		        try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
		        break;
		    }
		}
	}
}

