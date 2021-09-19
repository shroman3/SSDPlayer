package CLI;

import entities.Device;
import entities.StatisticsGetter;
import general.MessageLog;
import general.OneObjectCallback;
import general.TwoObjectsCallback;
import manager.VisualConfig;
import ui.DeviceView;
import ui.StatisticsView;

import java.awt.*;

public class MainCLI {
    private DeviceView deviceView;
    private StatisticsView statisticsView;
    private VisualConfig visualConfig;



    public MainCLI(VisualConfig visualConfig, String managerName, String outputFile, String inputTrace, boolean useBuiltInGenerator, boolean isGeneratorUniform, int workloadLength, int seed, double exponent, boolean isResizable, int maxWriteSize, boolean isWriteSizeUniform){
        this.visualConfig = visualConfig;
        MessageLog.initialize(null);
        new TracePlayerCLI(visualConfig,
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
                outputFile,
                useBuiltInGenerator,
                inputTrace, isGeneratorUniform, workloadLength, seed, exponent, isResizable, maxWriteSize, isWriteSizeUniform
        );
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


    private void updateDeviceCLI(final Device<?> device) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                deviceView.setDevice(device);
                statisticsView.updateStatistics(device);
            }
        });
    }
}
