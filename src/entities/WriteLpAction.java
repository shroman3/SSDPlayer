package entities;

public class WriteLpAction implements IDeviceAction {
	private int _lp;
	
	public WriteLpAction(int lp){
		set_lp(lp);
	}

	public int get_lp() {
		return _lp;
	}

	public void set_lp(int _lp) {
		this._lp = _lp;
	}
}
