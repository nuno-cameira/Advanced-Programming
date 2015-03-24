package ist.meic.pa;

import ist.meic.pa.fields.FieldFactory;
import ist.meic.pa.fields.FieldType;

import java.lang.reflect.Field;

public class InspectionObject {

	private Object obj = null;
	
	public Object getObj() {
		return obj;
	}

	public InspectionObject(Object o){
		this.obj = o;
	}
	
	public void printDetails(){
		//System.out.println(obj.toString() +" is an instance of "+obj.getClass().getName());
		//System.out.println("---------------");
		System.out.println("Called Object: " + obj.toString());
		printFields();
		// TODO print stack trace
		
	}
	
	public String getField(String field){
		Field f = null;
		try {
			f = obj.getClass().getDeclaredField(field);
			f.setAccessible(true);
			if(f.get(obj) == null)
				return "null";
			return f.get(obj).toString();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	public void setField(String field, String value){
		Field f = null;
		try {
			f = obj.getClass().getDeclaredField(field);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		f.setAccessible(true);
		
		FieldType ft = FieldFactory.getFieldType(obj, f);
		ft.modify(value);
	}
	
	
	
	public void printFields(){
		Field[] fields = obj.getClass().getDeclaredFields();
		System.out.print("       Fields: ");
		String formatOutput = "";
		for (Field f: fields){
			try {
				f.setAccessible(true);				
				/*if(f.getModifiers() > 0){
					System.out.print(Modifier.toString(f.getModifiers())+" ");
				}*/
				System.out.println(formatOutput + f.getType() + " " + f.getName() + " = "  + f.get(obj));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			formatOutput = "               ";
		}
	}

}
