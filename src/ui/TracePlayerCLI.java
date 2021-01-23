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
import manager.*;
import ui.breakpoints.InfoCLI;
import utils.Utils;


import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Constructor;
import java.util.List;


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

//		return new UniformResizableWorkloadGenerator<D,S>(manager, getWorkloadLength(), getSeed(), getMaxWriteSize(), isWriteSizeUniform());
    public TracePlayerCLI(VisualConfig visualConfig,
                       TwoObjectsCallback<Device<?>, Iterable<StatisticsGetter>> resetDevice,
                       OneObjectCallback<Device<?>> updateDeviceView, OneObjectCallback<Boolean> resetDeviceView,
                       String managerName, String outputFileName,
                          boolean useWorkloadGenerator,
                          String traceFileName,
                          boolean isWorkloadUniform, Integer workloadLength, Integer seed, Double exp, boolean isResizable, Integer maxWriteSize, boolean isWriteSizeUniform) {
        Utils.validateNotNull(updateDeviceView, "Update device callback");
        Utils.validateNotNull(resetDevice, "Reset device callback");
        this.resetDevice = resetDevice;
        this.updateDeviceView = updateDeviceView;
        this.resetDeviceView = resetDeviceView;
        this.visualConfig = visualConfig;
        ActionLog.resetLog();
        setManagerAndTrace(managerName, traceFileName, outputFileName, useWorkloadGenerator, isWorkloadUniform, workloadLength, seed, exp, isResizable, maxWriteSize, isWorkloadUniform);
    }

    private void setParser(boolean useWorkloadGenerator,
                           String traceFileName,
                           boolean isWorkloadUniform, Integer workloadLength, Integer seed, Double exp, boolean isResizable, Integer maxWriteSize, boolean isWriteSizeUniform) throws IOException {
        if(!useWorkloadGenerator){
            FileTraceParser<?, ?> fileTraceParser = manager.getFileTraseParser();
            resetProgressBar(traceFileName);
            fileTraceParser.open(traceFileName);
            parser = fileTraceParser;
        } else { //useWorkloadGenerator
            List<? extends WorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>>> workloadWidgets = manager.getWorkLoadGeneratorWidgets();
            if(isResizable){
                if(!workloadWidgets.stream().allMatch(widget -> (widget instanceof UniformResizableWorkloadWidget))){
                    throw new IOException(manager.getManagerName() + " does not allow resizable workload generators");
                }

                if(isWorkloadUniform){ //UniformResizableWorkloadGenerator
                    UniformResizableWorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> uniformWidget;
                    UniformResizableWorkloadGenerator<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> uniformGenerator;
                    for (WorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> widget : workloadWidgets) {
                        if(widget instanceof UniformResizableWorkloadWidget){
                            uniformWidget = (UniformResizableWorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>>) widget;
                            uniformGenerator = (UniformResizableWorkloadGenerator<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>>) uniformWidget.createWorkloadGenerator(workloadLength, seed, maxWriteSize, isWriteSizeUniform);
                            parser = uniformGenerator;
                            break;
                        }
                    }
                } else { //ZipfResizableWorkloadGenerator
                    ZipfResizableWorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> zipfWidget;
                    ZipfResizableWorkloadGenerator<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> zipfGenerator;
                    for (WorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> widget : workloadWidgets) {
                        if(widget instanceof ZipfResizableWorkloadWidget){
                            zipfWidget = (ZipfResizableWorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>>) widget;
                            zipfGenerator = (ZipfResizableWorkloadGenerator<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>>) zipfWidget.createWorkloadGenerator(workloadLength, seed, exp, maxWriteSize, isWriteSizeUniform);
                            parser = zipfGenerator;
                            break;
                        }
                    }
                }
            } else {
                if(!workloadWidgets.stream().allMatch(widget -> (widget instanceof UniformWorkloadWidget))){
                    throw new IOException(manager.getManagerName() + " only allows resizable workload generators");
                }

                if(isWorkloadUniform){ //UniformWorkloadGenerator
                    UniformWorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> uniformWidget;
                    UniformWorkloadGenerator<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> uniformGenerator;
                    for (WorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> widget : workloadWidgets) {
                        if(widget instanceof UniformWorkloadWidget){
                            uniformWidget = (UniformWorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>>) widget;
                            uniformGenerator = (UniformWorkloadGenerator<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>>) uniformWidget.createWorkloadGenerator(workloadLength, seed);
                            parser = uniformGenerator;
                            break;
                        }
                    }
                } else { //ZipfWorkloadGenerator
                    ZipfWorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> zipfWidget;
                    ZipfWorkloadGenerator<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> zipfGenerator;
                    for (WorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>> widget : workloadWidgets) {
                        if(widget instanceof ZipfWorkloadWidget){
                            zipfWidget = (ZipfWorkloadWidget<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>>) widget;
                            zipfGenerator = (ZipfWorkloadGenerator<? extends Device<?>, ? extends SSDManager<?, ?, ?, ?, ? extends Device<?>>>) zipfWidget.createWorkloadGenerator(workloadLength, seed, exp);
                            parser = zipfGenerator;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void setManagerAndTrace(String managerName, String traceFileName, String outputFileName, boolean useWorkloadGenerator, boolean isWorkloadUniform, Integer workloadLength, Integer seed, Double exp, boolean isResizable, Integer maxWriteSize, boolean i) {
        manager = SSDManager.getManager(managerName);

        infoCLI = new InfoCLI(manager);
        FileTraceParser<? extends Device<?>, ?> traceParser = manager.getFileTraseParser();
        traceParser.getFileExtensions();
        resetDevice.message(traceParser.getCurrentDevice(), manager.getStatisticsGetters());
        ActionLog.resetLog();


        try {
            setParser(useWorkloadGenerator, traceFileName, isWorkloadUniform, workloadLength, seed, exp, isResizable, maxWriteSize, isWorkloadUniform);
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
