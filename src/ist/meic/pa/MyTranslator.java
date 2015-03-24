package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.Translator;

public class MyTranslator implements Translator {

	@Override
	public void onLoad(ClassPool pool, String classname)
			throws NotFoundException, CannotCompileException {
		System.out.println("on Load: " + classname);
		// Hacking like a pro
		/*
		 * if (classname.equals("ist.meic.pa.DebuggerCLI") ||
		 * classname.equals("javassist.Translator") ||
		 * classname.equals("javassist.NotFoundException") ||
		 * classname.equals("javassist.ClassPool")) {
		 * System.out.println("Nop, exit"); return; }
		 */

		CtClass cc = pool.get(classname);
		// cc.setModifiers(Modifier.PUBLIC);

		System.out.println("Changed methods:");
		CtMethod[] ctmethods = cc.getDeclaredMethods();

		CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
		String insertionString = "{ ist.meic.pa.DebuggerCLI.setLastObj($0); ist.meic.pa.DebuggerCLI.addToStack(new Exception().getStackTrace()[0].getMethodName(), $args);}";

		for (CtMethod ctm : ctmethods) {
			if (!Modifier.isStatic(ctm.getModifiers())) {
				System.out.println(" " + ctm.getName());
				ctm.insertBefore(insertionString);
			}
			ctm.addCatch("{ ist.meic.pa.DebuggerCLI.startShell(); throw $e; }",
					etype);
		}
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {
	}

}