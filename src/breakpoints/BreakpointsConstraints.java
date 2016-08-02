package breakpoints;

import java.util.HashMap;
import java.util.List;

import general.ConfigProperties;
import general.MessageLog;
import general.XMLGetter;
import general.XMLParsingException;
import manager.HotColdSSDManager;

public class BreakpointsConstraints {
	private static String ILLEGAL_CHIP = "";
	private static String ILLEGAL_PLANE;
	private static String ILLEGAL_BLOCK;
	private static String ILLEGAL_PAGE;
	private static String ILLEGAL_COUNT = "Count value should be a posivite integer";
	private static String ILLEGAL_PERCENT = "Percent value should be between 0 and 100";
	private static String ILLEGAL_WRITE_AMP = "Write Amplification should be greater than 1.0";
	private static String ILLEGAL_PARTITION = "Count value should be a posivite integer";
	private static String ILLEGAL_WRITE_LEVEL = "Write level should be 1 or 2";
	private static String ILLEGAL_WRITES_PER_ERASE = "Writes per erase should be greater than 1.0";
	private static String ILLEGAL_PARITY_OVERHEAD = "Parity overhead should be greater than 1.0";
	private static String ILLEGAL_LP;
	private static String ILLEGAL_STRIPE = "Stripe number should be non-negative";
	
	private static XMLGetter mXmlGetter;
	private static int mNumberOfPages;
	private static int mParitionsNumber;
	private static HashMap<SetterError, String> mErrorMap;
	
	public static void initialize(XMLGetter xmlGetter) {
		mErrorMap = new HashMap<>();
		mXmlGetter = xmlGetter;
		mNumberOfPages = ConfigProperties.getPagesInDevice();
		List<Integer> partitions;
		try {
			partitions = mXmlGetter.getListField(HotColdSSDManager.class.getSimpleName(), "partition");
			mParitionsNumber = (partitions == null)? 0 : partitions.size();
		} catch (XMLParsingException e) {
			e.printStackTrace();
		}

		initErrors();
	}
	
	private static void initErrors() {
		ILLEGAL_CHIP = "Chip index should be between 0 and " + (ConfigProperties.getChipsInDevice() - 1);
		ILLEGAL_PLANE = "Plane index should be between 0 and " + (ConfigProperties.getPlanesInChip() - 1);
		ILLEGAL_BLOCK = "Block index should be between 0 and " + (ConfigProperties.getBlocksInPlane() - 1);
		ILLEGAL_PAGE = "Page index should be between 0 and " + (ConfigProperties.getPagesInBlock() - 1);
		ILLEGAL_PARTITION = "Parition number should be between 1 and " + mParitionsNumber;
		ILLEGAL_LP = "Logical page index should be between 0 and " + (ConfigProperties.getPagesInDevice() - 1);
		
		mErrorMap.put(SetterError.ILLEGAL_CHIP, ILLEGAL_CHIP);
		mErrorMap.put(SetterError.ILLEGAL_PLANE, ILLEGAL_PLANE);
		mErrorMap.put(SetterError.ILLEGAL_BLOCK, ILLEGAL_BLOCK);
		mErrorMap.put(SetterError.ILLEGAL_PAGE, ILLEGAL_PAGE);
		mErrorMap.put(SetterError.ILLEGAL_COUNT, ILLEGAL_COUNT);
		mErrorMap.put(SetterError.ILLEGAL_PERCENT, ILLEGAL_PERCENT);
		mErrorMap.put(SetterError.ILLEGAL_WRITE_AMP, ILLEGAL_WRITE_AMP);
		mErrorMap.put(SetterError.ILLEGAL_PARTITION, ILLEGAL_PARTITION);
		mErrorMap.put(SetterError.ILLEGAL_WRITE_LEVEL, ILLEGAL_WRITE_LEVEL);
		mErrorMap.put(SetterError.ILLEGAL_WRITES_PER_ERASE, ILLEGAL_WRITES_PER_ERASE);
		mErrorMap.put(SetterError.ILLEGAL_PARITY_OVERHEAD, ILLEGAL_PARITY_OVERHEAD);
		mErrorMap.put(SetterError.ILLEGAL_LP, ILLEGAL_LP);
		mErrorMap.put(SetterError.ILLEGAL_STRIPE, ILLEGAL_STRIPE);
	}
	
	public static boolean isPageIndexLegal(int index) {
		return index < ConfigProperties.getPagesInBlock() 
				&& index >= 0;
	}
	
	public static boolean isBlockIndexLegal(int index) {
		return index < ConfigProperties.getBlocksInPlane() 
				&& index >= 0;
	}
	
	public static boolean isPlaneIndexLegal(int index) {
		return index < ConfigProperties.getPlanesInChip()
				&& index >= 0;
	}
	
	public static boolean isChipIndexLegal(int index) {
		return index < ConfigProperties.getChipsInDevice()
				&& index >= 0;
	}
	
	public static boolean isCountValueLegal(int count) {
		return count >= 0;
	}
	
	public static boolean isGCCountLegal(int count) {
		return count >= 1;
	}
	
	public static boolean isPercentValueLegal(int percent) {
		return percent >= 0 && percent <= 100;
	}
	
	public static boolean isPartitionIndexLegal(int partition) {
		return partition >= 1 && partition <= mParitionsNumber;
	}
	
	public static boolean isWriteAmplificationValueLegal(double value) {
		return value >= 1.0;
	}
	
	public static boolean isParityOverheadValueLegal(double value) {
		return value >= 1.0;
	}
	
	public static boolean isWriteLevelLegal(int writeLevel) {
		return writeLevel == 1 || writeLevel == 2;
	}
	
	public static boolean isLPLegal(int lp) {
		return lp >= 0 && lp < mNumberOfPages;
	}
	
	public static boolean isWritesPerEraseValueLegal(double value) {
		return value >= 1.0;
	}

	public static Exception reportSetterException(SetterError setterError) {
		MessageLog.log(mErrorMap.get(setterError));
		return new Exception(mErrorMap.get(setterError));
	}

	public static boolean isStripeLegal(int stripe) {
		return true;
	}
}
