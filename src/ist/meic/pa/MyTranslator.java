package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
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

		CtClass returnType = null;
		
		for (CtMethod ctm : ctmethods) {
			if (!Modifier.isStatic(ctm.getModifiers())) {
				System.out.println(" " + ctm.getName());
				ctm.insertBefore(insertionString);
				
				
				returnType = ctm.getReturnType();
				System.out.println("--------"+ returnType.getName());
				CtMethod m = CtNewMethod.make(
						"public "+returnType.getName()+" "+ctm.getName()+"Return(Object r) { return ($r)r; }", cc);
				cc.addMethod(m);
				
				CtField f = CtField.make("public boolean doIReturn = false;", cc);
				cc.addField(f);
				
			}
			
			
			
			
			if(!ctm.getName().equals("main")){
				ctm.addCatch(
						"{ System.out.println($e); Object o = ist.meic.pa.DebuggerCLI.startShell(); if(doIReturn==true){return "+ ctm.getName()+"Return(o)" +";} throw $e; }",
						etype);	
			}
			else ctm.addCatch(
					"{ System.out.println($e); ist.meic.pa.DebuggerCLI.startShell(); throw $e; }",
					etype);		
		}
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {
	}

}