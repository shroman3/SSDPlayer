package breakpoints;

import java.util.List;

import general.ConfigProperties;
import general.XMLGetter;
import general.XMLParsingException;
import manager.HotColdSSDManager;

public class BreakpointsConstraints {
	private static String ILLEGAL_CHIP = "";
	private static String ILLEGAL_PLANE;
	private static String ILLEGAL_BLOCK;
	private static String ILLEGAL_COUNT = "Count value should be a posivite integer";
	private static String ILLEGAL_PERCENT = "Percent value should be between 0 and 100";
	private static String ILLEGAL_WRITE_AMP = "Write Amplification should be greater than 1.0";
	private static String ILLEGAL_PARTITION = "Count value should be a posivite integer";
	private static String ILLEGAL_WRITE_LEVEL = "Write level should be 1 or 2";
	private static String ILLEGAL_WRITES_PER_ERASE = "Writes per erase should be greater than 1.0";
	private static String ILLEGAL_LP;
	
	private static XMLGetter mXmlGetter;
	private static int mNumberOfPages;
	private static int mParitionsNumber;
	
	public static void initialize(XMLGetter xmlGetter) {
		mXmlGetter = xmlGetter;
		mNumberOfPages = ConfigProperties.getPagesInDevice();
		List<Integer> partitions;
		try {
			partitions = mXmlGetter.getListField(HotColdSSDManager.class.getSimpleName(), "partition");
			mParitionsNumber = (partitions == null)? 0 : partitions.size();
		} catch (XMLParsingException e) {
			e.printStackTrace();
		}

		ILLEGAL_CHIP = "Chip index should be between 0 and " + (ConfigProperties.getChipsInDevice() - 1);
		
		initErrors();
	}
	
	private static void initErrors() {
		ILLEGAL_CHIP = "Chip index should be between 0 and " + (ConfigProperties.getChipsInDevice() - 1);
		ILLEGAL_PLANE = "Plane index should be between 0 and " + (ConfigProperties.getPlanesInChip() - 1);
		ILLEGAL_BLOCK = "Block index should be between 0 and " + (ConfigProperties.getBlocksInPlane() - 1);
		ILLEGAL_PARTITION = "Parition number should be between 1 and " + mParitionsNumber;
		ILLEGAL_LP = "Logical page index should be between 0 and " + (ConfigProperties.getPagesInDevice() - 1);
	}
	
	public static boolean isBlockIndexLegal(int index) {
		return index < ConfigProperties.getBlocksInPlane() 
				&& index >= 0;
	}
	
	public static String getBlockIndexError() {
		return ILLEGAL_BLOCK;
	}
	
	public static boolean isPlaneIndexLegal(int index) {
		return index < ConfigProperties.getPlanesInChip()
				&& index >= 0;
	}
	
	public static String getPlaneIndexError() {
		return ILLEGAL_PLANE;
	}
	
	public static boolean isChipIndexLegal(int index) {
		return index < ConfigProperties.getChipsInDevice()
				&& index >= 0;
	}
	
	public static String getChipIndexError() {
		return ILLEGAL_CHIP;
	}

	public static boolean isCountValueLegal(int count) {
		return count >= 0;
	}
	
	public static String getCountError() {
		return ILLEGAL_COUNT;
	}
	
	public static boolean isPercentValueLegal(int percent) {
		return percent >= 0 && percent <= 100;
	}
	
	public static String getPercentError() {
		return ILLEGAL_PERCENT;
	}
	
	public static boolean isPartitionIndexLegal(int partition) {
		return partition >= 1 && partition <= mParitionsNumber;
	}
	
	public static String getPartitionError() {
		return ILLEGAL_PARTITION;
	}
	
	public static boolean isWriteAmplificationValueLegal(double value) {
		return value >= 1.0;
	}
	
	public static String getWriteAmplificationError() {
		return ILLEGAL_WRITE_AMP;
	}

	public static boolean isWriteLevelLegal(int writeLevel) {
		return writeLevel == 1 || writeLevel == 2;
	}
	
	public static String getWriteLevelError() {
		return ILLEGAL_WRITE_LEVEL;
	}

	public static boolean isLPLegal(int lp) {
		return lp >= 0 && lp < mNumberOfPages;
	}
	
	public static String getLPError() {
		return ILLEGAL_LP;
	}
	
	public static boolean isWritesPerEraseValueLegal(double value) {
		return value >= 1.0;
	}
	
	public static String getWritesPerEraseError() {
		return ILLEGAL_WRITES_PER_ERASE;
	}
}
