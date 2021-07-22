package ui.breakpoints;

import com.ibm.icu.util.Calendar;
import entities.*;
import general.MessageLog;
import log.Message.ErrorMessage;
import manager.FileTraceParser;
import manager.SSDManager;
import manager.VisualConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Map;

public class InfoCLI {
    private SSDManager<?, ?, ?, ?, ?> mManager;
    private Device<?> mCurrentDevice = null;
    private int mComandNumber;



    public InfoCLI(SSDManager<?, ?, ?, ?, ?> manager) {
        this.mManager = manager;
        initComponents();

    }


    public void saveInfoToXml(String path) {
        File file = new File(path + ".xml");
        generateInfoXml(file);
    }

    private void initComponents() {
        createDeviceTree();
    }

    public void setDevice(Device<?> currentDevice, int currFrameCounter) {
        this.mCurrentDevice = currentDevice;
        setEntityInfo(null);
        this.mComandNumber = currFrameCounter;
    }

    private void setEntityInfo(TreePath selectedNodePath) {
        if ((selectedNodePath == null) || (selectedNodePath.getPathCount() == 0)) {
            return;
        }

        if (this.mCurrentDevice == null) {
            return;
        }

        EntityInfo entityInfo = null;
        if (selectedNodePath.getPathCount() == 1) {
            entityInfo = this.mCurrentDevice.getInfo();
            entityInfo.add("Request number", Integer.toString(this.mComandNumber), 0);
            addStatisticsToDeviceInfo(this.mManager.getStatisticsGetters(), entityInfo, this.mCurrentDevice);
        } else {
            Chip<?> chip = this.mCurrentDevice.getChip(
                    ((TreeEntity) ((DefaultMutableTreeNode) selectedNodePath.getPathComponent(1)).getUserObject())
                            .getIndex());
            if (selectedNodePath.getPathCount() == 2) {
                entityInfo = chip.getInfo();
            } else {
                Plane<?> plane = chip.getPlane(
                        ((TreeEntity) ((DefaultMutableTreeNode) selectedNodePath.getPathComponent(2)).getUserObject())
                                .getIndex());
                if (selectedNodePath.getPathCount() == 3) {
                    entityInfo = plane.getInfo();
                } else {
                    Block<?> block = plane
                            .getBlock(((TreeEntity) ((DefaultMutableTreeNode) selectedNodePath.getPathComponent(3))
                                    .getUserObject()).getIndex());
                    if (selectedNodePath.getPathCount() == 4) {
                        entityInfo = block.getInfo();
                    } else {
                        Page page = block
                                .getPage(((TreeEntity) ((DefaultMutableTreeNode) selectedNodePath.getPathComponent(4))
                                        .getUserObject()).getIndex());
                        entityInfo = page.getInfo();
                    }
                }
            }
        }

    }

    private void addStatisticsToDeviceInfo(Iterable<StatisticsGetter> statisticsGetters, EntityInfo entityInfo,
                                           Device<?> device) {
        for (StatisticsGetter getter : statisticsGetters) {
            Map.Entry<String, String> infoEntry = getter.getInfoEntry(device);
            if (infoEntry != null) {
                entityInfo.add((String) infoEntry.getKey(), (String) infoEntry.getValue(), 5);
            }
        }
    }

    private void createDeviceTree() {
        DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode("Device");
        createDeviceNodes(deviceNode);
    }

    private void createDeviceNodes(DefaultMutableTreeNode top) {
        for (int chipIndex = 0; chipIndex < this.mManager.getChipsNum(); chipIndex++) {
            DefaultMutableTreeNode chipNode = new DefaultMutableTreeNode(new TreeEntity("Chip", chipIndex));
            createChipNodes(chipNode);
            top.add(chipNode);
        }
    }

    private void createChipNodes(DefaultMutableTreeNode chipNode) {
        for (int planeIndex = 0; planeIndex < this.mManager.getPlanesNum(); planeIndex++) {
            DefaultMutableTreeNode planeNode = new DefaultMutableTreeNode(new TreeEntity("Plane", planeIndex));
            createBlockNodes(planeNode);
            chipNode.add(planeNode);
        }
    }

    private void createBlockNodes(DefaultMutableTreeNode planeNode) {
        for (int blockIndex = 0; blockIndex < this.mManager.getBlocksNum(); blockIndex++) {
            DefaultMutableTreeNode blockNode = new DefaultMutableTreeNode(new TreeEntity("Block", blockIndex));
            createPageNodes(blockNode);
            planeNode.add(blockNode);
        }
    }

    private void createPageNodes(DefaultMutableTreeNode blockNode) {
        for (int pageIndex = 0; pageIndex < this.mManager.getPagesNum(); pageIndex++) {
            DefaultMutableTreeNode pageNode = new DefaultMutableTreeNode(new TreeEntity("Page", pageIndex));
            blockNode.add(pageNode);
        }
    }


    private void generateInfoXml(File file) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("device");
            EntityInfo deviceinfo = this.mCurrentDevice.getInfo();
            addStatisticsToDeviceInfo(this.mManager.getStatisticsGetters(), deviceinfo, this.mCurrentDevice);
            addComponentInfo(rootElement, deviceinfo);

            doc.appendChild(rootElement);

            for (Chip<?> chip : this.mCurrentDevice.getChips()) {
                addChipToXml(rootElement, chip, doc);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
            MessageLog.log(new ErrorMessage("Failed to save file"));
        }
    }

    private void addChipToXml(Element rootElement, Chip<?> chip, Document doc) {
        Element chipElement = doc.createElement("chip");
        addComponentInfo(chipElement, chip.getInfo());

        rootElement.appendChild(chipElement);

        for (Plane<?> plane : chip.getPlanes()) {
            addPlaneToXml(chipElement, plane, doc);
        }
    }

    private void addPlaneToXml(Element chipElement, Plane<?> plane, Document doc) {
        Element planeElement = doc.createElement("plane");
        addComponentInfo(planeElement, plane.getInfo());

        chipElement.appendChild(planeElement);

        for (Block<?> block : plane.getBlocks()) {
            addBlockToXml(planeElement, block, doc);
        }
    }

    private void addBlockToXml(Element planeElement, Block<?> block, Document doc) {
        Element blockElement = doc.createElement("block");
        addComponentInfo(blockElement, block.getInfo());

        planeElement.appendChild(blockElement);

        for (Page page : block.getPages()) {
            addPageToXml(blockElement, page, doc);
        }
    }

    private void addPageToXml(Element blockElement, Page page, Document doc) {
        Element pageElement = doc.createElement("page");
        addComponentInfo(pageElement, page.getInfo());

        blockElement.appendChild(pageElement);
    }

    private void addComponentInfo(Element rootElement, EntityInfo info) {
        for (EntityInfoEntry entry : info.getInfoList())
            rootElement.setAttribute(entry.description.replace(" ", "_"), entry.value);
    }

    private class TreeEntity {
        private String mDesc;
        private int mIndex;

        public TreeEntity(String desc, int index) {
            this.mDesc = desc;
            this.mIndex = index;
        }

        public String toString() {
            return this.mDesc + " " + this.mIndex;
        }

        public int getIndex() {
            return this.mIndex;
        }
    }
}
