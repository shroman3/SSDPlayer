package entities;

import java.util.ArrayList;
import java.util.List;

public final class ActionLog {
	List<IDeviceAction> actions;
	public ActionLog(){
		 actions= new  ArrayList<IDeviceAction>();
	}
	
	public void addAction(IDeviceAction action){
		actions.add(action);
	}
	
	public List<IDeviceAction> getActionsByType(Class<?> actionClass){
		List<IDeviceAction> result = new ArrayList<IDeviceAction>();
		for (IDeviceAction action : actions) {
			if (actionClass.isInstance(action)){
				result.add(action);
			}
		} 
		return result;
	}
}
