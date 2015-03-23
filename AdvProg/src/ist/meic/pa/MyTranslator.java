package ist.meic.pa;

import javassist.*;

public class MyTranslator implements Translator{

	@Override
	public void onLoad(ClassPool pool, String classname) throws NotFoundException,
			CannotCompileException {
		System.out.println(classname);
        CtClass cc = pool.get(classname);
        //cc.setModifiers(Modifier.PUBLIC);

        CtMethod[] ctmethods = cc.getDeclaredMethods();
        
        CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
        for(CtMethod ctm : ctmethods){
        	if (!Modifier.isStatic(ctm.getModifiers())) {
				System.out.println(" " + ctm.getName());
				ctm.insertBefore("{ System.out.println(\"RAWR \"); ist.meic.pa.DebuggerCLI.setLastObj($0); }");
			}
        	ctm.addCatch("{ ist.meic.pa.DebuggerCLI.startShell(); throw $e; }", etype);
        }
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {	
	}

}