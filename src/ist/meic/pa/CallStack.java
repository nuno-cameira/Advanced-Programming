package ist.meic.pa;

public class CallStack {
	public Object className;
	public String methodName;
	public Object[] methodArgs;

	public CallStack(Object className, String methodName,
			Object[] methodArgs) {
		this.className = className;
		this.methodName = methodName;
		this.methodArgs = methodArgs;
	}
}