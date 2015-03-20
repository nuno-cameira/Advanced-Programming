package ist.meic.pa;

import java.util.Scanner;

import javassist.*;

public class DebuggerCLI {

	public static String test = "sdf";
	static Scanner scanner = new Scanner(System.in);

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
				System.out.println("execute Get: " + argument);
				break;
			case "Set":
				argument = scanner.next();
				value = scanner.next();
				System.out.println("execute Set: " + argument + " " + value);
				break;
			case "Retry":
				System.out.println("execute Retry: " + argument + " " + value);
				break;
			default:
				System.out.println("Unknown command");
			}
			command = scanner.next();
		}
		System.exit(0);
	}

	public static void main(String[] args) {

		String classname = "test.Example";

		Translator translator = new MyTranslator();
		ClassPool pool = ClassPool.getDefault();
		Loader loader = new Loader();

		// CtClass cc;
		try {
			loader.addTranslator(pool, translator);
			// cc = pool.get(classname);

			// cc.writeFile();
			System.out.println("run class after instrumentation");
			loader.run(classname, args);
			// printClassInfo(cc);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
