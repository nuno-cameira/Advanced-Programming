package ist.meic.pa;

import java.util.Scanner;

import javassist.*;

public class DebuggerCLI {

	public static String test = "sdf";
	static Scanner scanner = new Scanner(System.in);
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

	public static void startShell(){

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
				break;
			case "Return":
				argument = scanner.next();
				System.out.println("execute Return: "+argument);
				break;
			case "Get":
				argument = scanner.next();
				System.out.println("execute Get: "+argument);
				break;
			case "Set":
				argument = scanner.next();
				value = scanner.next();
				System.out.println("execute Set: "+argument+" "+value);
				break;
			case "Retry":
				System.out.println("execute Retry: "+argument+" "+value);
				break;
			}
			command = scanner.next();
		}
	}

	public static void main(String[] args) {

		String classname = "test.Example";

		Translator translator = new MyTranslator();
		Loader loader = new Loader();
		ClassPool pool = ClassPool.getDefault();

		CtClass cc;
		try {
			loader.addTranslator(pool, translator);
			cc = pool.get(classname);
			pool.appendClassPath("ist.meic.pa");
			loader.run(classname, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		// start shell when exception is thrown
		//startShell();

	}
}