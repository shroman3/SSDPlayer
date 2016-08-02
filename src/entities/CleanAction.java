package entities;

public class CleanAction implements IDeviceAction {
	private int mChipIndex;
	private int mPlaneIndex;
	private int mValidPAges;
	
	public CleanAction(int chipIndex, int planeIndex, int validPages){
		mChipIndex = chipIndex;
		mPlaneIndex = planeIndex;
		mValidPAges = validPages;
	}
	
	public int getChipIndex() {
		return mChipIndex;
	}
	public void setChipIndex(int mChipIndex) {
		this.mChipIndex = mChipIndex;
	}
	public int getPlaneIndex() {
		return mPlaneIndex;
	}
	public void setPlaneIndex(int mPlaneIndex) {
		this.mPlaneIndex = mPlaneIndex;
	}
	public int getValidPAges() {
		return mValidPAges;
	}
	public void setValidPAges(int mValidPAges) {
		this.mValidPAges = mValidPAges;
	}
}
