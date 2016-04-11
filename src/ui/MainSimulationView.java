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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import breakpoints.BreakpointBase;
import breakpoints.BreakpointsDeserializer;
import entities.Device;
import entities.StatisticsGetter;
import general.ConfigProperties;
import general.Consts;
import general.OneObjectCallback;
import general.TwoObjectsCallback;
import general.XMLGetter;
import general.XMLParsingException;
import manager.SSDManager;
import manager.VisualConfig;
import ui.breakpoints.TriggeredBreakpointsView;

public class MainSimulationView extends JFrame {
	private static final long serialVersionUID = 251948453746299747L;
	private static final String VERSION = "1.0";
	private static final String CONFIG_XML = "resources/ssd_config.xml";
	private static final String BREAKPOINTS_XML = "resources/ssd_breakpoints.xml";
	private VisualConfig visualConfig;
	private List<BreakpointBase> initialBreakpoints;
	private JPanel devicePanel;
	private JPanel statisticsPanel;
	private DeviceView deviceView;
	private StatisticsView statisticsView;
	private TracePlayer tracePlayer;
	private JPanel triggeredBreakpointsView;
	private JPanel southInnerPanel;

	public static void main(String[] args) {
		try {
			XMLGetter xmlGetter = new XMLGetter(CONFIG_XML);
			final List<BreakpointBase> initialBreakpoints = BreakpointsDeserializer.deserialize(BREAKPOINTS_XML);
			
			SSDManager.initializeManager(xmlGetter);
			ConfigProperties.initialize(xmlGetter);
			final VisualConfig visualConfig = new VisualConfig(xmlGetter);

			initLookAndFeel();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						MainSimulationView window = new MainSimulationView(visualConfig, initialBreakpoints);
						window.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			});
		} catch (ParserConfigurationException | SAXException | IOException | XMLParsingException e) {
			throw new RuntimeException("Unable to load config XML file(" + CONFIG_XML + ")\n"+ e.getMessage());
		}
	}


	public MainSimulationView(VisualConfig visualConfig, List<BreakpointBase> initialBreakpoints) {
		super("SSDPlayer " + VERSION);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				tracePlayer.stopTrace();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.visualConfig = visualConfig;
		this.initialBreakpoints = initialBreakpoints;
		initialize();
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
		
		tracePlayer = new TracePlayer(visualConfig, new TwoObjectsCallback<Device<?,?,?,?>, Iterable<StatisticsGetter>>() {
			@Override
			public void message(Device<?, ?, ?, ?> device, Iterable<StatisticsGetter> statisticsGetters) {
				resetDevice(device, statisticsGetters);
			}
		}, new OneObjectCallback<Device<?,?,?,?>>() {
			@Override
			public void message(Device<?, ?, ?, ?> device) {
				updateDevice(device);				
			}
		});
		tracePlayer.setInitialBreakpoints(initialBreakpoints);
		
		southPanel.add(tracePlayer);
		
		southInnerPanel = new JPanel();
		southInnerPanel.setLayout(new BoxLayout(southInnerPanel, BoxLayout.X_AXIS));
		triggeredBreakpointsView = new TriggeredBreakpointsView();

		statisticsPanel = new JPanel(new FlowLayout());
		JScrollPane scrollableStatisticsPane = new JScrollPane(statisticsPanel);
		scrollableStatisticsPane.setBorder(BorderFactory.createEmptyBorder());
		
		southInnerPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		statisticsPanel.setBorder(BorderFactory.createLineBorder(Color.pink));
		southInnerPanel.add(scrollableStatisticsPane);
		southInnerPanel.add(triggeredBreakpointsView);
		southPanel.add(southInnerPanel);
		
		setMinimumSize(new Dimension(550, 550));
	}

	private void resetDevice(final Device<?, ?, ?, ?> device, final Iterable<StatisticsGetter> statisticsGetters) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				devicePanel.removeAll();
				deviceView = new DeviceView(visualConfig, device);
				devicePanel.add(deviceView);
				devicePanel.updateUI();
				
				statisticsPanel.removeAll();
				statisticsView = new StatisticsView(visualConfig, statisticsGetters);
				statisticsPanel.add(statisticsView);
				statisticsPanel.updateUI();
			}
		});
	}

	private void updateDevice(final Device<?, ?, ?, ?> device) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				deviceView.setDevice(device);
				statisticsView.updateStatistics(device);
				statisticsPanel.updateUI();
			}
		});
	}

	private static void initLookAndFeel() {
		UIManager.put("nimbusBase", Consts.Colors.CONTROL);
		UIManager.put("nimbusFocus", Consts.Colors.HIGHLIGHT);
		UIManager.put("nimbusBlueGrey", Consts.Colors.HIGHLIGHT);
		UIManager.put("control", Consts.Colors.CONTROL);
		UIManager.put("text", Consts.Colors.TEXT);
		UIManager.put("nimbusDisabledText", Consts.Colors.BG);
		UIManager.put("nimbusLightBackground", Consts.Colors.HIGHLIGHT);
		UIManager.put("nimbusSelectedText", Consts.Colors.BG);
		UIManager.put("nimbusSelectionBackground", Consts.Colors.TEXT);
		UIManager.put("info", Consts.Colors.CONTROL);
		UIManager.put("nimbusBorder", Consts.Colors.BORDER);
		UIManager.put("nimbusLightBackground", Consts.Colors.HIGHLIGHT);
		UIManager.put("controlLHighlight", Consts.Colors.HIGHLIGHT);
		UIManager.put("ComboBox.background", Consts.Colors.CONTROL);
		UIManager.put("Button.background", Consts.Colors.CONTROL);
		
		
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

