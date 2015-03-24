package ist.meic.pa;

import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import javassist.ClassPool;
import javassist.Loader;
import javassist.Translator;

public class DebuggerCLI {

	private static Scanner scanner = new Scanner(System.in);
	private static InspectionObject lastObj = null;
	private static List<CallStack> callStack = new Stack<CallStack>();
	
	
	private static class CallStack {

		String methodName;
		Object[] methodArgs;

		public CallStack(String methodName, Object[] methodArgs) {
			this.methodName = methodName;
			this.methodArgs = methodArgs;
		}

	}
		
	
	/*
	public static void printClassInfo(CtClass c) {

		System.out.println("Called Object: " + c.getName());
		
		for (CtMethod method : c.getMethods()) {
			System.out.println(method.getName());
		}
		
		for (CtField field : c.getFields()) {
			System.out.println(field.getName());
		}

	}*/
	
	public static void addToStack(String methodName, Object[] methodArgs){
		callStack.add(new DebuggerCLI.CallStack(methodName, methodArgs));
	}
	
	public static void printCallStack() {
		System.out.println("Call Stack:");
		String formatOutput = "";
		for(CallStack cs : callStack){
			System.out.print(lastObj.getObj().getClass().getName()+"."+cs.methodName+"(");
			for(Object o : cs.methodArgs){
				System.out.print(formatOutput + o);
				formatOutput = ", ";
			}
			System.out.println(")");
		}
	}
	
 	public static void setLastObj(Object lastObj) {
 		DebuggerCLI.lastObj = new InspectionObject(lastObj);
 	}

	public static void startShell(){
		
		//System.out.println(new Exception().getStackTrace()[0].getMethodName());

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
				return;
			case "Return":
				argument = scanner.next();
				System.out.println("execute Return: "+argument);
				break;
			case "Get":
				argument = scanner.next();
				System.out.println("execute Get: "+argument);
				DebuggerCLI.processGet(argument);
				break;
			case "Set":
				argument = scanner.next();
				value = scanner.next();
				System.out.println("execute Set: "+argument+" "+value);
				DebuggerCLI.processSet(argument, value);
				break;
			case "Retry":
				System.out.println("execute Retry: "+argument+" "+value);
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
 		System.out.println(argument+" "+DebuggerCLI.lastObj.getField(argument));
 	}
 	
	private static void processSet(String field, String value){
		DebuggerCLI.lastObj.setField(field, value);
	}
	

	public static void main(String[] args) {

		String classname = "test.Example";

		Translator translator = new MyTranslator();
		ClassPool pool = ClassPool.getDefault();
		Loader loader = new Loader();
		// VERY IMPORTANT LINE
		loader.delegateLoadingOf(ist.meic.pa.DebuggerCLI.class.getName());
		
		try {
			loader.addTranslator(pool, translator);
			loader.run(classname, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		//start shell when exception is thrown
		//startShell();

	}
}