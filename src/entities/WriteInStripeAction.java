package entities;

public class WriteInStripeAction implements IDeviceAction {
	private int mStripe;
	
	public WriteInStripeAction(int stripe){
		mStripe = stripe;
	}

	public int getStripe() {
		return mStripe;
	}

	public void setStripe(int stripe) {
		mStripe = stripe;
	}
}
