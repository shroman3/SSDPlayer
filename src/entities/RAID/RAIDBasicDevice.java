package entities.RAID;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import entities.Device;

/**
 * 
 * @author Or Mauda
 * 
 */
public abstract class RAIDBasicDevice<P extends RAIDBasicPage, B extends RAIDBasicBlock<P>, T extends RAIDBasicPlane<P,B>, C extends RAIDBasicChip<P,B,T>> extends Device<P,B,T,C> {
	public static abstract class Builder<P extends RAIDBasicPage, B extends RAIDBasicBlock<P>, T extends RAIDBasicPlane<P,B>, C extends RAIDBasicChip<P,B,T>> extends Device.Builder<P,B,T,C> {
		private RAIDBasicDevice<P,B,T,C> device;
		
		public abstract RAIDBasicDevice<P,B,T,C> build();
		
		protected void setDevice(RAIDBasicDevice<P,B,T,C> device) {
			super.setDevice(device);
			this.device = device;
		}

		
		public Builder<P,B,T,C> setTotalDataWritten(int totalDataWritten) {
			device.totalDataWritten = totalDataWritten;
			setTotalWritten(device.getTotalWritten()+1);
			return this;
		}
		
		public Builder<P,B,T,C> setTotalParityWritten(int totalParityWritten) {
			device.totalParityWritten = totalParityWritten;
			setTotalWritten(device.getTotalWritten()+1);
			return this;
		}
	}
	
	private int totalDataWritten = 0;
	private int totalParityWritten = 0;
	
	protected RAIDBasicDevice() {}
	
	protected RAIDBasicDevice(RAIDBasicDevice<P,B,T,C> other) {
		super(other);
		this.totalDataWritten = other.totalDataWritten;
		this.totalParityWritten = other.totalParityWritten;
	}
	
	public abstract Builder<P,B,T,C> getSelfBuilder();
	
	public int getTotalDataWritten() {
		return totalDataWritten;
	}
	
	public int getTotalParityWritten() {
		return totalParityWritten;
	}
	
	@SuppressWarnings("unchecked")
	public Device<P,B,T,C>  invalidate(int stripe, int parityNumber) {
		List<C> updatedChips = new ArrayList<C>();
		for (C chip : getChips()) {
			updatedChips.add((C) chip.invalidate(stripe, parityNumber));
		}
		Builder<P,B,T,C>  builder = getSelfBuilder();
		builder.setChips(updatedChips);
		return builder.build();
	}
	
