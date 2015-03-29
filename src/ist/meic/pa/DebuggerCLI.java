package ist.meic.pa;

import ist.meic.pa.fields.FieldFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import javassist.ClassPool;
import javassist.Loader;
import javassist.Translator;

public class DebuggerCLI {

	private static Scanner scanner = new Scanner(System.in);
	private static InspectionObject lastObj = null;
	private static Stack<CallStack> callStack = new Stack<CallStack>();

	private static Throwable thrownException = null;

	static class CallStack {

		Object className;
		String methodName;
		Object[] methodArgs;

		public CallStack(Object className, String methodName,
				Object[] methodArgs) {
			this.className = className;
			this.methodName = methodName;
			this.methodArgs = methodArgs;
		}
	}

	public static Stack<CallStack> getCallStack() {
		return callStack;
	}

	public static void printArgs(Object[] methodArgs) {
		for (Object o : methodArgs) {
			System.out.println("ARGUMENTS" + o);
		}
	}

	public static void addToStack(Object classname, String methodName,
			Object[] methodArgs) {
		if (lastObj.getObj() != null) {
			callStack.push(new DebuggerCLI.CallStack(lastObj.getObj(),
					methodName, methodArgs));
		} else {
			callStack.push(new DebuggerCLI.CallStack(classname, methodName,
					methodArgs));
		}
	}

	public static void printCallStack() {
		System.out.println("Call Stack:");
		String formatOutput = "";
		// for (CallStack cs : callStack) {
		List<CallStack> stampTemp = new Stack<CallStack>();
		// Stack<CallStack> stampTemp = new Stack<CallStack>();
		stampTemp.addAll(callStack);
		Stack<CallStack> stampTemp2 = new Stack<CallStack>();
		stampTemp2 = (Stack<CallStack>) stampTemp;
		while (!stampTemp2.empty()) {
			CallStack cs = stampTemp2.pop();
			System.out.print(cs.className.getClass().getName() + "."
					+ cs.methodName + "(");
			for (Object o : cs.methodArgs) {
				System.out.print(formatOutput + o);
				formatOutput = ", ";
			}
			formatOutput = "";
			System.out.println(")");
		}
	}

	public static void setLastObj(Object lastObj) {
		DebuggerCLI.lastObj = new InspectionObject(lastObj);
	}

	public static Object run() {
		Object o = null;
		try {
			o = DebuggerCLI.lastObj.invokeMethodOnStack();
			if(o != null && o instanceof Exception){
				return startShell();
			}

		} catch (IllegalArgumentException
				| SecurityException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			System.out.println(e.getCause());
			setThrownException(e.getCause());
			return startShell();
		}

		return o;
	}

	public static Object startShell() {
		System.out.print("DebuggerCLI:> ");
		String command = scanner.next();
		String argument = "";
		String value = "";

		while (!command.equals("Abort")) {
			switch (command) {
			case "Info":
				DebuggerCLI.lastObj.printDetails();
				printCallStack();
				break;
			case "Throw":
				callStack.pop();
				CallStack c = callStack.peek();
				setLastObj(c.className);
				return getThrownException();
			case "Return":
				argument = scanner.next();
				return processReturn(argument);
			case "Get":
				argument = scanner.next();
				processGet(argument);
				break;
			case "Set":
				argument = scanner.next();
				value = scanner.next();
				processSet(argument, value);
				break;
			case "Retry":
				processRetry();
				break;
			default:
				System.out.println("Unknown command");
			}
			System.out.print("DebuggerCLI:> ");
			command = scanner.next();
		}
		System.exit(0);
		return null;
	}

	private static Object processReturn(String argument) {
		String methodName = callStack.peek().methodName;
		Method m = null;

		if (lastObj.getObj() == null) {
			try {
				CallStack cs = callStack.peek();
				Class<?> c = (Class<?>) cs.className;
				m = c.getMethod(methodName,
						DebuggerCLI.getClassesOfMethodArgs(cs));
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				m = lastObj
						.getObj()
						.getClass()
						.getMethod(methodName,
								getClassesOfMethodArgs(callStack.peek()));
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(m!=null){
		m.setAccessible(true);
		System.out.println("Return -> method type"
				+ FieldFactory.getType(argument, m.getReturnType().getName())
						.getClass());
		callStack.pop();
		return FieldFactory.getType(argument, m.getReturnType().getName());
		}
		return m;
	}

	private static void processGet(String argument) {
		System.out.println(DebuggerCLI.lastObj.getField(argument));
	}

	private static void processSet(String field, String value) {
		DebuggerCLI.lastObj.setField(field, value);
	}

	private static void processRetry() {

		try {
			DebuggerCLI.lastObj.invokeMethodOnStack();
		} catch (IllegalArgumentException | SecurityException
				| NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			System.out.println(e.getCause());
		}
	}

	public static Class<?>[] getClassesOfMethodArgs(CallStack cs) {
		ArrayList<Class<?>> argsTemp = new ArrayList<Class<?>>();

		// builds an ArrayList with the argument's type for that method
		for (Object o : cs.methodArgs) {
			Class<?> cc = null;
			if (Unwrapper.isWrapperType(o.getClass())) {
				cc = Unwrapper.unwrap(o.getClass());
			} else {
				cc = o.getClass();
			}
			argsTemp.add(cc);
		}

		// converts ArrayList to a simple array
		Class<?>[] argsType = new Class<?>[argsTemp.size()];
		Class<?>[] args = argsTemp.toArray(argsType);
		return args;
	}

	public static void main(String[] args) {

		String classname = args[0];

		// Translator translator = new MyTranslator();
		ClassPool pool = ClassPool.getDefault();
		Loader loader = new Loader();
		// VERY IMPORTANT LINE
		loader.delegateLoadingOf(ist.meic.pa.DebuggerCLI.class.getName());

		try {
			loader.addTranslator(pool, new MyTranslator());
			System.out.println("MAIN:" + classname);
			Class<?> c = Class.forName(classname);
			Object o = c.newInstance();
			Object[] obs = { classname };
			//
			callStack.push(new DebuggerCLI.CallStack(o, "main", obs));
			setLastObj(Class.forName(classname).newInstance());
			loader.run(classname, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static Throwable getThrownException() {
		return thrownException;
	}

	public static void setThrownException(Throwable thrownException) {
		DebuggerCLI.thrownException = thrownException;
	}
}