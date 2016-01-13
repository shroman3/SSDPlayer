package general;

public class ConfigProperties {
	private static int mChipsInDevice;
	private static int mPlanesInChip;
	private static int mBlocksInPlane;
	private static int mPagesInBlock;
	
	public static void initialize(XMLGetter xmlGetter) throws XMLParsingException {
		mChipsInDevice = xmlGetter.getIntField("physical", "chips");
		mPlanesInChip = xmlGetter.getIntField("physical", "planes");
		mBlocksInPlane = xmlGetter.getIntField("physical", "blocks");
		mPagesInBlock = xmlGetter.getIntField("physical", "pages");
	}
	
	public static int getChipsInDevice() {
		return mChipsInDevice;
	}
	
	public static int getPlanesInChip() {
		return mPlanesInChip;
	}
	
	public static int getBlocksInPlane() {
		return mBlocksInPlane;
	}
	
	public static int getPagesInBlock() {
		return mPagesInBlock;
	}
	
	public static int getBlocksInDevice() {
		return mBlocksInPlane * mPlanesInChip * mChipsInDevice;
	}
	
	public static int getPagesInDevice() {
		return getBlocksInDevice() * mPagesInBlock;
	}
}