	/**
	 * Checks if the page with logical page 'lp' is highlighted.
	 *
	 * @param lp the logical page
	 * @return true, if is highlighted
	 */
	public boolean isPageHighlighted(int lp) {
		Pair<Boolean, Boolean> chipAnswer;
		for (C chip : getChips()) {
			chipAnswer = chip.isHighlighted(lp);
			if (chipAnswer.getValue0() == true && chipAnswer.getValue1() == true) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the page with parity number and stripe is highlighted.
	 *
	 * @param parityNumber the parity number
	 * @param stripe the stripe
	 * @return true, if is highlighted
	 */
	public boolean isPageHighlighted(int parityNumber, int stripe) {
		Pair<Boolean, Boolean> chipAnswer;
		for (C chip : getChips()) {
			chipAnswer = chip.isHighlighted(parityNumber, stripe);
			if (chipAnswer.getValue0() == true && chipAnswer.getValue1() == true) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Sets the highlight field of all pages with the same stripe as the page with index (chipIndex, planeIndex, blockIndex, pageIndex).
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param chipIndex the chip index
	 * @param planeIndex the plane index
	 * @param blockIndex the block index
	 * @param pageIndex the page index
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated device.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, RAIDBasicDevice<P,B,T,C>> setHighlightByPhysicalP(boolean toHighlight,int chipIndex, int planeIndex, int blockIndex, int pageIndex) {
		int stripe = getChip(chipIndex).getPlane(planeIndex).getBlock(blockIndex).getPage(pageIndex).getStripe();
		Triplet<Integer, List<P>, RAIDBasicDevice<P, B, T, C>> details = new Triplet<Integer, List<P>, RAIDBasicDevice<P, B, T, C>>(-1, null, null);
		List<P> stripePages = new ArrayList<P>(0);
		List<C> deviceChips = new ArrayList<C>();
		Triplet<Integer,List<P>,RAIDBasicChip<P,B,T>> chipDetails;
		for (C chip : getChips()) {
			chipDetails = chip.setHighlightByPhysicalP(toHighlight, stripe);
			if (chipDetails.getValue0() != -1) { // means we found a chip which includes a page on our stripe
				details = details.setAt0(chipDetails.getValue0());
				stripePages.addAll(chipDetails.getValue1()); // add this chip relevant pages
			}
			deviceChips.add((C) chipDetails.getValue2());
		}
		Builder<P,B,T,C>  builder = getSelfBuilder();
		builder.setChips(deviceChips);
		details = details.setAt2(builder.build());
		details = details.setAt1(stripePages);
		return details;
	}
	
	/**
	 * Sets the highlight field of all pages on the same stripe as the page with lp.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param lp the lp
	 * @param stripe the stripe to be highlighted or un-highlighted.
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated device.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, RAIDBasicDevice<P,B,T,C>> setHighlightByLogicalP(boolean toHighlight, int lp) {
		int stripe = -1; // means the stripe is still unknown
		List<P> pages = new ArrayList<P>(0);
		List<C> deviceChips = new ArrayList<C>();
		Triplet<Integer, List<P>, RAIDBasicDevice<P, B, T, C>> details = new Triplet<Integer, List<P>, RAIDBasicDevice<P, B, T, C>>(stripe, null, null);
		Triplet<Integer, List<P>, RAIDBasicChip<P, B, T>> chipDetails;
		for (C chip : getChips()) {
			chipDetails = chip.setHighlightByLogicalP(toHighlight, lp, stripe);
			if (chipDetails.getValue0() != -1) { // means we found a block which includes a chip on our stripe
				details = details.setAt0(chipDetails.getValue0()); // set our stripe
				pages.addAll(chipDetails.getValue1()); // add this chip relevant pages
				stripe = chipDetails.getValue0();
			}
			deviceChips.add((C) chipDetails.getValue2());
		}
		Builder<P,B,T,C>  builder = getSelfBuilder();
		builder.setChips(deviceChips);
		details = details.setAt2(builder.build());
		details = details.setAt1(pages);
		return details;
	}
	
	/**
	 * Sets the highlight field of all pages on the same stripe as the given parity page.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param parityNumber the parityNumber (Currently not relevant)
	 * @param stripe the stripe to be highlighted or un-highlighted.
	 * @param parityMatters - if it's false, then highlight this stripe and ignore the parity. otherwise, don't ignore the parity. 
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated device.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, ? extends RAIDBasicDevice<P,B,T,C>> setHighlightByParityP(boolean toHighlight, int parityNumber, int stripe, boolean parityMatters) {
		List<P> pages = new ArrayList<P>(0);
		List<C> deviceChips = new ArrayList<C>();
		Triplet<Integer, List<P>, RAIDBasicDevice<P, B, T, C>> details = new Triplet<Integer, List<P>, RAIDBasicDevice<P, B, T, C>>(-1, null, null);
		Triplet<Integer, List<P>, RAIDBasicChip<P, B, T>> chipDetails;
		for (C chip : getChips()) {
			chipDetails = chip.setHighlightByParityP(toHighlight, parityNumber, stripe, parityMatters);
			if (chipDetails.getValue0() == stripe) { // means we found a chip which includes a page on our stripe
				details = details.setAt0(chipDetails.getValue0()); // set our stripe
				pages.addAll(chipDetails.getValue1()); // add this chip relevant pages
			}
			deviceChips.add((C) chipDetails.getValue2());
		}
		Builder<P,B,T,C>  builder = getSelfBuilder();
		builder.setChips(deviceChips);
		details = details.setAt2(builder.build());
		details = details.setAt1(pages);
		return details;
	}
}