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
		/*if (classname.equals("ist.meic.pa.DebuggerCLI")
				|| classname.equals("javassist.Translator")
				|| classname.equals("javassist.NotFoundException")
				|| classname.equals("javassist.ClassPool")) {
			System.out.println("Nop, exit");
			return;
		}*/
		
		CtClass cc = pool.get(classname);

		System.out.println("Changed methods:");
		CtMethod[] m = cc.getDeclaredMethods();
		/*for (CtMethod mm : m) {
			System.out.println(" " + mm.getName());
		}*/

		CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
		for (CtMethod ctm : m) {
			if (!Modifier.isStatic(ctm.getModifiers())) {
				System.out.println(" " + ctm.getName());
				ctm.insertBefore("{ System.out.println(\"RAWR \"); ist.meic.pa.DebuggerCLI.setLastObj($0); }");
			}
			ctm.addCatch(
					"{ System.out.println($e); ist.meic.pa.DebuggerCLI.startShell(); throw $e; }",
					etype);
		}
	}

	@Override
	public void start(ClassPool pool) throws NotFoundException,
			CannotCompileException {
	}

}
