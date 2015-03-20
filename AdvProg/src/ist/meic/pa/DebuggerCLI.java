package ist.meic.pa;

import javassist.*;

public class DebuggerCLI {

	public static void printClassInfo(CtClass c){
		
		System.out.println("Called Object: "+c.getName());
		for(CtMethod method: c.getMethods()){
			System.out.println(method.getName());
		}
		for(CtField field: c.getFields()){
			System.out.println(field.getName());
		}
		
	}
	
	public static void main(String[] args){
		
		String classname = "ist.meic.pa.Test";

		Translator translator = new MyTranslator();
	    Loader loader = new Loader();
		ClassPool pool = ClassPool.getDefault();
		
		
		CtClass cc;
		try {

			loader.addTranslator(pool, translator);
			loader.run(classname, args);
			cc = pool.get(classname);
			printClassInfo(cc);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
}
