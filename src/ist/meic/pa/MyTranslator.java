package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;

public class MyTranslator implements Translator{

	@Override
	public void onLoad(ClassPool pool, String classname) throws NotFoundException,
			CannotCompileException {
		System.out.println("on Load: "+ classname);
		if(classname.equals("ist.meic.pa.DebuggerCLI") || classname.equals("javassist.Translator")){
			System.out.println("Nop, exit");
			return;
		}
        CtClass cc = pool.get(classname);
        //cc.setModifiers(Modifier.PUBLIC);
        
        /*CtMethod m = cc.getDeclaredMethod("bar");
        m.insertBefore("System.out.println(\"before\");");
        CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
        m.addCatch("{ System.out.println(\"sdgrf\"+$e); throw $e; }", etype);*/
        
        System.out.println("Changed methods:");
        CtMethod[] m = cc.getDeclaredMethods();
        for(CtMethod mm : m){
        	System.out.println(" " + mm.getName());
        }
        
        CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
        for(CtMethod ctm : m){
        	
        	//ctm.addCatch("{ ist.meic.pa.DebuggerCLI.startShell(); throw $e; }", etype);
        	//ctm.addCatch("{ System.out.println(ist.meic.pa.DebuggerCLI.test); throw $e; }", etype);
        	ctm.addCatch("{ System.out.println(\"DERP\"); ist.meic.pa.DebuggerCLI.sayHi(); throw $e; }", etype);
        }
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {	
	}

}
