package ist.meic.pa;

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

	private static class CallStack {

		Object className;
		String methodName;
		Object[] methodArgs;
		
		

		public CallStack(Object className, String methodName, Object[] methodArgs) {
			this.className = className;
			this.methodName = methodName;
			this.methodArgs = methodArgs;
		}

	}


	public static void addToStack(String methodName, Object[] methodArgs) {
		/*System.out.println("MN "+methodName);
		for(Object o : methodArgs){
			System.out.println("AA"+ o);
		}*/
		//System.out.println("OO "+lastObj.getObj().getClass().getName());
		callStack.push(new DebuggerCLI.CallStack(lastObj.getObj(), methodName, methodArgs));
	}

	public static void printCallStack() {
		System.out.println("Call Stack:");
		String formatOutput = "";
		//for (CallStack cs : callStack) {
		List<CallStack> stampTemp = new Stack<CallStack>();
		//Stack<CallStack> stampTemp = new Stack<CallStack>();
		stampTemp.addAll(callStack);
		Stack<CallStack> stampTemp2 = new Stack<CallStack>();
		stampTemp2 = (Stack<CallStack>)stampTemp;
			while(!stampTemp2.empty()){
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
		//System.out.println("setLastObj");
		//System.out.println("OBJ "+lastObj);
		DebuggerCLI.lastObj = new InspectionObject(lastObj);
	}

	public static void startShell() {

		//System.out.println(new Exception().getStackTrace()[0].getMethodName());
		//System.out.println(new Exception().getStackTrace()[0].getClassName());

		System.out.print("DebuggerCLI:> ");
		String command = scanner.next();
		String argument = "";
		String value = "";

		while (!command.equals("Abort")) {
			switch (command) {
			case "Info":
				System.out.println("execute Info: ");
				//System.out.println("sdf"+lastObj.getObj());
				DebuggerCLI.lastObj.printDetails();
				printCallStack();
				break;
			case "Throw":
				System.out.println("execute Throw: ");
				callStack.pop();
				CallStack c = callStack.peek();
				setLastObj(c.className);
				return;
			case "Return":
				argument = scanner.next();
				System.out.println("execute Return: " + argument);
				break;
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
	}

	private static void processGet(String argument) {
		//System.out.println("execute Get: " + argument);
		System.out.println(DebuggerCLI.lastObj.getField(argument));
	}

	private static void processSet(String field, String value) {
		System.out.println("execute Set: " + field + " " + value);
		DebuggerCLI.lastObj.setField(field, value);
	}

	// TODO ASK: When we do Retry does it count as execution of the program? Do
	// we add that method to the call stack?
	private static void processRetry() {
		System.out.println("execute Retry:");

		CallStack cs = callStack.peek();
		String methodName = cs.methodName;
		callStack.pop();

		ArrayList<Class<?>> argsTemp = new ArrayList<Class<?>>();

		// builds an ArrayList with the argument's type for that method
		for (Object o : cs.methodArgs) {
			System.out.println("ProcessRetry -> " +o);
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

		try {
			/*System.out.println("--------");
			for(Method m : lastObj.getObj().getClass()
					.getDeclaredMethods()){
				System.out.println(m);
			}*/
			Method m = lastObj.getObj().getClass()
					.getDeclaredMethod(methodName, args);
			m.setAccessible(true);
			m.invoke(lastObj.getObj(), cs.methodArgs);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		String classname = args[0];

		Translator translator = new MyTranslator();
		ClassPool pool = ClassPool.getDefault();
		Loader loader = new Loader();
		// VERY IMPORTANT LINE
		loader.delegateLoadingOf(ist.meic.pa.DebuggerCLI.class.getName());

		try {
			loader.addTranslator(pool, translator);
			System.out.println("MAIN:" +classname);
			Class<?> c = Class.forName(classname);
			Object o = c.newInstance();
			Object[] obs = {classname};
			//
			callStack.push(new DebuggerCLI.CallStack(o, "main", obs));
			setLastObj(Class.forName(classname).newInstance());
			loader.run(classname, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}