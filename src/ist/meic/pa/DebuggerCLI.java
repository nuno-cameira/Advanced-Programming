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

	// private static boolean canExecute = true;

	private static class CallStack {

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
	
	
	public static void printArgs(Object[] methodArgs){
		for (Object o : methodArgs) {
			System.out.println("ARGUMENTS" + o);
		}
	}

	public static void addToStack(String methodName, Object[] methodArgs) {

		System.out.println("Add to stack ->  " + methodName);
		for (Object o : methodArgs) {
			System.out.println("Add to stack -> for ->" + o);
		}

		// System.out.println("OO "+lastObj.getObj().getClass().getName());
		callStack.push(new DebuggerCLI.CallStack(lastObj.getObj(), methodName,
				methodArgs));
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
		// System.out.println("setLastObj");
		// System.out.println("OBJ "+lastObj);
		DebuggerCLI.lastObj = new InspectionObject(lastObj);
	}

	public static Object run() {
		String methodName = callStack.peek().methodName;
		System.out.println("Debugger -> run -> " + methodName);

		CallStack cs = callStack.peek();

		Class<?>[] args = getClassesOfMethodArgs(cs);
		for (Class<?> c : args) {
			System.out.println("Debugger -> run -> for" + c.getName());
		}

		try {

			Method m = lastObj.getObj().getClass().getMethod(methodName, args);
			m.setAccessible(true);
			for(Object o : callStack.peek().methodArgs){
				System.out.println("CENA s"+o.getClass());
			}
			return m.invoke(lastObj.getObj(), callStack.peek().methodArgs);
		} catch (IllegalAccessException | IllegalArgumentException
				| NoSuchMethodException | SecurityException
				| InvocationTargetException e) {
			//System.out.println(e);
			System.out.println(e.getCause());
			thrownException = e.getCause();
			//e.printStackTrace();
			return startShell();

		}
	}

	public static Object startShell() {

		/*
		 * System.out.println("canExecute "+canExecute); if(canExecute==false){
		 * System.out.println("IN IF " + canExecute); canExecute=true; //return;
		 * }
		 */

		// System.out.println(new
		// Exception().getStackTrace()[0].getMethodName());
		// System.out.println(new
		// Exception().getStackTrace()[0].getClassName());

		System.out.print("DebuggerCLI:> ");
		String command = scanner.next();
		String argument = "";
		String value = "";

		while (!command.equals("Abort")) {
			switch (command) {
			case "Info":
				System.out.println("execute Info: ");
				DebuggerCLI.lastObj.printDetails();
				printCallStack();
				break;
			case "Throw":
				System.out.println("execute Throw: ");
				callStack.pop();
				CallStack c = callStack.peek();
				setLastObj(c.className);
				return thrownException;
			case "Return":
				argument = scanner.next();
				System.out.println("execute Return: " + argument);
				String methodName = callStack.peek().methodName;
				
				

				
				Method m;
				try {
					m = lastObj.getObj().getClass()
							.getMethod(methodName, getClassesOfMethodArgs(callStack.peek()));
					m.setAccessible(true);
					System.out.println("Return -> method type" + FieldFactory.getType(argument, m.getReturnType().getName()).getClass());
					return FieldFactory.getType(argument, m.getReturnType().getName());
				} catch (NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
					
					
					/*System.out.println("CENA");
					Field f = lastObj.getObj().getClass().getField("doIReturn");
					f.setAccessible(true);
					f.set(lastObj.getObj(), true);
					return m.invoke(lastObj.getObj(),
							Double.parseDouble(argument));
					// System.out.println("CENA1");
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {/* canExecute=false;
					System.out.println("FINALLY");
				}*/
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
		return null;
	}

	private static void processGet(String argument) {
		// System.out.println("execute Get: " + argument);
		System.out.println(DebuggerCLI.lastObj.getField(argument));
	}

	private static void processSet(String field, String value) {
		System.out.println("execute Set: " + field + " " + value);
		DebuggerCLI.lastObj.setField(field, value);
	}

	private static void processRetry() {
		System.out.println("execute Retry:");

		CallStack cs = callStack.peek();
		String methodName = cs.methodName;
		//callStack.pop();
		System.out.println("MM "+methodName);

		Class<?>[] args = getClassesOfMethodArgs(cs);

		try {
			/*
			 * System.out.println("--------"); for(Method m :
			 * lastObj.getObj().getClass() .getDeclaredMethods()){
			 * System.out.println(m); }
			 */
			
			Method m = lastObj.getObj().getClass()
					.getDeclaredMethod(methodName, args);
			m.setAccessible(true);
			m.invoke(lastObj.getObj(), cs.methodArgs);
		} catch (IllegalAccessException | IllegalArgumentException
				| NoSuchMethodException | SecurityException
				| InvocationTargetException e) {
			//System.out.println(e);
			System.out.println(e.getCause());
		}
	}

	private static Class<?>[] getClassesOfMethodArgs(CallStack cs) {
		ArrayList<Class<?>> argsTemp = new ArrayList<Class<?>>();

		// builds an ArrayList with the argument's type for that method
		for (Object o : cs.methodArgs) {
			System.out.println(".getClassesOfMethodArgs..." + o);
			// System.out.println("ProcessRetry -> " +o);
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

		//Translator translator = new MyTranslator();
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
}