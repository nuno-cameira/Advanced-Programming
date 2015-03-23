package ist.meic.pa;

import java.util.Scanner;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Loader;
import javassist.Translator;

public class DebuggerCLI {

	static Scanner scanner = new Scanner(System.in);
	private static InspectionObject lastObj = null;
	
	public static void printClassInfo(CtClass c) {

		System.out.println("Called Object: " + c.getName());
		/*
		for (CtMethod method : c.getMethods()) {
			System.out.println(method.getName());
		}
		*/
		for (CtField field : c.getFields()) {
			System.out.println(field.getName());
		}

	}
	
 	public static void setLastObj(Object lastObj) {
 		DebuggerCLI.lastObj = new InspectionObject(lastObj);
 	}
 
 	private static void processGet(String argument) {	
 		System.out.println(argument+" "+DebuggerCLI.lastObj.getField(argument));
 	}
 	
	public static void processSet(String field, String value){
		DebuggerCLI.lastObj.setField(field, value);
	}

	public static void startShell(){
		
		// TODO inject
		 /*class Local {};
	     String name = Local.class.getEnclosingMethod().getName();
	     System.out.println("LE NAME " + name);*/

		System.out.print("DebuggerCLI:> ");
		String command = scanner.next();
		String argument = "";
		String value = "";
		
		while (!command.equals("Abort")) {
			switch (command) {
			case "Info":
				System.out.println("execute Info: ");
				DebuggerCLI.lastObj.printDetails();
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
	
	
	/*
	private static void processSet(String argument, String value) {
		System.out.println("execute Set: " + argument + " " + value);		

		try {
			Class<?>  c = lastObj.getClass();
			Field field = c.getDeclaredField(argument);
			field.setAccessible(true);
			Object o  = lastObj;
			// TODO We need to convert value
			field.set(o, value);
			System.out.println(field.get(o));
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}*/
		
		/*
		ClassPool pool = ClassPool.getDefault();
		try {
			CtClass c = pool.get(lastObj.getClass().getName());
			CtField f = c.getDeclaredField(argument);
			if (f.getType().isPrimitive()) {
				System.out.println("yay");
				c.defrost();
				CtMethod m = CtNewMethod.setter("set" + argument, f);
				c.addMethod(m);
				c.writeFile();
				
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}*/

	// TODO shouldn't this use like CtField and CtClass instead of Class<?> and
	// Field?
	/*private static void processGet(String argument) {
		System.out.println("execute Get: " + argument);
		try {
			// System.out.println(lastObj);
			Class<?> c = lastObj.getClass();
			// System.out.println(c);
			Field f = c.getDeclaredField(argument);
			f.setAccessible(true);
			Object value = f.get(lastObj);
			System.out.println("Field: " + f.getName());
			System.out.println("value: " + value);
		} catch (NoSuchFieldException e) {
			System.out.println("Error: there is no such field");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}*/
	
	

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