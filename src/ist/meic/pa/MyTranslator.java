package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class MyTranslator implements Translator {

	public void onLoad(ClassPool pool, String classname)
			throws NotFoundException, CannotCompileException {

		CtClass cc = pool.get(classname);
		CtMethod[] ctmethods = cc.getDeclaredMethods();

		String packageName = cc.getPackageName();

		if (!packageName.equals("ist.meic.pa")
				&& !packageName.startsWith("javassist") && packageName != null) {

			for (CtMethod ctm : ctmethods) {

				ctm.instrument(new ExprEditor() {
					public void edit(MethodCall m)
							throws CannotCompileException {
						String name = m.getMethodName();
						if (!m.getClassName().startsWith("java.io")) {

							m.replace("{  "
									+ "   ist.meic.pa.DebuggerCLI.setLastObj($0); "
									+ "   ist.meic.pa.DebuggerCLI.addToStack($class,\""
									+ name
									+ "\", $args); "
									+ "   Object o = ist.meic.pa.DebuggerCLI.run(); "
									+ "   if(o instanceof Exception) { "
									+ "      	 throw (Throwable)o; " + "   } "
									+ "   else {" + "      $_ = ($r)o;"
									+ "   } "

									+ "}");
						}
					}
				});
			}
		}
	}

	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {
		// not needed
	}

}