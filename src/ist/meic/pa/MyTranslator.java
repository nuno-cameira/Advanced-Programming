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

	@Override
	public void onLoad(ClassPool pool, String classname)
			throws NotFoundException, CannotCompileException {
		System.out.println("on Load: " + classname);

		CtClass cc = pool.get(classname);
		// cc.setModifiers(Modifier.PUBLIC);

		CtMethod[] ctmethods = cc.getDeclaredMethods();

		for (CtMethod ctm : ctmethods) {
			ctm.instrument(new ExprEditor() {
				public void edit(MethodCall m) throws CannotCompileException {
					String packageName = null;
					try {
						packageName = m.getMethod().getDeclaringClass()
								.getPackageName();
						//System.out.println("  Tried to instrument "+m.getMethodName()+packageName);

						if (packageName.equals("test") || m.getMethodName().equals("parseInt")) {
							System.out.println("  Instrumented "
									+ m.getMethodName());
							String name = m.getMethodName();

							m.replace("{  ist.meic.pa.DebuggerCLI.setLastObj($0); "
									+ "   ist.meic.pa.DebuggerCLI.addToStack($class,\""+ name + "\", $args); "
//									+ "   System.out.println(\""+ name + "\");"
									+ "   Object o = ist.meic.pa.DebuggerCLI.run(); "
									+ "   if(o instanceof Exception) { "
//									+ "      System.out.println(\"->EXCEPTION\"); "
//									+ "      if(\""+ name + "\".equals(\"main\")) {"
									
//									+ "      } else {"
									+ "      	 throw (Throwable)o; "
//									+ "      }"
									+ "   } "
									+ "   else {"
//									+ "      System.out.println(\"->\"\""+name+"\"+\" \"+($r)o);"
									+ "      $_ = ($r)o;"
									+ "   } "
									+ "}");
						}
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
				}
			});
		/*	
			ctm.instrument(new ExprEditor() {
				public void edit(Handler h) throws CannotCompileException {
					String packageName = h.getEnclosingClass().getPackageName();
//					System.out.println(packageName);
					if (packageName.equals("test")) {
						h.insertBefore("{System.out.println(\"HANDLER CATCH \"+$1); }");
					}
				}
			});
			*/
		}
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {
	}

}