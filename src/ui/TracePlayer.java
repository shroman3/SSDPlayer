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
package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicProgressBarUI;

import breakpoints.BreakpointBase;
import breakpoints.IBreakpoint;
import entities.ActionLog;
import entities.Device;
import entities.StatisticsGetter;
import entities.RAID.RAIDBasicPage;
import general.Consts;
import general.MessageLog;
import general.OneObjectCallback;
import general.TwoObjectsCallback;
import log.Message.BreakpointMessage;
import log.Message.ErrorMessage;
import log.Message.InfoMessage;
import manager.RAIDBasicSSDManager;
import manager.SSDManager;
import manager.TraceParser;
import manager.FileTraceParser;
import manager.VisualConfig;
import manager.WorkloadGenerator;
import ui.breakpoints.InfoDialog;
import ui.breakpoints.ManageBreakpointsDialog;
import ui.sampling.SamplingRateDialog;
import ui.zoom.ZoomLevelDialog;
import utils.Utils;
import zoom.IZoomLevel;

/**
 * 
 * November 2015: revised by Or Mauda for additional RAID functionality.
 *
 */
public class TracePlayer extends JPanel {
	private static final long serialVersionUID = 1L;

	private final ImageIcon iconPlay = new ImageIcon(getClass().getResource("/ui/images/play.png"));
	private final ImageIcon iconPause = new ImageIcon(getClass().getResource("/ui/images/pause.png"));

	private final JButton playPauseButton = new JButton(iconPlay);
	private final JButton stopButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/stop.png")));
	private final JButton nextButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/next.png")));
	private final JButton openButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/eject.png")));
	private final JButton generateButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/generate.png")));
	private final JButton showStripeButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/showStripe.png")));
	private final JButton breakpointsButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/breakpoint.png")));
	private final JButton zoomButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/zoom.png")));
	private final JButton infoButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/info.png")));
	private final JButton samplingRateButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/filter.png")));

	private JProgressBar progressBar;
	private TraceParser<? extends Device<?>, ?> parser;

	private JFileChooser traceChooser;
	private JLabel stopLabel;

	private int numberOfLines = 0;
	private String fileName = "";

	private boolean isPaused = true;

	private ScheduledExecutorService schedular;
	private int currFrameCounter = 0;

	private long readingSpeed;

	private SeparatorComboBox managersList;

	private static SSDManager<?, ?, ?, ?, ? extends Device<?>> manager;

	private LoadGeneratorsCreatorsFrame creatorsFrame;

	private StripesInfoFrame stripesFrame;

	private OneObjectCallback<Device<?>> updateDeviceView;

	private TwoObjectsCallback<Device<?>, Iterable<StatisticsGetter>> resetDevice;

	private Device<?> currentDevice;

	private List<BreakpointBase> breakpoints;
	private ManageBreakpointsDialog breakpointsDialog;
	private ZoomLevelDialog zoomDialog;
	private InfoDialog infoDialog;
	private VisualConfig visualConfig;
	private SamplingRateDialog samplingRateDialog;

	private OneObjectCallback<Boolean> resetDeviceView;

	private boolean abortSignal = false;

