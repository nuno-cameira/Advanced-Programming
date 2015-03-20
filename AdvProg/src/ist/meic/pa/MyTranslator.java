package ist.meic.pa;

import javassist.*;

public class MyTranslator implements Translator{

	@Override
	public void onLoad(ClassPool pool, String classname) throws NotFoundException,
			CannotCompileException {
        CtClass cc = pool.get(classname);
        //cc.setModifiers(Modifier.PUBLIC);
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {	
	}

}
