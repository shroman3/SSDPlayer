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

import breakpoints.IBreakpoint;
import entities.Device;
import entities.StatisticsGetter;
import general.Consts;
import general.OneObjectCallback;
import general.TwoObjectsCallback;
import manager.SSDManager;
import manager.TraceParser;
import manager.TraceParserGeneral;
import manager.VisualConfig;
import manager.WorkloadGenerator;
import utils.Utils;

public class TracePlayer extends JPanel {
	private static final long serialVersionUID = 1L;

	private ImageIcon iconPlay = new ImageIcon(getClass().getResource("/ui/images/play.png"));
	private ImageIcon iconPause = new ImageIcon(getClass().getResource("/ui/images/pause.png"));
	
	private JButton playPauseButton = new JButton(iconPlay);
	private JButton stopButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/stop.png")));
	private JButton nextButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/next.png")));
	private JButton openButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/eject.png")));
	private JButton generateButton = new JButton(new ImageIcon(getClass().getResource("/ui/images/generate.png")));
	private JButton breakpointsButton = new JButton("Breakpoints");
	
	private JProgressBar progressBar;
	private TraceParser<?,?> parser;
    
	private JFileChooser traceChooser;
	private JLabel stopLabel;
	
	private int numberOfLines = 0;
	private String fileName = "";
	
	private boolean isPaused = true;
	 
	private Timer traceReadTimer;
	private int currFrameCounter = 0;

	private int readingSpeed;

	private SeparatorComboBox managersList;

	private SSDManager<?,?,?,?,?> manager;

	private LoadGeneratorsCreatorsFrame creatorsFrame;
	
	private OneObjectCallback<Device<?, ?, ?, ?>> updateDevice;

	private TwoObjectsCallback<Device<?, ?, ?, ?>, Iterable<StatisticsGetter>> resetDevice;

	private Device<?,?,?,?> currentDevice;
	
	private List<IBreakpoint> breakpoints;
	private ManageBreakpointsDialog breakpointsDialog;
	
    public TracePlayer(VisualConfig visualConfig, TwoObjectsCallback<Device<?, ?, ?, ?>, Iterable<StatisticsGetter>> resetDevice, OneObjectCallback<Device<?,?,?,?>> updateDevice) {
    	Utils.validateNotNull(updateDevice, "Update device callback");
    	Utils.validateNotNull(resetDevice, "Reset device callback");
		this.resetDevice = resetDevice;
		this.updateDevice = updateDevice;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(new RoundedBorder(Consts.Colors.BORDER));
		initTraceParsing(visualConfig);
		initManagerSelection();
		initButtons();
		initProgressBar();
    }

	public void stopTrace() {
		isPaused = true;
		if (traceReadTimer != null) {
			traceReadTimer.cancel();
			traceReadTimer = null;
			parser.close();
			playPauseButton.setEnabled(false);
			playPauseButton.setIcon(iconPlay);
			stopButton.setEnabled(false);
			nextButton.setEnabled(false);
			openButton.setEnabled(true);
			generateButton.setEnabled(true);
			managersList.setEnabled(true);
			currFrameCounter = 0;
			fileName = "";
//			numberOfLines = 0;
//			stopLabel.setText("0");
//			setProgressBarFrame(0);
		}
	}
	
	public void setInitialBreakpoints(List<IBreakpoint> initialBreakpoints) {
		breakpoints = new ArrayList<IBreakpoint>();
		breakpoints.addAll(initialBreakpoints);
		setBreakpointsManager(manager);
		breakpointsDialog = new ManageBreakpointsDialog(SwingUtilities.windowForComponent(this));
		breakpointsDialog.addBreakpoints(breakpoints);
	}
	
	private void initManagerSelection() {
		Vector<Object> items = new Vector<>();
		for (String manager : SSDManager.getAllSimulationManagerNames()) {			
			items.addElement(manager);
		}
		items.addElement( new JSeparator() );
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
		add(Box.createRigidArea(new Dimension(10,0)));
		setManager((String) managersList.getItemAt(managersList.getSelectedIndex()));
	}

	private void setManager(String managerName) {
		manager = SSDManager.getManager(managerName);
		TraceParserGeneral<?,?> traseParser = manager.getTraseParser();
		traceChooser = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir"));
		traceChooser.setCurrentDirectory(workingDirectory);
		traceChooser.setAcceptAllFileFilterUsed(false);
		traceChooser.setFileFilter(new FileNameExtensionFilter(manager.getManagerName() + " Trace Files", 
				traseParser.getFileExtensions()));
		setWorkloadGenerators(manager);
		setBreakpointsManager(manager);
		resetDevice.message(traseParser.getCurrentDevice(), manager.getStatisticsGetters());
	}
	