	public TracePlayer(VisualConfig visualConfig,
			TwoObjectsCallback<Device<?>, Iterable<StatisticsGetter>> resetDevice,
			OneObjectCallback<Device<?>> updateDeviceView, OneObjectCallback<Boolean> resetDeviceView) {
		Utils.validateNotNull(updateDeviceView, "Update device callback");
		Utils.validateNotNull(resetDevice, "Reset device callback");
		this.resetDevice = resetDevice;
		this.updateDeviceView = updateDeviceView;
		this.resetDeviceView = resetDeviceView;
		this.visualConfig = visualConfig;
		ActionLog.resetLog();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(new RoundedBorder(Consts.getInstance().colors.BORDER));
		initTraceParsing(visualConfig);
		initManagerSelection();
		initButtons();
		initProgressBar();
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void stopTrace() {
		isPaused = true;
		if (schedular != null) {
			updateDeviceView.message(currentDevice);
			schedular.shutdown();
			schedular = null;
			parser.close();
			playPauseButton.setEnabled(false);
			playPauseButton.setIcon(iconPlay);
			stopButton.setEnabled(false);
			nextButton.setEnabled(false);
			showStripeButton.setEnabled(false);
			openButton.setEnabled(true);
			generateButton.setEnabled(true);
			managersList.setEnabled(true);
			currFrameCounter = 0;
			fileName = "";
			if (stripesFrame != null) {
				StripesInfoFrame.reset(stripesFrame);
				RAIDBasicPage.resetHighlights();
			}
		}
	}

	public void setInitialBreakpoints(List<BreakpointBase> initialBreakpoints) {
		breakpoints = new ArrayList<BreakpointBase>();
		breakpoints.addAll(initialBreakpoints);
		breakpointsDialog = new ManageBreakpointsDialog(SwingUtilities.windowForComponent(this), manager);
		breakpointsDialog.setBreakpoints(breakpoints);
	}

	public static SSDManager<?, ?, ?, ?, ?> getManager() {
		return manager;
	}

	public IZoomLevel getZoomLevel() {
		return zoomDialog.getZoomLevel();
	}

	public void abort() {
		abortSignal = true;
	}

	private void initManagerSelection() {
		Vector<Object> items = new Vector<>();
		for (String manager : SSDManager.getAllSimulationManagerNames()) {
			items.addElement(manager);
		}
		items.addElement(new JSeparator());
		for (String manager : SSDManager.getAllVisualizationManagerNames()) {
			items.addElement(manager);
		}
		managersList = new SeparatorComboBox(items);

		managersList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String managerName = (String) managersList.getItemAt(managersList.getSelectedIndex());
				if (!manager.getManagerName().equals(managerName)) {
					setManager(managerName);
				}
			}
		});
		managersList.setMaximumSize(new Dimension(170, 25));
		managersList.setPreferredSize(new Dimension(170, 25));
		managersList.setToolTipText("Choose Simulation manager");
		add(managersList);
		add(Box.createRigidArea(new Dimension(10, 0)));
		setManager((String) managersList.getItemAt(managersList.getSelectedIndex()));
	}

	private void setManager(String managerName) {
		manager = SSDManager.getManager(managerName);
		FileTraceParser<? extends Device<?>, ?> traseParser = manager.getFileTraseParser();
		traceChooser = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir"));
		traceChooser.setCurrentDirectory(workingDirectory);
		traceChooser.setAcceptAllFileFilterUsed(false);
		traceChooser.setFileFilter(new FileNameExtensionFilter(manager.getManagerName() + " Trace Files",
				traseParser.getFileExtensions()));
		setWorkloadGenerators(manager);
		setStripeInformation(manager);
		setZoomLevelOptions(manager);
		setInfoDialog(manager);
		setSamplingRateDialog(manager);
		resetDevice.message(traseParser.getCurrentDevice(), manager.getStatisticsGetters());
		ActionLog.resetLog();
	}

	private void setSamplingRateDialog(SSDManager<?, ?, ?, ?, ?> manager2) {
		this.samplingRateDialog = new SamplingRateDialog(SwingUtilities.windowForComponent(this), manager, this.visualConfig);
	}

	private void setInfoDialog(SSDManager<?, ?, ?, ?, ?> manager2) {
		this.infoDialog = new InfoDialog(SwingUtilities.windowForComponent(this), manager, this.visualConfig);
	}

	private void setZoomLevelOptions(SSDManager<?, ?, ?, ?, ?> manager) {
		zoomDialog = new ZoomLevelDialog(SwingUtilities.windowForComponent(this), manager, visualConfig,
				resetDeviceView);
		zoomDialog.resetZoomLevel();
	}

	private void setWorkloadGenerators(SSDManager<?, ?, ?, ?, ?> manager2) {
		try {
			creatorsFrame = new LoadGeneratorsCreatorsFrame(SwingUtilities.windowForComponent(this), manager);
			generateButton.setVisible(true);
		} catch (Exception e) {
			creatorsFrame = null;
			generateButton.setVisible(false);
		}
	}

	private void setStripeInformation(SSDManager<?, ?, ?, ?, ?> manager) {
		if (stripesFrame != null) {
			StripesInfoFrame.reset(stripesFrame);
			RAIDBasicPage.resetHighlights();
		}
		if (!manager.hasStripes()) {
			stripesFrame = null;
			showStripeButton.setVisible(false);
		} else {
			showStripeButton.setVisible(true);
			showStripeButton.setEnabled(false);
		}
	}

	private void initButtons() {
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openTrace();
			}
		});
		addButton(openButton, "Open New Trace");
		openButton.setEnabled(true);

		generateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showGeneratorCreatorsTrace();
			}
		});
		addButton(generateButton, "Generate New Workload");
		generateButton.setEnabled(true);

		breakpointsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showBreakpointsDialog();
			}
		});
		addButton(breakpointsButton, ManageBreakpointsDialog.DIALOG_HEADER);
		breakpointsButton.setEnabled(true);
		breakpointsButton.setBorder(BorderFactory.createLineBorder(Consts.getInstance().colors.ACTIVE));

		playPauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				playPauseTrace();
			}
		});
		addButton(playPauseButton, "Play");

		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ((schedular != null) && isPaused) {
					parseNextCommand();
				}
			}
		});
		addButton(nextButton, "Next Frame");

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stopTrace();
			}
		});
		addButton(stopButton, "Stop Trace");

		zoomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showZoomDialog();
			}
		});
		addButton(zoomButton, ZoomLevelDialog.DIALOG_HEADER);
		zoomButton.setEnabled(true);

		infoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TracePlayer.this.showInfoDialog();
			}
		});
		addButton(infoButton, "Info");
		infoButton.setEnabled(true);


		samplingRateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showSamplingRateDialog();
			}
		});
		addButton(samplingRateButton, "Sample View");
		samplingRateButton.setEnabled(true);


		add(Box.createRigidArea(new Dimension(5, 0)));

		showStripeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showStripesInfo();
			}
		});
		addButton(showStripeButton, "Show Stripes Information");
	}

	private void openTrace() {
		int returnVal = traceChooser.showOpenDialog(SwingUtilities.windowForComponent(this));
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		String fileName = traceChooser.getSelectedFile().getPath();
		if (fileName != null) {
			FileTraceParser<?, ?> fileTraceParser = manager.getFileTraseParser();
			try {
				resetProgressBar(fileName);
				fileTraceParser.open(fileName);
				parser = fileTraceParser;
				reStartTimer();
				if (stripesFrame != null) {
					StripesInfoFrame.reset(stripesFrame);
					RAIDBasicPage.resetHighlights();
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error occured during file open. Check the choosen trace.");
				e.printStackTrace();
			}
		}
	}

	private void initProgressBar() {
		JLabel startLabel = new JLabel("0");
		startLabel.setFont(Consts.getInstance().fonts.CAPTION_BOLD);
		add(startLabel);
		add(Box.createRigidArea(new Dimension(5, 0)));

		progressBar = new JProgressBar(0, 0);
		add(progressBar);
		progressBar.setFont(Consts.getInstance().fonts.CAPTION_BOLD);
		progressBar.setBackground(Consts.getInstance().colors.INNER_BG);
		progressBar.setForeground(Consts.getInstance().colors.CONTROL_TEXT);
		progressBar.setUI(new BasicProgressBarUI() {
			protected Color getSelectionBackground() {
				return Consts.getInstance().colors.CONTROL_TEXT;
			}

			protected Color getSelectionForeground() {
				return Consts.getInstance().colors.INNER_BG;
			}
		});
		add(Box.createRigidArea(new Dimension(5, 0)));

		stopLabel = new JLabel("0");
		stopLabel.setMinimumSize(new Dimension(50, 25));
		stopLabel.setPreferredSize(new Dimension(50, 25));
		stopLabel.setFont(Consts.getInstance().fonts.CAPTION_BOLD);
		add(stopLabel);
		progressBar.setBorderPainted(true);
		progressBar.setStringPainted(true);
		progressBar.setMaximumSize(new Dimension(2000, 25));
		progressBar.setBorder(BorderFactory.createLineBorder(Consts.getInstance().colors.BORDER));
		setProgressBarFrame(0);
	}

	private void addButton(JButton button, String title) {
		button.setMaximumSize(new Dimension(25, 25));
		button.setPreferredSize(new Dimension(25, 25));
		addButtonNoSize(button, title);
	}

	private void addButtonNoSize(JButton button, String title) {
		button.setToolTipText(title);
		button.setEnabled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		add(button);
		add(Box.createRigidArea(new Dimension(5, 0)));
	}

	private void setProgressBarFrame(int frame) {
		progressBar.setValue(frame);
		progressBar.setString(fileName + frame + "/" + numberOfLines);
	}

	private void initTraceParsing(VisualConfig visualConfig) {
		readingSpeed = 1000000000 / visualConfig.getSpeed();
	}

	private void reStartTimer() {
		resetDevice.message(parser.getCurrentDevice(), manager.getStatisticsGetters());
		schedular = Executors.newScheduledThreadPool(1);
		schedular.scheduleWithFixedDelay(new Runnable() {
			public void cancel() {
				stopTrace();
			}
			 @Override
			 public void run() {
				 if (isPaused)
					 return;
				 if (!parseNextCommand()) {
					 cancel();
				 }
			 }
		}, 0, readingSpeed, TimeUnit.NANOSECONDS);

		playPauseButton.setEnabled(true);
		stopButton.setEnabled(true);
		nextButton.setEnabled(true);
		showStripeButton.setEnabled(true);
		openButton.setEnabled(false);
		generateButton.setEnabled(false);
		managersList.setEnabled(false);
	}

	private void resetProgressBar(String fileName) throws IOException {
		LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
		lnr.skip(Long.MAX_VALUE);
		int numOfLines = lnr.getLineNumber() + 1;
		lnr.close();
		String[] filePath = fileName.split("[\\\\/]");
		fileName = filePath[filePath.length - 1];
		resetProgressBar(fileName.split("\\.")[0], numOfLines);
	}

	private void resetProgressBar(String fileName, int numOfLines) {
		numberOfLines = numOfLines;
		progressBar.setMaximum(numberOfLines);
		stopLabel.setText("" + numberOfLines);
		this.fileName = fileName + " : ";
		setProgressBarFrame(0);
	}

	private boolean parseNextCommand() {
		if (currFrameCounter >= numberOfLines) {
			updateDeviceView.message(currentDevice);
			MessageLog.log(new InfoMessage("Simulation Ended"));
			return false;
		}
		if (abortSignal) {
			abortSignal = false;
			return false;
		}
		try {
			Device<?> updatedDevice = parser.parseNextCommand();
			if (updatedDevice != null) {
				//The first condition is to check whether this frame should be displayed according to the sampling rate.
				//The second one is to make sure each GC execution is displayed - currently disabled to make heavy GUI traces faster
				//The third is to make sure that when people press the "next" button, the next frame will be displayed.
				if(currFrameCounter % visualConfig.getViewSample() == 0
						//|| (currentDevice != null && (currentDevice.getGCExecutions() < updatedDevice.getGCExecutions()))
						|| isPaused) {
					updateDeviceView.message(updatedDevice);
				}
				// FrameCounter runs from 0 to numberOfLines-1, displayed from 1 to numberOfLines
				setProgressBarFrame(currFrameCounter + 1);
				++currFrameCounter;

				Device<?> previousDevice = currentDevice;
				currentDevice = updatedDevice;
				updateInfo(currentDevice, currFrameCounter);
				checkBreakpoints(previousDevice, currentDevice);
			} else {
				MessageLog.log(new ErrorMessage("Trace has ended before stop frame was reached"));
				return false;
			}
		} catch (Throwable e) {
			MessageLog.log(new ErrorMessage("Failed to parse next command. " + e.getMessage()));
			e.printStackTrace();
			return false;
		}
		ActionLog.nextCommand();
		return true;
	}

	private void updateInfo(Device<?> currentDevice, int currFrameCounter) {
		this.infoDialog.setDevice(currentDevice, currFrameCounter - 1);
	}

	private void checkBreakpoints(Device<?> previousDevice, Device<?> currentDevice) {
		boolean anyHits = false;

		for (IBreakpoint breakpoint : breakpoints) {
			if (breakpoint.isActive() && (breakpoint.breakpointHit(previousDevice, currentDevice))) {
				breakpoint.setIsHit(true);
				MessageLog.log(new BreakpointMessage(breakpoint.getHitDescription()));
				anyHits = true;
			} else {
				breakpoint.setIsHit(false);
			}
		}

		if (anyHits) {
			pauseTrace();
			breakpointsButton.setBorderPainted(true);
		} else {
			breakpointsButton.setBorderPainted(false);
		}
	}

	private void playPauseTrace() {
		if (isPaused) {
			isPaused = false;
			playPauseButton.setIcon(iconPause);
			playPauseButton.setToolTipText("Pause");
			showStripeButton.setEnabled(false);
		} else {
			pauseTrace();
		}
	}

	public void pauseTrace() {
		if(!isPaused) {
			updateDeviceView.message(currentDevice);
			isPaused = true;
			playPauseButton.setIcon(iconPlay);
			playPauseButton.setToolTipText("Play");
			showStripeButton.setEnabled(true);
		}
	}

	private void showGeneratorCreatorsTrace() {
		if (creatorsFrame != null) {
			creatorsFrame = new LoadGeneratorsCreatorsFrame(SwingUtilities.windowForComponent(this), manager);
			creatorsFrame.setVisible(true);
			WorkloadGenerator<?, ?> generator = creatorsFrame.getWorkloadGenerator();
			if (generator != null) {
				parser = generator;
				resetProgressBar(generator.getName(), generator.getTraceLength());
				reStartTimer();
			}
		}
	}

	private void showStripesInfo() {
		stripesFrame = new StripesInfoFrame(SwingUtilities.windowForComponent(this),
				(RAIDBasicSSDManager<?, ?, ?, ?, ?>) manager, parser, updateDeviceView);
		stripesFrame.setVisible(true);
	}

	private void showBreakpointsDialog() {
		pauseTrace();
		breakpointsDialog.setManager(manager);
		breakpointsDialog.updateHitBreakpoints();
		breakpointsDialog.setVisible(true);

		breakpoints.clear();
		breakpoints.addAll(breakpointsDialog.getBreakpoints());
	}

	private void showZoomDialog() {
		pauseTrace();
		zoomDialog.setVisible(true);
	}

	private void showInfoDialog() {
		pauseTrace();
		this.infoDialog.setVisible(true);
	}

	private void showSamplingRateDialog(){
		pauseTrace();
		this.samplingRateDialog.resetSmaplingRate();
		this.samplingRateDialog.setVisible(true);
	}
}
