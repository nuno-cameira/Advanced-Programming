package ist.meic.pa;

import ist.meic.pa.fields.FieldFactory;

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

	public static Stack<CallStack> getCallStack() {
		return callStack;
	}
	
	public static void addToStack(Object classname, String methodName,
			Object[] methodArgs) {
		if (lastObj.getObj() != null) {
			callStack.push(new CallStack(lastObj.getObj(),
					methodName, methodArgs));
		} else {
			callStack.push(new CallStack(classname, methodName,
					methodArgs));
		}
	}
	
	public static void printCallStack() {
		System.out.println("Call Stack:");
		String formatOutput = "";
		
		List<CallStack> stackTemp = new Stack<CallStack>();
		stackTemp.addAll(callStack);
		Stack<CallStack> callStackTemp = new Stack<CallStack>();
		callStackTemp = (Stack<CallStack>) stackTemp;
		while (!callStackTemp.empty()) {
			CallStack cs = callStackTemp.pop();
			if (cs.className instanceof Class<?>) {
				Class<?> c = (Class<?>) cs.className;
				System.out.print(c.getName() + "." + cs.methodName + "(");
			} else {
				System.out.print(cs.className.getClass().getName() + "."
						+ cs.methodName + "(");
			}
			for (Object o : cs.methodArgs) {
				System.out.print(formatOutput + o);
				formatOutput = ", ";
			}
			formatOutput = "";
			System.out.println(")");
		}
	}

	public static Throwable getThrownException() {
		return thrownException;
	}

	public static void setThrownException(Throwable thrownException) {
		DebuggerCLI.thrownException = thrownException;
	}

	public static void setLastObj(Object lastObj) {
		DebuggerCLI.lastObj = new InspectionObject(lastObj);
	}

	public static Object run() {
		Object ret = null;
		try {
			return DebuggerCLI.lastObj.invokeMethodOnStack();
		} catch (IllegalArgumentException | SecurityException
				| NoSuchMethodException | IllegalAccessException
				| InvocationTargetException e) {
			System.out.println(e.getCause());
			setThrownException(e.getCause());
		}
		
		do {
		  ret = startShell();
		  if(ret.equals("retry")){
		    try{
		      return DebuggerCLI.lastObj.invokeMethodOnStack();
		    }catch (IllegalArgumentException | SecurityException
					| NoSuchMethodException | IllegalAccessException
					| InvocationTargetException e) {
				System.out.println(e.getCause());
				setThrownException(e.getCause());
			}
		  }
		} while (ret.equals("retry"));
		return ret;
	}

	public static Object startShell() {
		System.out.print("DebuggerCLI:> ");
		String command = scanner.next();
		String argument = "";
		String value = "";

		while (!command.equals("Abort")) {
			switch (command) {
			case "Info":
				DebuggerCLI.lastObj.printInfo();
				printCallStack();
				break;
			case "Throw":
				callStack.pop();
				CallStack c = callStack.peek();
				if(c.className instanceof Class<?>){
					setLastObj(null);
				}else{
					setLastObj(c.className);
				}
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
				return "retry";
			default:
				System.out.println("Unknown command");
			}
			System.out.print("DebuggerCLI:> ");
			command = scanner.next();
		}
		System.exit(0);
		return null;
	}

	public static Method getMethodSafe() {
		CallStack cs = callStack.peek();
		String methodName = cs.methodName;

		Object last = DebuggerCLI.lastObj.getObj();
		
		if (cs.methodArgs.length > 0 && cs.methodArgs[0] == null) {
			/*
			 *  Non-static method
			 */
			if (last != null) {
				Method[] methods = last.getClass().getMethods();
				for (Method m : methods) {
					if (m.getName().equals(cs.methodName)) {
						m.setAccessible(true);
						return m;
					}
				}
			} else {
				/*
				 *  Static method
				 */
				Class<?> c = (Class<?>) cs.className;
				Method[] methods = c.getMethods();
				for (Method m : methods) {
					if (m.getName().equals(cs.methodName)) {
						m.setAccessible(true);
						return m;
					}
				}
			}
		}
		
		/*
		 * Arguments not null
		 */
		Class<?>[] args = DebuggerCLI.getClassesOfMethodArgs(cs);

		Method m = null;
		
		try {
			if (last != null) {
				/*
				 * Non-static method
				 */
				m = last.getClass().getMethod(methodName, args);
				m.setAccessible(true);
				return m;
			} else {
				/*
				 * Static-method
				 */
				Class<?> c = (Class<?>) cs.className;
				m = c.getMethod(methodName, args);
				m.setAccessible(true);
				return m;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}

	private static Object processReturn(String argument) {
		Method m = getMethodSafe();
		if (m != null) {
			callStack.pop();
			return FieldFactory.getType(argument, m.getReturnType().getName());
		}else{
			System.out.println("Method not found!");
			return m;
		}

	}

	private static void processGet(String argument) {
		System.out.println(DebuggerCLI.lastObj.getField(argument));
	}

	private static void processSet(String field, String value) {
		DebuggerCLI.lastObj.setField(field, value);
	}

	public static Class<?>[] getClassesOfMethodArgs(CallStack cs) {
		ArrayList<Class<?>> argsTemp = new ArrayList<Class<?>>();

		/*
		 * Builds an ArrayList with the argument's type for that method
		 */
		for (Object o : cs.methodArgs) {
			Class<?> cc = null;
			if (Unwrapper.isWrapperType(o.getClass())) {
				cc = Unwrapper.unwrap(o.getClass());
			} else {
				cc = o.getClass();
			}
			argsTemp.add(cc);
		}

		/*
		 *  Converts ArrayList to a simple array
		 */
		Class<?>[] argsType = new Class<?>[argsTemp.size()];
		Class<?>[] args = argsTemp.toArray(argsType);
		return args;
	}
	
	public static void main(String[] args) {

		String classname = args[0];
		String[] arguments = new String[args.length - 1];

		for (int i = 0; i < args.length - 1; i++) {
			arguments[i] = args[i + 1];
		}

		Translator translator = new MyTranslator();
		ClassPool pool = ClassPool.getDefault();
		Loader loader = new Loader();
		
		loader.delegateLoadingOf(ist.meic.pa.DebuggerCLI.class.getName());

		try {
			loader.addTranslator(pool, translator);
			Class<?> c = Class.forName(classname);
			Object o = c.newInstance();
			
			callStack.push(new CallStack(o, "main", arguments));
			setLastObj(Class.forName(classname));
			loader.run(classname, arguments);
		} catch (Throwable e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		}
	}
}