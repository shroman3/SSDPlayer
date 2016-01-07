package breakpoints;

public class BreakpointComponent {
	private String mPropertyName;
	private String mLabel;
	private Class<?> mParamType;
	
	public BreakpointComponent(String propertyName, Class<?> paramType, String label) {
		mPropertyName = propertyName;
		mParamType = paramType;
		mLabel = label;
	}
	
	public String getPropertyName() {
		return mPropertyName;
	}
	
	public String getGetterName() {
		return "get" + Character.toUpperCase(mPropertyName.charAt(0)) + mPropertyName.substring(1);
	}
	
	public String getSetterName() {
		return "set" + Character.toUpperCase(mPropertyName.charAt(0)) + mPropertyName.substring(1);
	}
	
	public Class<?> getParamType() {
		return mParamType;
	}
	
	public String getLabel() {
		return mLabel;
	}
}
