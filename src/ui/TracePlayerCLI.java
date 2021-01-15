package ui;

import entities.ActionLog;
import entities.Device;
import entities.RAID.RAIDBasicPage;
import entities.StatisticsGetter;
import general.Consts;
import general.MessageLog;
import general.OneObjectCallback;
import general.TwoObjectsCallback;
import log.Message.ErrorMessage;
import log.Message.InfoMessage;
import manager.FileTraceParser;
import manager.SSDManager;
import manager.TraceParser;
import manager.VisualConfig;
import ui.breakpoints.InfoCLI;
import utils.Utils;


import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;


public class TracePlayerCLI {
    private OneObjectCallback<Device<?>> updateDeviceView;
    private TwoObjectsCallback<Device<?>, Iterable<StatisticsGetter>> resetDevice;
    private OneObjectCallback<Boolean> resetDeviceView;
    private VisualConfig visualConfig;
    private static SSDManager<?, ?, ?, ?, ? extends Device<?>> manager;
    private TraceParser<? extends Device<?>, ?> parser;
    private int currFrameCounter = 0;
    private int numberOfLines = 0;
    private Device<?> currentDevice;
    private InfoCLI infoCLI;


    public TracePlayerCLI(VisualConfig visualConfig,
                       TwoObjectsCallback<Device<?>, Iterable<StatisticsGetter>> resetDevice,
                       OneObjectCallback<Device<?>> updateDeviceView, OneObjectCallback<Boolean> resetDeviceView,
                       String managerName, String traceFileName, String outputFileName) {
        Utils.validateNotNull(updateDeviceView, "Update device callback");
        Utils.validateNotNull(resetDevice, "Reset device callback");
        this.resetDevice = resetDevice;
        this.updateDeviceView = updateDeviceView;
        this.resetDeviceView = resetDeviceView;
        this.visualConfig = visualConfig;
        ActionLog.resetLog();
        setManagerAndTrace(managerName, traceFileName, outputFileName);
    }

    private void setManagerAndTrace(String managerName, String traceFileName, String outputFileName) {
        manager = SSDManager.getManager(managerName);
        infoCLI = new InfoCLI(manager);
        FileTraceParser<? extends Device<?>, ?> traceParser = manager.getFileTraseParser();
        traceParser.getFileExtensions();
        resetDevice.message(traceParser.getCurrentDevice(), manager.getStatisticsGetters());
        ActionLog.resetLog();

        FileTraceParser<?, ?> fileTraceParser = manager.getFileTraseParser();
        try {
            resetProgressBar(traceFileName);
            fileTraceParser.open(traceFileName);
            parser = fileTraceParser;
            reStartTimer(outputFileName);
        } catch (IOException e) {
            System.out.println("could not open trace file");
            e.printStackTrace();
        }
    }

    private void resetProgressBar(String fileName) throws IOException {
        LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
        lnr.skip(Long.MAX_VALUE);
        numberOfLines = lnr.getLineNumber() + 1;
        lnr.close();
    }


    private void reStartTimer(String outputFileName) {
        resetDevice.message(parser.getCurrentDevice(), manager.getStatisticsGetters());
        while(parseNextCommand());
        stopTrace();
        infoCLI.saveInfoToXml(outputFileName);
    }

    public void stopTrace() {
        updateDeviceView.message(currentDevice);
        parser.close();
        currFrameCounter = 0;

    }

    private boolean parseNextCommand() {
        if (currFrameCounter >= numberOfLines) {
            updateDeviceView.message(currentDevice);
            return false;
        }

        try {
            Device<?> updatedDevice = parser.parseNextCommand();
            if (updatedDevice != null) {
                updateDeviceView.message(updatedDevice);

                // FrameCounter runs from 0 to numberOfLines-1, displayed from 1 to numberOfLines
                ++currFrameCounter;

                Device<?> previousDevice = currentDevice;
                currentDevice = updatedDevice;
                updateInfo(currentDevice, currFrameCounter);
            } else {
                System.out.println("Trace has ended before stop frame was reached");
                return false;
            }
        } catch (Throwable e) {
            System.out.println("Failed to parse next command. " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        ActionLog.nextCommand();
        return true;
    }


    private void updateInfo(Device<?> currentDevice, int currFrameCounter) {
        this.infoCLI.setDevice(currentDevice, currFrameCounter - 1);
    }


}
