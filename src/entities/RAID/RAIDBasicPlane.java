package entities.RAID;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import entities.Plane;

/**
 * 
 * @author Or Mauda
 * 
 */
public abstract class RAIDBasicPlane<P extends RAIDBasicPage, B extends RAIDBasicBlock<P>> extends Plane<P, B> {
	public static abstract class Builder<P extends RAIDBasicPage, B extends RAIDBasicBlock<P>> extends Plane.Builder<P,B> {
		public abstract RAIDBasicPlane<P,B> build();
	}
	
	protected RAIDBasicPlane() {}
	
	protected RAIDBasicPlane(RAIDBasicPlane<P,B> other) {
		super(other);
	}
	
	public abstract Builder<P, B> getSelfBuilder();
	
	/**
	 * @param stripe - Logical Page's stripe to be invalidated (part of address)
	 * @param stripe - Logical Page's parity number to be invalidated (part of address)
	 * @return new plane with the Logical Page specified invalidated from the blocks
	 */
	@SuppressWarnings("unchecked")
	public Plane<P,B> invalidate(int stripe, int parityNumber) {
		List<B> updatedBlocks = new ArrayList<B>();
		for (B block : getBlocks()) {
			updatedBlocks.add((B) block.invalidate(stripe, parityNumber));
		}
		Builder<P,B> builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks);
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
		Pair<Boolean, Boolean> blockAnswer;
		for (B block : getBlocks()) {
			blockAnswer = block.isHighlighted(parityNumber, stripe);
			if (blockAnswer.getValue0() == true) {
				return blockAnswer;
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
		Pair<Boolean, Boolean> blockAnswer;
		for (B block : getBlocks()) {
			blockAnswer = block.isHighlighted(lp);
			if (blockAnswer.getValue0() == true) {
				return blockAnswer;
			}
		}
		return new Pair<Boolean, Boolean>(false, false);
	}
	
	/**
	 * Sets the highlight field of all pages with 'stripe' value.
	 *
	 * @param toHighlight - indicates if to highlight or to un-highlight
	 * @param blockIndex the block index
	 * @param pageIndex the page index
	 * @param stripe the stripe to be highlighted or un-highlighted.
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated plane.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, RAIDBasicPlane<P,B>> setHighlightByPhysicalP(boolean toHighlight, int stripe) {
		List<P> pages = new ArrayList<P>(0);
		List<B> planeBlocks = new ArrayList<B>();
		Triplet<Integer, List<P>, RAIDBasicPlane<P, B>> details = new Triplet<Integer, List<P>, RAIDBasicPlane<P,B>>(-1, null, null);
		Triplet<Integer,List<P>,RAIDBasicBlock<P>> blockDetails;
		for (B block : getBlocks()) {
			blockDetails = block.setHighlightByPhysicalP(toHighlight, stripe);
			if (blockDetails.getValue0() != -1) { // means we found a block which includes a page on our stripe
				details = details.setAt0(blockDetails.getValue0()); // set our stripe
				pages.addAll(blockDetails.getValue1()); // add this block relevant pages
			}
			planeBlocks.add((B) blockDetails.getValue2());
		}
		Builder<P,B> builder = getSelfBuilder();
		builder.setBlocks(planeBlocks);
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
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated plane.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, RAIDBasicPlane<P,B>> setHighlightByLogicalP(boolean toHighlight, int lp, int stripe) {
		List<P> pages = new ArrayList<P>(0);
		List<B> planeBlocks = new ArrayList<B>();
		Triplet<Integer, List<P>, RAIDBasicPlane<P, B>> details = new Triplet<Integer, List<P>, RAIDBasicPlane<P,B>>(stripe, null, null);
		Triplet<Integer, List<P>, RAIDBasicBlock<P>> blockDetails;
		for (B block : getBlocks()) {
			blockDetails = block.setHighlightByLogicalP(toHighlight, lp, stripe);
			if (blockDetails.getValue0() != -1) { // means we found a block which includes a page on our stripe
				details = details.setAt0(blockDetails.getValue0()); // set our stripe
				pages.addAll(blockDetails.getValue1()); // add this block relevant pages
				stripe = blockDetails.getValue0();
			}
			planeBlocks.add((B) blockDetails.getValue2());
		}
		Builder<P,B> builder = getSelfBuilder();
		builder.setBlocks(planeBlocks);
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
	 * @return a triplet, which first value is the stripe, second value is a list of the pages on the same stripe, third value is the updated plane.
	 */
	@SuppressWarnings("unchecked")
	public Triplet<Integer, List<P>, RAIDBasicPlane<P,B>> setHighlightByParityP(boolean toHighlight, int parityNumber, int stripe, boolean parityMatters) {
		List<P> pages = new ArrayList<P>(0);
		List<B> planeBlocks = new ArrayList<B>();
		Triplet<Integer, List<P>, RAIDBasicPlane<P,B>> details = new Triplet<Integer, List<P>, RAIDBasicPlane<P,B>>(-1, null, null);
		Triplet<Integer, List<P>, RAIDBasicBlock<P>> blockDetails;
		for (B block : getBlocks()) {
			blockDetails = block.setHighlightByParityP(toHighlight, parityNumber, stripe, parityMatters);
			if (blockDetails.getValue0() == stripe) { // means we found a block which includes a page on our stripe
				details = details.setAt0(blockDetails.getValue0()); // set our stripe
				pages.addAll(blockDetails.getValue1()); // add this block relevant pages
				//stripe = blockDetails.getValue0();
			}
			planeBlocks.add((B) blockDetails.getValue2());
		}
		Builder<P,B> builder = getSelfBuilder();
		builder.setBlocks(planeBlocks);
		details = details.setAt2(builder.build());
		details = details.setAt1(pages);
		return details;
	}
}