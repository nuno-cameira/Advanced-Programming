package ist.meic.pa;

import javassist.*;

public class MyTranslator implements Translator{

	@Override
	public void onLoad(ClassPool pool, String classname) throws NotFoundException,
			CannotCompileException {
		System.out.println(classname);
        CtClass cc = pool.get(classname);
        //cc.setModifiers(Modifier.PUBLIC);
        
        /*CtMethod m = cc.getDeclaredMethod("bar");
        m.insertBefore("System.out.println(\"before\");");
        CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
        m.addCatch("{ System.out.println(\"sdgrf\"+$e); throw $e; }", etype);*/
        CtMethod x = null;
        CtMethod[] m = cc.getDeclaredMethods();
        for(CtMethod mm : m){
        	System.out.println(mm.getName());
        	if(mm.equals("startShell")){
        		x = mm;
        		
        	}
        }
        
        CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
        for(CtMethod ctm : m){
        	
        	ctm.addCatch("{ ist.meic.pa.DebuggerCLI.startShell(); throw $e; }", etype);
        	ctm.addCatch("{ test.Example.printBla(); throw $e; }", etype);
        }
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {	
	}

}