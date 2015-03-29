package ist.meic.pa;

import ist.meic.pa.DebuggerCLI.CallStack;
import ist.meic.pa.fields.FieldFactory;
import ist.meic.pa.fields.FieldType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InspectionObject {

	private Object obj = null;

	public Object getObj() {
		return obj;
	}

	public InspectionObject(Object o) {
		this.obj = o;
	}	
	
	public Object invokeMethodOnStack() throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		
		CallStack cs = DebuggerCLI.getCallStack().peek();
		String methodName = cs.methodName;

		// check if arguments to method were null
		if (cs.methodArgs.length>=1 && cs.methodArgs[0]==null) { 
			if (this.obj != null) {
				Method[] methods = this.obj.getClass().getMethods();
				for(Method m: methods){
					if(m.getName().equals(cs.methodName)){
						m.setAccessible(true);
						Object o = m.invoke(this.obj, cs.methodArgs);
						DebuggerCLI.getCallStack().pop(); // if there is no exception thrown on invoke.. pop the method from the call stack
						return o;
					}
				}
			}else{
				Class<?> c = (Class<?>) cs.className;
				Method[] methods = c.getMethods();
				for(Method m: methods){
					if(m.getName().equals(cs.methodName)){
						m.setAccessible(true);
						Object o = m.invoke(this.obj, cs.methodArgs);
						DebuggerCLI.getCallStack().pop();
						return o;
					}
				}			
			}
		}
		
		Class<?>[] args = DebuggerCLI.getClassesOfMethodArgs(cs);

		if (this.obj != null) {
			Method m = this.obj.getClass().getMethod(methodName, args);
			m.setAccessible(true);
			Object o = m.invoke(this.obj, cs.methodArgs);
			DebuggerCLI.getCallStack().pop();
			return o;
		} else {
			Class<?> c = (Class<?>) cs.className;
			Method m = c.getMethod(methodName, args);
			m.setAccessible(true);
			Object o = m.invoke(this.obj, cs.methodArgs);
			DebuggerCLI.getCallStack().pop();
			return o;
		}
	}

	public void printInfo() {
		if (this.obj != null) {
			System.out.println("Called Object:  " + obj.toString());
		} else { 
			System.out.println("Called Object:  null");
		}
		printFields();
	}

	public String getField(String field) {
		Field f = null;

		if (this.getObj() == null) {
			CallStack cs = DebuggerCLI.getCallStack().peek();
			try {
				f = ((Class<?>) cs.className).getDeclaredField(field);
				try {
					if (f.get(obj) == null)
						return "null";
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					return f.get(obj).toString();
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				f = obj.getClass().getDeclaredField(field);
			} catch (NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			f.setAccessible(true);
			try {
				if (f.get(obj) == null)
					return "null";
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				return f.get(obj).toString();
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "null";
	}

	public void setField(String field, String value) {
		Field f = null;
		if (this.getObj() == null) {
			CallStack cs = DebuggerCLI.getCallStack().peek();
			try {
				f = cs.className.getClass().getDeclaredField(field);
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		} else {
			try {
				f = obj.getClass().getDeclaredField(field);
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}

		f.setAccessible(true);
		FieldType ft = FieldFactory.getFieldType(obj, f);
		ft.modify(value);
	}

	public void printFields() {

		Field[] fields = null;
		if (this.getObj() == null) {
			CallStack cs = DebuggerCLI.getCallStack().peek();
			fields = ((Class<?>) cs.className).getDeclaredFields();
		} else {
			fields = obj.getClass().getDeclaredFields();
		}

		System.out.print("       Fields:  ");
		String formatOutput = "";
		for (Field f : fields) {
			try {
				f.setAccessible(true);
				System.out.print(formatOutput + f.getName());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			formatOutput = " ";
		}
		System.out.println();
	}

}
