package ist.meic.pa;

import ist.meic.pa.fields.FieldFactory;
import ist.meic.pa.fields.FieldType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class InspectionObject {

	Object o = null;
	
	public InspectionObject(Object o){
		this.o = o;
	}
	
	public void printDetails(){
		System.out.println(o.toString() +" is an instance of "+o.getClass().getName());
		System.out.println("---------------");
		printFields();
		// TODO print stack trace
		
	}
	
	public String getField(String field){
		Field f = null;
		try {
			f = o.getClass().getDeclaredField(field);
			f.setAccessible(true);
			if(f.get(o) == null)
				return "null";
			return f.get(o).toString();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "null";
	}
	
	public void setField(String field, String value){
		Field f = null;
		try {
			f = o.getClass().getDeclaredField(field);
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		f.setAccessible(true);
		
		FieldType ft = FieldFactory.getFieldType(o, f);
		ft.modify(value);
	}
	
	
	
	public void printFields(){
		Field[] fields = o.getClass().getDeclaredFields();
		for (Field f: fields){
			try {
				f.setAccessible(true);				
				if(f.getModifiers() > 0){
					System.out.print(Modifier.toString(f.getModifiers())+" ");
				}
				System.out.println(f.getType() + " " + f.getName() + " = "  + f.get(o));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

}
