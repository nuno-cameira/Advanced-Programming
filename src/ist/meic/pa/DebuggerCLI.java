package ist.meic.pa;

import java.lang.reflect.Field;
import java.util.Scanner;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Loader;
import javassist.Translator;

public class DebuggerCLI {

	static Scanner scanner = new Scanner(System.in);
	// static List<Object> objFlow = new ArrayList<Object>();
	private static Object lastObj = new Object();

	/*
	 * public static Object getLastObj() { return lastObj; }
	 */

	public static void setLastObj(Object lastObj) {
		DebuggerCLI.lastObj = lastObj;
	}

	public static void printClassInfo(CtClass c) {

		System.out.println("Called Object: " + c.getName());
		/*
		 * for (CtMethod method : c.getMethods()) {
		 * System.out.println(method.getName()); }
		 */
		for (CtField field : c.getFields()) {
			System.out.println(field.getName());
		}
	}

	public static void sayHi() {
		System.out.println("HELLOOO");
	}

	public static void startShell() {

		System.out.print("DebuggerCLI:> ");
		String command = scanner.next();
		String argument = "";
		String value = "";

		while (!command.equals("Abort")) {
			switch (command) {
			case "Info":
				System.out.println("execute Info: ");
				break;
			case "Throw":
				System.out.println("execute Throw: ");
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
				System.out.println("execute Retry: " + argument + " " + value);
				break;
			default:
				System.out.println("Unknown command");
			}
			System.out.print("DebuggerCLI:> ");
			command = scanner.next();
		}
		System.exit(0);
	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*ClassPool pool = ClassPool.getDefault();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

	// TODO shouldn't this use like CtField and CtClass instead of Class<?> and
	// Field?
	private static void processGet(String argument) {
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
			System.out.println("run class after instrumentation");
			loader.run(classname, args);
		} catch (Throwable e) {
			// System.out.println(e);
			e.printStackTrace();
			// startShell();

		}
	}
}
