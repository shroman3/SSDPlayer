package entities.RAID;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import entities.Chip;

/**
 * 
 * @author Or Mauda
 * 
 */
public abstract class RAIDBasicChip<P extends RAIDBasicPage, B extends RAIDBasicBlock<P>, T extends RAIDBasicPlane<P,B>> extends Chip<P,B,T> {
	public static abstract class Builder<P extends RAIDBasicPage, B extends RAIDBasicBlock<P>, T extends RAIDBasicPlane<P,B>> extends Chip.Builder<P,B,T> {
		public abstract RAIDBasicChip<P,B,T> build();
	}
	
	protected RAIDBasicChip() {}
	
	protected RAIDBasicChip(RAIDBasicChip<P,B,T> other) {
		super(other);
	}
	
	public abstract Builder<P,B,T> getSelfBuilder();
	
	@SuppressWarnings("unchecked")
	public Chip<P, B, T> invalidate(int stripe, int parityNumber) {
		List<T> updatedPlanes = new ArrayList<T>();
		for (T plane : getPlanes()) {
			updatedPlanes.add((T) plane.invalidate(stripe, parityNumber));
		}
		Builder<P,B,T> builder = getSelfBuilder();
		builder.setPlanes(updatedPlanes);
		return builder.build();
	}
	
	/**
	 * Checks if the page with parity number and stripe is highlighted.
	 *
	 * @param parityNumber the parity number
	 * @param stripe the stripe
	 * @return the pair [wasFound, isHighlighted] (isHighlighted matter only when wasFound==true)
	 */
	public Pair<Boolean,Boolean> isHighlighted(int parityNumber, int stripe) {
		Pair<Boolean, Boolean> planeAnswer;
		for (T plane : getPlanes()) {
			planeAnswer = plane.isHighlighted(parityNumber, stripe);
			if (planeAnswer.getValue0() == true) {
				return planeAnswer;
			}
		}
		return new Pair<Boolean, Boolean>(false, false);
	}
	
	/**
	 * Checks if the page with logical page 'lp' is highlighted.
	 *
	 * @param lp the logical page
	 * @return the pair [wasFound, isHighlighted] (isHighlighted matter only when wasFound==true)
	 */
	public Pair<Boolean, Boolean> isHighlighted(int lp) {
		Pair<Boolean, Boolean> planeAnswer;
		for (T plane : getPlanes()) {
			planeAnswer = plane.isHighlighted(lp);
			if (planeAnswer.getValue0() == true) {
				return planeAnswer;
			}
		}
		return new Pair<Boolean, Boolean>(false, false);
	}
	
	/**
	 * Sets the highlight field of all pages with 'stripe' value.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param stripe the stripe to be highlighted or un-highlighted.
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated chip.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, RAIDBasicChip<P, B, T>> setHighlightByPhysicalP(boolean toHighlight, int stripe) {
		List<P> pages = new ArrayList<P>(0);
		List<T> chipPlanes = new ArrayList<T>();
		Triplet<Integer, List<P>, RAIDBasicChip<P, B, T>> details = new Triplet<Integer, List<P>, RAIDBasicChip<P, B, T>>(-1, null, null);
		Triplet<Integer, List<P>, RAIDBasicPlane<P, B>> planeDetails;
		for (T plane : getPlanes()) {
			planeDetails = plane.setHighlightByPhysicalP(toHighlight, stripe);
			if (planeDetails.getValue0() != -1) { // means we found a plane which includes a page on our stripe
				details = details.setAt0(planeDetails.getValue0()); // set our stripe
				pages.addAll(planeDetails.getValue1()); // add this plane relevant pages
			}
			chipPlanes.add((T) planeDetails.getValue2());
		}
		Builder<P,B,T> builder = getSelfBuilder();
		builder.setPlanes(chipPlanes);
		details = details.setAt2(builder.build());
		details = details.setAt1(pages);
		return details;
	}
	
	/**
	 * Sets the highlight field of all pages on the same stripe as the page with lp.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param lp the lp
	 * @param stripe the stripe to be highlighted or un-highlighted. IMPORTANT: set to -1 if unknown
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated chip.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, RAIDBasicChip<P, B, T>> setHighlightByLogicalP(boolean toHighlight, int lp, int stripe) {
		List<P> pages = new ArrayList<P>(0);
		List<T> chipPlanes = new ArrayList<T>();
		Triplet<Integer, List<P>, RAIDBasicChip<P, B, T>> details = new Triplet<Integer, List<P>, RAIDBasicChip<P, B, T>>(stripe, null, null);
		Triplet<Integer,List<P>,RAIDBasicPlane<P,B>> planeDetails;
		for (T plane : getPlanes()) {
			planeDetails = plane.setHighlightByLogicalP(toHighlight, lp, stripe);
			if (planeDetails.getValue0() != -1) { // means we found a plane which includes a page on our stripe
				details = details.setAt0(planeDetails.getValue0()); // set our stripe
				pages.addAll(planeDetails.getValue1()); // add this plane relevant pages
				stripe = planeDetails.getValue0();
			}
			chipPlanes.add((T) planeDetails.getValue2());
		}
		Builder<P,B,T> builder = getSelfBuilder();
		builder.setPlanes(chipPlanes);
		details = details.setAt2(builder.build());
		details = details.setAt1(pages);
		return details;
	}
	
	/**
	 * Sets the highlight field of all pages on the same stripe as the given parity page.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param parityNumber the parityNumber
	 * @param stripe the stripe to be highlighted or un-highlighted.
	 * @param parityMatters - if it's false, then highlight this stripe and ignore the parity. otherwise, don't ignore the parity. 
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated chip.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, RAIDBasicChip<P, B, T>> setHighlightByParityP(boolean toHighlight, int parityNumber, int stripe, boolean parityMatters) {
		List<P> pages = new ArrayList<P>(0);
		List<T> chipPlanes = new ArrayList<T>();
		Triplet<Integer, List<P>, RAIDBasicChip<P, B, T>> details = new Triplet<Integer, List<P>, RAIDBasicChip<P, B, T>>(-1, null, null);
		Triplet<Integer,List<P>,RAIDBasicPlane<P,B>> planeDetails;
		for (T plane : getPlanes()) {
			planeDetails = plane.setHighlightByParityP(toHighlight, parityNumber, stripe, parityMatters);
			if (planeDetails.getValue0() == stripe) { // means we found a plane which includes a page on our stripe
				details = details.setAt0(planeDetails.getValue0()); // set our stripe
				pages.addAll(planeDetails.getValue1()); // add this plane relevant pages
			}
			chipPlanes.add((T) planeDetails.getValue2());
		}
		Builder<P,B,T> builder = getSelfBuilder();
		builder.setPlanes(chipPlanes);
		details = details.setAt2(builder.build());
		details = details.setAt1(pages);
		return details;
	}
}