	private void setBreakpointsManager(SSDManager<?, ?, ?, ?, ?> manager2) {
		if(breakpoints == null){
			return;
		}
		for(IBreakpoint breakpoint : breakpoints){
			breakpoint.setManager(manager2);
		}
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
				if ((traceReadTimer != null) && isPaused) {
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
		addButton(stopButton , "Stop Trace");
		
		breakpointsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showBreakpointsDialog();
			}
		});
		addButton(breakpointsButton, ManageBreakpointsDialog.DIALOG_HEADER);
		breakpointsButton.setEnabled(true);
		
		add(Box.createRigidArea(new Dimension(5,0)));
	}

	private void openTrace() {
        int returnVal = traceChooser.showOpenDialog(SwingUtilities.windowForComponent(this));
        if (returnVal != JFileChooser.APPROVE_OPTION) {
        	return;
        }
		parser = manager.getTraseParser();
        String fileName = traceChooser.getSelectedFile().getPath();
        if (fileName != null) {
			try {
				resetProgressBar(fileName);
				parser.open(fileName);
				reStartTimer();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error occured during file open. Check the choosen trace.");
				e.printStackTrace();
			}
        }
	}
	
	private void initProgressBar() {
		JLabel startLabel = new JLabel("0");
		startLabel.setFont(Consts.UI.BOLD);
		add(startLabel);
		add(Box.createRigidArea(new Dimension(5,0)));
		
		progressBar = new JProgressBar(0, 0);
		add(progressBar);
		progressBar.setFont(Consts.UI.BOLD);
		progressBar.setBackground(Consts.Colors.BG);
		progressBar.setForeground(Consts.Colors.TEXT);
		progressBar.setUI(new BasicProgressBarUI() {
		      protected Color getSelectionBackground() { return Consts.Colors.TEXT; }
		      protected Color getSelectionForeground() { return Consts.Colors.BG; }
		    });
		add(Box.createRigidArea(new Dimension(5,0)));		
		
		stopLabel = new JLabel("0");
		stopLabel.setMinimumSize(new Dimension(50, 25));
		stopLabel.setPreferredSize(new Dimension(50, 25));
		stopLabel.setFont(Consts.UI.BOLD);
		add(stopLabel);
		progressBar.setBorderPainted(true);
		progressBar.setStringPainted(true);
		progressBar.setMaximumSize(new Dimension(2000, 25));
		progressBar.setBorder(BorderFactory.createLineBorder(Consts.Colors.BORDER));
		setProgressBarFrame(0);
	}
	
	private void addButton(JButton button, String title) {
		button.setMaximumSize(new Dimension(25,25));
		button.setPreferredSize(new Dimension(25,25));
		addButtonNoSize(button, title);
	}

	private void addButtonNoSize(JButton button, String title) {
		button.setToolTipText(title);
		button.setEnabled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		add(button);
		add(Box.createRigidArea(new Dimension(5,0)));
	}

	private void setProgressBarFrame(int frame) {
		progressBar.setValue(frame);
		progressBar.setString(fileName + frame + "/"+ numberOfLines);
	}
	
	private void initTraceParsing(VisualConfig visualConfig) {
		readingSpeed = 60000/visualConfig.getSpeed();
	}

	private void reStartTimer() {
		resetDevice.message(parser.getCurrentDevice(), manager.getStatisticsGetters());
		traceReadTimer = new Timer();
		traceReadTimer.schedule(new TimerTask() {
			@Override
			public boolean cancel() {
				stopTrace();
				return super.cancel();
			}

			@Override
		    public void run() {
				if(isPaused) return;
		    	if (!parseNextCommand()) {
		    		cancel();
		    	}
		    }
		}, 0, readingSpeed);
		
		playPauseButton.setEnabled(true);
		stopButton.setEnabled(true);
		nextButton.setEnabled(true);
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
		fileName = filePath[filePath.length-1];
		resetProgressBar(fileName.split("\\.")[0], numOfLines);
	}

	private void resetProgressBar(String fileName, int numOfLines) {
		numberOfLines = numOfLines;
		progressBar.setMaximum(numberOfLines);
		stopLabel.setText(""+numberOfLines);
		this.fileName = fileName + " : ";
		setProgressBarFrame(0);
	}

	private boolean parseNextCommand() {
		if(currFrameCounter >= numberOfLines) {
    		return false;
    	}
    		
    	try {
			Device<?,?,?,?> updatedDevice = parser.parseNextCommand();
			if (updatedDevice != null) {
				updateDevice.message(updatedDevice);
				setProgressBarFrame(currFrameCounter);
				++currFrameCounter;
				
				Device<?,?,?,?> previousDevice = currentDevice;
				currentDevice = updatedDevice;
				checkBreakpoints(previousDevice, currentDevice);
			} else {
				System.out.println("Trace has ended before stop frame was reached");
				return false;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
    	return true;
	}

	private void checkBreakpoints(Device<?, ?, ?, ?> previousDevice, Device<?, ?, ?, ?> currentDevice) {
		for (IBreakpoint breakpoint : breakpoints) {
			if (breakpoint.breakpointHit(previousDevice, currentDevice)) {
				pauseTrace();
				break;
			}
		}
		
	}
	
	private void playPauseTrace() {
		if (isPaused) {
			isPaused = false;
			playPauseButton.setIcon(iconPause);
			playPauseButton.setToolTipText("Pause");
		} else {
			pauseTrace();
		}
	}

	private void pauseTrace() {
		isPaused = true;
		playPauseButton.setIcon(iconPlay);
		playPauseButton.setToolTipText("Play");
	}
	
	private void showGeneratorCreatorsTrace() {
		if (creatorsFrame != null) {
			creatorsFrame = new LoadGeneratorsCreatorsFrame(SwingUtilities.windowForComponent(this), manager);
			creatorsFrame.setVisible(true);
			WorkloadGenerator<?,?> generator = creatorsFrame.getWorkloadGenerator();
			if (generator != null) {
				parser = generator;
				resetProgressBar(generator.getName(), generator.getTraceLength());
				reStartTimer();
			}
		}
    }
	
	private void showBreakpointsDialog() {
		breakpointsDialog.setVisible(true);
	}
}
