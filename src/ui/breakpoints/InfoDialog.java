package ui.breakpoints;

import com.ibm.icu.util.Calendar;
import entities.Block;
import entities.Chip;
import entities.Device;
import entities.EntityInfo;
import entities.EntityInfoEntry;
import entities.Page;
import entities.Plane;
import entities.StatisticsGetter;
import general.MessageLog;
import general.OneObjectCallback;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import log.Message.ErrorMessage;
import manager.SSDManager;
import manager.VisualConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class InfoDialog extends JDialog {
	private static final long serialVersionUID = 3732587581005190445L;
	private static final String DIALOG_HEADER = "Information";
	private SSDManager<?, ?, ?, ?, ?> mManager;
	private JPanel mMainPanel;
	private OneObjectCallback<Boolean> resetDevice;
	private Device<?, ?, ?, ?> mCurrentDevice = null;
	private JTree deviceInfoTree;
	private TreeSelectionListener SelectionListener = new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent e) {
			InfoDialog.this.setEntityInfo(e.getNewLeadSelectionPath());
		}
	};
	private JPanel mEntityInfoPanel;
	private final JLabel mNoSelectedLabel = new JLabel("Please selecet an entity");
	private final JLabel mNoDevice = new JLabel("Please start the simulation for info");
	private JScrollPane mTreeScrollPane;
	private JSplitPane splitPane;
	private int mComandNumber;

	public InfoDialog(Window parentWindow, SSDManager<?, ?, ?, ?, ?> manager, VisualConfig visualConfig,
			OneObjectCallback<Boolean> resetDevice) {
		super(parentWindow, "Information");

		this.resetDevice = resetDevice;
		this.mManager = manager;

		setDefaultLookAndFeelDecorated(true);
		setModal(true);
		setResizable(true);
		setDefaultCloseOperation(2);
		setSize(660, 350);
		setLocationRelativeTo(parentWindow);
		initComponents();
	}

	private void initComponents() {
		this.mMainPanel = new JPanel();
		this.mMainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.mMainPanel.setLayout(new BoxLayout(this.mMainPanel, 1));

		this.mEntityInfoPanel = new JPanel();
		this.mEntityInfoPanel.setLayout(new BoxLayout(this.mEntityInfoPanel, 1));
		this.mEntityInfoPanel.add(this.mNoDevice);

		createDeviceTree();

		this.splitPane = new JSplitPane(1);
		this.splitPane.setLeftComponent(this.mTreeScrollPane);
		this.splitPane.setRightComponent(this.mEntityInfoPanel);
		this.splitPane.setDividerLocation(300);

		this.mMainPanel.add(this.splitPane);
		add(this.mMainPanel);
		Box buttonsBox = Box.createHorizontalBox();
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InfoDialog.this.saveInfoToXml();
			}
		});
		buttonsBox.add(saveButton);
		this.mMainPanel.add(buttonsBox);
	}

	public void setDevice(Device<?, ?, ?, ?> currentDevice, int currFrameCounter) {
		this.mCurrentDevice = currentDevice;
		this.deviceInfoTree.clearSelection();
		setEntityInfo(null);
		this.mComandNumber = currFrameCounter;
	}

	private void setEntityInfo(TreePath selectedNodePath) {
		this.mEntityInfoPanel.removeAll();
		if ((selectedNodePath == null) || (selectedNodePath.getPathCount() == 0)) {
			this.mEntityInfoPanel.add(this.mNoSelectedLabel);
			return;
		}

		if (this.mCurrentDevice == null) {
			this.mEntityInfoPanel.add(this.mNoDevice);
			return;
		}

		EntityInfo entityInfo = null;
		if (selectedNodePath.getPathCount() == 1) {
			entityInfo = this.mCurrentDevice.getInfo();
			entityInfo.add("Request number", Integer.toString(this.mComandNumber), 0);
			addStatisticsToDeviceInfo(this.mManager.getStatisticsGetters(), entityInfo, this.mCurrentDevice);
		} else {
			Chip<?, ?, ?> chip = this.mCurrentDevice.getChip(
					((TreeEntity) ((DefaultMutableTreeNode) selectedNodePath.getPathComponent(1)).getUserObject())
							.getIndex());
			if (selectedNodePath.getPathCount() == 2) {
				entityInfo = chip.getInfo();
			} else {
				Plane<?, ?> plane = chip.getPlane(
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

		showEntityInfo(entityInfo);
		this.splitPane.updateUI();
	}

	private void addStatisticsToDeviceInfo(Iterable<StatisticsGetter> statisticsGetters, EntityInfo entityInfo,
			Device<?, ?, ?, ?> device) {
		for (StatisticsGetter getter : statisticsGetters) {
			Entry<String, String> infoEntry = getter.getInfoEntry(device);
			if (infoEntry != null) {
				entityInfo.add((String) infoEntry.getKey(), (String) infoEntry.getValue(), 5);
			}
		}
	}

	public static String convertToMultiline(String orig) {
		return "<html>" + orig.replaceAll("\n", "<br>");
	}

	private void showEntityInfo(EntityInfo entityInfo) {
		if (entityInfo == null) {
			this.mEntityInfoPanel.add(this.mNoSelectedLabel);
			return;
		}
		for (EntityInfoEntry entry : entityInfo.getInfoList()) {
			JLabel entryLable = new JLabel(convertToMultiline(entry.description + ": " + entry.value));
			this.mEntityInfoPanel.add(entryLable);
		}
	}

	private void createDeviceTree() {
		this.mMainPanel.removeAll();

		DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode("Device");
		createDeviceNodes(deviceNode);

		this.deviceInfoTree = new JTree(deviceNode);
		this.deviceInfoTree.getSelectionModel().setSelectionMode(1);

		this.deviceInfoTree.addTreeSelectionListener(this.SelectionListener);

		this.mTreeScrollPane = new JScrollPane(this.deviceInfoTree);
		this.mTreeScrollPane.setBorder(BorderFactory.createEmptyBorder());
		this.mTreeScrollPane.setHorizontalScrollBarPolicy(31);
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

	private void saveInfoToXml() {
		JFileChooser fileChooser = new JFileChooser();
		String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		fileChooser.setSelectedFile(
				new File(this.mManager.getManagerName() + "_" + timeStamp + "_" + this.mComandNumber + ".xml"));
		if (fileChooser.showSaveDialog(this) == 0) {
			File file = fileChooser.getSelectedFile();
			generateInfoXml(file);
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

			for (Chip<?, ?, ?> chip : this.mCurrentDevice.getChips()) {
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

	private void addChipToXml(Element rootElement, Chip<?, ?, ?> chip, Document doc) {
		Element chipElement = doc.createElement("chip");
		addComponentInfo(chipElement, chip.getInfo());

		rootElement.appendChild(chipElement);

		for (Plane<?, ?> plane : chip.getPlanes()) {
			addPlaneToXml(chipElement, plane, doc);
		}
	}

	private void addPlaneToXml(Element chipElement, Plane<?, ?> plane, Document doc) {
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
