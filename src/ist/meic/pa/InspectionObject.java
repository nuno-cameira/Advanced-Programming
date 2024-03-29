package ist.meic.pa;

import ist.meic.pa.CallStack;
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

		/*
		 * Check if arguments to method were null
		 */
		if (cs.methodArgs.length > 1 && cs.methodArgs[0] == null) {
			if (this.obj != null) {
				/*
				 * Non-static method
				 */
				Method[] methods = this.obj.getClass().getMethods();
				for (Method m : methods) {
					if (m.getName().equals(cs.methodName)) {
						m.setAccessible(true);
						Object o = m.invoke(this.obj, cs.methodArgs);
						/*
						 * If there is no exception thrown on invoke, then pop
						 * the method from the call stack
						 */
						DebuggerCLI.getCallStack().pop();
						return o;
					}
				}
			} else {
				/*
				 * Static method
				 */
				Class<?> c = (Class<?>) cs.className;
				Method[] methods = c.getMethods();
				for (Method m : methods) {
					if (m.getName().equals(cs.methodName)) {
						m.setAccessible(true);
						Object o = m.invoke(this.obj, cs.methodArgs);
						DebuggerCLI.getCallStack().pop();
						return o;
					}
				}
			}
		}

		/*
		 * The arguments are not null
		 */
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
				Class<?> className = (Class<?>) cs.className;
				while(className.getSuperclass() != null){
					try{
						f = className.getDeclaredField(field);
						break;
					}
					catch(NoSuchFieldException e){
						className = className.getSuperclass();
					}
				}
				try {
					if (f.get(obj) == null) {
						return "null";
					}
					return f.get(obj).toString();
				} catch (IllegalArgumentException | IllegalAccessException e) {
					System.out.println("");
				}
			} catch (SecurityException e) {
				System.out.print("No such field.");
			}
		} else {
			try {
				Class<?> className = (Class<?>) obj.getClass();
				while(className.getSuperclass() != null){
					try{
						f = className.getDeclaredField(field);
						break;
					}
					catch(NoSuchFieldException e){
						className = className.getSuperclass();
					}
				}
				
				f.setAccessible(true);
				if (f.get(obj) == null) {
					return "null";
				}
				return f.get(obj).toString();
			} catch (SecurityException e) {
				System.out.print("No such field.");
			} catch (IllegalArgumentException e) {
				System.out.println(e.getCause());
			} catch (IllegalAccessException e) {
				System.out.print(e.getCause());
			}
		}
		return "";
	}

	public void setField(String field, String value) {
		Field f = null;
		if (this.getObj() == null) {
			CallStack cs = DebuggerCLI.getCallStack().peek();
			try {
				Class<?> className = (Class<?>) cs.className;
				while(className.getSuperclass() != null){
					try{
						f = className.getClass().getDeclaredField(field);
						break;
					}
					catch(NoSuchFieldException e){
						className = className.getSuperclass();
					}
				}
				
			} catch (SecurityException e) {
				System.out.println("No such field");
				return;
			}
		} else {
			try {
				Class<?> className = (Class<?>) obj.getClass();
				while(className.getSuperclass() != null){
					try{
						f = className.getDeclaredField(field);
						break;
					}
					catch(NoSuchFieldException e){
						className = className.getSuperclass();
					}
				}
			} catch (SecurityException e) {
				System.out.println("No such field");
				return;
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
